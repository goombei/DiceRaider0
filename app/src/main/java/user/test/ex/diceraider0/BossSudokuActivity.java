package user.test.ex.diceraider0;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class BossSudokuActivity extends AppCompatActivity {

    SudokuAlgo algo;

    Button newSudoku , correct;
    int[] sudoku;
    Button[] sudokuBtn;
    Button[] dialogBtn;
    Dialog dialog; // 다이얼로그 표시
    //입력한 숫자값 RGB 색깔 android:textColor="#613d07" 진갈색


    //타이머 관련 변수
    TextView time_out;
    //임시 변수들: 스톱위치 진행상황을 저장할 변수
    int myLimitTime = 240;

    SoundManager sm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boss_sudoku);
        algo = new SudokuAlgo();

        sudokuBtn = new Button[algo.totalCell];
        dialogBtn = new Button[9];
        algo.makeSudoku(algo.level1);

        algo.printMap(algo.getSolutionMap());

        newSudoku = (Button)findViewById(R.id.newSudoku);
        correct = (Button)findViewById(R.id.correct);

        //타이머 텍스트뷰 검색
        time_out = (TextView)findViewById(R.id.time_out);

        correct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               /* boolean result = algo.checkValidAll();
                //algo.getComparResult();

                algo.makeOriginalMap(algo.getsudokuBlindMap());

                showSudoku();

                if (result) {
                    Toast.makeText(BossSudokuActivity.this, "정답111", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(BossSudokuActivity.this, "오답111", Toast.LENGTH_SHORT).show();
                //    algo.printOriginalMap();
                //    algo.printsudokuBlind();
                }
*/
                /*newSudoku.setVisibility(View.VISIBLE);
                correct.setVisibility(View.INVISIBLE);
                */
                finishBoss();
            }

        });

        newSudoku.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                algo.makeSudoku(algo.level1);
                showSudoku();


                //핸들러 호출
                handler.post(myTimer);

                newSudoku.setVisibility(View.INVISIBLE);
                correct.setVisibility(View.VISIBLE);

            }
        });

        for(int i = 0;i < sudokuBtn.length ;i++ )
            try {
                sudokuBtn[i] = (Button) findViewById(new R.id().getClass().getField("button" + (i + 1)).getInt(null));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }

        showSudoku();

        sm = new SoundManager(this);

        //3초 후 자동 종료

        if (getIntent().getBooleanExtra("boss1_thrupass", false)){
            handler.postDelayed(runnable, 3000);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (event.getAction()) {
            case KeyEvent.KEYCODE_BACK:
                break;
        }

        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        sm.playBGM(sm.BGM_FINALBOSS_SUDOKU, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sm.stopBGM();
    }

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
//            Toast.makeText(BossSudokuActivity.this, view.getId() - R.id.button1 + "", Toast.LENGTH_SHORT).show();
            dialog = new Dialog(BossSudokuActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setContentView(R.layout.dialog_sudoku);

            Log.i("MY", algo.getBlindNumber(view.getId() - R.id.button1) + "," + algo.getSolutionNumber(view.getId() - R.id.button1) );


            for ( int i = 0 ; i <dialogBtn.length;i++)
            {
                try{
                    dialogBtn[i] = (Button)dialog.findViewById(new R.id().getClass().getField("dialbutton" + (i + 1)).getInt(null));
                    dialogBtn[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            ((Button) view).setText(v.getId() - R.id.dialbutton1 + 1 + "");
                            ((Button)view).setTextColor(Color.parseColor("#9c0303"));  //입력한 값 색깔 진갈색으로 변경

                            algo.setDatasudokuBlind(view.getId() - R.id.button1, v.getId() - R.id.dialbutton1 + 1 );
                            dialog.dismiss();
                        }
                    });


                }catch (Exception e)
                {

                }
            }
            dialog.show();
        }
    };

    // 버튼에 글자가 없는경우 클릭2를 통해서 반응이 없도록 설정한다.
    View.OnClickListener click2 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        }
    };

    public void showSudoku ()
    {
        sudoku = algo.getsudokuBlind();
        for ( int i = 0 ; i < sudokuBtn.length;i++)
        {
            sudokuBtn[i].setTextSize(10);
            if (sudoku[i] != 0)
            {
                sudokuBtn[i].setText(sudoku[i] + "");
                // 버튼에 글자가 쓰여지면 클릭되도 아무 반응이 없도록 변경
                sudokuBtn[i].setOnClickListener(click2);

            }
            else
            {
                sudokuBtn[i].setText("");
                // 버튼에 글자가 쓰여지지 않으면 다이얼로그를 생성하도록 수정
                sudokuBtn[i].setOnClickListener(click);
            }
        }
    }

    private void finishBoss() {
        int rewardMoney;
        boolean result = algo.checkValidAll();

        if (result) {
//            Toast.makeText(BossSudokuActivity.this, "정답", Toast.LENGTH_SHORT).show();
            rewardMoney = 1000;

        } else {
//            Toast.makeText(BossSudokuActivity.this, "오답", Toast.LENGTH_SHORT).show();
            rewardMoney = 0;
            /*
            algo.printOriginalMap();
            algo.printsudokuBlind();
            */
        }

        Intent i = new Intent(BossSudokuActivity.this, MainActivity.class);
        // i.putExtra("rewardItem", rewardItem);
        i.putExtra("rewardMoney", rewardMoney);
        i.putExtra("StageClear", result);
        setResult(RESULT_OK, i);

        finish();
    }

    Handler handler = new Handler();

    //화면 갱신 핸들러
    Runnable myTimer =  new Runnable() {
        @Override
        public void run() {
            myLimitTime--;
            String result_time = String.format("%02d:%02d", myLimitTime/60, myLimitTime%60);
            time_out.setText(result_time);  //getTimeOut() 연산을 핸들러가 할 수 있는 최고속력으로 계속 반복


            handler.postDelayed(this, 1000);  //무한반복

            if(myLimitTime <= 0){  //제한 시간이 끝나면
                handler.removeCallbacks(this);  //핸들러 정지

//                boolean result = algo.checkValidAll();
//                //algo.getComparResult();
//
//                algo.makeOriginalMap(algo.getsudokuBlindMap());
//
//                showSudoku();

                finishBoss();
            }
        }
    };

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            //보상
            int rewardMoney = 0;
            String rewardItem = "";

            Intent i = new Intent(BossSudokuActivity.this, MainActivity.class);
            i.putExtra("rewardMoney", rewardMoney);
            i.putExtra("rewardItem", rewardItem);
            i.putExtra("StageClear", true);
            setResult(RESULT_OK, i);
            finish();
        }
    };
}
