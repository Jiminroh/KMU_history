package com.example.teamproject2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

public class SixthActivity extends AppCompatActivity {

    private FirebaseDatabase mDatabase; // 데이터베이스에 접근.
    private DatabaseReference mReference; // 데이터베이스의 주소 저장.

    private ListViewAdapter adapter = new ListViewAdapter(); // 적용할 Adapter.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sixth);

        mDatabase = FirebaseDatabase.getInstance(); // 현재 데이터 베이스를 접근할 수 있는 진입점 받기.
        mReference = mDatabase.getReference("user"); // 'user' child로 이동.

        // 경로의 전체 내용을 읽고 변경사항을 수신 대기.
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            // 이벤트 발생 시점에 특정 경로에 있던 콘텐츠의 정적 스냅샷 읽음.
            public void onDataChange(DataSnapshot dataSnapshot) {
                // listview를 Adapter에 적용.
                ListView listview;
                listview = (ListView) findViewById(R.id.listKinds6);
                listview.setAdapter(adapter);

                // 선택된 지하철역 값과 접속중인 ID값을 String으로 받음.
                Intent intent = getIntent();
                String subwayselection="";
                subwayselection = intent.getStringExtra("subwayselection");
                String preId = "";
                preId = intent.getStringExtra("preId");

                // 데이터 읽어오기.
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    Information info = messageData.getValue(Information.class);
                    // 선택된 지하철역 값과 지하철 리스트의 값이 동일하고 접속중인 ID 값와 가져온 User ID 값 다를 시,
                    // Adapter에 해당 User 프로필 add.
                    if((subwayselection+"역").equals(info.getStation()) && !preId.equals(info.getId())){
                        adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dogimage),
                                info.getId(),info.getName(), info.getSize(), info.getKinds(), info.getStation());
                    }
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