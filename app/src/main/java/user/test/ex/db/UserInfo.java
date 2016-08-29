package user.test.ex.db;

/**
 * Created by INC-B-17 on 2016-04-07.
 */
public class UserInfo {
    //스테이지 정보
    private int actNumber = 1;

    //초기값 1000
    private String hp = "";
    private int maxHp = 1000;
    //초기값 0
    private String att = "";
    //초기값 0
    private String def = "";
    //초기값 0인데 현재는 5000으로 박혀있음
    private String money = "";

    //1-20까지 아이템창의 아이템 정보
    //ex) item1 = whip, item2 = cloth, item3 = empty .... item20 = empty
    private String[] items;

    //착용중인 아이템 정보
    //setItems[0] - whip, whip2 ... setItems[1] - cloth, cloth2... setItems[3] - portion
    private String[] setItems;

    //착용중인 아이템의 item창 1-20사이의 숫자
    //selectedItem[0] - 착용 whip의 위치 ... //selectedItem[1] - 착용 cloth의 위치...
    private String[] selectedItems;

    public String getHp() {
        return hp;
    }

    public void setHp(String hp) {
        if (Integer.parseInt(hp) < 0) {
            hp = "0";
        }
        this.hp = hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public String getAtt() {
        return att;
    }

    public void setAtt(String att) {
        this.att = att;
    }

    public String getDef() {
        return def;
    }

    public void setDef(String def) {
        this.def = def;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String[] getItems() {
        return items;
    }

    public void setItems(String[] items) {
        this.items = items;
    }



    public String[] getSetItems() {
        return setItems;
    }

    public String getSetItem(int num) {
        return setItems[num];
    }

    public void setSetItems(String[] setItems) {
        this.setItems = setItems;
    }

    public void setSetItem(int num, String str) {
        this.setItems[num] = str;
    }

    public String[] getSelectedItems() {
        return selectedItems;
    }

    public int getSelectedItem(int num) {
        return Integer.parseInt(selectedItems[num]);
    }

    public void setSelectedItems(String[] selectedItems) {
        this.selectedItems = selectedItems;
    }

    public void setSelectedItem(int num, String str) {
        this.selectedItems[num] = str;
    }

    // 인트형 오버로딩 ( 개발시 편의성을 위함 )
    public void setMoney(int money) {
        this.money = ""+money;
    }

    public void setHp(int hp) {
        this.hp = ""+hp;
    }

    public void setAtt(int att) {
        this.att = ""+att;
    }

    // 인트형 불러오기 ( 데이터 불러올시 편하도록 위함 )
    public int getHpParseInt() {
        return Integer.parseInt(hp);
    }

    public int getAttParseInt() {
        return Integer.parseInt(att);
    }

    public int getMoneyParseInt() {
        return Integer.parseInt(money);
    }

    public int getActNumber() {
        return actNumber;
    }

    public void setActNumber(int actNumber) {
        this.actNumber = actNumber;
    }

    //무기 착용 여부 가져오기
    public String getSettingWhip() {
        return setItems[0];
    }
    //포션 착용 여부 가져오기
    public String getSettingPortion() {
        //0 무기 1 방어구 2 포션 3 비워둠
        return setItems[2];
    }

    public void setItemEmpty(int num) {
        this.items[num] = "empty";
        for (int i = num; i < items.length - 1; i++) {
            this.items[i] = this.items[i + 1];
        }
    }
}
