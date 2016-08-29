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
 * Created by INC-B-17 on 2016-04-08.
 */
public class InventoryDialog extends Dialog {

    //이걸 사용하는 이유는 채찍을 더 좋은 채찍으로 바꿀 때
    //기존의 선택된 영역을 모르기 때문에 일단 저장하고 InventoryActivity에서 before 콜백함수로 처리한다.
    static int beforeSelected = 0;

    ImageView itemImage, closeBtn;
    Button btn1, btn2;
    TextView noticeText1, noticeText2;
    String[] userSetDbItem;

    String[] userSetItems = new String[4];
    String[] userSelectedItems = new String[4];
    String[] userItems = new String[20];

    Activity activity;

    DialogCallback callback;

    //아이템 능력치 비교
    //현재 능력치
    int curInfo = 0;
    //아이템 능력치
    int setInfo = 0;
    String what = "";

    public InventoryDialog(Context context, DialogCallback callback) {
        super(context);
        this.callback = callback;

        activity = (Activity) context;

        //타이틀 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.inventory_dialog);

        //가로모드 꽉 채우기
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        setCancelable(false);

        userSetDbItem = DBUserInfo.userInfo.getSetItems();

        itemImage = (ImageView) findViewById(R.id.itemImage);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        closeBtn = (ImageView) findViewById(R.id.closeBtn);

        noticeText1 = (TextView) findViewById(R.id.noticeText1);
        noticeText2 = (TextView) findViewById(R.id.noticeText2);

    }


    //파라미터 1 context
    //파라미터 2 itemName = DB에 실제 저장된 string
    //파라미터 3 itemType = whip-1 cloth-2...
    //파라미터 4 image = dialog에 띄울 itemImage
    //파라미터 5 setView = 세팅될 아이템 이미지 영역 whip-1 cloth-2
    //파라미터 6 selectedItem = 선택한 아이템의 background 영역
    //파라미터 7 selectedItemImage = 선택한 아이템의 image 영역
    //파라미터 8 selectedItemNum = 선택한 아이템의 1-20중의 Num
    //파라미터 9 setItemInfo = 세팅된 아이템의 실제 장비창의 num

    public void itemDialog(final Context context, final String itemName, final int itemType, final int image, final ImageView setView,
                           final ImageView selectedItem, final ImageView selectedItemImage, final int selectedItemNum, final String setItemInfo) {
        itemImage.setBackgroundResource(image);

        //기본 세팅
        btn1.setText("착용하기");
        btn2.setText("판매하기");
        switch (itemName) {
            case "whip":
                what = "ATT ";
                curInfo = Integer.parseInt(DBUserInfo.userInfo.getAtt()); //현재 공격력
                setInfo = 5;
                setDialogATTText();
                break;

            case "whip2":
                what = "ATT ";
                curInfo = Integer.parseInt(DBUserInfo.userInfo.getAtt()); //현재 공격력
                setInfo = 10;
                setDialogATTText();
                break;

            case "whip3":
                what = "ATT ";
                curInfo = Integer.parseInt(DBUserInfo.userInfo.getAtt()); //현재 공격력
                setInfo = 20;
                setDialogATTText();
                break;

            case "bundle1":
                what = "ATT ";
                curInfo = Integer.parseInt(DBUserInfo.userInfo.getAtt()); //현재 공격력
                setInfo = 1;
                setDialogATTText();
                break;


            case "cloth":
                what = "HP ";
                curInfo = (DBUserInfo.userInfo.getMaxHp());
                setInfo = 500;
                setDialogHPText();
                break;

            case "cloth2":
                what = "HP ";
                curInfo = (DBUserInfo.userInfo.getMaxHp());
                setInfo = 1000;
                setDialogHPText();
                break;

            case "cloth3":
                what = "HP ";
                curInfo = (DBUserInfo.userInfo.getMaxHp());
                setInfo = 2000;
                setDialogHPText();
                break;

            case "bundle2":
                what = "HP ";
                curInfo = (DBUserInfo.userInfo.getMaxHp());
                setInfo = 100;
                setDialogHPText();
                break;

            case "portion":
                break;
        }

        //착용하기 버튼 이벤트
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DB에 세팅된 아이템정보와 내가 착용하려는 아이템이 같을 때
                int set = 0;
                switch (itemType) {
                    case 0:
                    case 1:
                    case 2:
                    case 100:
                        set = 0;
                        break;
                    case 3:
                    case 4:
                    case 5:
                    case 200:
                        set = 1;
                        break;

                    case 6:
                        set = 2;
                        break;
                }
                if (userSetDbItem[set].equals(itemName)) {
                    Toast.makeText(getContext(), "이미 착용중 입니다.", Toast.LENGTH_SHORT).show();
                }
                // 다를 때
                else {
                    //뷰 세팅
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //세팅될 아이템 이미지 영역에 image 세팅
                            setView.setBackgroundResource(image);
                            //선택한 아이템의 background 영역을 RED로 세팅
//                            selectedItem.setBackgroundColor(Color.RED);

                            //체크 표시로 변경
                            selectedItem.setVisibility(View.VISIBLE);

                            //아이템 장착중의 같은 타입의 다른 클래스 아이템 착용할 경우 버그 처리(빨간색 남아있는거)
                            //기존꺼는 돌려놔야돼
                            beforeSelected = selectedItemNum;


                        }
                    });

                    //DBUserInfo 세팅
                    //DB에 저장하진 않고 있다.
                    userSetItems = DBUserInfo.userInfo.getSetItems();
                    switch (itemType) {
                        //채찍은 선택아이템 1번에 저장
                        case 0:
                        case 1:
                        case 2:
                        case 100:
                            userSetItems[0] = itemName;
//                    DBUserInfo.userInfo.setItems(userSetItems);
                            //아이템 1-20까지중 선택된 영역을 저장하고 있다.
                            userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                            //userSelectedItems[0]-whip, userSelectedItems[1]-cloth
                            if (!userSelectedItems[0].equals("empty")) {
                                beforeSelected = Integer.parseInt(userSelectedItems[0]);
                                callback.before();
                            }
                            userSelectedItems[0] = String.valueOf(selectedItemNum);
//                            DBUserInfo.userInfo.setSelectedItems(userSelectedItems);
                            break;

                        //갑옷은 선택아이템 2번에 저장
                        case 3:
                        case 4:
                        case 5:
                        case 200:
                            userSetItems[1] = itemName;
                            //아이템 1-20까지중 선택된 영역을 저장하고 있다.
                            userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                            //userSelectedItems[0]-whip, userSelectedItems[1]-cloth
                            if (!userSelectedItems[1].equals("empty")) {
                                beforeSelected = Integer.parseInt(userSelectedItems[1]);
                                callback.before();
                            }
                            userSelectedItems[1] = String.valueOf(selectedItemNum);
//                            DBUserInfo.userInfo.setSelectedItems(userSelectedItems);
                            break;
                        case 6:
                            userSetItems[2] = itemName;
                            //아이템 1-20까지중 선택된 영역을 저장하고 있다.
                            userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                            //userSelectedItems[0]-whip, userSelectedItems[1]-cloth, userSelectedItems[2]-portion
                            if (!userSelectedItems[2].equals("empty")) {
                                beforeSelected = Integer.parseInt(userSelectedItems[2]);
                                callback.before();
                            }
                            userSelectedItems[2] = String.valueOf(selectedItemNum);
//                            DBUserInfo.userInfo.setSelectedItems(userSelectedItems);
                            break;

                    }


                    //능력치 향상하는 부분
                    //이 부분을 itemsetting에서 하는방향으로 수정 04.15 sonasd
//                    switch (itemType) {
//                        //0~2 까지 무기
//                        case 0:
//                            DBUserInfo.userInfo.setAtt(5+"");
//                            break;
//                        case 1:
//                            DBUserInfo.userInfo.setAtt(10+"");
//                            break;
//                        case 2:
//                            DBUserInfo.userInfo.setAtt(20+"");
//                            break;
//
//                        //3~5까지 갑옷
//                        case 3:
//                            DBUserInfo.userInfo.setHp(1500+"");
//                            break;
//                        case 4:
//                            DBUserInfo.userInfo.setHp(2000+"");
//                            break;
//                        case 5:
//                            DBUserInfo.userInfo.setHp(3000+"");
//                            break;
//
//                        //포션
//                        case 6:
//                            break;
//                    }

                    dismiss();
                    callback.setItemSound();
                    callback.call();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //금액 추가 처리
                //DB에서 긁어온 money 스트링
                String userMoney = DBUserInfo.userInfo.getMoney();
                //형변환
                int userM = Integer.parseInt(userMoney);
                switch (itemType) {
                    //whip 0~2
                    case 0:
                        DBUserInfo.userInfo.setMoney((userM + 250) + "");
                        DBUserInfo.userInfo.setAtt(0 + "");
                        Toast.makeText(context, "250원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        DBUserInfo.userInfo.setMoney((userM + 500) + "");
                        DBUserInfo.userInfo.setAtt(0 + "");
                        Toast.makeText(context, "500원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        DBUserInfo.userInfo.setMoney((userM + 1000) + "");
                        DBUserInfo.userInfo.setAtt(0 + "");
                        Toast.makeText(context, "1000원 획득", Toast.LENGTH_SHORT).show();
                        break;


                    //cloth 3~5
                    case 3:
                        DBUserInfo.userInfo.setMoney((userM + 250) + "");
//                        DBUserInfo.userInfo.setHp(1000 + "");
                        Toast.makeText(context, "250원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        DBUserInfo.userInfo.setMoney((userM + 500) + "");
//                        DBUserInfo.userInfo.setHp(1000 + "");
                        Toast.makeText(context, "500원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        DBUserInfo.userInfo.setMoney((userM + 1000) + "");
//                        DBUserInfo.userInfo.setHp(1000 + "");
                        Toast.makeText(context, "1000원 획득", Toast.LENGTH_SHORT).show();
                        break;

                    case 6:
                        DBUserInfo.userInfo.setMoney((userM + 50) + "");
                        Toast.makeText(context, "50원 획득", Toast.LENGTH_SHORT).show();
                        break;
                }
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //선택된 1-20 아이템 이미지 자체를 empty로 변경
                        selectedItemImage.setBackgroundResource(R.mipmap.box2);

                        //만약 선택한 아이템창의 번호와 세팅된 아이템과 같다면
                        if (!setItemInfo.equals("empty")) {
                            int setItemInfoNum = Integer.parseInt(setItemInfo);
                            if (setItemInfoNum == selectedItemNum) {
                                setView.setBackgroundResource(R.mipmap.box2);

                                //기본 타일로 고쳐놓는 부분 주석처리
//                                selectedItem.setBackgroundResource(R.mipmap.inven_n);
                                selectedItem.setVisibility(View.GONE);

                                //선택한 놈하고 세팅된 놈하고 같고 방어구 일 경우에 maxhp 세팅
                                switch (itemType) {
                                    case 3:
                                    case 4:
                                    case 5:
                                    case 200:
                                        DBUserInfo.userInfo.setMaxHp(1000);

                                }
                            }
                        }


                    }
                });

                //실제 아이템1-20창에서 해제
                userItems = DBUserInfo.userInfo.getItems();
                userItems[selectedItemNum] = "empty";

                //만약 선택한 아이템창의 번호와 세팅된 아이템과 같다면
                if (!setItemInfo.equals("empty")) {
                    int setItemInfoNum = Integer.parseInt(setItemInfo);
                    if (setItemInfoNum == selectedItemNum) {
                        switch (itemType) {
                            //무기면 userSetItems의 0번을 비우고
                            case 0:
                            case 1:
                            case 2:
                            case 100:
                                userSetItems = DBUserInfo.userInfo.getSetItems();
                                userSetItems[0] = "empty";

                                userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                                userSelectedItems[0] = "empty";


                                break;

                            //갑옷이면 userSetItems의 1번을 비워버려라
                            case 3:
                            case 4:
                            case 5:
                            case 200:
                                userSetItems = DBUserInfo.userInfo.getSetItems();
                                userSetItems[1] = "empty";

                                userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                                userSelectedItems[1] = "empty";
                                break;

                            case 6:
                                userSetItems = DBUserInfo.userInfo.getSetItems();
                                userSetItems[2] = "empty";

                                userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                                userSelectedItems[2] = "empty";

                        }

                    }
                }

                //만약 선택한 아이템창의 번호와 세팅된 아이템이 다르다면
                //선택한 아이템의 1~20까지의 숫자
//                            selectedItemNum

                //세팅된 아이템의 저장
                String[] userSelectedItemNum = new String[4];
                userSelectedItemNum = DBUserInfo.userInfo.getSelectedItems();

                for (int i = 0; i < userSelectedItemNum.length; i++) {
                    if (!userSelectedItemNum[i].equals("empty")) {
                        int check = Integer.parseInt(userSelectedItemNum[i]);

                        if (selectedItemNum <= check) {
                            beforeSelected = check;
                            userSelectedItemNum[i] = (check - 1) + "";
                            callback.before();
                        }
                    }
                }

                //위에서 분기처리 하니까 주석처리

                //                        userSetItems = DBUserInfo.userInfo.getSetItems();
//                        userSetItems[itemType] = "empty";
//
//                        userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
//                        userSelectedItems[itemType] = "empty";

                dismiss();
                callback.getCoinSound();
                callback.call();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    //파라미터 1 context
    //파라미터 2 image = dialog에 띄울 itemImage
    //파라미터 3 itemType = whip-1 cloth-2...
    //파라미터 4 setView = 세팅될 아이템 이미지 영역 whip-1 cloth-2
    //파라미터 5 selectedItem = 선택한 아이템의 background 영역
    //파라미터 6 selectedItemImage = 선택한 아이템의 image 영역
    //파라미터 7 selectedItemNum = 선택한 아이템의 1-20중의 Num
    public void statusDialog(final Context context, int image, final int itemType, final ImageView setView, final ImageView selectedItem, final ImageView selectedItemImage, final int selectedItemNum) {

        itemImage.setBackgroundResource(image);
        btn1.setText("착용해제");
        btn2.setText("판매하기");

        //착용해제
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //뷰 세팅
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //세팅된 아이템 이미지를 no 이미지로 변경
                        setView.setBackgroundResource(R.mipmap.box2);
                        //선택된 1-20 아이템 이미지 background를 inven_n으로 변경
//                        selectedItem.setBackgroundResource(R.mipmap.inven_n);
//                        체크이미지를 없애라
                        selectedItem.setVisibility(View.GONE);


                    }
                });
                //DBUserInfo 세팅
                switch (itemType) {
                    case 0:
                    case 1:
                    case 2:
                    case 100:
                        userSetItems = DBUserInfo.userInfo.getSetItems();
                        userSetItems[0] = "empty";

                        userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                        userSelectedItems[0] = "empty";
                        DBUserInfo.userInfo.setSelectedItems(userSelectedItems);
                        DBUserInfo.userInfo.setAtt(0 + "");
                        break;

                    case 3:
                    case 4:
                    case 5:
                    case 200:
                        userSetItems = DBUserInfo.userInfo.getSetItems();
                        userSetItems[1] = "empty";

                        userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                        userSelectedItems[1] = "empty";
                        DBUserInfo.userInfo.setSelectedItems(userSelectedItems);
                        DBUserInfo.userInfo.setMaxHp(1000);
                        break;

                    case 6:
                        userSetItems = DBUserInfo.userInfo.getSetItems();
                        userSetItems[2] = "empty";

                        userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                        userSelectedItems[2] = "empty";
                        break;

                }

                //위에서 분기처리 하니까 주석처리
//                userSetItems = DBUserInfo.userInfo.getSetItems();
//                userSetItems[itemType] = "empty";
//               DBUserInfo.userInfo.setSelectedItems(userSelectedItems);
//
//                userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
//                userSelectedItems[itemType] = "empty";
//                DBUserInfo.userInfo.setSelectedItems(userSelectedItems);
                dismiss();
                callback.call();
            }
        });

        //착용중인 아이템 판매
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //금액 추가 처리
                //DB에서 긁어온 money 스트링
                String userMoney = DBUserInfo.userInfo.getMoney();
                //형변환
                int userM = Integer.parseInt(userMoney);
                switch (itemType) {
                    //whip
                    case 0:
                        DBUserInfo.userInfo.setMoney((userM + 250) + "");
                        DBUserInfo.userInfo.setAtt(0 + "");
                        Toast.makeText(context, "250원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        DBUserInfo.userInfo.setMoney((userM + 500) + "");
                        DBUserInfo.userInfo.setAtt(0 + "");
                        Toast.makeText(context, "500원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        DBUserInfo.userInfo.setMoney((userM + 1000) + "");
                        DBUserInfo.userInfo.setAtt(0 + "");
                        Toast.makeText(context, "1000원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    case 100:
                        DBUserInfo.userInfo.setMoney(userM + "");
                        DBUserInfo.userInfo.setAtt(0 + "");
                        Toast.makeText(context, "0원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    //cloth
                    case 3:
                        DBUserInfo.userInfo.setMoney((userM + 250) + "");
                        DBUserInfo.userInfo.setMaxHp(1000);
//                        DBUserInfo.userInfo.setHp(0 + "");
                        Toast.makeText(context, "250원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    case 4:
                        DBUserInfo.userInfo.setMoney((userM + 500) + "");
                        DBUserInfo.userInfo.setMaxHp(1000);
//                        DBUserInfo.userInfo.setHp(0 + "");
                        Toast.makeText(context, "500원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    case 5:
                        DBUserInfo.userInfo.setMoney((userM + 1000) + "");
                        DBUserInfo.userInfo.setMaxHp(1000);
//                        DBUserInfo.userInfo.setHp(0 + "");
                        Toast.makeText(context, "1000원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    case 200:
                        DBUserInfo.userInfo.setMoney(userM + "");
                        DBUserInfo.userInfo.setMaxHp(1000);
//                        DBUserInfo.userInfo.setHp(0 + "");
                        Toast.makeText(context, "1000원 획득", Toast.LENGTH_SHORT).show();
                        break;
                    case 6:
                        DBUserInfo.userInfo.setMoney((userM + 50) + "");
                        Toast.makeText(context, "50원 획득", Toast.LENGTH_SHORT).show();
                }

                //선택된 item 창 empty로
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //세팅된 아이템 이미지를 no 이미지로 변경
                        setView.setBackgroundResource(R.mipmap.box2);
                        //선택된 1-20 아이템 이미지 background를 inven_n으로 변경
                        //주석처리
//                        selectedItem.setBackgroundResource(R.mipmap.inven_n);
                        selectedItem.setVisibility(View.GONE);
                        //선택된 1-20 아이템 이미지 자체를 empty로 변경
                        selectedItemImage.setBackgroundResource(R.mipmap.box2);
                    }
                });

                //DBUserInfo 세팅
                switch (itemType) {
                    case 0:
                    case 1:
                    case 2:
                    case 100:
                        //세팅 아이템 해제
                        userSetItems = DBUserInfo.userInfo.getSetItems();
                        userSetItems[0] = "empty";

                        //선택된 영역 해제
                        userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                        userSelectedItems[0] = "empty";


//                DBUserInfo.userInfo.setSelectedItems(userSelectedItems);

                        //실제 아이템1-20창에서 해제
                        userItems = DBUserInfo.userInfo.getItems();
                        userItems[selectedItemNum] = "empty";
//                DBUserInfo.userInfo.setSetItems(userItems);
                        break;

                    case 3:
                    case 4:
                    case 5:
                    case 200:
                        //세팅 아이템 해제
                        userSetItems = DBUserInfo.userInfo.getSetItems();
                        userSetItems[1] = "empty";

                        //선택된 영역 해제
                        userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                        userSelectedItems[1] = "empty";
//                DBUserInfo.userInfo.setSelectedItems(userSelectedItems);

                        //실제 아이템1-20창에서 해제
                        userItems = DBUserInfo.userInfo.getItems();
                        userItems[selectedItemNum] = "empty";
//                DBUserInfo.userInfo.setSetItems(userItems);
                        break;

                    case 6:
                        //세팅 아이템 해제
                        userSetItems = DBUserInfo.userInfo.getSetItems();
                        userSetItems[2] = "empty";

                        //선택된 영역 해제
                        userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
                        userSelectedItems[2] = "empty";
//                DBUserInfo.userInfo.setSelectedItems(userSelectedItems);

                        //실제 아이템1-20창에서 해제
                        userItems = DBUserInfo.userInfo.getItems();
                        userItems[selectedItemNum] = "empty";
//                DBUserInfo.userInfo.setSetItems(userItems);

                }

                //선택한 아이템의 1~20까지의 숫자
//                            selectedItemNum

                //세팅된 아이템의 저장
                String[] userSelectedItemNum = new String[4];
                userSelectedItemNum = DBUserInfo.userInfo.getSelectedItems();

                for (int i = 0; i < userSelectedItemNum.length; i++) {
                    if (!userSelectedItemNum[i].equals("empty")) {
                        int check = Integer.parseInt(userSelectedItemNum[i]);

                        if (selectedItemNum <= check) {
                            beforeSelected = check;
                            userSelectedItemNum[i] = (check - 1) + "";
                            callback.before();
                        }
                    }
                }

                //세팅 아이템 해제
                //위에서 분기처리중이니까 주석처리
//                userSetItems = DBUserInfo.userInfo.getSetItems();
//                userSetItems[itemType] = "empty";
//
//                //선택된 영역 해제
//                userSelectedItems = DBUserInfo.userInfo.getSelectedItems();
//                userSelectedItems[itemType] = "empty";
////                DBUserInfo.userInfo.setSelectedItems(userSelectedItems);
//
//                //실제 아이템1-20창에서 해제
//                userItems = DBUserInfo.userInfo.getItems();
//                userItems[selectedItemNum] = "empty";
////                DBUserInfo.userInfo.setSetItems(userItems);
                dismiss();
                callback.getCoinSound();
                callback.call();

            }


        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    //다이얼로그 종료후에 item새로고침을 위한 콜백메서드
    interface DialogCallback {
        void call();

        //이전 이미지 처리
        void before();

        //아이템 장착할 때 사운드
        void setItemSound();

        //코인 증가할 때 소리
        void getCoinSound();

    }

    //다이얼로그 공격력 텍스트 비교 세팅
    public void setDialogATTText() {
        noticeText1.setText(what);

        if (curInfo < setInfo) {
            noticeText2.setText((setInfo - curInfo) + " up!");
            noticeText2.setTextColor(Color.parseColor("#1DDB16"));
        } else if (setInfo < curInfo) {
            noticeText2.setText((curInfo - setInfo) + "down!");
            noticeText2.setTextColor(Color.parseColor("#FF0000"));

        } else {
            noticeText1.setText("");
            noticeText2.setText("");
        }

    }

    //다이얼로그 HP 텍스트 비교 세팅
    public void setDialogHPText() {
        noticeText1.setText(what);

        if ((curInfo - 1000) < setInfo) {
            noticeText2.setText(setInfo - (curInfo - 1000) + " up!");
            noticeText2.setTextColor(Color.parseColor("#1DDB16"));
        } else if (setInfo < (curInfo - 1000)) {
            noticeText2.setText(((curInfo - 1000) - setInfo) + "down!");
            noticeText2.setTextColor(Color.parseColor("#FF0000"));
        } else {
            noticeText1.setText("");
            noticeText2.setText("");
        }
    }
}







