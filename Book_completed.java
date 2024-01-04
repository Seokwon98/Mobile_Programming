package com.example.readingnote.Library;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readingnote.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Book_completed extends AppCompatActivity {
    int position;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String date, title, author, image, publisher, notelist, time1;
    TextView mBook_complete_date, mBook_complete_enddate, mBook_complete_enroll;
    SimpleDateFormat format1 = new SimpleDateFormat( "yyyy-MM-dd");
    Date time = new Date();
    RatingBar mBook_complete_ratingbar;
    float rating;
    EditText mBook_complete_review;
    int i = 0;
    JSONArray savedarray;
    ArrayList<Completed_Book_item> completedlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_completed);
        sharedPreferences = getSharedPreferences("책", 0);
        editor = sharedPreferences.edit();
        completedlist = new ArrayList<>();


        //뷰 연결 시작
        mBook_complete_date = findViewById(R.id.Book_complete_date);
        mBook_complete_enddate = findViewById(R.id.Book_complete_enddate);
        mBook_complete_ratingbar = findViewById(R.id.Book_complete_ratingbar);
        mBook_complete_review = findViewById(R.id.Book_complete_review);
        mBook_complete_enroll = findViewById(R.id.Book_complete_enroll);
        //뷰 연결 종료

        Intent intent = getIntent();
        position = intent.getIntExtra("포지션",0); // 읽고 있는 책 중에 몇 번째 책인지 포지션을 받음

        //해당 책 정보 불러오기 시작
        final String json = sharedPreferences.getString("저장목록",null);
        try {
            JSONArray jsonArray = new JSONArray(json);
            date = jsonArray.getJSONObject(position-1).getString("날짜");
            title = jsonArray.getJSONObject(position-1).getString("제목");
            author = jsonArray.getJSONObject(position-1).getString("저자");
            image = jsonArray.getJSONObject(position-1).getString("이미지");
            publisher = jsonArray.getJSONObject(position-1).getString("출판사");
            notelist = jsonArray.getJSONObject(position-1).getString("노트목록");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        //해당 책 정보 불러오기 끝
        //읽기 시작한 날짜 설정 시작
        mBook_complete_date.setText(date);
        //읽기 시작한 날짜 설정 종료

        //읽기 종료 날짜 설정 시작
        time1 = format1.format(time);
        mBook_complete_enddate.setText(time1);
        //읽기 종료 날짜 설정 종료

        //별점 시작
        mBook_complete_ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener(){

            @Override
            public void onRatingChanged(RatingBar ratingBar1, float rating1, boolean fromUser){
                rating = rating1;
                i=1;
                Log.e("별점확인", "onRatingChanged: "+rating );
            }
        });


        //별점 종료

        //리뷰 시작
        mBook_complete_review.setSelection(mBook_complete_review.length());
        //리뷰 종료

        // 기존 다 읽은 책 정보 불러오기 시작
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

        // 기존 다 읽은 책 정보 불러오기 종료
        //다 읽은책 등록 시작
        mBook_complete_enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Book_completed.this);
                builder.setTitle("");
                builder.setMessage("다 읽은 책 목록에 책을 추가하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                show();
                                finish();

                            }
                        });
                builder.setNegativeButton("아니오",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                builder.show();

            }
        });

        //다 읽은책 등록 종료
    }
  void show(){


      //다 읽은 책 정보 저장 시작
      if(i==1)
      completedlist.add(new Completed_Book_item(title,image, author, publisher, date, notelist, Float.toString(rating), mBook_complete_review.getText().toString(),time1));
      if(i==0 && rating == 0)
          completedlist.add(new Completed_Book_item(title,image, author, publisher, date, notelist, Float.toString(3), mBook_complete_review.getText().toString(),time1));
      try{
        JSONArray jsonArray = new JSONArray();

          for(int i=0; i<completedlist.size(); i++){
              JSONObject jsonObject = new JSONObject();
              jsonObject.put("제목",completedlist.get(i).getTtitle());
              jsonObject.put("이미지",completedlist.get(i).getImage());
              jsonObject.put("저자",completedlist.get(i).getAuthor());
              jsonObject.put("출판사",completedlist.get(i).getPublisher());
              jsonObject.put("시작날짜",completedlist.get(i).getStartdate());
              if(completedlist.get(i).getNotelist()!=null)
              jsonObject.put("노트목록",completedlist.get(i).getNotelist());
              else{
                  jsonObject.put("노트목록","");
              }
              jsonObject.put("별점",completedlist.get(i).getRate());
              jsonObject.put("리뷰",completedlist.get(i).getReview());
              jsonObject.put("종료날짜",completedlist.get(i).getEnddate());
              jsonArray.put(i,jsonObject);
          }
          Log.e("종료책 확인중180", jsonArray.getJSONObject(0).toString() );
          String a = jsonArray.toString();
          editor.putString("종료책목록",jsonArray.toString());
          editor.apply();
      }catch (Exception e){

      }

      //다 읽은 책 정보 저장 종료


      // 기존 읽고 있는 책 저장 목록에서 삭제 시작
      String jsonsaved = sharedPreferences.getString("저장목록",null);
      try {
          savedarray = new JSONArray(jsonsaved);
      } catch (JSONException e) {
          e.printStackTrace();
      }
      if(position!=1){
          savedarray.remove(position-1);
          editor.putString("저장목록",savedarray.toString());
          editor.apply();

      }
      else if(savedarray.length()!=1 && position==1){
          savedarray.remove(position-1);
          editor.putString("저장목록",savedarray.toString());
          editor.apply();
      } //여기까지 ok
      else if(savedarray.length()==1 && position==1){
          editor.remove("저장목록");
          editor.apply();
      }
      // 기존 읽고 있는 책 저장 목록에서 삭제 종료
      finish();
      Intent intent = new Intent(getApplicationContext(), Library.class);
      startActivity(intent);
  }

}