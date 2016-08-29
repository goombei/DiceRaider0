package user.test.ex.diceraider0;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import user.test.ex.db.DBUserInfo;

public class InventoryActivity extends AppCompatActivity {

    //0~2까지 무기자리
    public final int ITEM_WHIP = 0;
    public final int ITEM_WHIP2 = 1;
    public final int ITEM_WHIP3 = 2;

    //3~5까지 갑옷자리
    public final int ITEM_CLOTH = 3;
    public final int ITEM_CLOTH2 = 4;
    public final int ITEM_CLOTH3 = 5;

    //6
    public final int ITEM_PORTION = 6;

    public final int ITEM_BUNDLE1 = 100;
    public final int ITEM_BUNDLE2 = 200;


    //아이템 1-20 background 영역
    ImageView item1, item2, item3, item4, item5, item6, item7, item8, item9, item10, item11, item12, item13, item14, item15, item16, item17, item18, item19, item20;
    //아이템 1-20 image 영역 (둘이 겹쳐있음)
    ImageView itemImage1, itemImage2, itemImage3, itemImage4, itemImage5, itemImage6, itemImage7, itemImage8, itemImage9,
            itemImage10, itemImage11, itemImage12, itemImage13, itemImage14, itemImage15, itemImage16, itemImage17, itemImage18, itemImage19, itemImage20;
    //선택된 아이템 1-20까지 체크 표시 해줄 image 영역(세개가 겹쳐있음)
    ImageView selectedIcon1, selectedIcon2, selectedIcon3, selectedIcon4, selectedIcon5, selectedIcon6, selectedIcon7, selectedIcon8, selectedIcon9, selectedIcon10,
            selectedIcon11, selectedIcon12, selectedIcon13, selectedIcon14, selectedIcon15, selectedIcon16, selectedIcon17, selectedIcon18, selectedIcon19, selectedIcon20;

    //세팅된 아이템 1-4 background 영역
    ImageView setItem1, setItem2, setItem3, setItem4;
    //세팅도니 아이템 1-4 image 영역(둘이 겹쳐있음)
    ImageView setItemImage1, setItemImage2, setItemImage3, setItemImage4;

    //아이템 20개 담을 변수
    String[] userDbItem;
    //세팅된 아이템 4개 담을 변수
    String[] userSetDbItem;
    //아이템 20개중 세팅된 아이템과 매핑할 변수
    String[] userSelectedItemNum;
    //DB에서 금액을 가지고 올 변수
    String userMoney;

    //클릭 이벤트로 쓸 변수
    ImageView[] items;
    ImageView[] setItems;

    //얘는 이미지를 보여줄 변수
    ImageView[] itemImages;
    ImageView[] setItemImages;

    //얘는 선택된 이미지를 체크해서 보여줄 변수
    ImageView[] selectedIcons;

    //소지금액을 보여줄 Textview
    TextView moneyText;

    //hp를 보여줄 TextView
    TextView curhpText;
    TextView maxhpText;

    //공격력을 보여줄 TextView
    TextView attText;

    InventoryDialog dialog;
    Context context;

    SoundManager sm;

    boolean setCloth;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        context = this;

        bundle = getIntent().getExtras();

        userDbItem = new String[20];
        userSetDbItem = new String[4];
        userSelectedItemNum = new String[4];

        /**
         * 잘 안된다 일단 pass하고 다 쳐넣자~
         */
//        items = new ImageView[20];
//        for (int i = 0; i < items.length; i++) {
//            try {
//                items[i] = (ImageView)findViewById(new R.id().getClass().getField("item" + i+1).getInt(null));
//                items[i].setOnClickListener(click);
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//        }


//        itemImages = new ImageView[20];
//        for (int i = 0; i < itemImages.length; i++) {
//            try {
//                itemImages[i] = (ImageView)findViewById(new R.id().getClass().getField("itemImage" + i+1).getInt(null));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }

        item1 = (ImageView) findViewById(R.id.item1);
        item2 = (ImageView) findViewById(R.id.item2);
        item3 = (ImageView) findViewById(R.id.item3);
        item4 = (ImageView) findViewById(R.id.item4);
        item5 = (ImageView) findViewById(R.id.item5);
        item6 = (ImageView) findViewById(R.id.item6);
        item7 = (ImageView) findViewById(R.id.item7);
        item8 = (ImageView) findViewById(R.id.item8);
        item9 = (ImageView) findViewById(R.id.item9);
        item10 = (ImageView) findViewById(R.id.item10);
        item11 = (ImageView) findViewById(R.id.item11);
        item12 = (ImageView) findViewById(R.id.item12);
        item13 = (ImageView) findViewById(R.id.item13);
        item14 = (ImageView) findViewById(R.id.item14);
        item15 = (ImageView) findViewById(R.id.item15);
        item16 = (ImageView) findViewById(R.id.item16);
        item17 = (ImageView) findViewById(R.id.item17);
        item18 = (ImageView) findViewById(R.id.item18);
        item19 = (ImageView) findViewById(R.id.item19);
        item20 = (ImageView) findViewById(R.id.item20);

        setItem1 = (ImageView) findViewById(R.id.setItem1);
        setItem2 = (ImageView) findViewById(R.id.setItem2);
        setItem3 = (ImageView) findViewById(R.id.setItem3);
        setItem4 = (ImageView) findViewById(R.id.setItem4);

        items = new ImageView[]{item1, item2, item3, item4, item5, item6, item7, item8, item9, item10,
                item11, item12, item13, item14, item15, item16, item17, item18, item19, item20};

        //1-20 아이템 클릭이벤트
        for (int i = 0; i < items.length; i++) {
            items[i].setOnClickListener(click);
        }

        setItems = new ImageView[]{setItem1, setItem2, setItem3, setItem4};

        //세팅된 1-4 아이템 클릭이벤트
        for (int i = 0; i < setItems.length; i++) {
            setItems[i].setOnClickListener(click);
        }

        itemImage1 = (ImageView) findViewById(R.id.itemImage1);
        itemImage2 = (ImageView) findViewById(R.id.itemImage2);
        itemImage3 = (ImageView) findViewById(R.id.itemImage3);
        itemImage4 = (ImageView) findViewById(R.id.itemImage4);
        itemImage5 = (ImageView) findViewById(R.id.itemImage5);
        itemImage6 = (ImageView) findViewById(R.id.itemImage6);
        itemImage7 = (ImageView) findViewById(R.id.itemImage7);
        itemImage8 = (ImageView) findViewById(R.id.itemImage8);
        itemImage9 = (ImageView) findViewById(R.id.itemImage9);
        itemImage10 = (ImageView) findViewById(R.id.itemImage10);
        itemImage11 = (ImageView) findViewById(R.id.itemImage11);
        itemImage12 = (ImageView) findViewById(R.id.itemImage12);
        itemImage13 = (ImageView) findViewById(R.id.itemImage13);
        itemImage14 = (ImageView) findViewById(R.id.itemImage14);
        itemImage15 = (ImageView) findViewById(R.id.itemImage15);
        itemImage16 = (ImageView) findViewById(R.id.itemImage16);
        itemImage17 = (ImageView) findViewById(R.id.itemImage17);
        itemImage18 = (ImageView) findViewById(R.id.itemImage18);
        itemImage19 = (ImageView) findViewById(R.id.itemImage19);
        itemImage20 = (ImageView) findViewById(R.id.itemImage20);

        setItemImage1 = (ImageView) findViewById(R.id.setItemImage1);
        setItemImage2 = (ImageView) findViewById(R.id.setItemImage2);
        setItemImage3 = (ImageView) findViewById(R.id.setItemImage3);
        setItemImage4 = (ImageView) findViewById(R.id.setItemImage4);

        itemImages = new ImageView[]{itemImage1, itemImage2, itemImage3, itemImage4, itemImage5, itemImage6, itemImage7, itemImage8, itemImage9, itemImage10, itemImage11, itemImage12,
                itemImage13, itemImage14, itemImage15, itemImage16, itemImage17, itemImage18, itemImage19, itemImage20};

        setItemImages = new ImageView[]{setItemImage1, setItemImage2, setItemImage3, setItemImage4};

        selectedIcon1 = (ImageView) findViewById(R.id.selectedIcon1);
        selectedIcon2 = (ImageView) findViewById(R.id.selectedIcon2);
        selectedIcon3 = (ImageView) findViewById(R.id.selectedIcon3);
        selectedIcon4 = (ImageView) findViewById(R.id.selectedIcon4);
        selectedIcon5 = (ImageView) findViewById(R.id.selectedIcon5);
        selectedIcon6 = (ImageView) findViewById(R.id.selectedIcon6);
        selectedIcon7 = (ImageView) findViewById(R.id.selectedIcon7);
        selectedIcon8 = (ImageView) findViewById(R.id.selectedIcon8);
        selectedIcon9 = (ImageView) findViewById(R.id.selectedIcon9);
        selectedIcon10 = (ImageView) findViewById(R.id.selectedIcon10);
        selectedIcon11 = (ImageView) findViewById(R.id.selectedIcon11);
        selectedIcon12 = (ImageView) findViewById(R.id.selectedIcon12);
        selectedIcon13 = (ImageView) findViewById(R.id.selectedIcon13);
        selectedIcon14 = (ImageView) findViewById(R.id.selectedIcon14);
        selectedIcon15 = (ImageView) findViewById(R.id.selectedIcon15);
        selectedIcon16 = (ImageView) findViewById(R.id.selectedIcon16);
        selectedIcon17 = (ImageView) findViewById(R.id.selectedIcon17);
        selectedIcon18 = (ImageView) findViewById(R.id.selectedIcon18);
        selectedIcon19 = (ImageView) findViewById(R.id.selectedIcon19);
        selectedIcon20 = (ImageView) findViewById(R.id.selectedIcon20);

        selectedIcons = new ImageView[]{selectedIcon1, selectedIcon2, selectedIcon3, selectedIcon4, selectedIcon5, selectedIcon6, selectedIcon7, selectedIcon8, selectedIcon9, selectedIcon10,
                selectedIcon11, selectedIcon12, selectedIcon13, selectedIcon14, selectedIcon15, selectedIcon16, selectedIcon17, selectedIcon18, selectedIcon19, selectedIcon20};

        moneyText = (TextView) findViewById(R.id.moneyText);
        curhpText = (TextView) findViewById(R.id.curhpText);
        maxhpText = (TextView) findViewById(R.id.maxhpText);
        attText = (TextView) findViewById(R.id.attText);


        itemSetting();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sm = new SoundManager(this);
        sm.initSoundPool();
        sm.playBGM(sm.BGM_INVENTORY, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.stopBGM();
    }

    InventoryDialog.DialogCallback callback = new InventoryDialog.DialogCallback() {
        @Override
        public void call() {

            //아이템 판매할 경우 아이템 창 새로고침
            //아이템 정보를 새로 담을 그릇
            String refreshItems[] = new String[20];
            int cnt = 0;


            //empty가 아니면 담아라
            for (int i = 0; i < userDbItem.length; i++) {
                if (!userDbItem[i].equals("empty")) {
                    refreshItems[cnt] = userDbItem[i];
                    cnt++;
                }
            }

            //빈공간 모드 empty로 채우기
            for (int i = cnt; i < userDbItem.length; i++) {
                refreshItems[i] = "empty";
            }

            //UserInfo에 세팅
            DBUserInfo.userInfo.setItems(refreshItems);

            //아이템 세팅
            //다이얼로그가 종료되면 onresume이 호출되는지 알았는데 아니었다...
            setCloth = true;
            itemSetting();
        }

        @Override
        public void before() {
//            items[InventoryDialog.beforeSelected].setBackgroundResource(R.mipmap.inven_n);
            selectedIcons[InventoryDialog.beforeSelected].setVisibility(View.GONE);
        }

        @Override
        public void setItemSound() {
            sm.playSound(sm.SOUND_SET_ITEM);
        }

        @Override
        public void getCoinSound() {
            sm.playSound(sm.SOUND_COIN);
        }
    };


    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            //보유 아이템(1-20번째 아이템) 이벤트
            for (int i = 0; i < items.length; i++) {
                if (v == items[i]) {
                    //아이템이 whip면
                    //userDbItem은 DB에서 긁어온 1-20번째 아이템 데이터
                    if (userDbItem[i].equals("whip")) {
                        dialog = new InventoryDialog(context, callback);

                        //파라미터 1 context
                        //파라미터 2 itemName = DB에 실제 저장된 string
                        //파라미터 3 itemType = whip-1 cloth-2...
                        //파라미터 4 image = dialog에 띄울 itemImage
                        //파라미터 5 setView = 세팅될 아이템 이미지 영역 whip-1 cloth-2
                        //파라미터 6 selectedItem = 선택한 아이템의 background 영역 > 체크표시로 변경
                        //파라미터 7 selectedItemImage = 선택한 아이템의 image 영역
                        //파라미터 8 selectedItemNum = 선택한 아이템의 1-20중의 Num
                        //파라미터 9 setItemInfo = 세팅된 아이템의 실제 장비창의 num

                        dialog.itemDialog(context, "whip", ITEM_WHIP, R.mipmap.whip2, setItemImages[0], selectedIcons[i], itemImages[i], i, userSelectedItemNum[0]);
                        dialog.show();
                    }

                    if (userDbItem[i].equals("whip2")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.itemDialog(context, "whip2", ITEM_WHIP2, R.mipmap.whip3, setItemImages[0], selectedIcons[i], itemImages[i], i, userSelectedItemNum[0]);
                        dialog.show();
                    }

                    if (userDbItem[i].equals("whip3")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.itemDialog(context, "whip3", ITEM_WHIP3, R.mipmap.whip4, setItemImages[0], selectedIcons[i], itemImages[i], i, userSelectedItemNum[0]);
                        dialog.show();
                    }

                    //기본으로 주어지는 번들아이템
                    if (userDbItem[i].equals("bundle1")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.itemDialog(context, "bundle1", ITEM_BUNDLE1, R.mipmap.whip, setItemImages[0], selectedIcons[i], itemImages[i], i, userSelectedItemNum[0]);
                        dialog.show();
                    }

                    //아이템이 cloth면
                    if (userDbItem[i].equals("cloth")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.itemDialog(context, "cloth", ITEM_CLOTH, R.mipmap.armor1, setItemImages[1], selectedIcons[i], itemImages[i], i, userSelectedItemNum[1]);
                        dialog.show();
                    }

                    if (userDbItem[i].equals("cloth2")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.itemDialog(context, "cloth2", ITEM_CLOTH2, R.mipmap.armor2, setItemImages[1], selectedIcons[i], itemImages[i], i, userSelectedItemNum[1]);
                        dialog.show();
                    }

                    if (userDbItem[i].equals("cloth3")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.itemDialog(context, "cloth3", ITEM_CLOTH3, R.mipmap.armor3, setItemImages[1], selectedIcons[i], itemImages[i], i, userSelectedItemNum[1]);
                        dialog.show();
                    }

                    if (userDbItem[i].equals("bundle2")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.itemDialog(context, "bundle2", ITEM_BUNDLE2, R.mipmap.armor, setItemImages[1], selectedIcons[i], itemImages[i], i, userSelectedItemNum[1]);
                        dialog.show();
                    }

                    if (userDbItem[i].equals("portion")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.itemDialog(context, "portion", ITEM_PORTION, R.mipmap.big_portion, setItemImages[2], selectedIcons[i], itemImages[i], i, userSelectedItemNum[2]);
                        dialog.show();
                    } else {
                        //비어있으면 아무것도 하지마
                    }

                }
            }

            //세팅 아이템(세팅된 1-4번째 아이템) 이벤트
            for (int i = 0; i < setItems.length; i++) {
                if (v == setItems[i]) {

                    //파라미터 1 context
                    //파라미터 2 image = dialog에 띄울 itemImage
                    //파라미터 3 itemType = whip-1 cloth-2...
                    //파라미터 4 setView = 세팅될 아이템 이미지 영역 whip-1 cloth-2
                    //파라미터 5 selectedItem = 선택한 아이템의 background 영역
                    //파라미터 6 selectedItemImage = 선택한 아이템의 image 영역
                    //파라미터 7 selectedItemNum = 선택한 아이템의 1-20중의 Num

                    //아이템이 whip면
                    if (userSetDbItem[i].equals("whip")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.statusDialog(context, R.mipmap.whip2, ITEM_WHIP, setItemImages[0], selectedIcons[Integer.parseInt(userSelectedItemNum[0])], itemImages[Integer.parseInt(userSelectedItemNum[0])], Integer.parseInt(userSelectedItemNum[0]));
                        dialog.show();
                    }

                    if (userSetDbItem[i].equals("whip2")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.statusDialog(context, R.mipmap.whip3, ITEM_WHIP2, setItemImages[0], selectedIcons[Integer.parseInt(userSelectedItemNum[0])], itemImages[Integer.parseInt(userSelectedItemNum[0])], Integer.parseInt(userSelectedItemNum[0]));
                        dialog.show();
                    }

                    if (userSetDbItem[i].equals("whip3")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.statusDialog(context, R.mipmap.whip4, ITEM_WHIP3, setItemImages[0], selectedIcons[Integer.parseInt(userSelectedItemNum[0])], itemImages[Integer.parseInt(userSelectedItemNum[0])], Integer.parseInt(userSelectedItemNum[0]));
                        dialog.show();
                    }

                    if (userSetDbItem[i].equals("bundle1")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.statusDialog(context, R.mipmap.whip, ITEM_BUNDLE1, setItemImages[0], selectedIcons[Integer.parseInt(userSelectedItemNum[0])], itemImages[Integer.parseInt(userSelectedItemNum[0])], Integer.parseInt(userSelectedItemNum[0]));
                        dialog.show();
                    }

                    //아이템이 cloth면
                    if (userSetDbItem[i].equals("cloth")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.statusDialog(context, R.mipmap.armor1, ITEM_CLOTH, setItemImages[1], selectedIcons[Integer.parseInt(userSelectedItemNum[1])], itemImages[Integer.parseInt(userSelectedItemNum[1])], Integer.parseInt(userSelectedItemNum[1]));
                        dialog.show();
                    }

                    if (userSetDbItem[i].equals("cloth2")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.statusDialog(context, R.mipmap.armor2, ITEM_CLOTH2, setItemImages[1], selectedIcons[Integer.parseInt(userSelectedItemNum[1])], itemImages[Integer.parseInt(userSelectedItemNum[1])], Integer.parseInt(userSelectedItemNum[1]));
                        dialog.show();
                    }

                    if (userSetDbItem[i].equals("cloth3")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.statusDialog(context, R.mipmap.armor3, ITEM_CLOTH3, setItemImages[1], selectedIcons[Integer.parseInt(userSelectedItemNum[1])], itemImages[Integer.parseInt(userSelectedItemNum[1])], Integer.parseInt(userSelectedItemNum[1]));
                        dialog.show();
                    }

                    if (userSetDbItem[i].equals("bundle2")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.statusDialog(context, R.mipmap.armor, ITEM_BUNDLE2, setItemImages[1], selectedIcons[Integer.parseInt(userSelectedItemNum[1])], itemImages[Integer.parseInt(userSelectedItemNum[1])], Integer.parseInt(userSelectedItemNum[1]));
                        dialog.show();
                    }

                    //아이템이 portion이면
                    if (userSetDbItem[i].equals("portion")) {
                        dialog = new InventoryDialog(context, callback);
                        dialog.statusDialog(context, R.mipmap.big_portion, ITEM_PORTION, setItemImages[2], selectedIcons[Integer.parseInt(userSelectedItemNum[2])], itemImages[Integer.parseInt(userSelectedItemNum[2])], Integer.parseInt(userSelectedItemNum[2]));
                        dialog.show();
                    } else {
                        //비어있으면 아무것도 하지마
                    }
                }
            }
        }

    };

    public void itemSetting() {


        //저장된 아이템 정보를 담아라
        userDbItem = DBUserInfo.userInfo.getItems();
        userSetDbItem = DBUserInfo.userInfo.getSetItems();
        userSelectedItemNum = DBUserInfo.userInfo.getSelectedItems();


        String j = "";
        //아이템창 세팅
        for (int i = 0; i < userDbItem.length; i++) {

            String item = userDbItem[i];
            switch (item) {
                case "empty":
                    //x아이콘 표시 주석처리 04.19
//                    itemImages[i].setBackgroundResource(R.mipmap.no);
                    itemImages[i].setBackgroundResource(R.mipmap.box2);
                    break;

                case "whip":
                    itemImages[i].setBackgroundResource(R.mipmap.whip2);
                    break;
                case "whip2":
                    itemImages[i].setBackgroundResource(R.mipmap.whip3);
                    break;
                case "whip3":
                    itemImages[i].setBackgroundResource(R.mipmap.whip4);
                    break;
                case "bundle1":
                    itemImages[i].setBackgroundResource(R.mipmap.whip);
                    break;

                case "cloth":
                    itemImages[i].setBackgroundResource(R.mipmap.armor1);
                    break;
                case "cloth2":
                    itemImages[i].setBackgroundResource(R.mipmap.armor2);
                    break;
                case "cloth3":
                    itemImages[i].setBackgroundResource(R.mipmap.armor3);
                    break;
                case "bundle2":
                    itemImages[i].setBackgroundResource(R.mipmap.armor);
                    break;

                case "portion":
                    itemImages[i].setBackgroundResource(R.mipmap.big_portion);
                    break;
            }
        }
        //세팅 아이템 세팅
        for (int i = 0; i < setItemImages.length; i++) {
            String setItem = userSetDbItem[i];

            //현재체력
            int curHp = DBUserInfo.userInfo.getHpParseInt();
            //현재최대체력
            int curMaxHp = DBUserInfo.userInfo.getMaxHp();
            //기본값 체력
            int defaultHp = 1000;
            //기존 아이템 능력치
            int curItem = curMaxHp - defaultHp;
            //세팅할 아이템 능력치
            int settingItem = 0;

            //현재체력 - (현재 최대체력 - 디폴트체력(1000)) + 아이템의 능력치(500)
            int itemInfo = curHp - curItem;
//            DBUserInfo.userInfo.getHpParseInt() - (DBUserInfo.userInfo.getMaxHp() - 1000)

            switch (setItem) {
                case "empty":
                    //x아이콘 표시 주석처리 04.19
//                    setItemImages[i].setBackgroundResource(R.mipmap.no);
                    setItemImages[i].setBackgroundResource(R.mipmap.box2);
                    break;
                case "whip":
                    setItemImages[i].setBackgroundResource(R.mipmap.whip2);
                    DBUserInfo.userInfo.setAtt(5 + "");
                    break;
                case "whip2":
                    setItemImages[i].setBackgroundResource(R.mipmap.whip3);
                    DBUserInfo.userInfo.setAtt(10 + "");
                    break;
                case "whip3":
                    setItemImages[i].setBackgroundResource(R.mipmap.whip4);
                    DBUserInfo.userInfo.setAtt(20 + "");
                    break;
                case "bundle1":
                    setItemImages[i].setBackgroundResource(R.mipmap.whip);
                    DBUserInfo.userInfo.setAtt(1 + "");
                    break;

                case "cloth":
                    setItemImages[i].setBackgroundResource(R.mipmap.armor1);
                    if (setCloth) {
                        //현재체력 - (현재 최대체력 - 디폴트체력(1000)) + 아이템의 능력치(500)
//                        settingItem = 500;
//                        DBUserInfo.userInfo.setHp((itemInfo + settingItem) + "");
                    }
                    DBUserInfo.userInfo.setMaxHp(1500);

                    break;
                case "cloth2":
                    setItemImages[i].setBackgroundResource(R.mipmap.armor2);
                    if (setCloth) {
//                        settingItem = 1000;
//                        DBUserInfo.userInfo.setHp((itemInfo + settingItem) + "");
                    }
                    DBUserInfo.userInfo.setMaxHp(2000);
                    break;
                case "cloth3":
                    setItemImages[i].setBackgroundResource(R.mipmap.armor3);
                    if (setCloth) {
//                        settingItem = 2000;
//                        DBUserInfo.userInfo.setHp((itemInfo + settingItem) + "");
                    }
                    DBUserInfo.userInfo.setMaxHp(3000);
                    break;
                case "bundle2":
                    setItemImages[i].setBackgroundResource(R.mipmap.armor);
                    if (setCloth) {
//                        settingItem = 100;
//                        DBUserInfo.userInfo.setHp((itemInfo + settingItem) + "");
                    }
                    DBUserInfo.userInfo.setMaxHp(1100);
                    break;

                case "portion":
                    setItemImages[i].setBackgroundResource(R.mipmap.big_portion);
                    break;
            }

            if (DBUserInfo.userInfo.getHpParseInt() > DBUserInfo.userInfo.getMaxHp()) {
                DBUserInfo.userInfo.setHp(DBUserInfo.userInfo.getMaxHp());
            }


        }

        //셀렉티드 아이템 세팅
        for (int i = 0; i < userSelectedItemNum.length; i++) {
            String selectedItem = userSelectedItemNum[i];
            //비어있거나
            if (!selectedItem.equals("")) {
                //empty라는 string값을 가지고 있지 않으면
                if (!selectedItem.equals("empty")) {
                    //select의 정보를 담고
                    int select = Integer.parseInt(selectedItem);
                    //1-20의 아이템background 영역의 색을 RED로 변경
//                    items[select].setBackgroundColor(Color.RED);
                    //기존의 백그라운드 빨간색으로 했던것을 체크표시로
                    selectedIcons[select].setVisibility(View.VISIBLE);
                }

            }
        }

        //status 세팅
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                moneyText.setText(DBUserInfo.userInfo.getMoney());
                curhpText.setText(DBUserInfo.userInfo.getHp() + " / ");
                maxhpText.setText(DBUserInfo.userInfo.getMaxHp() + "");
                attText.setText(DBUserInfo.userInfo.getAtt());
            }
        });

        setCloth = false;

        bossItemCheck();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }


    //보스한테 진입하기 위해 꼭 필요한 아이템 체크

    private void bossItemCheck() {
        boolean pass = bundle.getBoolean("pass", false);
        int stage = bundle.getInt("stage", 0);

        int[] res_true = {
                R.mipmap.key,
                R.drawable.item_knife,
                R.mipmap.crossbow
        };

        if (pass) {
            int res = res_true[stage];
            setItemImage4.setBackgroundResource(res);
            setItemImage4.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
    }
}
