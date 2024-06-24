package com.example.trabalhofinal_progmovel;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Obstaculo {
    private int x, y, width, height;
    private Paint paint;

    public Obstaculo(int x, int y, int width, int height, int color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.paint = new Paint();
        this.paint.setColor(color);
    }

    public void draw(Canvas canvas) {
        Rect rect = new Rect(x, y, x + width, y + height);
        canvas.drawRect(rect, paint);
    }

    public void update() {
        x -= 5; // Velocidade do obstáculo
    }

    public boolean isOffScreen() {
        return x + width < 0;
    }

    public Rect getBounds() {
        return new Rect(x, y, x + width, y + height);
    }
}

