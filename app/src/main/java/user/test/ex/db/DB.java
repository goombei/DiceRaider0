package user.test.ex.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DB {

    /**
     * 수정이 필요한 부분은 직접 말해주세요 sonasd
     */

    //DB
    private SQLiteDatabase db, rankDB;
    //단말기 내의 DB파일의 위치
    String dbPath = "";
    String dbResult = "";

    ArrayList<String> dbResultNameList, dbResultScoreList;

    //첫 설치 여부 판단해서 assets에 있는 db파일을 폰에 복사해서 쓴다.
    SharedPreferences pref;
    //첫 설치 여부 판단할 변수 처음엔 true로 세팅
    boolean isSetupFirst = true;

    Context context;

    public DB(Context context) {
        this.context = context;

        dbPath = Environment.getExternalStorageDirectory() + "/database/";
        load();
//        isDeleteDB();
        dbFileCopy();
        save();

        //DB 읽기
        //user,ItemDB
        db = context.openOrCreateDatabase(dbPath + "gameDB.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
        //rankDB 읽기
//        rankDB = context.openOrCreateDatabase(dbPath + "rankDB.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);

    }

    //첫 설치 여부를 판단 해줄놈
    public void load() {
        pref = PreferenceManager.getDefaultSharedPreferences(context);
        //첫 설치 여부 판단하고 값이 없을 경우 true로 세팅
        isSetupFirst = pref.getBoolean("save", true);
    }

    //사용자가 임의로 DB를 지웠을 경우 DB를 다시 복사하게 판단해줄놈
//    public void isDeleteDB() {
//        String dbFilePath = dbPath + "gameDB.db";
//        File path = new File(dbPath);
//        File filePath = new File(dbFilePath);
//        if (!path.exists()) {
//            isSetupFirst = true;
//        }
//
//        if (!filePath.isFile()) {
//            isSetupFirst = true;
//        }
//
//        isSetupFirst = false;
//    }

    public void save() {
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("save", isSetupFirst);
        edit.commit();
    }



    public void dbFileCopy() {
        //assets 폴더에 있는 파일 접근하기 위해 도와주는 놈
        AssetManager assetManager =  context.getAssets();
        String[] files = null;
        String mkdir = dbPath;

        //assets 항목에 모든 것들을 files 배열에 넣는다.
        try {
            files = assetManager.list("");
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < files.length; i++) {
            InputStream is = null;
            OutputStream os = null;

            try {
                is = assetManager.open(files[i]);
                File mPath = new File(mkdir);

                if (!mPath.exists()) {
                    isSetupFirst = true;
                } else {
                    //폴더만 있고 파일이 없는 경우 리스트로 검색하여
                    //생성할 파일을 찾는다.
                    String[] sub = mPath.list();
                    if(sub.length == 0) {
                        isSetupFirst = true;
                    } else {
                        for (int j = 0; j < sub.length; j++) {
                            if (sub[j].equals("gameDB.db")) {
                                break;
                            }
                            if (j == sub.length -1) {
                                isSetupFirst = true;
                            }
                        }
                        //rankDB.db 추가 16.04.15 sonasd
//                        for (int k = 0; k < sub.length; k++) {
//                            if (sub[k].equals("rankDB.db")) {
//                                break;
//                            }
//                            if (k == sub.length - 1) {
//                                isSetupFirst = true;
//                            }
//                        }
                    }
                }


                if (isSetupFirst) {
                    mPath.mkdirs();
                    os = new FileOutputStream(mkdir + "/" + files[i]);
                    copyFile(is, os);

                    is.close();
                    os.flush();
                    os.close();
                    isSetupFirst = false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void copyFile(InputStream is, OutputStream os) {

        byte[] buffer = new byte[1048576];
        int read;

        try {
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void useDB(String query) {
        Cursor cursor = db.rawQuery(query, null);
        String[] col = new String[cursor.getColumnCount()];
        col = cursor.getColumnNames();

        String[] str = new String[cursor.getColumnCount()];

        //데이터베이스에서 읽어올 자료가 없을 떄까지 반복
        while (cursor.moveToNext()) {
            for (int i = 0; i < col.length; i++) {
                str[i] = "";
                str[i] += cursor.getString(i);

                dbResult = str[i];
            }
        }
        cursor.close();
        cursor = null;
    }

    /**
     * DB 검색하는 놈
     */
    public void searchDB(String search) {
        Cursor cursor = db.rawQuery("select " + search + " from hero", null);
        String[] col = new String[cursor.getColumnCount()];
        col = cursor.getColumnNames();

        String[] str = new String[cursor.getColumnCount()];

        //데이터베이스에서 읽어올 자료가 없을 떄까지 반복
        while (cursor.moveToNext()) {
            for (int i = 0; i < col.length; i++) {
                str[i] = "";
                str[i] += cursor.getString(i);

                dbResult = str[i];
            }
        }
        cursor.close();
        cursor = null;
    }

    /**
     * DB 검색하는 놈인데 모든 값을 List에 담고 있다.
     */
    public void searchDBList(String search) {

        dbResultNameList = new ArrayList<>();
        dbResultScoreList = new ArrayList<>();

        Cursor cursor = db.rawQuery("select " + search + " from rank order by cast (score as integer) asc", null);
        String[] col = new String[cursor.getColumnCount()];
        col = cursor.getColumnNames();

        String[] str = new String[cursor.getColumnCount()];

        //데이터베이스에서 읽어올 자료가 없을 떄까지 반복
        while (cursor.moveToNext()) {
            for (int i = 0; i < col.length; i++) {
                str[i] = "";
                str[i] += cursor.getString(i);

                switch (search) {
                    case "name":
                        dbResultNameList.add(str[i]);
                        break;
                    case "score":
                        dbResultScoreList.add(str[i]);
                        break;
                }
            }
        }

        cursor.close();
        cursor = null;
    }

    /**
     * DB insert
     */

    public  void insertDBList(String name, String score) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("score", score);
        db.insert("rank", null, values);
    }

    /**
     * MonsterDB 검색하는놈
     * 필요없음
     */

//    public void searchMonsterDB(String search) {
//        Cursor cursor = db.rawQuery("select " + search + " from hero", null);
//        String[] col = new String[cursor.getColumnCount()];
//        col = cursor.getColumnNames();
//
//        String[] str = new String[cursor.getColumnCount()];
//
//        //데이터베이스에서 읽어올 자료가 없을 떄까지 반복
//        while (cursor.moveToNext()) {
//            for (int i = 0; i < col.length; i++) {
//                str[i] = "";
//                str[i] += cursor.getString(i);
//
//                dbResult = str[i];
//            }
//        }
//
//    }


    /**
     * DB update 하는놈
     */

    public void setDB(String column, String data) {
        ContentValues values = new ContentValues();
        values.put(column, data);

        db.update("hero", values, null, null);
//        db.insert("hero", null, values);
    }

    public void allSettingDB() {
        ContentValues values = new ContentValues();
        values.put("hp", DBUserInfo.userInfo.getHp());
        values.put("att", DBUserInfo.userInfo.getAtt());
        values.put("def", DBUserInfo.userInfo.getDef());
        values.put("money", DBUserInfo.userInfo.getMoney());

        String[] userItems = new String[20];
        userItems = DBUserInfo.userInfo.getItems();
        for (int i = 0; i < userItems.length; i++) {
            values.put("item"+(i+1), userItems[i]);
        }

        String[] setUserItems = new String[4];
        setUserItems = DBUserInfo.userInfo.getSetItems();
        for (int i = 0; i < setUserItems.length; i++) {
            values.put("setitem"+(i+1), setUserItems[i]);
        }

        String[] selectedUserItems = new String[4];
        selectedUserItems = DBUserInfo.userInfo.getSelectedItems();
        for (int i = 0; i < selectedUserItems.length; i++) {
            values.put("selecteditem"+(i+1), selectedUserItems[i]);
        }

        db.update("hero", values, null, null);

    }

    public String getDb() {
        return dbResult;
    }

    public void closeDB() {
        db.close();
    }

    public ArrayList<String> getDbResultNameList() {
        return dbResultNameList;
    }

    public ArrayList<String> getDbResultScoreList() {
        return dbResultScoreList;
    }

    public void clearList() {
        dbResultNameList.clear();
        dbResultScoreList.clear();
    }
}
