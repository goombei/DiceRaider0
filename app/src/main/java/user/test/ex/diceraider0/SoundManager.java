package user.test.ex.diceraider0;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by INC-B-17 on 2016-04-19.
 */
public class SoundManager {

    //BGM, Sound on/off 정보를 가지고 있는 변수
    private boolean isBGMPlay = true;
    private boolean isSoundPlay = true;

    //테스트용 음악(BGM) default 반복
    public final int BGM_TEST = R.raw.test3;
    public final int BGM_TEST2 = R.raw.test2;
    public final int BGM_TEST3 = R.raw.test3;
    public final int BGM_TEST4 = R.raw.test4;

    public final int BGM_START = 0;
    public final int[] BGM_MAIN_ACTS = {
            R.raw.act1,
            R.raw.act2,
            R.raw.act3};
    //상점 음악
    public final int BGM_SHOP = R.raw.shop;
    //인벤토리 음악
    public final int BGM_INVENTORY = R.raw.inventory;
    //1st보스 음악
    public final int BGM_FIRSTBOSS = R.raw.first_boss;
    //2nd보스 음악
    public final int BGM_SECONDBOSS = R.raw.second_boss;
    //3nd(스도쿠)보스 음악
    public final int BGM_FINALBOSS_SUDOKU = R.raw.sudoku;
    //key딸 때 bgm
    public final int BGM_KEY = R.raw.key;
    //랭킹 음악
    public final int BGM_RANK = R.raw.rank;


    //일반 사운드 default 1회
    //돈 차감되는 소리로 사용 예정
    public final int SOUND_COIN = R.raw.coin;
    //주사위 소리 타입 2가지
    public final int SOUND_DICE1 = R.raw.dice01;
    public final int SOUND_DICE2 = R.raw.dice02;
    //아이템 착용할 때 소리
    public final int SOUND_SET_ITEM = R.raw.set_item;
    //열쇠 찰칵 소리
    public final int SOUND_LOCK = R.raw.lock;
    //문 열고 닫는 소리
    public final int SOUND_DOOR_OPEN = R.raw.door_open;
    public final int SOUND_DOOR_CLOSE = R.raw.door_close;
    //주인공이 데미지를 입을 때마다 나는 소리
    public final int SOUND_DAMAGED_HERO = R.raw.damaged_hero;
    //몬스터소리(거미, 박쥐, 뱀, 군인, 미친새끼, 원주민, 미라)
    public final int SOUND_MONSTER_SPIDER = R.raw.monster_spider;
    public final int SOUND_MONSTER_BAT = R.raw.monster_bat;
    public final int SOUND_MONSTER_SNAKE = R.raw.monster_snake;
    public final int SOUND_MONSTER_SOLDIER = R.raw.monster_soldier;
    public final int SOUND_MONSTER_CRAZY = R.raw.monster_crazy;
    public final int SOUND_MONSTER_MUMMY = R.raw.monster_mummy;
    public final int SOUND_MONSTER_NATIVE = R.raw.monster_native;


    Context context;
    SoundPool soundPool;
    MediaPlayer mediaPlayer;

    int sound;

    public SoundManager(Context context) {
        this.context = context;

        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        isBGMPlay = pref.getBoolean("isBgmPlay", true);
        isSoundPlay = pref.getBoolean("isSoundPlay", true);
    }

    //이게 안되는 이유가 있었다 긴 음악은 재생이 안된다 MediaPlayer를 사용해야 된다.
    //찾아보니까 누구는 6초이상 누구는 10초이상의 소리 재생이 안된다는 소리가 있다.


    public void initSoundPool() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
    }

    public void playSound(int sound) {
        if (isSoundPlay) {
            sound = soundPool.load(context, sound, 1);
            soundPool.play(sound, 1, 1, 0, 0, 1);
        }
    }

    public void stopSound() {
        if (isSoundPlay) {
            soundPool.stop(sound);
        }
    }

    public void destroySoundId() {
        soundPool.release();
    }

    public void playBGM(int bgm, boolean repeat) {
        //bgm off 상태로 앱에 접속할 경우 Main에서 생성자를 타지 않기 때문에 다시 호출해야 된다.
        SharedPreferences pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        isBGMPlay = pref.getBoolean("isBgmPlay", true);
        if (isBGMPlay) {
            mediaPlayer = MediaPlayer.create(context, bgm);
            mediaPlayer.setLooping(repeat);
            mediaPlayer.start();
//        mediaPlayer.stop();
//        mediaPlayer.reset();
        }

    }

    public void stopBGM() {
        if (isBGMPlay && mediaPlayer.isPlaying())
            mediaPlayer.stop();
    }

}
