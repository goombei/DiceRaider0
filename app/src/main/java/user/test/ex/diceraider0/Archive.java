package user.test.ex.diceraider0;

/**
 * Created by INC-B-13 on 2016-03-30.
 */

/* Hogan
 * 2016-04-20
 * RETURN_BOSS: RETURN_BOSS_VINE에서 변경. 1,2탄 보스에 둘 다 BossActivity 활용(View만 변경)
 *
 * 2016-04-19
 * STAGE_MAX: 스테이지 배열 길이
 * TILE_RES: 타일 비트맵 리소스 배열
 * HERO_RES: 영웅 비트맵 리소스 배열
 *
 * 2016-04-18
 * RETURN_BOSS_VINE 추가
 * 전체 칸 갯수(BOARD_LENGTH)를 Archive로 가져옴
 * mode 삭제(불필요, 미사용)
 * 사용하지 않는 상수 하단 정렬 및 주석화
 */

public interface Archive
{
    //상수 : 처음값이 프로그램 종료시까지 변경되지 않는 수;

    boolean BTN_TEST_ENABLED = true;
    boolean BOSS_STAGE1_PASS = false;

    //TEST_MODE 테스트 정보
    int TEST_NO = 0;
    int TEST_ACTION = 1;
    int TEST_ELITE = 2;
    int TEST_BOSS = 3;
    int TEST_RANK = 4;
    int TEST_TRAP = 5;
    int TEST_MONEY = 6;
    int TEST_SHOP = 7;

    int[] R_RES_TEST = {
            R.id.r_test_no,
            R.id.r_test_battle,
            R.id.r_test_elite,
            R.id.r_test_boss,
            R.id.r_test_rank,
            R.id.r_test_trap,
            R.id.r_test_money,
            R.id.r_test_shop};

    int[] R_RES_STAGE = {
            R.id.r_stage1,
            R.id.r_stage2,
            R.id.r_stage3};

    //TEST_STAGE 테스트 스테이지 정보
    int TEST_STAGE1 = 0;
    int TEST_STAGE2 = 1;
    int TEST_STAGE3 = 2;


    int BOARD_LENGTH = 20; // 전체 칸 갯수
    int STAGE_MAX = 3; // 스테이지 배열 길이

    // 주사위 정보
    int MAX_6 = 6;
    int SPIN_MSEC = 100;


    // 칸(타일) 정보
    int NORMAL_TILE_TYPES = 4; // 타일 종류 갯수(특수 타일 제외)
    int SPECIAL_TILE_TYPES = 2; // 특수 타일 종류 갯수(중간 보스 ,보스)
    int ALL_TILE_TYPES = NORMAL_TILE_TYPES + SPECIAL_TILE_TYPES;
    int INVALID = -1;

    // PLAIN    TREASURE    TRAP    SHOP   ANTAGONIST  BOSS
    // 0        1           2       3       4           5

    int PLAIN = 0; // 잉여칸
    int TREASURE = 1; // 보물칸
    int TRAP = 2; // 함정칸
    int SHOP = 3; // 상점/문 칸
    int ANTAGONIST = 4; // 적 등장 칸

    int BOSS = 5; // 보스 칸

    String[] evStr = {"PLAIN", "TREASURE", "TRAP", "SHOP", "ANTAGONIST", "BOSS", "null"};


    // 보스 관련
    int FPS = 30;

    boolean BOSS_STAGE_ONE_PASS = false;

    int CHANCE_TO_GET_KEY_ELITE = 25;
    int CHANCE_TO_GET_KEY_NORMAL = 5;

    boolean TEST_IDOL_TRUE = true;
    boolean TEST_IDOL_FALSE = false;


    //DialogMainFragment
    //"mode"
    int MODE_DICE = 0;
    int MODE_TREASURE = 1;
    int MODE_TRAP = 2;
    int MODE_REWARD = 3;
    int MODE_END_STAGE = 4;
    int MODE_RESTART = 5;
    int MODE_END_ACTION = 6;
    int MODE_TEST = 7;

    //intent 관련
    int RETURN_BOSS_TOMB_PUZZLE = 0;
    int RETURN_BOSS_TOMB_KEY = 1;
    int RETURN_BOSS_JUNGLE = 2;
    int RETURN_ARROW_BOSS = 3;// 에로우 보스의 결과를 리턴하여 넘긴다.
    int RETURN_ACTION = 4;
    int RETURN_RANK = 5; // 랭크화면에서 넘어와서 타이틀화면으로 넘김
    int RETURN_TITLE = 6; // 스타트 화면으로 갔다가 돌아올 때 WallView를 초기화한다.

    // 타일 비트맵 ID
    int[][] TILE_RES = {{
            R.drawable.plain_tomb,
            R.drawable.money_tomb,
            R.drawable.trap_tomb,
            R.drawable.shop,
            R.drawable.anta_tomb,
            R.drawable.boss
            }, {
            R.drawable.plain_jungle,
            R.drawable.money_jungle,
            R.drawable.trap_jungle,
            R.drawable.shop,
            R.drawable.anta_jungle,
            R.drawable.boss
            }, {
            R.drawable.plain_dungeon,
            R.drawable.money_dungeon,
            R.drawable.trap_dungeon,
            R.drawable.shop,
            R.drawable.anta_dungeon,
            R.drawable.boss }};

    // WallView 벽 비트맵 ID
    int[] WALL_RES = { // 벽 배경
            R.drawable.tombwall720,
            R.drawable.junglewall720,
            R.drawable.dungeonwall720,
            R.drawable.tombwall720,
            R.drawable.tombwall720};

    // 영웅 비트맵 ID
    int[] HERO_RES
            = { R.drawable.hero01,
            R.drawable.hero02,
            R.drawable.hero03,
            R.drawable.hero02 };

    //주사위 비트맵 ID
    int[] DICE_RES
            = { R.drawable.dice01,
            R.drawable.dice02,
            R.drawable.dice03,
            R.drawable.dice04,
            R.drawable.dice05,
            R.drawable.dice06};
    // 록픽 리소스
    int[]LOCKPIC_RES = {
            R.drawable.lock_back1 ,
            R.drawable.lock_back2 ,
            R.drawable.lock_back3,
            R.drawable.lock_pick_g ,
            R.drawable.lock_pick_b ,
            R.drawable.lock_pick_key };
    // 록픽 관련 상수
    int LOCK_BITMAP_BACK1 = 0; // 메인 백그라운드 [블랙]
    int LOCK_BITMAP_BACK2 = 1; // 키 빈칸 백그라운드 [나무]
    int LOCK_BITMAP_BACK3 = 2; // 열쇠 구멍 [회색]
    int LOCK_BITMAP_PICK_G = 3; // 키가 들어갔을시 바뀌는 색 [ 골드 ]
    int LOCK_BITMAP_PICK_B = 4; // 기가 들어가지 않았을때 바뀌는색 [ 회색 ]
    int LOCK_BITMAP_KEY = 5;   // 스틱
    int LOCK_BITMAP_MAX = 6;
    int LOCK_MAX_KEY = 5;
    int LOCK_STICK = 5;



    int[]ARBOSS_RES = {
            R.mipmap.crossbow ,
            R.mipmap.crossbow01 ,
            R.mipmap.arraw,
            R.mipmap.fireball ,
            R.drawable.dragon01,
            R.drawable.dragon02,
            R.drawable.dragon03,
            R.drawable.dragon04,
            R.drawable.dragon05,
            R.drawable.dragon06,
            R.drawable.dragon_wp};

    // 화살 보스 넘버
    int BITMAP_CROSSBOW  = 0;
    int BITMAP_CROSSBOW_AIM  = 1;
    int BITMAP_ARROW  = 2;
    int BITMAP_FIREBALL = 3;
    int BITMAP_DRAGONWP = 4;

    // 엑3 보스 모드 상수
    int BOSS_ATTACK = 1;
    int BOSS_NORMAL = 0;
    int MAX_BOSS_ATTACK = 10;

    // 정글 보스 공격력
    int DMG_BOSS_JUNGLE = 200;

    //boss_vine 비트맵 ID
    int[] VINE_RES = {
            R.drawable.vine01,
            R.drawable.vine02 };
    int WEAK_RES = R.drawable.vine_weak;
    int EYE_RES = R.drawable.eye_of_horus;


    String BITMAP_LockKey = "LockKey";
    /*
    int USER_INIT = 0;
    int USERDICE = 1;
    int USERDICE_OK = 3;
    int USERMOVE = 4;
    int USERMOVE_OK = 5;

    //final int USER_SELECT_XY = 5;

    int MAX_MAP = 25;

    int MAP_SHOP = 0;
    int MAP_TRAP = 1;
    int MAP_MONEY = 2;
    int MAP_MONSTER = 3;
    int MAP_BOSS = 4;
    */
}