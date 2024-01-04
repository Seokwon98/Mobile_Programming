package com.example.readingnote.Library;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readingnote.ImageLoadTask;
import com.example.readingnote.NoteAdapter;
import com.example.readingnote.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.security.auth.login.LoginException;

public class Book_ing_detail extends AppCompatActivity {

    private static final String TAG = "Book_ing_detail";
    TextView mBook_ing_Detail_title, mBook_ing_Detail_author, mBook_ing_Detail_date, mBook_ing_Detail_delete,mBook_ing_Detail_complete,mStopWatch;

    ImageView mBook_ing_Detail_image, mAdd_note,mBook_ing_detail_backbtn;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String i;
    ArrayList<Library_save_list_item> savedbooklist;
    ArrayList<Postnote> notelist;
    private ArrayList<Note_item> NoteList; // 리사이클러뷰 용
    private NoteAdapter noteAdapter;
    int j, NoteListSize;
    JSONArray a;
    String json;
    Button mTimeStart, mTimeStop, mTimeSave, mTimePause;
    private Thread timeThread = null;
    private Boolean isRunning = true;
    long now = System.currentTimeMillis();
    Date mDate = new Date(now);
    SimpleDateFormat simpleDate = new SimpleDateFormat("yyyy-MM-dd");
    String getTime = simpleDate.format(mDate);
    HashMap<String,Integer> recordlist = new HashMap<>(1000);





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_ing_detail);
        savedbooklist = new ArrayList<>();
        notelist = new ArrayList<>();
        sharedPreferences = getSharedPreferences("책", 0);
        editor = sharedPreferences.edit();



        //뷰 연결 시작
        mBook_ing_Detail_author = findViewById(R.id.Book_ing_detail_author);
        mBook_ing_Detail_date = findViewById(R.id.Book_ing_detail_date);
        mBook_ing_Detail_image = findViewById(R.id.Book_ing_Detail_image);
        mBook_ing_Detail_title = findViewById(R.id.Book_ing_Detail_title);
        mBook_ing_Detail_delete = findViewById(R.id.Book_ing_Detail_delete);
        mAdd_note = findViewById(R.id.Add_note);
        mBook_ing_detail_backbtn = findViewById(R.id.Book_ing_detail_backbtn);
        mBook_ing_Detail_complete = findViewById(R.id.Book_ing_detail_complete);
        mStopWatch = findViewById(R.id.StopWatch);
        mTimeSave = findViewById(R.id.TimeSave);
        mTimeStart = (Button) findViewById(R.id.TimeStart);
        mTimeStop = findViewById(R.id.TimeStop);
        mTimePause = findViewById(R.id.TimePause);
        RecyclerView recyclerView = findViewById(R.id.Note_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //뷰 연결 끝


        //리스트에 저장된 것 불러오기 시작
        json = sharedPreferences.getString("저장목록", null);

        if (json != null) {
            try {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    String a = jsonArray.getJSONObject(i).getString("제목");
                    String b = jsonArray.getJSONObject(i).getString("이미지");
                    String c = jsonArray.getJSONObject(i).getString("저자");
                    String d = jsonArray.getJSONObject(i).getString("출판사");
                    String f = jsonArray.getJSONObject(i).getString("날짜");
                    savedbooklist.add(new Library_save_list_item(a, b, c, d, f, NoteList));
                    //위에 NoteList를 정의한 부분은 없다,, 아마 onStart에서 정의한 부분이 리사이클러뷰 할 때 반영 되고, 이번 구문은 상관 없는듯?
                    Log.e(TAG+"85", savedbooklist.toString() );
                    Log.e(TAG+"86", json );

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e("jsonarray 불러오기", "데이터 없음");
        }
        // 불러오기 끝

        Intent intent = getIntent();
        i = intent.getExtras().getString("포지션");
        j = Integer.parseInt(i);
        Log.e("받은 포지션", i);


        mBook_ing_Detail_title.setText(savedbooklist.get(j - 1).getNewbook_title());
        mBook_ing_Detail_date.setText(savedbooklist.get(j - 1).getNewbook_date());
        mBook_ing_Detail_author.setText(savedbooklist.get(j - 1).getNewbook_author());
        ImageLoadTask task = new ImageLoadTask(savedbooklist.get(j - 1).getNewbook_image(), mBook_ing_Detail_image);
        task.execute();


        //다 읽은 책 등록 시작
        mBook_ing_Detail_complete.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(),Book_completed.class);
                intent.putExtra("포지션",j);
                startActivity(intent);
            }
        });
        //다 읽은 책 등록 끝
        //리사이클러뷰 시작
        NoteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(NoteList);
        recyclerView.setAdapter(noteAdapter);
        //리사이클러뷰 끝

        //뒤로가기 버튼 시작
        mBook_ing_detail_backbtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                finish();
            }
        });
        //뒤로가기 버튼 끝

        // 삭제 버튼 시작
        mBook_ing_Detail_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                show();
            }
        });
        // 삭제 버튼 끝

        //노트 추가 시작
        mAdd_note.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Postnote.class);
                startActivityForResult(intent, 0);
            }
        });
        //노트 추가 끝


        //스탑 워치 시작
        mTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                mTimePause.setVisibility(View.VISIBLE);

                timeThread = new Thread(new timeThread());
                timeThread.start();
            }
        });

        mTimeStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mTimeStart.setVisibility(View.VISIBLE);
                mTimePause.setVisibility(View.GONE);
                mStopWatch.setText("00:00:00:00");
                timeThread.interrupt();
            }
        });

        mTimePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunning = !isRunning;
                if (isRunning) {
                    mTimePause.setText("일시정지");
                } else {
                    mTimePause.setText("시작");
                }
            }
        });

        mTimeSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTimeStart.setVisibility(View.VISIBLE);
                mTimePause.setVisibility(View.GONE);
                String time = mStopWatch.getText().toString();
                Log.e("기록", time ); //00:00:00:00
                String[] record = time.split(":");
                int hour = Integer.parseInt(record[0]); //시간
                int minute = Integer.parseInt(record[1]); //분
                int second = Integer.parseInt(record[2]); //초
                if(!recordlist.containsKey(getTime)) {
                    recordlist.put(getTime,hour * 3600 + minute * 60 + second);
                    int newrecord = recordlist.get(getTime);
                    Log.e("새로운 기록", String.valueOf(recordlist.get(getTime)));
                }
                else{
                    int oldrecord = recordlist.get(getTime);
                    int newrecord = oldrecord+hour * 3600 + minute * 60 + second;
                    recordlist.put(getTime,newrecord);
                    Log.e("이전 기록", String.valueOf(recordlist.get(getTime)) );
                }
                mStopWatch.setText("00:00:00:00");
                timeThread.interrupt();
                Log.e("나눈 후 기록", String.valueOf(hour)+":"+String.valueOf(minute)+":"+String.valueOf(second) );
                Toast.makeText(getApplicationContext(),"기록이 저장되었습니다.",Toast.LENGTH_SHORT).show();

                List keys = new ArrayList(recordlist.keySet());
                Log.e("252",String.valueOf(recordlist.size()) );
                Log.e("253",keys.get(0).toString() ); //2020-10-30
                try{

                JSONArray recordArray = new JSONArray();
                for(int i=0; i<=recordlist.size(); i++){
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(keys.get(i).toString(),String.valueOf(recordlist.get(keys.get(i))));
                    recordArray.put(i,jsonObject);
                    Log.e("261", recordArray.toString() );
                    editor.putString("기록",recordArray.toString());
                    editor.apply();
                }
                    Log.e("260", recordArray.toString() );

                }catch (Exception e){

                }




            }
        });
        //스탑 워치 종료


    } //여기까지 onCreate

    //from Postnote 노트 등록했을 때 시작
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(getApplicationContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
            return;
        }
        if (requestCode == 0) {
            String contents = data.getStringExtra("글");
            String imageuri = data.getStringExtra("이미지");
            String date = data.getStringExtra("날짜");


            NoteList.add(new Note_item(date,imageuri,contents));
            NoteListSize = NoteList.size();
            Log.e(TAG+"노트 등록할 때 노트리스트 사이즈", Integer.toString(NoteList.size()) );
            noteAdapter.notifyDataSetChanged();

            if (imageuri != null) {
                Log.e("디테일 받음 이미지", imageuri);
//                Log.e("이미지가 있다면", NoteList.get(0).Savednote_image);
            }
            savedbooklist.get(j-1).setNotelist(NoteList); //업데이트 된 notelist를 savedbooklist에 반영
//            Log.e("이미지가 있다면2", savedbooklist.get(j-1).NoteList.get(0).Savednote_image);

            //savedbooklist 안에 있는 notelist를 반영하여 savedbooklist의 jsonarray
            a = new JSONArray();

            for(int i=0; i<savedbooklist.get(j-1).NoteList.size(); i++){  //notelist 사이즈 만큼 반복해서 넣는 것
                try {
                    JSONObject noteObject = new JSONObject();
                    if(savedbooklist.get(j-1).NoteList.get(i).getSavednote_contents()!=null){
                    noteObject.put("글", savedbooklist.get(j-1).NoteList.get(i).getSavednote_contents());}
                    else if(savedbooklist.get(j-1).NoteList.get(i).getSavednote_contents()==null){
                        noteObject.put("글", "");
                    }
                    if(savedbooklist.get(j-1).NoteList.get(i).getSavednote_image()!=null){
                    noteObject.put("이미지",savedbooklist.get(j-1).NoteList.get(i).getSavednote_image());}
                    if(savedbooklist.get(j-1).NoteList.get(i).getSavednote_image()==null){
                    noteObject.put("이미지","");
                    }
                    noteObject.put("날짜",savedbooklist.get(j-1).NoteList.get(i).getSavednote_date());
                    //noteObject는 정상대로 쌓이고 있음
//                    Log.e("noteObject", noteObject.toString()  );
                    a.put(i,noteObject);
//                    Log.e("북디테일이미지", savedbooklist.get(j-1).NoteList.get(i).getSavednote_image());
                    Log.e("북디테일날짜", savedbooklist.get(j-1).NoteList.get(i).getSavednote_date());
                    Log.e("북디테일콘텐츠", savedbooklist.get(j-1).NoteList.get(i).getSavednote_contents());
                    if(savedbooklist.get(j-1).NoteList.get(i).getSavednote_image()==null){

                        Log.e("이미지 값 확인", "이미지 값이 없음" );
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } Log.e("a", a.toString()  );



            Log.e("notelist 목록 확인", savedbooklist.get(j-1).getNotelist().get(0).getSavednote_contents() );
            String jsonn = a.toString();
            Log.e("노트목록 json", json );


            try {
                JSONArray b = new JSONArray(json);
                b.getJSONObject(j-1).put("노트목록",a.toString());
                Log.e("노트목록 json2", b.toString() );// 여기까지 확인
                editor.putString("저장목록",b.toString());
                editor.apply();
                String json2 = sharedPreferences.getString("저장목록", null);
                Log.e("저장 확인", json2 );// 여기까지 확인
                // shared 안에 저장되어 있는 것 꺼내 와서 notelist 안에 저장

            } catch (JSONException e) {
                e.printStackTrace();
            }








//여기까지 확인





        }
    }


    //from Postnote 끝끝


    //삭제 다이얼로그 메소드 시작
    void show()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("읽고 있는 책 목록에서 삭제하시겠습니까?");
        builder.setPositiveButton("예",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(j!=1){
                            savedbooklist.remove(j-1);
                            String json = sharedPreferences.getString("저장목록",null);
                            Log.e(TAG+"삭제 직전 255", json );
                            try {
                                JSONArray jsonArray = new JSONArray(json);
                                jsonArray.remove(j-1);
                                editor.putString("저장목록",jsonArray.toString());
                                editor.apply();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String json2 = sharedPreferences.getString("저장목록",null);
                            Log.e(TAG+"삭제 직전 266", json2 );
                        }
                        else if(savedbooklist.size()!=1 && j==1){
                            savedbooklist.remove(j-1);
                            String json = sharedPreferences.getString("저장목록",null);
                            Log.e(TAG+"삭제 직전 255", json );
                            try {
                                JSONArray jsonArray = new JSONArray(json);
                                jsonArray.remove(j-1);
                                editor.putString("저장목록",jsonArray.toString());
                                editor.apply();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            String json2 = sharedPreferences.getString("저장목록",null);
                            Log.e(TAG+"삭제 직전 266", json2 );

                        }
                        else if(savedbooklist.size()==1 && j==1){
                            editor.clear();
                            editor.apply();
                        }
                        finish();

                        Intent intent = new Intent(getApplicationContext(), Library.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("아니오",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }
    //삭제 다이얼로그 메소드 끝

    // 스탑워치 핸들러 시작
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int mSec = msg.arg1 % 100;
            int sec = (msg.arg1 / 100) % 60;
            int min = (msg.arg1 / 100) / 60;
            int hour = (msg.arg1 / 100) / 360;
            //1000이 1초 1000*60 은 1분 1000*60*10은 10분 1000*60*60은 한시간

            @SuppressLint("DefaultLocale") String result = String.format("%02d:%02d:%02d:%02d", hour, min, sec, mSec);

            mStopWatch.setText(result);
        }
    };

    public class timeThread implements Runnable {
        @Override
        public void run() {
            int i = 0;

            while (true) {
                while (isRunning) { //일시정지를 누르면 멈춤
                    Message msg = new Message();
                    msg.arg1 = i++;
                    handler.sendMessage(msg);

                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run() {
                                mStopWatch.setText("");
                                mStopWatch.setText("00:00:00:00");
                            }
                        });
                        return; // 인터럽트 받을 경우 return
                    }
                }
            }
        }
    }
    // 스탑워치 핸들러 종료


    //액티비티 생명주기 시작
    @Override
    public void onPause(){
        super.onPause();
        Log.i(TAG, "onPause()");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.i(TAG, "onStop()");
        if(a!=null){
            Log.i(TAG, a.toString()); //노트 목록 잘 저장되어 있음
        }

        String json = sharedPreferences.getString("저장목록",null);
        if(json!=null){
        Log.e(TAG+"저장목록 321", json );}




    }

    @Override
    public void onResume(){
        super.onResume();
        Log.i(TAG, "onResume()");
        String jsonn = sharedPreferences.getString("저장목록", null);
        if(jsonn!=null)
        Log.e(TAG, jsonn);

    }

    @Override
    public void onStart(){
        super.onStart();
        Log.i(TAG, "onStart()");
        String jsonn = sharedPreferences.getString("저장목록", null);
        if(jsonn != null) {
            Log.e("북디테일 스타트 저장목록", jsonn);
            try {
                JSONArray jsonarray = new JSONArray(jsonn);
                String a = jsonarray.getJSONObject(j-1).getString("노트목록");
                Log.e("북디테일 스타트 노트목록", a );
                JSONArray jsonarray2 = new JSONArray(a); // 해당 책의 노트목록 길이
                String b = jsonarray2.getJSONObject(0).getString("글");
                    String c = jsonarray2.getJSONObject(0).getString("이미지");

                Log.e("북디테일 스타트 노트목록의 글", b );
                Log.e("북디테일 스타트 노트목록의 이미지", c );
                Log.e("사이즈를 재자", Integer.toString(jsonarray2.length()) );
                for(int i=0; i<jsonarray2.length(); i++){
                    String q = jsonarray2.getJSONObject(i).getString("날짜");
                    String w = jsonarray2.getJSONObject(i).getString("이미지");
                    String e = jsonarray2.getJSONObject(i).getString("글");
                    Log.e("북디테일 스타트 노트목록 날짜", q);
//                    if(w!=null){
                        Log.e("북디테일 스타트 노트목록 이미지",w);
//                    }
                    //해당 책의 노트목록만 NoteList에 저장
                    if(NoteList.size()<jsonarray2.length()){
                    NoteList.add(new Note_item(q,w,e));
                                        noteAdapter.notifyDataSetChanged();}
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        //기록 불러와서 저장하기 시작
        String savedrecords = sharedPreferences.getString("기록",null);
        if(savedrecords!=null) {
            try {
                JSONArray jsonArray = new JSONArray(savedrecords);
                //jsonarray key값 가져오기 시작
                for (int i = 0; i <= jsonArray.length(); i++) {
                    JSONObject json = jsonArray.getJSONObject(i);
                    Iterator<String> keys = json.keys();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        recordlist.put(key, Integer.parseInt(json.get(key).toString()));
                        Log.e("572", recordlist.get(key).toString());
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        //기록 불러와서 저장하기 종료
    }

    @Override
    public void onRestart(){
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }
    // 액티비티 생명주기 끝
}