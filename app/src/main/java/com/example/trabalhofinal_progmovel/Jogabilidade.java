package com.example.trabalhofinal_progmovel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Jogabilidade extends SurfaceView implements Runnable, SurfaceHolder.Callback, GestureDetector.OnGestureListener {
    private Thread thread;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String uid;
    private boolean isPlaying;
    private SurfaceHolder surfaceHolder;
    private GestureDetector gestureDetector;
    private TextView txtScore;

    private TextView txtNewRecorde;
    private boolean novoRecorde = false;
    private boolean isPaused = false;
    private ImageButton btnPause;

    private float passaroY;
    private float velocity = 0;
    private final float gravity = 1f;
    private final float flapVelocity = -20;
    private Bitmap passaroBitmap;
    private float passaroX;
    private int passaroWidth, passaroHeight;

    private List<Obstaculo> obstacles;
    private Random random;
    private int numero = 0;
    private int obstacleGap = 480; // Gap entre os obstáculos de cima e de baixo (ajustado para 400)
    private int obstacleFrequency = 600; // Distância entre os obstáculos (ajustado para 500)
    private int obstacleWidth = 180; // Largura dos obstáculos

    private Bitmap backgroundBitmap;
    private SoundPool soundPool;
    private int flapSoundId;
    private int crashSoundId;
    private int pontuaSoundId;

    public Jogabilidade(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public Jogabilidade(Context context, float startY) {
        super(context);
        this.passaroY = startY;
        init(context);
    }

    private void init(Context context) {
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        gestureDetector = new GestureDetector(context, this);

        FirebaseApp.initializeApp(context);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Obtenha o usuário autenticado
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            uid = user.getUid();
        } else {
            Log.e("Jogabilidade", "Usuário não autenticado");
        }

        // Carrega as imagens
        passaroBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vermelho);
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);

        passaroWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        passaroHeight = passaroWidth;
        passaroBitmap = Bitmap.createScaledBitmap(passaroBitmap, passaroWidth, passaroHeight, false);

        obstacles = new ArrayList<>();
        random = new Random();

        txtScore = ((Activity) context).findViewById(R.id.txtScore);
        txtNewRecorde = ((Activity) context).findViewById(R.id.txtNewRecorde);
        Typeface typeface = ResourcesCompat.getFont(this.getContext(), R.font.pixelify_sans_regular);
        if (txtScore != null) {
            atualizarScore();
        } else {
            Log.e("Jogabilidade", "TextView txtScore não foi encontrada");
        }

        btnPause = ((Activity) context).findViewById(R.id.btnPause);
        btnPause.setImageResource(android.R.drawable.ic_media_pause);
        btnPause.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPaused) {
                    resume();
                } else {
                    pause();
                }
            }
        });
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(audioAttributes)
                .build();

        // Carrega o som de flapping
        flapSoundId = soundPool.load(context, R.raw.flap, 1);
        crashSoundId = soundPool.load(context, R.raw.fail, 1);
        pontuaSoundId = soundPool.load(context, R.raw.pontuacao, 1);
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    private void spawnObstacle() {
        int screenHeight = getHeight();
        int gapStart = random.nextInt(screenHeight - obstacleGap); // Ponto onde o gap começa

        // Obstáculo superior
        obstacles.add(new Obstaculo(getWidth(), 0, obstacleWidth, gapStart, Color.GREEN, true));
        // Obstáculo inferior
        obstacles.add(new Obstaculo(getWidth(), gapStart + obstacleGap, obstacleWidth, screenHeight - (gapStart + obstacleGap), Color.GREEN, false));
    }

    public void update() {
        velocity += gravity;
        passaroY += velocity;

        if (passaroY < 0) {
            passaroY = 0;
            velocity = 0;
            gameOver();
        } else if (passaroY > getHeight() - passaroHeight) {
            gameOver();
        }

        if (obstacles.isEmpty() || obstacles.get(obstacles.size() - 1).getBounds().left < getWidth() - obstacleFrequency) {
            spawnObstacle();
        }

        Iterator<Obstaculo> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Obstaculo obstacle = iterator.next();
            obstacle.update();
            if (obstacle.isOffScreen()) {
                iterator.remove();
            } else {
                // Verifica colisão com o pássaro
                if (Rect.intersects(obstacle.getBounds(), new Rect((int) passaroX, (int) passaroY,
                        (int) passaroX + passaroWidth, (int) passaroY + passaroHeight))) {
                    gameOver();
                    return;
                }
                if (!obstacle.isScored() && obstacle.getBounds().right < passaroX) {
                    obstacle.setScored(true);
                    if (obstacle.isTop()) {
                        // Incrementa o score apenas se for o obstáculo superior
                        increaseScore();
                        soundPool.play(pontuaSoundId, 1, 1, 0, 0, 2);
                    }
                }
            }
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                // Desenha o background
                canvas.drawBitmap(backgroundBitmap, 0, 0, null);

                // Desenha o pássaro
                canvas.drawBitmap(passaroBitmap, passaroX, passaroY, null);

                // Desenha os obstáculos
                for (Obstaculo obstacle : obstacles) {
                    obstacle.draw(canvas);
                }

                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPaused = false;
        isPlaying = true;
        thread = new Thread(this);
        thread.start();

        // Atualiza a imagem do botão de pause para "pausar"
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnPause.setImageResource(android.R.drawable.ic_media_pause);
            }
        });
    }

    public void pause() {
        isPaused = true;
        try {
            isPlaying = false;
            if (thread != null) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Atualiza a imagem do botão de pause para "play"
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                btnPause.setImageResource(android.R.drawable.ic_media_play);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isPaused) {
            resume();
            return true; // Consumir o evento de toque
        }
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        soundPool.play(flapSoundId, 1, 1, 0, 0, 1);
        velocity = flapVelocity;
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        velocity = flapVelocity;
        return true;
    }

    private void gameOver() {
        isPlaying = false;
        soundPool.play(crashSoundId, 1, 1, 0, 0, 1);

        db.collection("users").document(uid).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Long recordeAtual = documentSnapshot.getLong("recorde");
                            if (recordeAtual == null || numero > recordeAtual) {
                                db.collection("users").document(uid)
                                        .update("recorde", numero)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("Jogabilidade", "Recorde atualizado com sucesso");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("Jogabilidade", "Erro ao atualizar o recorde", e);
                                            }
                                        });
                                novoRecorde = true;
                            }
                        }
                        startGameOverActivity(novoRecorde, numero);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Jogabilidade", "Erro ao obter recorde do usuário", e);
                        startGameOverActivity(false, numero);
                    }
                });
    }

    private void startGameOverActivity(boolean novoRecorde, int novoRecordeNumero) {
        Intent intent = new Intent(getContext(), com.example.trabalhofinal_progmovel.GameOverSelecao.class);
        intent.putExtra("novoRecorde", novoRecorde);
        intent.putExtra("novoRecordeNumero", novoRecordeNumero);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        passaroX = getWidth() / 3 - passaroWidth / 2;
        resume();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    private void atualizarScore() {
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                txtScore.setText(String.valueOf(numero));
            }
        });
    }

    private void increaseScore() {
        numero += 1;
        atualizarScore();
    }
}
