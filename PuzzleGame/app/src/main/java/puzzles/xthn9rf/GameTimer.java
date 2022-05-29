package puzzles.xthn9rf;

import android.os.CountDownTimer;
import android.widget.TextView;

public class GameTimer {
    TextView timerText;
    CountDownTimer timer;
    public static final long MIDDLE_DIFFICULT = 25*1000;
    public static final long HARD_DIFFICULT = 20*1000;
    public static final long INSANE_DIFFICULT = 15*1000;
    public static long duration;
    public static long prevChoise;
    public static boolean finished = false;



    public GameTimer(TextView timerText) {
        this.timerText = timerText;
    }

    public void startTimer() {
        timer = new CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                duration = millisUntilFinished;
                timerText.setText(getTimerText());
            }

            @Override
            public void onFinish() {
                finished = true;
            }
        }.start();
    }

    public void resetTimer(){
        duration = prevChoise;
        finished = false;
        timerText.setText(getTimerText());
    }

    private String getTimerText() {
        int rounded = (int) Math.round(duration);

        int seconds = (rounded / 1000) % 60;
        int minutes = (rounded / 1000) / 60;

        return formatTime(seconds, minutes);
    }

    private String formatTime(int seconds, int minutes)
    {
        return String.format("%02d",minutes) + ":" + String.format("%02d",seconds);
    }


}