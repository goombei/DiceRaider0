package user.test.ex.db;

import android.content.Context;

/**
 * Created by INC-B-17 on 2016-04-07.
 */
public class DBUserInfo {

    public final int SLOT_EQUIP_POTION = 2;

    Context context;
    protected DB db;
    public static UserInfo userInfo;

    public DBUserInfo(Context context) {
        this.context = context;

        db = new DB(context);

        userInfo = new UserInfo();
        //유저 아이템 배열
        String[] userItems = new String[20];
        //유저 세팅아이템 배열
        String[] userSetItems = new String[4];
        //선택된 아이템 배열
        String[] userSelectedItems = new String[4];

        //hp 긁어오기
        db.searchDB("hp");
        userInfo.setHp(db.getDb());

        //공격력 긁어오기
        db.searchDB("att");
        userInfo.setAtt(db.getDb());

        //방어력 긁어오기
        db.searchDB("def");
        userInfo.setDef(db.getDb());

        //money 긁어오기
        db.searchDB("money");
        userInfo.setMoney(db.getDb());

        //item 1-20까지 긁어오기
        for (int i = 0; i < userItems.length; i++) {
            db.searchDB("item"+ (i+1));
            userItems[i] = db.getDb();
        }
        userInfo.setItems(userItems);

        //settingItem 1-4긁어오기
        for (int i = 0; i < userSetItems.length; i++) {
            db.searchDB("setitem" + (i+1));
            userSetItems[i] = db.getDb();
        }
        userInfo.setSetItems(userSetItems);

        //userSelectedItems 1-4 긁어오기
        for (int i = 0; i < userSelectedItems.length; i++) {
            db.searchDB("selecteditem" + (i+1));
            userSelectedItems[i] = db.getDb();
        }
        userInfo.setSelectedItems(userSelectedItems);



    }

    public void itemSetting() {
        String[] userSetDbItem = DBUserInfo.userInfo.getSetItems();

        for (int i = 0; i < userSetDbItem.length; i++) {
            String setItem = userSetDbItem[i];

            switch (setItem) {
                case "empty":
                    break;

                case "whip":
                    DBUserInfo.userInfo.setAtt(5 + "");
                    break;
                case "whip2":
                    DBUserInfo.userInfo.setAtt(10 + "");
                    break;
                case "whip3":
                    DBUserInfo.userInfo.setAtt(20 + "");
                    break;
                case "bundle1":
                    DBUserInfo.userInfo.setAtt(1 + "");
                    break;

                case "cloth":
                    DBUserInfo.userInfo.setHp(1500 + "");
                    DBUserInfo.userInfo.setMaxHp(1500);
                    break;
                case "cloth2":
                    DBUserInfo.userInfo.setHp(2000 + "");
                    DBUserInfo.userInfo.setMaxHp(2000);
                    break;
                case "cloth3":
                    DBUserInfo.userInfo.setHp(3000 + "");
                    DBUserInfo.userInfo.setMaxHp(3000);
                    break;
                case "bundle2":
                    DBUserInfo.userInfo.setHp(1100 + "");
                    DBUserInfo.userInfo.setMaxHp(1100);
                    break;
            }
        }
    }

    public void allSettingDB(){
        db.allSettingDB();
    }

    public void initDBData() {
        String[] items = {
                "bundle1", "bundle2", "empty", "empty", "empty",
                "empty", "empty", "empty", "empty", "empty",
                "empty", "empty", "empty", "empty", "empty",
                "empty", "empty", "empty", "empty", "empty" };

        String[] setItems = {"bundle1", "bundle2", "empty", "empty"};

        String[] selectedItems = {"0", "1", "empty", "empty"};

        //금액 초기값 설정
        userInfo.setMoney(1000);
        //1~20 아이템 세팅
        userInfo.setItems(items);
        //1~20 장착 아이템 세팅
        userInfo.setSetItems(setItems);
        //장착 아이템의 장비창에서의 위치
        userInfo.setSelectedItems(selectedItems);

        //userinfo에 세팅한 값을 실제로 DB에 저장
        allSettingDB();
        itemSetting(); // 아이템 능력치 세팅//아이템 능력치 세팅

    }

    public void usePotion() {


        int heal,
        currHP = userInfo.getHpParseInt(),
        maxHP = userInfo.getMaxHp();

        // 장비한 경우, 피가 가득차지 않은 경우만 사용
        if (checkEquip(SLOT_EQUIP_POTION) && currHP < maxHP) {

            heal = maxHP / 2;

            int num = userInfo.getSelectedItem(SLOT_EQUIP_POTION);

            userInfo.setSetItem(SLOT_EQUIP_POTION, "empty");
            userInfo.setSelectedItem(SLOT_EQUIP_POTION, "empty");
            userInfo.setItemEmpty(num);
        } else {
            heal = 0;
        }

        currHP = currHP + heal;
        currHP = (currHP > maxHP) ? maxHP : currHP;

        userInfo.setHp(currHP);
    }


    public boolean checkEquip(int num) {

        String setItem = userInfo.getSetItem(num);
        return !setItem.equals("empty");
    }

    public void closeDB(){
        db.closeDB();
    }
}
