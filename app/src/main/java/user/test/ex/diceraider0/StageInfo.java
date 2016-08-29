package user.test.ex.diceraider0;

import android.util.Log;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by INC-B-05 on 2016-04-04.
 */

/* Hogan
 *
 * 2016-04-04
 * implements Archive 자주 사용할 각종 정보 (타일 등)
 */

public class StageInfo implements Archive{
    //스테이지 정보 생성기
    private int num_boss;// 보스의 수

    static final int STAGE_ONE = 0;
    static final int STAGE_TWO = 1;
    static final int STAGE_THREE = 2;
    static final int STAGE_FOUR = 3;
    static final int STAGE_FIVE = 4;

    //타일 정보 섞기
    final int[] SEED = new int[NORMAL_TILE_TYPES];
    private int[] tileInfo;
    int length;

    Random rnd = new Random(); // 난수 생성용

    public StageInfo(int num_stage, int length) {

        this.length = length;
        tileInfo = new int[length]; // 타일 정보 테이블

        initStage(num_stage);

        // test();
    }//constructor


    public int getInfoTable(int x) {
        //해당 타일 이벤트 정보 반환
        return tileInfo[x];
    }//getInfoTable()


    private void initStage(int num_stage){
        //스테이지 생성

        if (length >= 20) {
            switch (num_stage) {
                // PLAIN    TREASURE    TRAP    SHOP   ANTAGONIST  BOSS
                // 0        1           2       3       4           5

                case STAGE_FIVE:
                case STAGE_FOUR:
                case STAGE_THREE:
                case STAGE_TWO:
                case STAGE_ONE:
                    num_boss = 1;
                    // seed(1, 1, 5, 10, 5, 5);
                    break;
            }
        } else {
            num_boss = 1;
            for (int i = 0; i < SEED.length; i++) {
                SEED[i] = length;
            }
        }

        //섞기
        shuffle20();
    }



    private void shuffle20(){
        //시드 바탕으로 섞기

        int bossTile = length / num_boss; // bossTile은 다른 타일이 들어가지 않도록 배제한다.
        int eliteTile = length / 3;
        // HashSet<Integer> tempSet = new HashSet<>();
        Set<Integer> tempSet = new LinkedHashSet<>();
        Iterator<Integer> it;

        int start = 0;

        for (int i = eliteTile + 1; i < length; i += eliteTile){
            //보스 체크
            if (i == bossTile - 1) {
                tileInfo[i] = BOSS;
            } else {
                tileInfo[i] = ANTAGONIST;
            }

            while(true) {
                Log.i("MY", "stagewhile");
                tempSet.add(rnd.nextInt(NORMAL_TILE_TYPES));
                if (tempSet.size() >= NORMAL_TILE_TYPES) {
                    break;
                }
            }

            it = tempSet.iterator();

            for (int j = start; j < i; j++) {
                if (it.hasNext()){
                    tileInfo[j] = it.next();
                } else {
                    int tempInfo = rnd.nextInt(NORMAL_TILE_TYPES);
                    if (tempInfo != tileInfo[j - 1]){
                        tileInfo[j] = tempInfo;
                    } else {
                        j--;
                    }
                }
            }

            Log.i("MY", "" + tempSet);
            tempSet.clear();
            start = i + 1;
        }

    }//shuffle()

    /*
    // 생성 방식 변경 - seed 생성 함수 주석처리
    private void seed(int num_boss, int plus, int div0, int div1, int div2, int div3){
        this.num_boss = num_boss; // 보스 타일 수
        SEED[ANTAGONIST] = length / div0 + plus; // 적 타일 수
        SEED[SHOP] = length / div1; // 상점 타일 수
        SEED[TRAP] = length / div2 + plus; // 함정 타일 수
        SEED[TREASURE] = length / div3 + plus; // 보물 타일 수

        int others = 0;
        for (int i = TREASURE; i < SEED.length; i++) {
            others += SEED[i];
        }
        SEED[PLAIN] = length - others - num_boss; // 나머지 잉여 타일 수
    }
    */

}
