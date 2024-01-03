package com.example.readingnote.Library;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readingnote.ImageLoadTask;
import com.example.readingnote.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.security.auth.login.LoginException;


public class Save_newbook  extends AppCompatActivity {
    private static final String TAG = "Save_newbook 활동주기";
    ImageView mSave_backbtn, mSave_image;
    TextView mSave_title, mSave_author, mSave_publisher, mSave_reset;
    Button mSave_button;
    String title, author, publisher, url;
    ArrayList<Library_save_list_item> savedbooklist;
    ArrayList<Note_item> NoteList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    SimpleDateFormat format1 = new SimpleDateFormat( "yyyy-MM-dd");
    Date time = new Date();
    int k = 0;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_newbook);
        savedbooklist = new ArrayList<>();
        NoteList = new ArrayList<>();
        sharedPreferences = getSharedPreferences("책", 0);
        editor = sharedPreferences.edit();


        //뷰 연결
        mSave_backbtn = findViewById(R.id.Save_backbtn);
        mSave_author = findViewById(R.id.Save_author);
        mSave_publisher = findViewById(R.id.Save_publisher);
        mSave_title = findViewById(R.id.Save_title);
        mSave_image = findViewById(R.id.Save_image);
        mSave_button = findViewById(R.id.Save_button);
        mSave_reset = findViewById(R.id.Save_reset);
        //뷰 연결 끝

        //뒤로가기 클릭 이벤트 시작
        mSave_backbtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });
        //뒤로가기 클릭 이벤트 끝

        //Shared 초기화 버튼 시작
        mSave_reset.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                editor.clear();
                editor.apply();
                //토스트 메시지
                Toast.makeText(getApplicationContext(), "데이터가 초기화되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        //Shared 초기화 버튼 끝

        // 인텐트 받기 시작
        Intent intent = getIntent();
        title = intent.getStringExtra("제목");
        author = intent.getStringExtra("저자");
        publisher = intent.getStringExtra("출판사");
        url = intent.getStringExtra("이미지");
        Log.e("받은 url", url);

        // 인텐트 받기 끝

        //저장 버튼 클릭 이벤트 시작 : 위에서 받은 인텐트 값들을 Shared에 저장한다.
        mSave_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String time1 = format1.format(time);
                editor.putString("제목",title);
                editor.putString("이미지",url);
                editor.putString("저자",author);
                editor.putString("출판사",publisher);
                editor.putString("날짜",time1);
                editor.apply();
                //토스트 메시지
                Toast.makeText(getApplicationContext(), "저장되었습니다.", Toast.LENGTH_SHORT).show();
                Log.e("Save_newbook", "저장 버튼 눌림");
                finish();

            }
        });
        //저장 버튼 클릭 이벤트 끝
        // 뷰에 인텐트 값 넣기 시작
        mSave_title.setText(title);
        mSave_publisher.setText(publisher);
        mSave_author.setText(author);
        ImageLoadTask task = new ImageLoadTask(url, mSave_image);
        task.execute();

        // 뷰에 인텐트 값 넣기 끝
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
    public void onStop() {
        super.onStop();
        //저장한 값을 리스트에 추가 시작
        String a = sharedPreferences.getString("제목", "");
        String b = sharedPreferences.getString("이미지", "");
        String c = sharedPreferences.getString("저자", "");
        String d = sharedPreferences.getString("출판사", "");
        String f = sharedPreferences.getString("날짜", "");


        String json = sharedPreferences.getString("저장목록", null);


        if (json != null) {
            Log.e(TAG+"onStop 저장 전", json ); // ok
            if(!a.equals("")){ //제목이 empty 아닐 때
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    for (int i = 0; i < jsonArray.length(); i++) { //중복된 제목이 있는 지 확인하는 구문
                        String title = jsonArray.getJSONObject(i).getString("제목");
                        if(title.equals(a)){
                            k = 1;  //중복된 경우
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //중복일 때
                if (k == 1) {
                    Log.e(TAG, "중복된 값");
                }
                //중복 아닐 떄
                else if (k == 0 && !a.equals("")) {

                    savedbooklist.add(new Library_save_list_item(a, b, c, d, f, NoteList ));
                    int size = savedbooklist.size();
                    try {
                        JSONArray jsonArray = new JSONArray(json);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("제목", savedbooklist.get(size-1).getNewbook_title());
                            jsonObject.put("이미지", savedbooklist.get(size-1).getNewbook_image());
                            jsonObject.put("저자", savedbooklist.get(size-1).getNewbook_author());
                            jsonObject.put("출판사", savedbooklist.get(size-1).getNewbook_publisher());
                            jsonObject.put("날짜", savedbooklist.get(size-1).getNewbook_date());
                        int jsonsize = jsonArray.length();
                        jsonArray.put(jsonsize,jsonObject);
                        editor.putString("저장목록", jsonArray.toString());
                        editor.apply();
                        String jsoncheck = sharedPreferences.getString("저장목록", null);
                        Log.e(TAG+"187",  jsoncheck);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }

        } else if (json == null) {
            if (!a.equals("")) {
                savedbooklist.add(new Library_save_list_item(a, b, c, d, f, NoteList ));
                int size = savedbooklist.size();
                try {
                    JSONArray jsonArray = new JSONArray();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("제목", savedbooklist.get(size-1).getNewbook_title());
                    jsonObject.put("이미지", savedbooklist.get(size-1).getNewbook_image());
                    jsonObject.put("저자", savedbooklist.get(size-1).getNewbook_author());
                    jsonObject.put("출판사", savedbooklist.get(size-1).getNewbook_publisher());
                    jsonObject.put("날짜", savedbooklist.get(size-1).getNewbook_date());
                    int jsonsize = jsonArray.length();
                    jsonArray.put(jsonsize,jsonObject);
                    editor.putString("저장목록", jsonArray.toString());
                    editor.apply();
                    String jsoncheck = sharedPreferences.getString("저장목록", null);
                    Log.e(TAG+"187",  jsoncheck);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                savedbooklist.add(new Library_save_list_item(a, b, c, d, f,NoteList));
//                JSONArray jsonArray = new JSONArray();
//                for (int i = 0; i < savedbooklist.size(); i++) {
//
//                    JSONObject jsonObject = new JSONObject();
//                    try {
//                        jsonObject.put("제목", savedbooklist.get(i).getNewbook_title());
//                        jsonObject.put("이미지", savedbooklist.get(i).getNewbook_image());
//                        jsonObject.put("저자", savedbooklist.get(i).getNewbook_author());
//                        jsonObject.put("출판사", savedbooklist.get(i).getNewbook_publisher());
//                        jsonObject.put("날짜", savedbooklist.get(i).getNewbook_date());
//                        jsonArray.put(jsonObject);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//                    editor.putString("저장목록", jsonArray.toString());
//                    editor.apply();
//
//                }

                //변경 된 리스트를 shared에 저장 끝

            } else if (a.equals("")) {
                Log.e("초기화", "완료");
            }
        }


        //9/24시도 끝
        Log.e("a확인", a);

        String jsonstop =  sharedPreferences.getString("저장목록",null);
        if(jsonstop!=null)
        Log.e(TAG, jsonstop );
    }
//    }

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
        k = 0;
        if(k == 0){
            Log.e("duplicate", "false" );
        }
        editor.putString("제목","");
        editor.apply();
        // 액티비티 시작할 때 저장된 목록 savedbooklist로 집어넣기 시작
        String json = sharedPreferences.getString("저장목록",null);

        if(json!=null){
            try {
                JSONArray jsonArray = new JSONArray(json);
                for(int i=0; i<jsonArray.length(); i++){
                    String a = jsonArray.getJSONObject(i).getString("제목");
                    String b = jsonArray.getJSONObject(i).getString("이미지");
                    String c = jsonArray.getJSONObject(i).getString("저자");
                    String d = jsonArray.getJSONObject(i).getString("출판사");
                    String f = jsonArray.getJSONObject(i).getString("날짜");

                    savedbooklist.add(new Library_save_list_item(a,b,c,d,f,NoteList));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else{
            Log.e("jsonarray 불러오기", "데이터 없음" );
        }
        // 액티비티 시작할 때 저장된 목록 savedbooklist로 집어넣기 끝
        Log.i(TAG, "onStart()");
        if(json!=null)
        Log.i(TAG, json);

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



