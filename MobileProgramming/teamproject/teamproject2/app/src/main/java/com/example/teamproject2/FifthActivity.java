package com.example.teamproject2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class FifthActivity extends AppCompatActivity{

    ListView listview;
    String[] subway;
    int intCount = 0;

    // text file의 줄 카운팅함.
    InputStream inputStreamCounter;
    BufferedReader bufferedReaderCounter;

    // text file의 values를 String array에 로딩.
    InputStream inputStreamLoader;
    BufferedReader bufferedReaderLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth);

        //SearchView & listview 객체 xml과 연결.
        SearchView searchView = findViewById(R.id.search_view);
        listview = (ListView)findViewById(R.id.listview);

        // SearchView 기능 구현.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // 검색 버튼이 눌려졌을 때 이벤트 처리.
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 검색어가 변경되었을 때 이벤트 처리.
            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<String> filterList = new ArrayList<>();

                // 검색어와 일치하는 값을 보여줌.
                for (String tempSubwayList : subway){
                    if(tempSubwayList.toLowerCase().startsWith(newText.toLowerCase())){
                        filterList.add(tempSubwayList);
                    }
                }
                // Adapter 적용.
                ArrayAdapter<String> adapterWithFilteredList = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, filterList);
                listview.setAdapter(adapterWithFilteredList);
                return true;
            }
        });

        // is & br를 text file과 연결.
        // counters
        inputStreamCounter = this.getResources().openRawResource(R.raw.subway);
        bufferedReaderCounter = new BufferedReader(new InputStreamReader(inputStreamCounter));

        // loaders
        inputStreamLoader = this.getResources().openRawResource(R.raw.subway);
        bufferedReaderLoader = new BufferedReader(new InputStreamReader(inputStreamLoader));

        // tex file의 줄/라인 카운팅.
        try {
            while(bufferedReaderCounter.readLine()!=null){
                intCount++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        subway = new String[intCount];

        // string array에 text file의 줄/라인 저장.
        try{
            for(int i=0 ; i<intCount ; i++){
                subway[i] = bufferedReaderLoader.readLine();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // Adapter 적용.
        ArrayAdapter < String > adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, subway);

        listview.setAdapter(adapter);

        // 접속중인 ID 값을 String으로 받음.
        Intent intent = getIntent();
        final String preId = intent.getStringExtra("preId");

        // listview의 item 클릭 시 기능 구현.
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 선택된 지하철역 값 intent 선언.
                Intent intent = new Intent(view.getContext(), SixthActivity.class);
                String subwayselection = (String) parent.getItemAtPosition(position);

                // 선택된 지하철역 값과 접속중인 ID 값을 넘겨줌.
                intent.putExtra("subwayselection",subwayselection);
                intent.putExtra("preId",preId);

                // 액티비티 실행.
                startActivity(intent);
            }
        });

    }
}