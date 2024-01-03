package com.example.readingnote.Library;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readingnote.R;
import com.example.readingnote.VerticalSpaceItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class Library_search_list  extends AppCompatActivity {
    private static final String TAG = "Library_search_list 생명주기";
    ArrayList<Library_search_list_item> newbooklist;
    Library_search_adapter newbookadapter;
    String keyword, str, mNewbook_image, mNewbook_title, mNewbook_author,mNewbook_publisher;
    TextView mText_Result;
    ImageView mSearchlist_backbtn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_search_list);
        sharedPreferences = getSharedPreferences("책", 0);


        //리싸이클러뷰 설정 시작
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.Library_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        newbooklist = new ArrayList<>();
        newbookadapter = new Library_search_adapter(newbooklist);
        recyclerView.setAdapter(newbookadapter);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(40));
        //리싸이클러뷰 설정 끝

        //네이버 검색 api 스레드 시작
        Intent intent = getIntent();
        keyword = intent.getStringExtra("keyword");
        final Handler mHandler = new Handler(Looper.getMainLooper());




        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    str = getNaverSearch(keyword);//여기까지 넘어옴 확인
                    Log.e("결과값", str );
                    final String[] array = str.split("#");

                    for(int i=0; i<array.length; i++){
                        System.out.println(i);
                       System.out.println(array[i]);
                    }
                    int n = (array.length-1)/4;
                    System.out.println(n);
                    for(int j=1; j<=n; j++){
                        final int o = 4*j-3;

                        mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    // 사용하고자 하는 코드
                                    newbooklist.add(new Library_search_list_item(array[o],array[o +1],array[o +2],array[o +3]));
                                    newbookadapter.notifyDataSetChanged();
                                    Log.e("newbooklist 사이즈", Integer.toString(newbooklist.size()) );

                                    //사용하고자 하는 코드 끝

//                                    //저장 시작
//                                    SharedPreferences sharedPreferences = getSharedPreferences("책",0);
//                                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                                    JSONArray jsonArray = new JSONArray();
//                                    JSONObject jsonObject = new JSONObject();
//                                    for (int i = 0; i < finalJ; i++){
//                                        try {
//                                            jsonObject.put("제목",newbooklist.get(i).getNewbook_title());
//                                            jsonObject.put("이미지",newbooklist.get(i).getNewbook_image());
//                                            jsonObject.put("저자",newbooklist.get(i).getNewbook_author());
//                                            jsonObject.put("출판사",newbooklist.get(i).getNewbook_publisher());
//                                            jsonArray.put(jsonObject);
//                                            editor.putString("검색",jsonArray.toString());
//                                            editor.apply();
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                      }
//                                    //저장 끝
                                }
                            }, 0);

                    }





//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//
//
//                            TextView searchResult2 = (TextView) findViewById(R.id.searchResult2);
//                            searchResult2.setText(str);
//
//                        }
//                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }



            }
        });thread.start();
        // 네이버 검색 api 스레드 끝



        //검색 결과 안내 메세지
        mText_Result = findViewById(R.id.Text_Result);
        mText_Result.setText("'"+keyword+"' 검색 결과");


        //뒤로가기 버튼
        mSearchlist_backbtn = findViewById(R.id.Searchlist_backbtn);
        mSearchlist_backbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),Library_search.class);
                startActivity(intent);
            }
        });


    }
    //onCreate 끝

    //네이버 검색api 메소드 시작
    private String getNaverSearch(String keyword) {
        String clientID = "ScN9ZGMElazxEtSmK4iH";
        String clientSecret = "SnGdrE1fEC";
        StringBuffer sb = new StringBuffer();
        //여기까지 확인

        try {


            String text = URLEncoder.encode(keyword, "UTF-8");



            String apiURL = "https://openapi.naver.com/v1/search/book.xml?query=" + text +"&display=100" + "&start=1";
            //여기까지 확인

            URL url = new URL(apiURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Naver-Client-Id", clientID);
            conn.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            //여기까지 확인
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            String tag;
            //여기까지 확인
            //inputStream으로부터 xml값 받기
            xpp.setInput(new InputStreamReader(conn.getInputStream(), "UTF-8"));

            xpp.next();
            int eventType = xpp.getEventType();
            //여기까지 확인

            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        tag = xpp.getName(); //태그 이름 얻어오기
                        //여기까지 확인

                        if (tag.equals("item")) ; //첫번째 검색 결과
                        else if (tag.equals("title")) {
                            Log.e("받은 키워드", keyword );
//                            sb.append("제목 : ");

                            xpp.next();

//                            String title = xpp.getText().replace("<b>", "").replace("</b>","");
//                            Log.e("제목", title );
//                            if(xpp.getText().contains("Naver Open API - book")){
//
//                            }
                            if(xpp.getText()!=null){
                                sb.append(xpp.getText().replace("<b>", "").replace("</b>","")+"#");}
                            if(xpp.getText()==null){
                                sb.append(xpp.getText()+"#");}
//                            sb.append("\n");


                        } else if (tag.equals("author")) {


//                            sb.append("저자명 : ");
                            xpp.next();


                            if(xpp.getText()!=null){
                                sb.append(xpp.getText().replace("<b>", "").replace("</b>","")+"#");}
                            if(xpp.getText()==null){
                                sb.append(xpp.getText()+"#");}
//                            sb.append("\n");


                        } else if (tag.equals("publisher")) {

//                            sb.append("출판사 : ");
                            xpp.next();


                            if(xpp.getText()!=null){
                                sb.append(xpp.getText().replace("<b>", "").replace("</b>","")+"#");}
                            if(xpp.getText()==null){
                                sb.append(xpp.getText()+"#");}
//                            sb.append("\n");


                        }
                        else if (tag.equals("image")) {

//                            sb.append("이미지url : ");
                            xpp.next();


                            if(xpp.getText()!=null){
                                sb.append(xpp.getText().replace("<b>", "").replace("</b>","")+"#");}
                            if(xpp.getText()==null){
                                sb.append(xpp.getText()+"#");}
//                            sb.append("\n");


                        }
                        break;
                }

                eventType = xpp.next();


            }

        } catch (Exception e) {
            return e.toString();

        }

        //여기까지 확인
        return sb.toString();

    }
    //네이버 검색 api 메소드 끝

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

