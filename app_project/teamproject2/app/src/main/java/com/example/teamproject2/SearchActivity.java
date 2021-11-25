package com.example.teamproject2;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SearchActivity extends Activity implements View.OnClickListener{
    // SearchActivity 에 있는 모든 EditText 뷰 변수
    EditText searchName, searchStation, searchId, searchNamePw, searchStationPw;
    // 사용자가 입력란에 입력한 값 String 형 변수로 생성
    String tsearchName, tsearchStation, tsearchId, tsearchNamePw, tsearchStationPw;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mReference;

    String[] stationList;   // 지하철 역 리스트
    String[] nameList;      // 펫 이름 리스트
    String[] IdList;        // 회원 아이디 리스트
    String[] PwList;        // 회원 비밀번호 리스트

    int Usercount =0;       // 데이터베이스에 저장된 총 회원 수 = 초기값 0으로 설정
    int intcount =0;        // 각 리스트에 접근하기 위한 index 초기값 0으로 설정

    private boolean idFound = false;      // 아이디를 찾은 여부
    private boolean pwFound = false;      // 비밀번호를 찾은 여부

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchName = (EditText) findViewById(R.id.SearchName); // 아이디 찾기의 펫 이름 입력 란
        searchStation = (EditText) findViewById(R.id.SearchStation); // 아아디 찾기의 거주지(지하철역) 입력 란
        searchId = (EditText) findViewById(R.id.SearchId); // 비밀번호 찾기의 아이디 입력 란
        searchNamePw = (EditText) findViewById(R.id.SearchNamePw); // 비밀번호 찾기의 펫 이름 입력 란
        searchStationPw = (EditText) findViewById(R.id.SearchStationPw); // 비밀번호 찾기의 거주지(지하철역) 입력 란

        Button searchIdbtn = (Button) findViewById(R.id.SearchIdbtn);
        Button searchPwbtn = (Button) findViewById(R.id.SearchPwbtn);
        searchIdbtn.setOnClickListener(this);
        searchPwbtn.setOnClickListener(this);

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("user"); // 변경값을 확인할 child 이름
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabase = FirebaseDatabase.getInstance();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    Usercount++;
                }
                // 데이터 베이스에 저장된 회원 수 만큼의 리스트 생성
                nameList = new String[Usercount];
                stationList = new String[Usercount];
                IdList = new String[Usercount];
                PwList = new String[Usercount];
                // 데이터 베이스의 모든 Information에 해당하는 값들에 맞는 리스트에 각각 넣어주기
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    Information info = messageData.getValue(Information.class);
                    nameList[intcount]=info.getName();
                    stationList[intcount]=info.getStation();
                    IdList[intcount]=info.getId();
                    PwList[intcount]=info.getPw();
                    ++intcount;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    // 클릭 이벤트 버튼을 switch 값으로 받는다
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.SearchIdbtn: // 아이디 찾기 버튼 클릭 이벤트 처리
                tsearchName = searchName.getText().toString();
                tsearchStation = searchStation.getText().toString();

                // 펫 이름 입력란 or 지하철 역 입력란 을 빈칸으로 넣어줬을 경우
                if(tsearchName.length() == 0 || tsearchStation.length() == 0){
                    Toast.makeText(this, "빈칸 없이 모두 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 그렇지 않았다면
                else if(tsearchName.length() !=0 && tsearchStation.length()!=0){
                    idFound = false;

                    for(int i=0;i<IdList.length;i++){
                        // 해당 펫 이름 리스트와 지하철역 리스트에 저장되어 있는 값이 입력란과 같다면
                        if((nameList[i].equals(tsearchName)) && ((stationList[i]).equals(tsearchStation))){
                            idFound=true;  // 아이디 찾기 성공
                            // 배열에 저장되어 있는 index 순서는 같으므로 그대로 해당 인덱스에 쓰여진 아이디 출력
                            Toast.makeText(this, "아이디는 " + IdList[i] + " 입니다!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // 아이디를 못 찾았을 경우
                    if(idFound==false){
                        Toast.makeText(this, "입력한 정보가 존재하지 않습니다!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;

            case R.id.SearchPwbtn: // 비밀번호 찾기 버튼 클릭 이벤트 처리
                tsearchId = searchId.getText().toString();
                tsearchNamePw = searchNamePw.getText().toString();
                tsearchStationPw = searchStationPw.getText().toString();

                // 아이디 입력란 or 펫 이름 입력란 or 지하철역 을 빈칸으로 넣어줬을 경우
                if(tsearchId.length() == 0 || tsearchNamePw.length() == 0 || tsearchStationPw.length() == 0){
                    Toast.makeText(this, "빈칸 없이 모두 입력하세요!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 그렇지 않았다면
                else if(tsearchId.length() != 0 && tsearchNamePw.length() != 0 && tsearchStationPw.length() != 0){
                    pwFound = false;  // 비밀번호 찾은 여부 false 로 다시 초기화
                    for(int i=0;i<IdList.length;i++){
                        // 해당 아이디 리스트와 지하철역 리스트와 펫 이름 리스트에 저장되어 있는 값이 입력란과 같다면
                        if((IdList[i].equals(tsearchId)) && ((stationList[i]).equals(tsearchStationPw)) && ((nameList[i]).equals(tsearchNamePw))){
                            pwFound=true;  // 비밀번호 찾기 성공
                            Toast.makeText(this, "비밀번호는 " + PwList[i] + " 입니다!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // 비밀번호를 못 찾았을 경우
                    if(pwFound==false){
                        Toast.makeText(this, "입력한 정보가 존재하지 않습니다!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                break;
        }
    }
}