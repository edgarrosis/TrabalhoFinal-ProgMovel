package com.example.trabalhofinal_progmovel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.TypedValue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Jogabilidade extends SurfaceView implements Runnable, SurfaceHolder.Callback, GestureDetector.OnGestureListener {
    private Thread thread;
    private boolean isPlaying;
    private SurfaceHolder surfaceHolder;
    private GestureDetector gestureDetector;

    private float passaroY;
    private float velocity = 0;
    private final float gravity = 1f;
    private final float flapVelocity = -20;
    private Bitmap passaroBitmap;
    private float passaroX;
    private int passaroWidth, passaroHeight;

    private List<Obstaculo> obstacles;
    private Random random;
    private int obstacleGap = 480; // Gap entre os obstáculos de cima e de baixo (ajustado para 400)
    private int obstacleFrequency = 600; // Distância entre os obstáculos (ajustado para 500)
    private int obstacleWidth = 180; // Largura dos obstáculos

    private Bitmap backgroundBitmap;

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

        // Carrega as imagens
        passaroBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vermelho);
        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background);

        passaroWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        passaroHeight = passaroWidth;
        passaroBitmap = Bitmap.createScaledBitmap(passaroBitmap, passaroWidth, passaroHeight, false);

        obstacles = new ArrayList<>();
        random = new Random();
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
        obstacles.add(new Obstaculo(getWidth(), 0, obstacleWidth, gapStart, Color.GREEN));
        // Obstáculo inferior
        obstacles.add(new Obstaculo(getWidth(), gapStart + obstacleGap, obstacleWidth, screenHeight - (gapStart + obstacleGap), Color.GREEN));
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
            } else if (Rect.intersects(obstacle.getBounds(), new Rect((int) passaroX, (int) passaroY, (int) passaroX + passaroWidth, (int) passaroY + passaroHeight))) {
                gameOver();
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
            Thread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            if (thread != null) {
                thread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent e) {
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
        Intent intent = new Intent(getContext(), com.example.trabalhofinal_progmovel.MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        getContext().startActivity(intent);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        passaroX = getWidth() / 2 - passaroWidth / 2;
        resume();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }
}
