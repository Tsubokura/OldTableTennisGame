package com.example.OldTableTennisGame;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import java.util.Random;

public class MySurfaceview extends SurfaceView implements SurfaceHolder.Callback,  Runnable {
    private SurfaceHolder sHolder;
    private Thread thread;

    public float position_x = 0;
    public float position_y = 0;//物体の位置

    public float positionbar_x = 0;
    public float positionbar_y = 0;

    public float position_opbar_x = 300;
    public float position_opbar_y = 50;

    int width = 0;
    int height = 0;

    int ifcolision = -1;

    int widthbar = 500;

    long loopCount = -1;//variables for counting how many loop is triggered.

    float vx = 0;
    //speed of object

    int dx = 3;
    int dy = 10;

    int opbardx = 4;

    float c1 = 0;
    float c2 = 0;

    Random random = new Random();

    static final long FPS = 80;
    static final long FRAME_TIME = 1000 / FPS;
    public MySurfaceview(Context context) {
        super(context);
        initialize();
    }

    public MySurfaceview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public MySurfaceview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize(){
        sHolder = getHolder();
        sHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }

    public int IfCollition(){
        if(position_x < 0 || position_x > width){
            ifcolision = 1;
        } else if(position_y < 0 || position_y > height){
            ifcolision = 2;
        } else if((position_x >= positionbar_x && position_x <= positionbar_x + widthbar)&&
                (position_y >= positionbar_y && position_y < positionbar_y + 50)){
            ifcolision = 3;
        } else if((position_x >= position_opbar_x && position_x <= position_opbar_x + widthbar)&&
                (position_y > position_opbar_y && position_y <= position_opbar_y + 50 )){
            ifcolision = 4;
        }
        return ifcolision;
    }

    public void CollisionAfter(){

        if(ifcolision == 1){
            dx = -dx;
        } else if(ifcolision == 2){
            dy = -dy;
        } else if(ifcolision == 3){
            dx *= c1;
            dy = -dy;
            dy *= c2;
        } else if(ifcolision == 4){
            dy = -dy;
        }
    }

    @Override
    public void run() {
        long waitTime = 0;

        long startTime = System.currentTimeMillis();

        while(thread != null){
            try{
                waitTime = (loopCount * FRAME_TIME) - (System.currentTimeMillis() - startTime);

                doDraw(getHolder());

                loopCount++;

                if(waitTime > 0){
                    Thread.sleep(waitTime);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void doDraw(SurfaceHolder holder){
        Canvas canvas = holder.lockCanvas();

        Bitmap lose = BitmapFactory.decodeResource(getResources(), R.drawable.lose);

        Paint paint = new Paint();
        if(loopCount == -1) {
            width = canvas.getWidth();
            height = canvas.getHeight();

            position_x = width / 2;
            position_y = height / 2;

            positionbar_x = width - 500;
            positionbar_y = height - 200;
        }

        c1 = (float) (random.nextFloat() + 0.6);
        c2 = (float) (random.nextFloat() + 0.5);

        System.out.println(c1);

        ifcolision = IfCollition();

        CollisionAfter();

        ifcolision = -1;

        if(position_x < position_opbar_x){
            position_opbar_x -= opbardx;
        } else if (position_x > position_opbar_x + widthbar){
            position_opbar_x += opbardx;
        }

        position_x += dx;

        position_y += dy;

        canvas.drawColor(Color.WHITE);

        vx = MainActivity.ax * 3;

        positionbar_x = vx + positionbar_x;

        positionbar_x = Math.min(positionbar_x, 900);

        if(positionbar_x <= 0){
            positionbar_x = 0;
        }

        if(position_y > positionbar_y + 60){
            canvas.drawBitmap(lose,0 , 500, paint);
            dy = 0;
            dx = 0;
        }

        canvas.drawCircle(position_x, position_y, 50, paint);

        canvas.drawRect(positionbar_x, positionbar_y, positionbar_x + widthbar, positionbar_y + 50, paint);

        canvas.drawRect(position_opbar_x, position_opbar_y, position_opbar_x + widthbar, position_opbar_y + 50, paint);

        holder.unlockCanvasAndPost(canvas);
    }
}
