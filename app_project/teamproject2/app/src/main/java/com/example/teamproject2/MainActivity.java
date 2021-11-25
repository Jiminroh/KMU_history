package com.example.teamproject2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static Context mContext;             // 현재 액티비티

    private FirebaseDatabase mDatabase;         // Firebase Database 사용
    private DatabaseReference mReference;       // Database 참조

    private EditText LoginId;       // 아이디 입력 텍스트 칸
    private EditText LoginPw;       // 비밀번호 입력 텍스트 칸

    private Boolean check;          // 회원가입을 했는지 여부를 알 수 있는 boolean 값

    private String IdStorage = new String(); // 현재 아이디 값

    public String getIdStorage() {  // 현재 아이디 값(String) 을 반환해주는 함수
        return IdStorage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        check = false; // check 초기값 false 로 설정

        mContext = this;

        LoginId = (EditText) findViewById(R.id.LoginId);     // 아이디 입력 Edit 텍스트 뷰
        LoginPw = (EditText) findViewById(R.id.LoginPw);     // 비밀번호 입력 Edit 텍스트 뷰

        TextView signup = (TextView) findViewById(R.id.signup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SecondActivity.class);
                startActivity(intent);

            }
        });

        // 아이디,비밀번호 찾기 텍스트뷰 클릭 이벤트 처리
        TextView search = (TextView) findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SecondActivity 로 이동
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);

            }
        });

        // 로그인 버튼 클릭 이벤트 처리
        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabase = FirebaseDatabase.getInstance();     // firebase의 instance 저장
                mReference = mDatabase.getReference("user");
                mReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String Id = LoginId.getText().toString(); // 아이디 텍스트 란에 쓴 String 값
                        String Pw = LoginPw.getText().toString(); // 비밀번호 텍스트 란에 쓴 String 값

                        IdStorage = Id;  // 현재 아이디 값을 IdStorage 값에 저장

                        // 데이터베이스를 돌면서 그 안에 저장된 Information 객체를 생성해주고
                        for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                            Information info = messageData.getValue(Information.class);
                            // 텍스트 란에 입력한 아이디,비밀번호 값이 존재하면
                            if (Id.equals(info.getId()) && Pw.equals(info.getPw())) {
                                check = true; // 회원여부 상태 true
                                Toast.makeText(getApplicationContext(), "Pet World에 오신것을 환영합니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(), ThirdActivity.class);
                                // 접속 중인 아이디 값을 "preId"라는 String key 값으로 전달
                                intent.putExtra("preId", info.getId());
                                LoginId.setText("");
                                LoginPw.setText("");
                                startActivity(intent); // ThirdActivity 로 이동
                            }
                        }
                        if (check == false) { // 회원여부가 false 일 때
                            Toast.makeText(getApplicationContext(), "아이디 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}