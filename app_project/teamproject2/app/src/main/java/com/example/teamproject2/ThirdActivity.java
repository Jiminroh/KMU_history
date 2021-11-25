package com.example.teamproject2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
// 거주위치, 선택위치 선택창
public class ThirdActivity extends AppCompatActivity {

    // 변수 생성
    private Spinner findSize;   // 스피너 값을 저장 할 변수
    private String Sizeofpet;   // 스피너 값을 String으로 받아올 변수 생성
    Context context3 =this;   // 현재 액티비티는 ThirdActivity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        // 버튼 생성
        Button btn_fourth = (Button) findViewById(R.id.btn_fourth);
        Button btn_fifth = (Button) findViewById(R.id.btn_fifth);
        Button btn_seventh = (Button) findViewById(R.id.btn_seventh);
        Button btn_logout = (Button) findViewById(R.id.btn_logout);

        // 스피너 생성
        findSize = (Spinner) findViewById(R.id.findSize);

        // 변수를 받아오기 위한 인텐트 생성
        Intent intent = getIntent();
        final String preId = intent.getStringExtra("preId");    // MainActivity에서보낸 값(현재 접속중인 아이디)를 받아서 저장

        // 버튼 클릭 리스너 생성
        btn_fourth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FourthActivity.class);  // FourthActivity로 연결 할 인텐트 생성
                Sizeofpet=findSize.getSelectedItem().toString();    // 스피너 값을 String으로 변환 후 Sizeofpet에 저장
                intent.putExtra("sizeofpet",Sizeofpet);     // Sizeofpet의 값을 FourthActivity로 전달
                intent.putExtra("preId",preId);     // preId의 값을 FourthActivity로 전달
                startActivity(intent);      // 화면전환 및 인텐트 전달
            }
        });

        btn_fifth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), FifthActivity.class);   // FifthActivity로 연결 할 인텐트 생성
                intent.putExtra("preId",preId);     // preId의 값을 FifthActivity로 전달
                startActivity(intent);      // 화면전환 및 인텐트 전달
            }
        });

        btn_seventh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeventhActivity.class);     // SixthActivity로 연결 할 인텐트 생성
                startActivity(intent);      // 화면전환 및 인텐트 전달
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context3).setTitle("로그아웃").setMessage("로그아웃 하시겠습니까?").setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Intent i = new Intent(ThirdActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                    }
                }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                }).show();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}