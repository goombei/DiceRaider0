package user.test.ex.diceraider0;

import android.util.Log;

/**
 * Created by Raphael on 2016-04-16.
 */
public class SudokuAlgo
{

    // 난이도에 따른 blind Cell 블록 수 설정
    private	static int [] levelBlindCount = new int [] {30,50,80};

    public final int	level1 = 0;
    public final int	level2 = 1;
    public final int	level3 = 2;


    // 해법을 위한 Queue 크기 설정
    private static int	queSize = 200;

    // 블록 크기 설정
    public final static int	widthCell = 3, heightCell = 3, block = 3;

    public final int	totalWidthCell = widthCell*block,
            totalHeightCell = heightCell*block,
            totalCell = widthCell*heightCell*block*block,
            blockCell = widthCell*heightCell;

    // 맵
    public int [] sudokuSol = new int[totalCell];	// 수도쿠 정답
    public int [] sudokuBlind = new int[totalCell];	// 수도쿠 문제
    public boolean[] sudokuWrite = new boolean[totalCell]; // 적었는지 체크
    int maxIndex = 0;

    // 해법을 위한 Queue Data 클래스
    class	QueData
    {
        int	index;
        int	number;
    }

    // 클래스 생성자
    public SudokuAlgo()
    {
        initMap();
    }

    // 맵 초기화
    public void	initMap()
    {
        int	i = 0;

        for( i = 0 ; i < totalCell ; i++ )
        {
            sudokuSol[i] = 0;
            sudokuBlind[i] = 0;
            sudokuWrite[i] = false;
        }
    }


    // 배열에 등록된 수를 하나씩 가져오기 위함.
    public int getBlindNumber( int index )
    {
        return sudokuBlind[index];
    }


    // 결과 비교 리턴 Boolean
    public boolean getComparResult()
    {
        for (int i = 0; i < totalCell; i ++)
        {
            if(sudokuSol[i] != sudokuBlind[i])
                return false;
        }

        return true;
    }


    // 특정 인덱스의 정답 값 보기
    public int	getSolutionNumber( int index )
    {
        return	sudokuSol[index];
    }


    // 난이도 버튼에 넣을때 쓸 것. 0이 쉬움, 1이 보통, 2가 어려움.
    public void setDifficult(int diff)
    {
        sudokuBlind(diff);
    }


    // 맵을 출력한다. (확인 용으로 사용.)
    public void printMap( int [] map )
    {
        int	x = 0, y = 0;
        String str ="";
        Log.i("MY", "+----------------------+\n");
        for( y = 0 ; y < totalHeightCell ; y++ )
        {
            str = "|";
            for( x = 0 ; x < totalWidthCell ; x++ )
            {

                //System.out.printf("%d ", map[x + y * totalHeightCell]);
                str += map[x+y*totalHeightCell];
                if( ((x+1)%heightCell) == 0 ) {
                    str += "| ";
                }else
                {
                    str += " ,";
                }
            }

            Log.i("MY",str);

            if( ((y+1)%heightCell) == 0 )
                Log.i("MY" , "+----------------------+\n" );
        }
    }


    // Original Map 출력
    public void	printOriginalMap()
    {
        printMap(sudokuSol);
    }

    // Blind Map 출력
    public void	printsudokuBlind()
    {
        printMap(sudokuBlind);
    }


    // 오류 검사 ( 3*3 검사 , 가로검사 , 세로검사 )
    private boolean	checkValidNumber( int [] sudoku, int chkIdx, int chkNum )
    {
        int	index = 0, offset = 0;
        int	i = 0;
        int	step = 0;

        boolean	[] seqNumFlag = new boolean[9];
        final int CASE_3by3 = 0;
        final int CASE_HORIZONTAL = 1;
        final int CASE_VERTICAL = 2;

        if (chkNum == 0) return false;
        // 맵에 검사할 숫자를 대입
        sudoku[chkIdx] = chkNum;



        // 3단계에 걸처 검사를 한다.
        for( step = 0 ; step < 3 ; step++ )
        {
            // 체크할 배열 초기화
            for( i = 0 ; i < blockCell ; i++ )
                seqNumFlag[i] = false;


            // Offset 계산(속도 향상을  위해)
            switch (step)
            {
                case CASE_3by3 : // 3 x 3 박스 검사
                    offset = (chkIdx /(totalWidthCell*heightCell))*(totalWidthCell*heightCell)
                            + ((chkIdx%totalWidthCell)/widthCell)*widthCell;
                    break;
                case CASE_HORIZONTAL : // 가로줄 검사
                    offset = (chkIdx / totalWidthCell)*totalWidthCell;
                    break;
                case CASE_VERTICAL : // 세로줄 검사
                    offset = (chkIdx % totalHeightCell);
                    break;
            }

            for( i = 0 ; i < 9 ; i++ )
            {
                // index 값 계산
                switch (step)
                {
                    case CASE_3by3 : // 3 x 3 박스 검사
                        index = offset + (i%widthCell) + (i/widthCell)*totalWidthCell;
                        break;
                    case CASE_HORIZONTAL : // 가로줄 검사
                        index = offset + i;
                        break;
                    case CASE_VERTICAL : // 세로줄 검사
                        index = offset + i*totalWidthCell;
                        break;
                }


                // 검사할 칸이 0인경우 ( 데이터가 없는경우 통과 )
                if( sudoku[index] == 0 )
                    continue;

                // SeqNumFlag 배열에 중복됬는지 해당 숫자를 체크한다.
                if( seqNumFlag[sudoku[index]-1] == false )
                    seqNumFlag[sudoku[index]-1] = true;
                else
                    break;
            }

            if( i < 9 )
                return	false;
        }

        return	true;
    }

    // 오류 검사 3단계를 수행하여 입력한 숫자를 검사
    public boolean	checkValidNumber( int index, int number )
    {
        return	checkValidNumber( sudokuBlind, index, number );
    }

    // 정상검사 확인
    public boolean checkValidAll ()
    {
        boolean retdata = false;
        for ( int i = 0 ; i < totalCell ; i++)
        {
            retdata = checkValidNumber(sudokuBlind, i, getBlindNumber(i) );
            if (!retdata)
                return retdata;
        }
        return retdata;
    }


    // 수도쿠 생성 알고리즘
    public boolean	makeOriginalMap(int [] makeMap)
    {
        int	index = 0;
        int	ranNum = 0;
        int delIndex = 0;
        int	[] numArray = new int[blockCell];

        // 해당 위치에 맞는 값을 찾기위해 큐 버퍼를 사용
        QueData [] sudokuQueData = new QueData [queSize];

        int	head = 0;
        int	i = 0;
        int	totalRunCount = 0;
        int	data = 0;

        // 큐 버퍼 할당
        for( i = 0 ; i < queSize ; i++ )
            sudokuQueData[i] = new QueData();

        do
        {

            if (makeMap[index] != 0 )
            {
                index++;
                continue;
            }


            // 넘버 어레이 테이블 설정
            for( i = 1 ; i <= blockCell ; i++ )
                numArray[i-1] = i;

            // Push Index
            for( i = 1 ; i <= blockCell ; i++ )
            {
                // 1~9 중 랜덤으로 중복을 막기 위해
                // Table 을 사용하여 9번 랜덤을 실행하면 항상 1~9가 나오도록 함.
                ranNum = (int)(Math.random() * (blockCell+1-i));
                data = numArray[ranNum];
                numArray[ranNum] = numArray[blockCell-i];


                // 숫자가 오류나지 않는다면 Queue 에 Push 한다.
                if( checkValidNumber(makeMap, index, data) == true )
                {
                    // 큐에 버퍼를 저장한다.
                    sudokuQueData[head].index = index;
                    sudokuQueData[head].number = data;

                    // 버퍼의 갯수가 한정되어 있어 증가를 한뒤 큐 사이즈만큼 mod 처리
                    head++;
                    head %= queSize;
                }
            }
            // 현재 인덱스를 저장
            delIndex = index;
            sudokuWrite[index] = true;
            // 인덱스 가져오기 ( 저장된 마지막 인덱스값을 가져온다. )
            // 0지점에서 가져올경우 -1이 되므로 최대값으로 변경한다.
            head--;
            if (head<0) head =queSize-1;

            // 현재 넣을 인덱스와 데이터를 가져온다.
            index = sudokuQueData[head].index;
            makeMap[index] = sudokuQueData[head].number;

            // 인덱스 증가
            index++;
            // 오류검사이후 스택이 안쌓이게되면 이전 데이터를 가져와야 한다.
            // 이전데이터를 가져올경우 뒤에 씌여진 데이터에 의해 오류검사 연산에 문제가 생길 수 있으므로
            // 현재까지의 데이터와 옮겨질 데이터 사이의 자료를 전부 지운다.
            for ( i = index;i <= delIndex;i++)
            {
                if (sudokuWrite[i])
                {
                    makeMap[i] = 0;
                    sudokuWrite[i] = false;
                }
            }

            totalRunCount++;
        }while( index < totalCell );

        return	true;
    }



    // 문제 출제하기 위해 Level 에 따라 맵의  숫자를 감춤
    public void	sudokuBlind( int level )
    {
        int	i = 0;
        int	leftBlind = levelBlindCount[level];

        assert( leftBlind > 0 );

        System.arraycopy(sudokuSol, 0, sudokuBlind, 0, totalCell );

        while( leftBlind-- > 0 )
        {
            i = (int)(Math.random() * totalCell);

            if( sudokuBlind[i] == 0 )
            {
                leftBlind++;
                continue;
            }

            sudokuBlind[i] = 0;
        }
    }

    // 해답 맵 데이터 얻기
    public int [] getSolutionMap()
    {
        int [] map = sudokuSol.clone();

        return	map;
    }

    // blind 맵 데이터 얻기
    public int [] getsudokuBlind()
    {
        int [] map = sudokuBlind.clone();

        return	map;
    }


    public int [] getsudokuBlindMap()
    {
        return	sudokuBlind;
    }

    // blind 맵에 숫자 입력하기
    public boolean	setDatasudokuBlind( int index, int number )
    {
        /*if( sudokuBlind[index] != 0 )
            return	false;*/

        sudokuBlind[index] = number;

        return	true;
    }


    // 자동으로 문제 생성 및 Blind
    public void	makeSudoku( int level )
    {
        // 맵 초기화
        initMap();

        // 맵 생성
        makeOriginalMap(sudokuSol);

        // Blind 맵 생성(3번째 인자는 난이도)
        sudokuBlind(level );

        // 표시
        printOriginalMap();
    }

}
