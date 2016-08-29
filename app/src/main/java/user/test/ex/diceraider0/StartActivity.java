package user.test.ex.diceraider0;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    private RelativeLayout layoutTitle;
    private TextView startTxt;

    private final int MAX_A = 0xff;
    private final int MIN_A = 0x00;
    private final int DELTA_A = 0x0f;

    private int currAlpha = 0x00;
    private int dirAlpha = 1;

    private boolean reset;

    //사운드
    SoundManager sm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Intent i = getIntent();
        Bundle b = i.getExtras();
        // 번들 가져오기 //by LMH
        if (b != null) {
            reset = b.getBoolean("reset", false);
        } else {
            reset = false;
        }

        //사운드 재생
        sm = new SoundManager(this);
        sm.playBGM(sm.BGM_TEST, true);

        layoutTitle = (RelativeLayout) findViewById(R.id.layoutTitle);

        layoutTitle.setOnTouchListener(touch);

        startTxt = (TextView) findViewById(R.id.startTxt);

        WindowManager wm = getWindowManager();
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        startTxt.setTextSize(dm.widthPixels / 20);
        handler.post(changeAlpha);

        /*text_visible = AnimationUtils.loadAnimation(
                StartActivity.this, R.anim.menu_invisible);
        handler.postDelayed(ani_text, 10);*/
    } // onCreate()


    @Override
    protected void onDestroy() {
        sm.stopBGM();
        sm = null;

        handler.removeCallbacksAndMessages(null);
        changeAlpha = null;
        System.gc();

        super.onDestroy();
    }


    View.OnTouchListener touch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            // layoutTitle.setBackgroundResource(R.drawable.b2);
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                // Intent i = new Intent(StartActivity.this , ainActivity.class);
                Intent i = new Intent(StartActivity.this, MainActivity.class);


                Bundle bundle = new Bundle();
                bundle.putBoolean("reset", reset);
                Log.i("MY", "스타트 엑티비티 버튼 " + reset);
                i.putExtras(bundle);


                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(i);
                finish();
                handler.removeCallbacks(changeAlpha);
            }
            return false;
        }
    };


    Handler handler = new Handler();

    Runnable changeAlpha = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 50);

            if (currAlpha >= MAX_A) {
                dirAlpha = -1;
            } else if (currAlpha <= MIN_A) {
                dirAlpha = 1;
            }
            currAlpha += DELTA_A * dirAlpha;

            int alpha = (currAlpha << 24) + 0x00ffffff;

            startTxt.setTextColor(Color.RED & alpha);
        }
    };


    // old code
    /*
    private Animation text_visible;
    private boolean visible;

    Runnable ani_text = new Runnable() {
        @Override
        public void run() {
            if (visible)
            {
                text_visible = AnimationUtils.loadAnimation(
                        StartActivity.this, R.anim.menu_invisible);
                startTxt.startAnimation(text_visible);
                startTxt.setVisibility(View.INVISIBLE);
            }

            else
            {
                text_visible = AnimationUtils.loadAnimation(
                        StartActivity.this, R.anim.menu_visible);
                startTxt.startAnimation(text_visible);
                startTxt.setVisibility(View.VISIBLE);
            }

            visible = !visible;
            handler.postDelayed(this, 500);
        }
    };*/
}
