package user.test.ex.diceraider0;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class KeyActivity extends AppCompatActivity {

    //센서 작업에 필요한 객체들 준비
    SensorManager manager;
    SensorEventListener sensorOriL;
    Sensor oriSensor;
    int heading , pitch , roll;
    LockKeyView lockKey;

    SoundManager sm;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        lockKey = new LockKeyView( this);
        setContentView(lockKey);

        manager= (SensorManager)getSystemService(SENSOR_SERVICE);
        oriSensor = manager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        /*
        lockKey.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN )
                {
                    lockKey.inKey = 5;
                }
                return false;
            }
        });
        */
        sensorOriL = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent)
            {// 센서 값이 변경 될 때 마다 호출되는 매서드
                heading = (int)sensorEvent.values[0];// 방위값
                pitch = (int)sensorEvent.values[1];  // 경사도
                roll = (int)sensorEvent.values[2];   // 기울기

                lockKey.setDelta(-roll/5 , -pitch/5);
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i)
            { // 센서 민감도 ( 반응속도 )가 변경되면 호출

            }
        };

        sm = new SoundManager(this);
        sm.playBGM(sm.BGM_KEY, true);

        if (Archive.BOSS_STAGE_ONE_PASS) {
            handler.postDelayed(runnable, 3000);
        }
        //센서 안되는 에뮬레이터 환경에서 3초 후 종료
        if (getIntent().getBooleanExtra("boss1_thrupass", false)) {
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 3000);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (event.getAction()) {
            case KeyEvent.KEYCODE_BACK:
                break;
        }

        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 센서 등록 ( 다른 화면에서 현재 화면으로 전환 되었을 때 )

        //registerListener( 센서이벤트 감지자 , 센서 종류 , 반응속도 );
        manager.registerListener(sensorOriL , oriSensor , SensorManager.SENSOR_DELAY_GAME);
        lockKey.keyMove.sendEmptyMessageDelayed(0,10);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 다른 화면으로 전활될 때 등록된 센서를 해제
        manager.unregisterListener(sensorOriL);
        lockKey.keyMove.removeMessages(0);
        Log.i("MY", "pause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("MY", "Distory");
        lockKey.clearMap();
        lockKey = null;
        sm.stopBGM();
        handler.removeCallbacksAndMessages(null);
    }

    protected void showDialogEnd()
    {
        dialog = new Dialog(this);

        //다이얼로그의 타이틀 제거
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        //다이얼로그의 자체 배경을 투명하게...
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //다이얼로그에 화면구성 레이아웃을 등록
        dialog.setContentView(R.layout.finish_dialog);
        TextView result = (TextView)dialog.findViewById(R.id.resulttxt);
        TextView moneyTxt = (TextView)dialog.findViewById(R.id.moneyTxt);
        TextView deltext = (TextView)dialog.findViewById(R.id.deltext);
        //ImageView getItemimg = (ImageView)dialog.findViewById(R.id.getItemView);

        Button finishBtn = (Button)dialog.findViewById(R.id.finishBtn);
        result.setText("보물을 열었습니다.");
        moneyTxt.setText("");
        deltext.setText("");
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rewardMoney = 0;
                String rewardItem = "";

                Intent i = new Intent(KeyActivity.this, MainActivity.class);
                i.putExtra("rewardMoney", rewardMoney);
                i.putExtra("rewardItem", rewardItem);
                i.putExtra("StageClear", true);
                setResult(RESULT_OK, i);
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
    }

    Handler handler = new Handler();

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int rewardMoney = 0;
            String rewardItem = "";

            Intent i = new Intent(KeyActivity.this, OldMainActivity.class);
            i.putExtra("rewardMoney", rewardMoney);
            i.putExtra("rewardItem", rewardItem);
            i.putExtra("StageClear", true);
            setResult(RESULT_OK, i);

            finish();
        }
    };
}
