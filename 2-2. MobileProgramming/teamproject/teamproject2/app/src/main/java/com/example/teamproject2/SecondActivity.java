package com.example.teamproject2;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teamproject2.Information;
import com.example.teamproject2.MainActivity;
import com.example.teamproject2.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

public class SecondActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;     // 이미지 요청코드 값을 1로 설정

    private FirebaseDatabase mDatabase;     // Firebase Database 사용
    private DatabaseReference mReference;   // Database 참조

    private EditText mEditId;           // 아이디 입력란
    private EditText mEditPw ;          // 비밀번호 입력란
    private EditText mEditPwCf;         // 비밀번호 확인 입력란
    private EditText mEditPetName;      // 펫 이름 입력란
    private EditText mEditKinds;        // 종 이름 입력란
    private Spinner mEditSize;          // 펫 사이즈 선택 Spinner
    private EditText mEditStation;      // 거주지(지하철역) 입력란

    private boolean imageCheck=false;   // 사진을 첨부했는지 안했는지 체크 여부
    private boolean idcheck = false;    // 아이디 중복검사 버튼을 눌렀는지 안눌렀는지 여부
    private boolean idcheckagain=false; // 아이디 중복검사를 안눌렀는데 회원가입을 눌렀을 때, id 중복검사 체크 여부

    private CheckBox inform_check;  //  이용약관 및 정보제공 체크박스

    String result_1, result_2;
    int Usercount =0;       // 데이터베이스에 저장된 총 회원 수 = 초기값 0으로 설정
    int userindex =0;       // 아이디 중복검사를 위한 index
    int intCount = 0;       // 지하철역을 검사하기 위한 index

    String[] subway;    // 지하철역을 저장하는 String 배열
    String[] idList;    // id 리스트를 저장하는 String 배열

    InputStream inputStreamCounter;
    BufferedReader bufferedReaderCounter;

    InputStream inputStreamLoader;
    BufferedReader bufferedReaderLoader;

    private final String closePopup_1 = "Close Popup_1"; // 이용약관 팝업
    private final String closePopup_2 = "Close Popup_2"; // 정보제공 팝업

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // 사진 선택 버튼
        Button btn_UploadPicture = (Button) findViewById(R.id.btn_UploadPicture);

        // subway 라는 txt 파일에서 지하철역 읽어오기
        inputStreamCounter = this.getResources().openRawResource(R.raw.subway);
        bufferedReaderCounter = new BufferedReader(new InputStreamReader(inputStreamCounter));

        inputStreamLoader = this.getResources().openRawResource(R.raw.subway);
        bufferedReaderLoader = new BufferedReader(new InputStreamReader(inputStreamLoader));

        try {
            while(bufferedReaderCounter.readLine()!=null){
                intCount++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // subway.txt 파일에서 읽어 온 모든 지하철역을 subway 배열에 저장
        subway = new String[intCount];

        try{
            for(int i=0 ; i<intCount ; i++){
                subway[i] = bufferedReaderLoader.readLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        mDatabase = FirebaseDatabase.getInstance();
        mReference = mDatabase.getReference("user"); // 변경값을 확인할 child 이름
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDatabase = FirebaseDatabase.getInstance();
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    Information info = messageData.getValue(Information.class);
                    Usercount++; // 총 회원 수 count
                }
                idList = new String[Usercount];  // 회원들의 id 리스트 생성
                for (DataSnapshot messageData : dataSnapshot.getChildren()) {
                    Information info = messageData.getValue(Information.class);
                    idList[userindex++]=info.getId();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // 이용약관 보기 버튼 클릭 이벤트 처리
        Button watch_btn1 = findViewById(R.id.watch_btn1);
        watch_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PopupActivity_1.class);
                startActivityForResult(intent, 10); // requestCode 10
            }
        });

        // 개인정보 정책 버튼 클릭 이벤트 처리
        Button watch_btn2 = findViewById(R.id.watch_btn2);
        watch_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PopupActivity_2.class);
                startActivityForResult(intent, 20); // requestCode 20
            }
        });

        // 이용약관 및 정보 동의 체크박스 선택 시 이벤트 처리
        CheckBox inform_check = findViewById(R.id.inform_check);
        inform_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox inform_check = findViewById(R.id.inform_check); // 이용약관 및 정보 동의
                if(result_1 == null || result_2 == null){ // 내용 미 확인 후 동의 체크 시
                    inform_check.setChecked(false);
                    Toast.makeText(getApplicationContext(), "이용약관 및 개인정보정책 내용을 \n확인해주세요!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 사진 선택 클릭 시 이벤트 처리
        btn_UploadPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");  // 이미지만을 보여준다(그 외는 x)
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // 항상 사진을 선택할 수 있게 보여준다 ()
                // 항상 선택 도구 표시(여러 옵션을 사용할 수 있는 경우)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        // 중복검사 버튼 클릭 이벤트 처리
        Button idcheck_button = findViewById(R.id.idcheck_button);
        idcheck_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idcheck=true;   // 아이디 중복검사 버튼을 한번이라도 눌렀으면 idcheck 값 true로 변경
                mEditId = (EditText) findViewById(R.id.InputId);
                String tid = mEditId.getText().toString();
                if(tid.length() == 0){
                    idcheckagain=false;      // 조건에 맞지 않은 아이디 입력을 방지하기 위한 처리
                    Toast.makeText(getApplicationContext(), "아이디를 제대로 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                // 데이터베이스에 아직 회원가입한 사람이 하나도 없을 때
                else if(idList.length == 0){
                    idcheckagain=true;
                    Toast.makeText(getApplicationContext(), "사용 가능한 아이디입니다!", Toast.LENGTH_SHORT).show();
                }
                else{
                    for(int i=0;i<idList.length;i++){
                        if(idList[i].equals(tid)){
                            idcheckagain=false; // 조건에 맞지 않은 아이디 입력을 방지하기 위한 처리
                            Toast.makeText(getApplicationContext(), "존재하는 아이디입니다!", Toast.LENGTH_SHORT).show();
                            break;
                        }
                        Toast.makeText(getApplicationContext(), "사용 가능한 아이디입니다!", Toast.LENGTH_SHORT).show();
                        idcheckagain=true;  // 아이디 중복검사 통과
                    }
                }
            }});

        // 회원 가입 버튼 클릭 시 이벤트 처리
        Button JoinBtn = (Button) findViewById(R.id.Joinbtn);
        JoinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values;
                mEditId = (EditText) findViewById(R.id.InputId);
                mEditPw = (EditText) findViewById(R.id.InputPw);
                mEditPwCf = (EditText) findViewById(R.id.InputConfirmPw);
                mEditPetName = (EditText) findViewById(R.id.InputName);
                mEditSize = (Spinner) findViewById(R.id.InputSize);
                mEditKinds = (EditText) findViewById(R.id.InputKinds);
                mEditStation = (EditText) findViewById(R.id.InputSubway);

                // 해당 입력 란에 쓴 모든 text 값들을 String 형으로 가져온다.
                String tid = mEditId.getText().toString();
                String tpw = mEditPw.getText().toString();
                String tpwcf = mEditPwCf.getText().toString();
                String tname = mEditPetName.getText().toString();
                String tkinds = mEditKinds.getText().toString();
                String tsize = mEditSize.getSelectedItem().toString();
                String tstation = mEditStation.getText().toString();

                // 비밀번호 조건은 8자 이상의 숫자와 영문, 특수문자 조합
                boolean pwCheck = Pattern.matches("^(?=.*\\d)(?=.*[~`!@#$%^&*()-])(?=.*[a-zA-Z]).{6,16}$", tpw); // 특수문자 포함 8자이상인지,
                boolean stationcheck = false;  // 지하철역 입력 조건 체크

                // 사용자가 지하철역 입력을 '00역'으로 입력할 수 있도록 하는 조건
                if(tstation.trim().length()!=0){
                    for(int i=0;i<subway.length;i++){
                        if(tstation.trim().equals(subway[i]+"역")) {
                            stationcheck = true;
                            break;
                        }
                    }
                    if(stationcheck == false) {
                        Toast.makeText(getApplicationContext(), "00역 입력조건으로 지켜주세요.", Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                // 아이디, 비밀번호, 비밀번호 확인이 제대로 이루어졌는지 체크하는 조건
                if(tid.trim().length() == 0 || tpw.trim().length() == 0 || tpwcf.trim().length() == 0 || tpw.trim().length() == 0 || tname.trim().length() == 0 || tkinds.trim().length() == 0 || tstation.trim().length()==0 ||imageCheck==false){
                    if((pwCheck==false && tpw.equals(tpwcf)==false)|| pwCheck==false && tpw.equals(tpwcf)==true){
                        Toast.makeText(getApplicationContext(),"8자 이상의 숫자와 영문,특수문자 조합을 지켜주세요.",Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(pwCheck==true && tpw.equals(tpwcf)==false){
                        Toast.makeText(getApplicationContext(),"비밀번호가 일치한지 제대로 확인해 주세요.",Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(pwCheck == true && tpw.equals(tpwcf)==true)
                        Toast.makeText(getApplicationContext(),"모든항목들을 조건에 맞춰서 모두 입력해주세요.",Toast.LENGTH_LONG).show();
                    return;
                }
                CheckBox inform_check = findViewById(R.id.inform_check); // 이용약관 및 정보 동의
                if(tid.trim().length() != 0 && tpw.trim().length() != 0 && tpwcf.trim().length() != 0 && tpw.trim().length() != 0 && tname.trim().length() != 0 && tkinds.trim().length() != 0 && tstation.trim().length()!=0 && imageCheck==true){
                    if(idcheck==false && inform_check.isChecked() == false){
                        Toast.makeText(getApplicationContext(),"아이디 중복검사를 해주세요.",Toast.LENGTH_LONG).show();
                        return;
                    }
                    else if(idcheck ==true && inform_check.isChecked() == false){
                        Toast.makeText(getApplicationContext(), "이용약관 및 사용자 정보제공 \n동의는 필수입니다!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else if(idcheckagain==true){
                        if(inform_check.isChecked() == false){
                            Toast.makeText(getApplicationContext(), "이용약관 및 사용자 정보제공 \n동의는 필수입니다!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else if(inform_check.isChecked() == true){
                            DatabaseReference user = mReference.child(tid);
                            user.child("id").setValue(tid);
                            user.child("pw").setValue(tpw);
                            user.child("name").setValue(tname);
                            user.child("size").setValue(tsize);
                            user.child("kinds").setValue(tkinds);
                            user.child("station").setValue(tstation);
                            Toast.makeText(getApplicationContext(), "회원가입을 축하드립니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                            startActivity(intent); // 회원 가입 완료했으면 다시 홈화면으로 이동
                            return;
                        }
                    }
                }

                // 중복검사 버튼을 누르지 않았을 경우를 대비한 조건
                if(idcheck==false){
                    Toast.makeText(getApplicationContext(),"아이디 중복검사를 해주세요.",Toast.LENGTH_LONG).show();
                    return;
                }
                // 중복검사 버튼은 눌렀지만 제대로 된 아이디를 고치지 않았을 시 나타나는 조건
                if(tid.trim().length() == 0 || idcheckagain==false){
                    Toast.makeText(getApplicationContext(), "아이디 중복검사를 다시 해주세요.", Toast.LENGTH_LONG).show();
                    return;
                }

                if(inform_check.isChecked() == false){
                    Toast.makeText(getApplicationContext(), "이용약관 및 사용자 정보제공 \n동의는 필수입니다!", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference user = mReference.child(tid);

                user.child("id").setValue(tid);
                user.child("pw").setValue(tpw);
                user.child("name").setValue(tname);
                user.child("size").setValue(tsize);
                user.child("kinds").setValue(tkinds);
                user.child("station").setValue(tstation);


                Toast.makeText(getApplicationContext(), "회원가입을 축하드립니다.", Toast.LENGTH_SHORT).show();
                Intent intent_home = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent_home);

            }
        });
    }

    // 사진의 회전 현상을 해결하기 위한 함수
    @RequiresApi(api = Build.VERSION_CODES.N)
    private Bitmap rotateImage(Uri uri, Bitmap bitmap) throws IOException{
        InputStream in;
        in = getContentResolver().openInputStream(uri);
        ExifInterface exif = new ExifInterface(in);
        in.close();

        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL);
        Matrix matrix = new Matrix();
        if(orientation == ExifInterface.ORIENTATION_ROTATE_90){
            matrix.postRotate(90);
        }
        else if(orientation == ExifInterface.ORIENTATION_ROTATE_180){
            matrix.postRotate(180);
        }
        else if(orientation == ExifInterface.ORIENTATION_ROTATE_270){
            matrix.postRotate(270);
        }
        return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
    }

    // 요청코드에 따른 이벤트 처리
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {   // 요청 코드가 10이면 이용약관 페이지로 이동
            if (resultCode == RESULT_OK) {
                //데이터 받기
                result_1 = data.getStringExtra("result_1"); // 이용약관
                result_1.toString();
            }
        }

        if (requestCode == 20) {    // 요청 코드가 20이면 개인정보 정책 페이지로 이동
            if (resultCode == RESULT_OK) {
                //데이터 받기
                result_2 = data.getStringExtra("result_2");   // 개인정보 정책
                result_2.toString();
            }
        }

        if(result_1 != null && result_2 != null ) {
            if (result_1.equals(closePopup_1) && result_2.equals(closePopup_2)) { // 정보 제공, 이용 약관 모두 확인 시
                CheckBox inform_check = findViewById(R.id.inform_check); // 이용약관 및 정보 동의
                inform_check.setChecked(true); // 체크 박스 체크
                inform_check.setEnabled(false); // 체크 박스 사용 불가 상태
            }
        }

        // 요청 코드가 이미지 선택 요청코드(= 1) 일 때
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            ImageView user_image = (ImageView) findViewById(R.id.user_image);
            Uri uri = data.getData();
            try {
                // 사진의 절대경로와 비트맵 형태로 받아와 rotateImage()한 후
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                bitmap=rotateImage(uri,bitmap);
                // user_image 를 가져온 사진으로 바꿔준다.
                user_image.setImageBitmap(bitmap);
                imageCheck=true; // 이미지 첨부 확인 여부 true
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}