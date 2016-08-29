package user.test.ex.diceraider0;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Switch;

/**
 * Created by INC-B-17 on 2016-04-21.
 */
public class SettingDialog extends Dialog {

    ImageView closeBtn;
    Switch bgmSwitch, soundSwitch;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    boolean isBgmPlay;
    boolean isSoundPlay;

    SettingCallBack callBack;

    public SettingDialog(Context context, SettingCallBack callBack) {
        super(context);

        this.callBack = callBack;
        //타이틀 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_setting);
        setCancelable(false);

        closeBtn = (ImageView)findViewById(R.id.closeBtn);
        closeBtn.setOnClickListener(click);

        bgmSwitch = (Switch)findViewById(R.id.bgmSwitch);
        bgmSwitch.setOnClickListener(click);

        soundSwitch = (Switch)findViewById(R.id.soundSwitch);
        soundSwitch.setOnClickListener(click);


        //쉐어드프리퍼런스 객체 생성
        pref = context.getSharedPreferences("pref", context.MODE_PRIVATE);
        editor = pref.edit();

        //초기 레이아웃 세팅
        isBgmPlay = pref.getBoolean("isBgmPlay", true);
        isSoundPlay = pref.getBoolean("isSoundPlay", true);
        bgmSwitch.setChecked(isBgmPlay);
        soundSwitch.setChecked(isSoundPlay);


    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.closeBtn:
                    dismiss();
                    break;

                case R.id.bgmSwitch:
                    //bgm on/off 여부 저장
                    isBgmPlay = bgmSwitch.isChecked();
                    editor.putBoolean("isBgmPlay", isBgmPlay);
                    editor.commit();
                    callBack.soundSetting();
                    break;

                case R.id.soundSwitch:
                    isSoundPlay = soundSwitch.isChecked();
                    editor.putBoolean("isSoundPlay", isSoundPlay);
                    editor.commit();
                    break;
            }

        }
    };

    //다이얼로그 종료후에 사운드를 재설정 하기 위한 콜백메서드
    interface SettingCallBack {

        void soundSetting();

    }
}
