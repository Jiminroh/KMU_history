package com.example.teamproject2;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// Adapter 생성
public class MsgAdapter extends BaseAdapter{

    // 변수 선언
    private FirebaseDatabase mDatabase;     // firebase에서 instance를 받아오기 위한 변수
    private DatabaseReference mReference;   // Database에 있는 Reference를 저장하기 위한 변수
    private Boolean check = false; // 상대방이 자신을 친구추가 했는지 여부를 저장하는 변수

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>() ;

    // ListViewAdapter의 생성자
    public MsgAdapter() {

    }


    // Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();


        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_msg, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView id = (TextView) convertView.findViewById(R.id.textView);
        TextView name = (TextView) convertView.findViewById(R.id.textView1);
        TextView size = (TextView) convertView.findViewById(R.id.textView2);
        TextView kinds = (TextView) convertView.findViewById(R.id.textView3);
        TextView station = (TextView) convertView.findViewById(R.id.textView4);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        final ListViewItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getIcon());
        id.setText(listViewItem.getId());
        name.setText(listViewItem.getName());
        size.setText(listViewItem.getSize());
        kinds.setText(listViewItem.getKinds());
        station.setText(listViewItem.getStation());

        // 대화하기 버튼을 누르면 메세지저장 및 대화페이지로 이동
        Button btn_msg = (Button) convertView.findViewById(R.id.btn_msg);
        btn_msg.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(final View v) {

                mDatabase = FirebaseDatabase.getInstance(); // firebase의 instance 저장
                mReference = mDatabase.getReference("friend").child(listViewItem.getId()); // 변경값을 확인할 child 이름
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (check == true){check = false;}  // 대화방을 들어갔다가 나오면 false로 다시 변경

                        // Adapter에 추가 기능
                        // child에 있는 값들을 for문을 돌며 순차적으로 messageData에 저장
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            Information info = messageData.getValue(Information.class);     // infomation를 이용해 값을 전달

                            // 현재 아이디와 firebase에 child("friend")의 id값들을 비교하여 상대방이 친구 추가를 했는지 비교, 했으면 true
                            if (((MainActivity) MainActivity.mContext).getIdStorage().equals(info.getId())){
                                check = true;
                                Intent intent = new Intent(v.getContext(),EighthActivity.class);  // EighthActivity로 연결 할 인텐트 생성
                                intent.putExtra("choiceId", listViewItem.getId());  // 상대아이디를 전달
                                intent.putExtra("myId", ((MainActivity) MainActivity.mContext).getIdStorage()); // 내 아이디를 전달
                                Toast.makeText(v.getContext(), listViewItem.getId()+"님과의 대화창입니다.", Toast.LENGTH_SHORT).show();
                                v.getContext().startActivity(intent);
                            }
                        }
                        // 상대방이 자신을 친구추가 하지 않은 경우
                        if (check == false){
                            Toast.makeText(v.getContext(), "상대방이 "+((MainActivity) MainActivity.mContext).getIdStorage()+"님을 친구추가 하지 않으셨습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가
    public void addItem(Drawable icon, String id, String name, String size, String kinds, String station) {
        ListViewItem item = new ListViewItem();

        item.setIcon(icon);
        item.setId(id);
        item.setName(name);
        item.setSize(size);
        item.setKinds(kinds);
        item.setStation(station);

        listViewItemList.add(item);
    }
}