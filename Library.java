package com.example.readingnote.Library;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.readingnote.Book_ing_adapter;
import com.example.readingnote.R;
import com.example.readingnote.Reading.Statistics;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Library extends AppCompatActivity {
    private static final String TAG = "Library 활동주기";
    CardView mAdd_book, mFinish;
    RecyclerView recyclerView;
    ArrayList<Book_ing> bookinglist;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String json;
    TextView mComplete_list;
    static int n;
    ArrayList<Completed_Book_item> completedlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        //뷰 연결 시작
        mAdd_book = findViewById(R.id.Add_book);
        mFinish = findViewById(R.id.Finish);
        recyclerView = findViewById(R.id.Book_ing_recyclerView);
        List<Item> items = new ArrayList<>();
        bookinglist = new ArrayList<>();
        sharedPreferences = getSharedPreferences("책",0);
        editor = sharedPreferences.edit();
        mComplete_list = findViewById(R.id.complete_list);
        completedlist = new ArrayList<>();
        //뷰 연결 끝


        // 지금 읽고 있는 책(book_ing) 리사이클러뷰 시작


        //bookinglist에 shared 저장 된 값 집어 넣기 시작
        json = sharedPreferences.getString("저장목록",null);

        if(json!=null){
            try {
                JSONArray jsonArray = new JSONArray(json);
                n= jsonArray.length();
                Book_ing_main book_ing_main = new Book_ing_main("읽고 있는 책","현재 "+n+"권의 책을 읽고 있어요.");
                items.add(new Item(0,book_ing_main));
                for(int i=0; i<jsonArray.length(); i++){
                    String a = jsonArray.getJSONObject(i).getString("제목");
                    String b = jsonArray.getJSONObject(i).getString("이미지");
                    String f = jsonArray.getJSONObject(i).getString("날짜");
//                    bookinglist.add(new Book_ing(a,b,f,"노트"));
                    Book_ing book_ing = new Book_ing(a,b,f,"노트");
                    items.add(new Item(1,book_ing));
                }
                Log.e("jsonarray 확인3", jsonArray.toString() );
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Book_ing_main book_ing_main = new Book_ing_main("읽고 있는 책","현재 읽고 있는 책이 없어요.");
            items.add(new Item(0,book_ing_main));
            Log.e("jsonarray 불러오기", "데이터 없음" );
        }
        //bookinglist에 shared 저장 된 값 집어 넣기 끝
        // 뷰페이저 처럼 옆으로 넘기기 시작
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        // 뷰페이저 처럼 옆으로 넘기기 끝
        recyclerView.setAdapter(new Book_ing_adapter(items));



        // 지금 읽고 있는 책 리사이클러뷰 끝

        // 읽고 있는 책 추가 시작
        mAdd_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Library_search.class);
                startActivity(intent);
            }
        });
        // 읽고 있는 책 추가 끝

        // 읽고 있는 책 보기 시작

        // 읽고 있는 책 보기 끝

        // 다 읽은 책 불러오기 시작
        String complete = sharedPreferences.getString("종료책목록",null);
        if(complete!=null){
            try{
                JSONArray jsonArray = new JSONArray(complete);
                for(int i=0; i<jsonArray.length(); i++){
                    String title = jsonArray.getJSONObject(i).getString("제목");
                    String image= jsonArray.getJSONObject(i).getString("이미지");
                    String author = jsonArray.getJSONObject(i).getString("저자");
                    String publisher = jsonArray.getJSONObject(i).getString("출판사");
                    String startdate = jsonArray.getJSONObject(i).getString("시작날짜");
                    String notelist = jsonArray.getJSONObject(i).getString("노트목록");
                    String rate = jsonArray.getJSONObject(i).getString("별점");
                    String review = jsonArray.getJSONObject(i).getString("리뷰");
                    String enddate = jsonArray.getJSONObject(i).getString("종료날짜");
                    completedlist.add(new Completed_Book_item(title, image, author, publisher, startdate, notelist, rate, review, enddate));

                }
            }catch (Exception e){

            }
            Log.e("종료책목록137", Integer.toString(completedlist.size()) );

        }
        mComplete_list.setText("현재까지 "+completedlist.size()+"권의 책을 읽었어요.");
        // 다 읽은 책 불러오기 종료

        // 다 읽은 책 클릭 시작
        mFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),Complete_list.class);
                startActivity(intent);
            }
        });
        // 다 읽은 책 클릭 종료
        //하단 메뉴바 시작
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigation);

        bottomNavigationView.setSelectedItemId(R.id.my_library);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.my_library:

                        return true;
                    case R.id.my_chart:
                        startActivity(new Intent(getApplicationContext(), Statistics.class));
                        overridePendingTransition(0,0);
                        return true;


                }
                return false;
            }
        });

        //하단 메뉴바 끝

    }
    //액티비티 생명주기 시작
    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "onPause()");
        String jsonpause =  sharedPreferences.getString("저장목록",null);
        if(jsonpause!=null)
        Log.e(TAG, jsonpause );
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG, "onStop()");
        String jsonstop =  sharedPreferences.getString("저장목록",null);
        if(jsonstop!=null)
        Log.e(TAG, jsonstop );
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume()");
        String jsonresume =  sharedPreferences.getString("저장목록",null);
        if(jsonresume!=null)
        Log.e(TAG, jsonresume );
    }

    @Override
    public void onStart(){
        super.onStart();

        Log.i(TAG, "onStart()");
        String jsonstart =  sharedPreferences.getString("저장목록",null);
        if(jsonstart!=null)
        Log.e(TAG, jsonstart );
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.i(TAG, "onRestart()");
        String jsonrestart =  sharedPreferences.getString("저장목록",null);
        if(jsonrestart!=null)
        Log.e(TAG, jsonrestart );
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
        String jsondestroy =  sharedPreferences.getString("저장목록",null);
        if(jsondestroy!=null)
        Log.e(TAG, jsondestroy );
    }
    // 액티비티 생명주기 끝
}
