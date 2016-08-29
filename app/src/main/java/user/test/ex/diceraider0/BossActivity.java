package user.test.ex.diceraider0;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

public class BossActivity extends AppCompatActivity implements BossLounge.BossListener{

    boolean knife = true; // 날붙이(knife) 소지 상태에 대해서 인텐트로 정보를 받아와야함.
    int hp, maxhp;
    BossLounge bossLounge;

    SoundManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 뷰 설정 전에 인텐트를 먼저 해야함
        Intent i = getIntent();
        Bundle bundle = i.getExtras();

        bossLounge = new BossLounge(this, bundle);
        setContentView(bossLounge);
        // handler.post(game_checker);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm = new SoundManager(this);
        sm.playBGM(sm.BGM_FIRSTBOSS, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.stopBGM();
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
    protected void onDestroy() {
        super.onDestroy();
        bossLounge.remove();

        // handler.removeCallbacksAndMessages(null);
        System.gc();
    }
    /*
    Handler handler = new Handler();

    Runnable game_checker = new Runnable() {
        @Override
        public void run() {
            int game_result;
            game_result = bossLounge.getGameover();

            if (game_result == bossLounge.WIN_HERO) {
                String rewardItem = bossLounge.boss.getRewardItem();
                int rewardMoney = bossLounge.boss.getRewardMoney();

                // 우선 다이얼로그를 띄워야
                Intent i = new Intent(BossActivity.this, ainActivity.class);
                i.putExtra("rewardMoney", rewardMoney);
                i.putExtra("rewardItem", rewardItem);
                i.putExtra("StageClear", true);
                BossActivity.this.setResult(RESULT_OK, i);

                finish(); // 액티비티 전환
            } else if (game_result == bossLounge.WIN_BOSS) {
                String rewardItem = "";
                int rewardMoney = 0;

                Intent i = new Intent(BossActivity.this, ainActivity.class);
                i.putExtra("rewardMoney", rewardMoney);
                i.putExtra("rewardItem", rewardItem);
                i.putExtra("StageClear", false);
                BossActivity.this.setResult(RESULT_OK, i);

                finish(); // 액티비티 전환
            } else if (game_result == bossLounge.WORKING){
                handler.postDelayed(this, 1000);
                // 계속 진행
            }
        }
    };
*/
    @Override
    public void endLounge(Bundle rewards) {
        Intent i = new Intent(BossActivity.this, MainActivity.class);
        i.putExtras(rewards);

        setResult(RESULT_OK, i);
        finish(); // 액티비티 전환
    }
}
