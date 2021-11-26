package com.example.teamproject2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.teamproject2.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Random;

// 대화방
public class EighthActivity extends AppCompatActivity {

    // 변수생성
    private FirebaseDatabase mDatabase;     // firebase에서 instance를 받아오기 위한 변수
    private DatabaseReference mReference;   // Database에 있는 Reference를 저장하기 위한 변수

    private ListView listView;      // 리스트뷰
    private EditText editText;      // 채팅에서 보낼 text
    private Button sendButton;      // text입력 후 보내기 위한 버튼

    private String userName;    // 자신의 이름

    private MessageAdapter adapter; // Adapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eighth);

        listView = (ListView) findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.editText);
        sendButton = (Button) findViewById(R.id.button);

        // 내 아이디와 상대 아이디를 가져와서 배열에 저장 후 sorting 후 채팅방생성
        // sortng -> 내 아이디 : id1 ,상대 : id2 , rname = id1 and id2
        //           내 아이디 : id2 ,상대 : id1 , rname = id1 and id2  두가지 경우 같은 방 생성
        Intent intent = getIntent();    // 인텐트를 받음
        String choiceId = intent.getStringExtra("choiceId");    // 상대 아이디
        String myId = intent.getStringExtra("myId");    // 내 아이디

        String sortArray[] = {myId, choiceId};  // 내 아이디, 상대 아이디를 배열에 저장
        Arrays.sort(sortArray);     // 배열 sorting

        final String rname = sortArray[0] + " and " + sortArray[1];     // 채팅방 생성

        userName = myId; // 내 아이디

        // MessageAdapter를 이용한 Adapter생성
        adapter = new MessageAdapter();;
        listView.setAdapter(adapter);   // 리스트뷰와 Adapter연결

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("message");
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().length() != 0){
                    ChatData chatData = new ChatData(userName, editText.getText().toString());
                    mReference.child(rname).push().setValue(chatData);  // firebase에 rname에 reference를 저장
                    editText.setText("");   // text창 초기화
                }
                // text창에 아무것도 입력하지않고 버튼을 눌렀을때
                else{
                    Toast.makeText(getApplicationContext(), "내용입력을 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mReference.child(rname).addChildEventListener(new ChildEventListener() {  // message는 child의 이벤트를 수신.
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatData chatData = dataSnapshot.getValue(ChatData.class);  // chatData를 가져옴
                adapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.dogimage), chatData.getUserName(),chatData.getMessage());  // adapter에 추가
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) { }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) { }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });


    }
}