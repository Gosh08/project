package com.example.gravity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class HomePage extends AppCompatActivity {

    public class MainActivity extends AppCompatActivity {
        private View contLabel;
        private TextView tvNotify;
        private TextView tvScore;
        private TextView tvBest;
        private ImageView ivCenter;
        private Astronaut astronaut;
        private int score = 0;
        private int level = 0;
        private int bestScore = 0;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_home_page);

            contLabel = findViewById(R.id.contNotify);
            tvNotify = findViewById(R.id.notify);
            tvScore = findViewById(R.id.score);
            tvBest = findViewById(R.id.best);
            ivCenter = findViewById(R.id.imgCenter);
            ivCenter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    play();
                }
            });

            astronaut = findViewById(R.id.racingView);
            initialize();
        }

        @Override
        protected void onResume() {
            super.onResume();
            if (astronaut != null && astronaut.getPlayState() == Astronaut.PlayState.Pause) {
                astronaut.resume();
            }
        }

        @Override
        protected void onPause() {
            super.onPause();
            if (astronaut != null && astronaut.getPlayState() == Astronaut.PlayState.Playing) {
                astronaut.pause();
            }
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            if (astronaut != null) {
                astronaut.reset();
            }
        }

        private Handler racingHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Astronaut.MSG_SCORE:
                        score += level;
                        tvScore.setText(Integer.toString(score));
                        break;
                    case Astronaut.MSG_COLLISION:
                        boolean achieveBest = false;
                        if (bestScore < score) {
                            tvBest.setText(Integer.toString(score));
                            bestScore = score;
                            saveBestScore(bestScore);
                            achieveBest = true;
                        }
                        collision(achieveBest);
                        break;
                    case Astronaut.MSG_COMPLETE:
                        prepare();
                        break;
                    case Astronaut.MSG_PAUSE:
                        setTvNotify(R.string.pause);
                        prepare();
                        break;
                    default:
                        break;
                }
            }
        };

        private void initialize() {
            reset();
            setTvNotify(R.string.ready);
            prepare();
        }

        private int loadBestScore() {
            SharedPreferences preferences = getSharedPreferences("MyFirstGame", MODE_PRIVATE);
            if (preferences.contains("BestScore")) {
                return preferences.getInt("BestScore", 0);
            } else {
                return 0;
            }
        }

        private void saveBestScore(int bestScore) {
            SharedPreferences preferences = getSharedPreferences("MyFirstGame", MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt("BestScore", bestScore);
            editor.commit();
        }

        private void reset() {
            score = 0;
            level = 1;
            bestScore = loadBestScore();
            astronaut.setSpeed(INIT_SPEED);
            astronaut.setPlayState(Astronaut.PlayState.Ready);
            tvScore.setText(Integer.toString(score));
            tvBest.setText(Integer.toString(bestScore));
        }

        private void restart() {
            reset();
            astronaut.setPlayState(Astronaut.PlayState.Restart);
            setTvNotify(R.string.restart);
            prepare();
        }

        private void prepare() {
            Astronaut.PlayState state = astronaut.getPlayState();
            int resource = R.drawable.r_play;
            if (state == Astronaut.PlayState.Pause) {
                resource = R.drawable.r_pause;
            } else if (state == Astronaut.PlayState.Restart) {
                resource = R.drawable.r_retry;
            }
            ivCenter.setImageResource(resource);
            showLabelContainer();
        }

        private void play() {
            if (astronaut.getPlayState() == Astronaut.PlayState.Collision
                    || astronaut.getPlayState() == Astronaut.PlayState.Restart) {
                initialize();
                astronaut.reset();
                return;
            }
        }
        // Click on playing
         {
         }  {
            ivCenter.setImageResource(R.drawable.r_pause);

            // Click on pause
            if (astronaut.getPlayState() == Astronaut.PlayState.Pause) {
                ivCenter.setImageResource(R.drawable.r_play);
                astronaut.resume();
                hideLabelContainer();
            } else if (astronaut.getPlayState() == Astronaut.PlayState.LevelUp) {
                astronaut.resume();
                hideLabelContainer();
            } else {
                hideLabelContainer();
                astronaut.play(racingHandler);
            }
        }
        private void pause() {
            Log.d("Astronaut", "pause(): ");
            ivCenter.setImageResource(R.drawable.r_pause);
            astronaut.pause();
        }
        private boolean canResume() {
            Astronaut.PlayState state = astronaut.getPlayState();
            return state == Astronaut.PlayState.Pause || state == Astronaut.PlayState.Ready || state == Astronaut.PlayState.Restart;
        }

        private void resume() {
            hideLabelContainer();
            play();
        }

        private void setTvNotify(int stringId) {
            tvNotify.setText(stringId);
        }
        private void collision(boolean achieveBest) {
            if (achieveBest) {
                setTvNotify(R.string.best_ranking);
            } else {
                setTvNotify(R.string.try_again);
            }
            contLabel.setVisibility(View.VISIBLE);
            contLabel.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
            ivCenter.setImageResource(R.drawable.r_retry);
        }
        private void showLabelContainer() {
            Log.d("Astronaut", "showLabelContainer: ");
            contLabel.setVisibility(View.VISIBLE);
            if (contLabel.getAnimation() != null) {
                contLabel.getAnimation().cancel();
            }
            contLabel.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left));
        }
        private void hideLabelContainer() {
            Log.d("Astronaut", "hideLabelContainer: ");
            Animation anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    contLabel.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            contLabel.startAnimation(anim);
        }

        private void moveCarTo(int direction) {
            astronaut.moveCarTo(direction);
        }

        private void moveCar() {
            astronaut.move();
        }

        private static final int INIT_SPEED = 8;


    }
}