package user.test.ex.diceraider0;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import user.test.ex.db.DBRankInfo;

public class RankActivity extends AppCompatActivity {

    ListView rankListView;

    ArrayList<String> nameList;
    ArrayList<String> scoreList;

    //리스트 뷰의 텍스트뷰에 박힐 두 변수
    String name = "";
    String score = "";

    Context context;

    DBRankInfo rankInfo;

    RankDialog rankDialog;
    SoundManager sm;

    Bundle args;
    boolean reset;

    boolean isTouchedOnce, isTouchedTwice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);
        context = this;
        args = getIntent().getExtras();
        reset = args.getBoolean("reset", false);
        Log.i("MY", "랭킹 버튼 " + reset);
        rankInfo = new DBRankInfo(this);
        rankListView = (ListView) findViewById(R.id.rankList);
        if (reset) {
            rankDialog = new RankDialog(this, callback);
            rankDialog.show();
        } else {
            init();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        sm = new SoundManager(this);
        sm.playBGM(sm.BGM_RANK, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.stopBGM();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        System.gc();
    }

    Dialogcallbak callback = new Dialogcallbak() {
        @Override
        public void callback() {
            name = rankDialog.getName();
            score = args.getInt("roll", 1000) + "";
            init();
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && !reset){
            finish();
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        checkEnd();
        return false;
    }

    Handler handler = new Handler();
    Runnable backTimer = new Runnable() {
        @Override
        public void run() {
            isTouchedOnce = false;
            isTouchedTwice = false;
        }
    };


    public void init() {
        if (reset) {
            rankInfo.dbSet(name, score);
        }
        rankInfo.load_rank_all();

        //처음 세팅
        nameList = rankInfo.getNameList();

        scoreList = rankInfo.getScoreList();

        RankAdapter adapter = new RankAdapter(context, R.layout.rank_list_form, nameList, scoreList, rankListView);
        rankListView.setAdapter(adapter);

    }


    private void checkEnd() {

        if (isTouchedOnce) {

            // 터치 인증 //by LMH
            Toast.makeText(getApplicationContext(), "Touch one more to exit", Toast.LENGTH_SHORT).show();
            isTouchedTwice = true;
            isTouchedOnce = false;

        } else if (isTouchedTwice) {

            // 번들 담기 //by LMH
            Intent intent = new Intent(RankActivity.this, MainActivity.class);
            Bundle bundle = getIntent().getExtras();
            //bundle.putBoolean("reset", reset);

            Log.i("MY" , "3번터치 "+bundle.getBoolean("reset",false));
            intent.putExtras(bundle);

            setResult(RESULT_OK, intent);
            finish();
        } else if (reset) {

            //랭크로 들어간 경우 리셋
            isTouchedOnce = true;
            handler.postDelayed(backTimer, 3000);
            Toast.makeText(getApplicationContext(), "Touch twice to exit", Toast.LENGTH_SHORT).show();
        }
    }


}

class RankDialog extends Dialog {

    EditText nameEditText;
    Button confirmBtn;

    String editTextname;

    Dialogcallbak dialogcallbak;

    public RankDialog(Context context, final Dialogcallbak dialogcallbak) {
        super(context);
        this.dialogcallbak = dialogcallbak;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.dialog_rank);
        setCancelable(false);

        nameEditText = (EditText) findViewById(R.id.nameEditText);
        nameEditText.addTextChangedListener(new TextWatcher() {

            String prevStr = "";

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                prevStr = charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (nameEditText.getLineCount() > 1) {
                    nameEditText.setText(prevStr);
                    nameEditText.setSelection(nameEditText.length());
                }
            }
        });

        confirmBtn = (Button) findViewById(R.id.confirmBtn);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextname = nameEditText.getText().toString();

                if (editTextname.equals("")) {
                    //비어 있으면 Toast메시지 노출
                    Toast.makeText(getContext(), "이름을 입력해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    //비어 있지 않으면
                    dismiss();
                    dialogcallbak.callback();
                }
            }
        });
    }

    public String getName() {
        return editTextname;
    }

}


interface Dialogcallbak {

    void callback();

}


