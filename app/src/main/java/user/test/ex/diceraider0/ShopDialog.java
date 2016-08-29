package user.test.ex.diceraider0;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import user.test.ex.db.DBUserInfo;

/**
 * Created by INC-B-17 on 2016-04-14.
 */
public class ShopDialog extends Dialog {

    Context context;
    Activity activity;

    ImageView itemImage, closeBtn;
    Button btn1, btn2;
    TextView noticeText1;

    //empty가 최초로 있는 번지
    int count = 100;

    //아이템창
    String[] items = new String[20];

    //콜백함수
    DialogCallback callback;

    public ShopDialog(Context context, DialogCallback callback) {
        super(context);
        this.context = context;
        this.callback = callback;
        setCancelable(false);

        activity = (Activity) context;

        //타이틀 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.inventory_dialog);

        //가로모드 꽉 채우기
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        itemImage = (ImageView) findViewById(R.id.itemImage);
        closeBtn = (ImageView)findViewById(R.id.closeBtn);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        noticeText1 = (TextView)findViewById(R.id.noticeText1);
    }

    //파라미터1 아이템 이미지지

    public void buyDialog(int resource, final String itemName, final int itemPrice, final String itemInfo) {
        itemImage.setBackgroundResource(resource);
        btn1.setText("구입");
        btn2.setText("취소");

        //구입 버튼을 클릭하면
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //아이템창에 empty가 있는 최초의 번지수를 찾는다.
                items = DBUserInfo.userInfo.getItems();

                for (int i = 0; i < items.length; i++) {
                    if (items[i].equals("empty")) {
                        count = i;
                        break;
                    }
                }


                //금액 차감하기 및 아이템 담기
                //현재 소지 금액
                int haveGold = Integer.parseInt(DBUserInfo.userInfo.getMoney());

                //소지 금액이 아이템 가격보다 크거나 같으면
                if (itemPrice <= haveGold) {
                    //물건 살 수 있음
                    //만약 count가 100이라면 = 아이템창에 빈공간이 없다면
                    if (count == 100) {
                        Toast.makeText(context, "아이템 창이 부족합니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "구입 완료", Toast.LENGTH_SHORT).show();
                        //금액 차감
                        DBUserInfo.userInfo.setMoney((haveGold - itemPrice) + "");

                        //아이템창에 스트링(아이템명)을 저장한다.
                        items[count] = itemName;
                        callback.coinSound();
                    }
                } else {
                    //물건 못 삼
                    Toast.makeText(context, "골드가 부족합니다.", Toast.LENGTH_SHORT).show();
                }

                dismiss();
                callback.call();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        noticeText1.setText(itemInfo);

    }

    //다이얼로그 종료후에 item새로고침을 위한 콜백메서드
    interface DialogCallback {
        void call();
        void coinSound();
    }
}
