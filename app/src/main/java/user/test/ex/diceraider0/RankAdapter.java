package user.test.ex.diceraider0;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by INC-B-17 on 2016-04-22.
 */
public class RankAdapter extends ArrayAdapter<String> {

    Context context;
    ArrayList<String> nameList, scoreList;
    int listViewLayout;
    ListView listView;

    public RankAdapter(Context context, int listViewLayout, ArrayList<String> nameList, ArrayList<String> scoreList, ListView listview) {
        super(context, listViewLayout, nameList);

        this.context = context;
        this.listViewLayout = listViewLayout;
        this.nameList = nameList;
        this.scoreList = scoreList;
        this.listView = listview;

    }

    //myList.setAdapter(adapter);가 호출될 떄 getView가 호출되며 arr의 사이즈 만큼 호출 된다.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //LayoutInflater를 통해서 리스트 뷰 정보 얻어오기
        LayoutInflater lif = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = lif.inflate(listViewLayout, null);

        //이름 세팅
        TextView nameText = (TextView)convertView.findViewById(R.id.nameText);
        nameText.setText(nameList.get(position));

        //스코어 세팅
        TextView scoreText = (TextView)convertView.findViewById(R.id.scoreText);
        scoreText.setText(scoreList.get(position));

        notifyDataSetChanged();

        return convertView;
    }

}

