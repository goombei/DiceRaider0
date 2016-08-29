package user.test.ex.diceraider0;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import user.test.ex.db.DBUserInfo;

public class ShopActivity extends AppCompatActivity {
    // MainActivity의 onPause()와 onResume()가 db세팅을 대신한다

    //money에 들어갈 textview
    TextView moneyText;

    //인벤토리로 넘어갈 button
    ImageView inventoryBtn;

    //상점의 각각의 아이템 클릭이벤트를 RelativeLayout에 걸어둠
    RelativeLayout bronzeWhipLayout, silverWhipLayout, goldWhipLayout, bronzeClothLayout, silverClothLayout, goldClothLayout, smallPotionLayout;

    ShopDialog dialog;
    Context context;

    //닫기버튼
    ImageView closeBtn;

    SoundManager sm;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        context = this;
        bundle = getIntent().getExtras();

        moneyText = (TextView)findViewById(R.id.moneyText);


        inventoryBtn = (ImageView)findViewById(R.id.inventoryBtn);
        inventoryBtn.setOnClickListener(click);

        closeBtn = (ImageView)findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(click);

        bronzeWhipLayout = (RelativeLayout)findViewById(R.id.bronzeWhipLayout);
        bronzeWhipLayout.setOnClickListener(click);

        silverWhipLayout = (RelativeLayout)findViewById(R.id.silverWhipLayout);
        silverWhipLayout.setOnClickListener(click);

        goldWhipLayout = (RelativeLayout)findViewById(R.id.goldWhipLayout);
        goldWhipLayout.setOnClickListener(click);

        bronzeClothLayout = (RelativeLayout)findViewById(R.id.bronzeClothLayout);
        bronzeClothLayout.setOnClickListener(click);

        silverClothLayout= (RelativeLayout)findViewById(R.id.silverClothLayout);
        silverClothLayout.setOnClickListener(click);

        goldClothLayout= (RelativeLayout)findViewById(R.id.goldClothLayout);
        goldClothLayout.setOnClickListener(click);

        smallPotionLayout = (RelativeLayout)findViewById(R.id.smallPotionLayout);
        smallPotionLayout.setOnClickListener(click);

//        bigPotionLayout = (RelativeLayout)findViewById(R.id.bigPotionLayout);
//        bigPotionLayout.setOnClickListener(click);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //bgm on
        sm = new SoundManager(this);
        sm.initSoundPool();
        sm.playBGM(sm.BGM_SHOP, true);

        //인벤토리에서 아이템 팔고 나왔을 경우를 대비해서 여기서 처리해야된다.
        moneyText.setText(DBUserInfo.userInfo.getMoney());

    }

    @Override
    protected void onPause() {
        super.onPause();
        //bgm stop
        sm.stopBGM();
    }

    @Override
    public void onBackPressed() {
        //백버튼 막기
    }

    //Dialog를 닫으면 gold를 동기화 하기 위해서 사용
    ShopDialog.DialogCallback callback = new ShopDialog.DialogCallback() {
        @Override
        public void call() {
            moneyText.setText(DBUserInfo.userInfo.getMoney());
        }

        @Override
        public void coinSound() {
            sm.playSound(sm.SOUND_COIN);
        }
    };

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.inventoryBtn:
                    Intent intent = new Intent(ShopActivity.this, InventoryActivity.class);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;

                case R.id.closeBtn:
                    finish();
                    break;

                case R.id.bronzeWhipLayout:
                    dialog = new ShopDialog(context, callback);
                    dialog.buyDialog(R.mipmap.whip2, "whip", 500, "Att 5 up!");
                    dialog.show();
                    break;

                case R.id.silverWhipLayout:
                    dialog = new ShopDialog(context, callback);
                    dialog.buyDialog(R.mipmap.whip3, "whip2", 1000, "Att 10 up!");
                    dialog.show();
                    break;

                case R.id.goldWhipLayout:
                    dialog = new ShopDialog(context, callback);
                    dialog.buyDialog(R.mipmap.whip4, "whip3", 2000, "Att 20 up!");
                    dialog.show();
                    break;

                case R.id.bronzeClothLayout:
                    dialog = new ShopDialog(context, callback);
                    dialog.buyDialog(R.mipmap.armor1, "cloth", 500, "hp 500 up!");
                    dialog.show();
                    break;

                case R.id.silverClothLayout:
                    dialog = new ShopDialog(context, callback);
                    dialog.buyDialog(R.mipmap.armor2, "cloth2", 1000, "hp 1000 up!");
                    dialog.show();
                    break;

                case R.id.goldClothLayout:
                    dialog = new ShopDialog(context, callback);
                    dialog.buyDialog(R.mipmap.armor3, "cloth3", 2000, "hp 2000 up!");
                    dialog.show();
                    break;

                case R.id.smallPotionLayout:
                    dialog = new ShopDialog(context, callback);
                    dialog.buyDialog(R.mipmap.big_portion, "portion", 100, "hp 50% 회복");
                    dialog.show();
                    break;

//                case R.id.bigPotionLayout:
//                    dialog = new ShopDialog(context, callback);
//                    dialog.buyDialog(R.mipmap.big_portion, "portion", 100);
//                    dialog.show();
//                    break;
            }
        }
    };


}
