package user.test.ex.db;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by INC-B-17 on 2016-04-25.
 */

/* Hogan
 * 2016-05-02
 * 게임 중에 랭크를 확인하기 위해,
 * dbSet()을 dbSet()과 load_rank_all()로 분리
 * dbSet()은 입력받은 name과 score를 데이터베이스로
 * load_rank_all()은 데이터베이스에서 전체 정보를 받아옵니다
 */
public class DBRankInfo {

    Context context;
    protected DB db;

    ArrayList<String> nameList;
    ArrayList<String> scoreList;



    public DBRankInfo(Context context) {
        this.context = context;

        nameList = new ArrayList<>();
        scoreList = new ArrayList<>();


        db = new DB(context);
//        db.useDB("select score from rank");

    }

    public void dbSet(String name, String score) {
        //값 인서트하고
        db.insertDBList(name, score);
    }

    public void load_rank_all() {
        //전체 db정보 조회
        db.searchDBList("name");
        nameList = db.getDbResultNameList();
//        db.clearList();

        db.searchDBList("score");
        scoreList = db.getDbResultScoreList();
//        db.clearList();
    }

    public ArrayList<String> getNameList() {
        return nameList;
    }

    public ArrayList<String> getScoreList() {
        return scoreList;
    }
}
