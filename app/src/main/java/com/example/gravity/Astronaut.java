package com.example.gravity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.Random;

public class Astronaut extends View {
    public static final String TAG = "Astronaut";
    public static final int SPACING = 2;
    public static final int MAX_COL_COUNT = 2;
    public static final int VERTICAL_COUNT = 20;
    public static final int HORIZONTAL_COUNT = MAX_COL_COUNT * 3 + 2 + 2;
    public static final int MSG_SCORE = 1000;
    public static final int MSG_COLLISION = 2000;
    public static final int MSG_COMPLETE = 3000;
    public static final int MSG_PAUSE = 4000;
    public static final int POS_LEFT = 0;
    public static final int POS_RIGHT = 1;
    private PlayState state;
    private int obstaclesResource;
    private PlayState playState;

    public Astronaut(Context context) {
        super(context);
    }


    public enum PlayState {
        Ready, Playing, Pause, LevelUp, Collision, Restart
    }

    private Handler handler1;
    private PlayState prevState;
    private int boardWidth;
    private int viewHeight;
    private int blockSize;
    private int speed;
    private int currentPos;
    private Paint paint;
    private Random random;
    private ArrayList<RectF> walls;
    private ArrayList<RectF> maps;
    private ArrayList<Asteroid> obstacles;
    private Asteroid myself;
    private Bitmap mapBitmap;


    public class Astronaut2 extends View {


        public Astronaut2(Context context) {
            super(context);
        }

        public Astronaut2(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public Astronaut2(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }
    }
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int height = bottom - top;
        if (bottom - top > 0) {
            int blockSize = (height - SPACING * (VERTICAL_COUNT + 1)) / VERTICAL_COUNT;
            int w = blockSize * HORIZONTAL_COUNT + SPACING * (HORIZONTAL_COUNT - 1);
            int h = blockSize * VERTICAL_COUNT + SPACING * (VERTICAL_COUNT + 1);
            int viewWidth = right - left;
            initialize(w, h, blockSize);
        }
    }
    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw(), state = " + state);
        super.onDraw(canvas);
        drawMaps(canvas);
        if (state == PlayState.Playing || state == PlayState.Collision) {
            drawObstacles(canvas);
        }
        if (myself != null && (state == PlayState.Playing || state == PlayState.Collision)) {
            myself.draw(canvas, state == PlayState.Collision);
        }
        if (handler1 != null && state == PlayState.Playing) {
            handler1.sendEmptyMessage(MSG_SCORE);
        }
        invalidate();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            move();
        }
        return super.onTouchEvent(event);
    }


    public PlayState getPlayState() {
        return state;
    }

    public void setPlayState(PlayState state) {
        prevState = state;
        this.state = state;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void play(Handler handler) {
        this.handler1 = handler;
        prevState = state;
        state = PlayState.Playing;
        moveLeft();
    }

    public void resume() {
        prevState = state;
        state = PlayState.Playing;
        if (prevState != PlayState.Pause) {
            moveLeft();
        }
    }

    public void pause() {
        prevState = state;
        state = PlayState.Pause;
        if (handler1 != null) {
            handler1.sendEmptyMessage(MSG_PAUSE);
        }
    }

    public void reset() {
        prevState = state;
        state = PlayState.Ready;
        createObstacles();
    }

    private void initialize(int width, int height, int blockSize) {
        if (myself != null) {
            return;
        }
        prevState = state;
        state = PlayState.Ready;
        boardWidth = width;
        viewHeight = height;
        this.blockSize = blockSize;
        setProperties();

        //createWall();
        createMap();
        createObstacles();
        Drawable drawable = getResources().getDrawable(R.drawable.astronaut, null);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        myself = new Asteroid(bitmap);
        myself.setX(200);
        myself.setY(viewHeight - myself.getHeight());
        Drawable drawableMap = getResources().getDrawable(R.drawable.c2a3406022ae527d1b1c5615885afbff, null);
        Bitmap tmpBitmap = ((BitmapDrawable) drawableMap).getBitmap();
        mapBitmap = Bitmap.createScaledBitmap(tmpBitmap, 1060, tmpBitmap.getHeight(), false);
    }
    private void createMap() {
        maps = new ArrayList<>();
        for (int i = 0; i <= 1; i++) {
            RectF left = new RectF(0.0f, 0.0f, 0.0f, 0.0f);
            left.left = 0f;
            left.right = (float) boardWidth;
            left.top = (-viewHeight + i * viewHeight);
            left.bottom = left.top + viewHeight;
            maps.add(left);
        }
    }

    private void setProperties() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Random random = new Random();
    }

    private void drawMaps(Canvas canvas) {
        if (state == PlayState.Playing) {
            if (maps != null) {
                for (RectF r : maps) {
                    r.top += 50;
                    if (r.top > viewHeight) {
                        r.top = -viewHeight;
                        r.bottom = r.top + viewHeight;
                    }
                    canvas.drawBitmap(mapBitmap, 0f, r.top, null);
                }
            }
        } else {
            canvas.drawBitmap(mapBitmap, 0f, 0f, null);
        }
    }
    private void createObstacles() {
        if (obstacles == null) {
            obstacles = new ArrayList<>();
        } else {
            obstacles.clear();
        }
        int carHeight = blockSize * 5 + SPACING * 3;
        int startOffset = -carHeight;
        int count = speed * MAX_COL_COUNT;
        if (speed >= 20) {
            count = count * 2;
        } else if (speed >= 30) {
            count = count * 3;
        } else if (speed >= 40) {
            count = count * 4;
        }
        for (int i = 0; i < count; i++) {
            int r = random.nextInt(MAX_COL_COUNT);
            int resourceId = obstaclesResource;
            Drawable drawable = getResources().getDrawable(resourceId);
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Asteroid obstacle = new Asteroid(bitmap, getLeftPositionX(r), startOffset);
            obstacles.add(obstacle);
            startOffset = startOffset - (carHeight + SPACING) * 2;
        }
        obstacles.get(obstacles.size() - 1).setLast(true);
    }
    private int getObstaclesResource() {
        int obstaclesSize = 3;
        int rand = random.nextInt(obstaclesSize);
        switch (rand) {
            case 0:
                rand = R.drawable.met_1;
                break;
            case 1:
                rand = R.drawable.met_2;
                break;
            case 2:
                rand = R.drawable.met_3;
                break;
        }
        return rand;
    }
    private void drawObstacles(Canvas canvas) {
        if (obstacles != null) {
            boolean isComplete = false;
            int size = obstacles.size();
            for (int i = 0; i < size; i++) {
                Asteroid obstacle = obstacles.get(i);
                if (state == PlayState.Playing) {
                    obstacle.moveDown(speed);
                }
                obstacle.draw(canvas);
                if (state == PlayState.Playing) {
                    if (isCollision(obstacle)) {
                        prevState = state;
                        state = PlayState.Collision;
                        if (handler1 != null) {
                            handler1.sendEmptyMessage(MSG_COLLISION);
                        }
                    }
                    if (obstacle.isLast() && obstacle.getY() >= viewHeight + obstacle.getHeight() + blockSize) {
                        isComplete = true;
                    }
                }
            }
            if (isComplete) {
                prevState = state;
                state = PlayState.LevelUp;
                createObstacles();
                if (handler1 != null) {
                    handler1.sendEmptyMessage(MSG_COMPLETE);
                }
            }
        }
    }
    private boolean isCollision(Asteroid obstacle) {
        if (myself == null) {
            return false;
        } else {
            return myself.getBoundary().intersect(obstacle.getBoundary());
        }
    }

    private int getLeftPositionX(int r) {
        int posX = 180;
        if (r == 1) {
            posX = 680;
        }
        return posX;
    }

    public void moveLeft() {
        if (state != PlayState.Playing) {
            return;
        }
        if (myself != null) {
            myself.setPosition(200);
        }
        currentPos = POS_LEFT;
    }

    public void moveRight() {
        if (state != PlayState.Playing) {
            return;
        }
        if (myself != null) {
            myself.setPosition(700);
        }
        currentPos = POS_RIGHT;
    }

    public void moveCarTo(int direction) {
        if (direction == POS_LEFT) {
            moveLeft();
        } else {
            moveRight();
        }
    }
    public void move() {
        PlayState state = playState;
        if (state == PlayState.Playing) {
            if (currentPos == POS_LEFT) {
                moveRight();
            } else {
                moveLeft();
            }
        }
    }






}


