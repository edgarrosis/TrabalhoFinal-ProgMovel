package com.example.trabalhofinal_progmovel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.util.TypedValue;

public class Jogabilidade extends SurfaceView implements Runnable, GestureDetector.OnGestureListener {
    private Thread thread;
    private boolean isPlaying;
    private SurfaceHolder surfaceHolder;
    private GestureDetector gestureDetector;

    private float passaroY;
    private float velocity = 0;
    private final float gravity = 1f;
    private final float flapVelocity = -25; // Ajuste conforme necessário
    private Bitmap passaroBitmap;
    private float passaroX;
    private int passaroWidth, passaroHeight;

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
        gestureDetector = new GestureDetector(context, this);

        passaroBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.vermelho); // Carrega a imagem do pássaro

        // Define o tamanho do pássaro em dp e converte para pixels
        passaroWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
        passaroHeight = passaroWidth; // Mantém o pássaro como um quadrado

        passaroBitmap = Bitmap.createScaledBitmap(passaroBitmap, passaroWidth, passaroHeight, false); // Redimensiona o pássaro

        passaroX = getWidth()/2 + passaroWidth / 2; // Centraliza horizontalmente
    }

    @Override
    public void run() {
        while (isPlaying) {
            update();
            draw();
            sleep();
        }
    }

    public void update() {
        velocity += gravity;
        passaroY += velocity;

        // Verifica colisão com a parte inferior ou superior da tela.
        if (passaroY < 0) {
            passaroY = 0;
            velocity = 0;
            gameOver();
        } else if (passaroY > getHeight() - passaroHeight) {
            gameOver();
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (canvas != null) {
                canvas.drawColor(Color.WHITE); // Limpa a tela
                canvas.drawBitmap(passaroBitmap, passaroX, passaroY, null); // Desenha o pássaro
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(17); // Cálculo para termos no jogo aproximadamente 60 FPS
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
        return true; // Indica que o gesto foi capturado
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
}
