package com.example.teamproject2;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

// Adapter 생성
public class ListViewAdapter extends BaseAdapter {

    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListViewItem> listViewItemList = new ArrayList<ListViewItem>();

    // ListViewAdapter의 생성자
    public ListViewAdapter() {

    }


    // Adapter에 사용되는 데이터의 개수를 리턴
    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
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

        // 버튼클릭시 클릭한 리스트에 있던 데이터들을 firebase에 저장
        Button btn_friend = (Button) convertView.findViewById(R.id.btn_friend);
        btn_friend.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "친구 목록에 추가되었습니다.", Toast.LENGTH_LONG).show();
                FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
                DatabaseReference mReference = mDatabase.getReference("friend");
                DatabaseReference user = mReference.child(((MainActivity) MainActivity.mContext).getIdStorage()).child(listViewItem.getId());

                user.child("id").setValue(listViewItem.getId());
                user.child("name").setValue(listViewItem.getName());
                user.child("size").setValue(listViewItem.getSize());
                user.child("kinds").setValue(listViewItem.getKinds());
                user.child("station").setValue(listViewItem.getStation());
            }
        });

        return convertView;
    }


    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position);
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
