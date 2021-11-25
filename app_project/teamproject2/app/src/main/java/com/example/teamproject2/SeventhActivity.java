package com.example.teamproject2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.teamproject2.Information;
import com.example.teamproject2.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.teamproject2.Information;
import com.example.teamproject2.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SeventhActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase; // 데이터 베이스에 접근.
    private DatabaseReference mReference; // 데이터 베이스의 주소 저장.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seventh);

        Toast.makeText(this,"서로 친구 신청이 된 상태에서 대화를 할 수 있습니다.",Toast.LENGTH_SHORT).show();

        // 현재 데이터 베이스를 접근할 수 있는 진입점 받기.
        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getInstance().getReference();

        // 파이어베이스에 프로젝트명/friend/User ID 형식으로 저장
        DatabaseReference downloadsRef = mReference.child("friend").child(((MainActivity)MainActivity.mContext).getIdStorage());

        // 경로의 전체 내용을 읽고 변경사항을 수신 대기.
        downloadsRef.addValueEventListener(new ValueEventListener() {
            @Override
            // 이벤트 발생 시점에 특정 경로에 있던 콘텐츠의 정적 스냅샷 읽음.
            public void onDataChange(DataSnapshot dataSnapshot) {

                // listview를 Adapter에 적용.
                final ListView listview;
                final MsgAdapter adapter;
                adapter = new MsgAdapter();
                listview = (ListView) findViewById(R.id.listKinds7);
                listview.setAdapter(adapter);

                // 데이터 읽어오기.
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    Information info = messageData.getValue(Information.class);
                    //Adapter에 item 추가
                    adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dogimage),
                            info.getId(),info.getName(), info.getSize(), info.getKinds(), info.getStation());
                }
                // listview 갱신.
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}