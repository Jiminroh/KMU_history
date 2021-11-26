package com.example.teamproject2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
// 거주위치 리스트
public class FourthActivity extends AppCompatActivity{

    // 변수 선언
    private FirebaseDatabase mDatabase;     // firebase에서 instance를 받아오기 위한 변수
    private DatabaseReference mReference;       // Database에 있는 Reference를 저장하기 위한 변수

    // Adapter 선언
    private ListViewAdapter adapter = new ListViewAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth);

        mDatabase = FirebaseDatabase.getInstance();     // firebase의 instance 저장
        mReference = mDatabase.getReference("user");    // 변경값을 확인할 child 이름
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final ListView listview;    // 리스트뷰 선언

                listview = (ListView) findViewById(R.id.listKinds4);    // 리스트뷰 ID
                listview.setAdapter(adapter);   // 리스트뷰와 Adapter연결

                // 변수를 받아오기 위한 인텐트 생성
                Intent intent = getIntent();
                String preId = "";      // 현재 아이디를 저장할 변수
                preId = intent.getStringExtra("preId");     // 인텐트로 받아온 뒤 변수에 저장
                String sizeofpet="";    // 스피너값을 저장할 변수
                sizeofpet = intent.getStringExtra("sizeofpet");     // 인텐트로 받아온 뒤 변수에 저장


                // Adapter에 추가 기능
                // child에 있는 값들을 for문을 돌며 순차적으로 messageData에 저장
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    final Information info = messageData.getValue(Information.class);   // infomation를 이용해 값을 전달
                    // 종류(대형견,중형견...)가 같고 가져온 아이디가 현재 접속중인 아이디와 다르면 실행
                    if(sizeofpet.equals(info.getSize()) && !preId.equals(info.getId())){
                        adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dogimage), // Adapter에 아이템 추가
                                info.getId(),info.getName(), info.getSize(), info.getKinds(), info.getStation());
                    }
                }
                adapter.notifyDataSetChanged(); // Adapter reseting
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}