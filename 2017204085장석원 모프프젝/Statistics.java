package com.example.readingnote.Reading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.example.readingnote.Library.Library;
import com.example.readingnote.MonthPicker;
import com.example.readingnote.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class Statistics extends AppCompatActivity {
    BarChart mBarChart1, mBarChart2, mBarChart3, mBarChart4 ;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView mPickDates,mPickedDates, mTotalBooks, mYearTotal, mTotalTime;
    int monthlybookslist[] = new int[12];
    float dailytimelist[] = new float[31];
    float monthlytimelist[] = new float[12];
    int dailybookslist[] = new int[31];
    int year,month;
    int swc;
    int yearbooks = 0; //연도별 권수
    int monthbooks = 0; //연도+월별 권수
    int monthlybooks, dailybooks, monthlytimes, dailytimes;
    int totaltimes;//누적시간


    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int day) {
            Log.e("YearMonthPickerTest", "year = " + year + ", month = " + month + ", day = " + day);
            yearbooks = 0;
            monthbooks = 0;
            mYearTotal.setText(Integer.toString(year)+"년 누적 통계량");
            mPickedDates.setText(Integer.toString(year)+"/"+Integer.toString(month));
               loadActivity();

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        sharedPreferences = getSharedPreferences("책", 0);
        editor = sharedPreferences.edit();

           ;

        //뷰 연결 시작
        mBarChart1 = findViewById(R.id.BarChart1);
        mPickDates = findViewById(R.id.PickDates);
        mPickedDates = findViewById(R.id.PickedDates);
        mTotalBooks = findViewById(R.id.TotalBooks);
        mYearTotal = findViewById(R.id.YearTotal);
        mBarChart2 = findViewById(R.id.BarChart2);
        mBarChart3 = findViewById(R.id.BarChart3);
        mBarChart4 = findViewById(R.id.BarChart4);
        mTotalTime = findViewById(R.id.TotalTime);
        //뷰 연결 종료


        //기간 선택 시작
        mPickDates.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                MonthPicker pd = new MonthPicker();
                pd.setListener(d);
                pd.show(getSupportFragmentManager(),"MonthPicker Test");
                swc=0;

            }
        });


        //기간 선택 종료





        //하단 메뉴바
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomnavigation);

        bottomNavigationView.setSelectedItemId(R.id.my_chart);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.my_library:
                        startActivity(new Intent(getApplicationContext(), Library.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.my_chart:

                        return true;


                }
                return false;
            }
        });
        //하단 메뉴바 끝
    }
    private void loadActivity(){
        String 날짜 = mPickedDates.getText().toString();
        String[] date = 날짜.split("/");
        String year1 = date[0]; //2020
        String date1 = date[1]; //10
        year = Integer.parseInt(year1);
        month = Integer.parseInt(date1);
        Log.e("종료날짜 확인1", String.valueOf(year)+"년"+String.valueOf(month));
        //종료 책 목록의 날짜 불러오기 시작
        String json = sharedPreferences.getString("종료책목록", null);

        if(json!=null) {
            try {
                JSONArray jsonArray = new JSONArray(json);

                for (int i = 0; i < jsonArray.length(); i++) {
                    String dates = jsonArray.getJSONObject(i).getString("종료날짜"); //2020-10-26

                    int endyear = Integer.parseInt(dates.substring(0, 4));//2020
                    int endmonth = Integer.parseInt(dates.substring(5, 7));//10
                    Log.e("종료날짜 확인", String.valueOf(endyear) + "년" + String.valueOf(endmonth));
                    if (endyear == year)
                        yearbooks++;
                    if (endyear == year && endmonth == month)
                        monthbooks++;

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.e("종료날짜 확인", String.valueOf(yearbooks) + "년" + String.valueOf(monthbooks));
            mTotalBooks.setText(Integer.toString(yearbooks) + "권");

//            Log.e("연책,월책", String.valueOf(yearbooks) + "/" + String.valueOf(monthbooks));
            //종료 책 목록의 날짜 불러오기 종료

            // 독서시간 시작
            String times = sharedPreferences.getString("기록",null); // [{"2020-10-26":"4200"},{"2020-11-01":"4"},{"2020-10-31":"5"},{"2020-10-30":"13"}]
            try{
                JSONArray timearray = new JSONArray(times);
                ArrayList MonthlyTime = new ArrayList();
                ArrayList DailyTime = new ArrayList();
                totaltimes=0;
                for(int i=0; i<=timearray.length(); i++){
                    JSONObject timekey = timearray.getJSONObject(i);
                    Iterator<String> keys = timekey.keys();
                    while(keys.hasNext()){
                        String key = keys.next();
                        Log.e("181", key ); //2020-10-30
                        String[] recorddates = key.split("-");
                        int recordyear = Integer.parseInt(recorddates[0]); //2020
                        int recordmonth = Integer.parseInt(recorddates[1]); //10
                        int recordday = Integer.parseInt(recorddates[2]); //26

                        if(year == recordyear) //선택 연도와 기록 연도가 같다면 선택 연도 누적 시간 추가
                            totaltimes+=Integer.parseInt(timekey.get(key).toString());
                        //누적시간 넣기 시작
                        int hour = (totaltimes / 10) / 360;
                        int min = (totaltimes-3600*hour)/60;
                        int sec = totaltimes-3600*hour-60*min;
                        mTotalTime.setText(String.valueOf(hour)+"시간 "+String.valueOf(min)+"분 "+String.valueOf(sec)+"초"); //4210
                        //누적시간 넣기 종료
                        for(int Month=0; Month<=11; Month++){
                            monthlytimes=0;
                            if(year==recordyear&&Month+1==recordmonth){
                                monthlytimes += Integer.parseInt(timekey.get(key).toString()); //4200
                                Float mhour = (float) (monthlytimes / 10) / 360;
                                Log.e("203", String.valueOf(monthlytimes) );
                                monthlytimelist[Month] = mhour;
                                MonthlyTime.add(new BarEntry(monthlytimelist[Month], Month));
                                Log.e("206", String.valueOf(Month) );
                                Log.e("207", String.valueOf(monthlytimelist[Month]) );
                            }
                        }

                        // 바 차트3 시작
                        ArrayList day = new ArrayList();
                        for (int m = 1; m <= 12; m++) {
                            day.add(String.valueOf(m));
                        }
                        BarDataSet bardataset = new BarDataSet(MonthlyTime, "한 달 단위 독서 시간");
                        mBarChart3.animateY(2000);
                        BarData data = new BarData(day, bardataset);      // MPAndroidChart v3.X 오류 발생
                        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                        mBarChart3.getXAxis().setDrawGridLines(false);
                        mBarChart3.getAxisLeft().setDrawGridLines(false);
                        mBarChart3.getAxisRight().setDrawGridLines(false);
                        mBarChart3.setDescription(null);
                        mBarChart3.setData(data);
                        mBarChart3.getXAxis().setDrawAxisLine(false);
                        mBarChart3.getAxisRight().setDrawLabels(false);
                        mBarChart3.getAxisRight().setDrawAxisLine(false);
                        mBarChart3.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                        bardataset.setDrawValues(false);

                        // 바 차트3 종료

                        for(int Day=0; Day<=30; Day++){
                            dailytimes=0;
                            if(year==recordyear && month==recordmonth && Day+1==recordday) {
                                dailytimes += Integer.parseInt(timekey.get(key).toString());
                                Log.e("217", String.valueOf(dailytimes));
                                Float mhour = (float) (dailytimes / 10) / 360;
                                dailytimelist[Day] = mhour;
                                DailyTime.add(new BarEntry(dailytimelist[Day], Day));


                                // 바 차트4 시작
                                ArrayList day2 = new ArrayList();
                                for (int m = 1; m <= 31; m++) {
                                    day2.add(String.valueOf(m));
                                }
                                Log.e("251", "확인");
                                BarDataSet bardataset2 = new BarDataSet(DailyTime, "하루 단위 독서 시간");
                                mBarChart4.animateY(2000);
                                BarData data2 = new BarData(day2, bardataset2);      // MPAndroidChart v3.X 오류 발생
                                bardataset2.setColors(ColorTemplate.COLORFUL_COLORS);
                                mBarChart4.getXAxis().setDrawGridLines(false);
                                mBarChart4.getAxisLeft().setDrawGridLines(false);
                                mBarChart4.getAxisRight().setDrawGridLines(false);
                                mBarChart4.setDescription(null);
                                mBarChart4.setData(data2);
                                mBarChart4.getXAxis().setDrawAxisLine(false);
                                mBarChart4.getAxisRight().setDrawLabels(false);
                                mBarChart4.getAxisRight().setDrawAxisLine(false);
                                mBarChart4.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                bardataset2.setDrawValues(false);

                            }
                            // 바 차트4 종료
                        }


                    }
                }



            }catch (Exception e){

            }


            // 독서시간 종료
            // 차트 데이터 작성 시작
            try {
                JSONArray jsonArray = new JSONArray(json);
                // 바 차트1 시작
                ArrayList MonthlyReading = new ArrayList();
                for (int Month = 0; Month <= 11; Month++) {
                    monthlybooks = 0;
                    for (int i = 0; i < jsonArray.length(); i++) { //종료 책 목록 훑는중
                        String dates = jsonArray.getJSONObject(i).getString("종료날짜");
                        int endyear = Integer.parseInt(dates.substring(0, 4));//2020
                        int endmonth = Integer.parseInt(dates.substring(5, 7));//10
                        if (endyear == year && endmonth == Month + 1) {
                            monthlybooks++;
                        }

                    }
                    monthlybookslist[Month] = monthlybooks;
                    MonthlyReading.add(new BarEntry(monthlybookslist[Month], Month));
                    Log.e("책 권수", String.valueOf(monthlybooks)); //월간 권수
                    Log.e("Month", String.valueOf(Month)); //월


                }


                ArrayList day = new ArrayList();
                for (int i = 1; i <= 12; i++) {
                    day.add(String.valueOf(i));
                }

                BarDataSet bardataset = new BarDataSet(MonthlyReading, "한 달 단위 독서 권 수");
                mBarChart1.animateY(2000);
                BarData data = new BarData(day, bardataset);      // MPAndroidChart v3.X 오류 발생
                bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                mBarChart1.getXAxis().setDrawGridLines(false);
                mBarChart1.getAxisLeft().setDrawGridLines(false);
                mBarChart1.getAxisRight().setDrawGridLines(false);
                mBarChart1.setDescription(null);
                mBarChart1.setData(data);
                mBarChart1.getXAxis().setDrawAxisLine(false);
                mBarChart1.getAxisRight().setDrawLabels(false);
                mBarChart1.getAxisRight().setDrawAxisLine(false);
                mBarChart1.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                bardataset.setDrawValues(false);
                // 바 차트1 종료

            } catch (JSONException e) {
                e.printStackTrace();
            }


            // 차트 데이터 작성 종료


            // 바 차트2 시작

            for (int Day = 0; Day <= 30; Day++) {
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    ArrayList DailyReading = new ArrayList();
                    for(int day=0; day <=30; day++){
                        dailybooks = 0;
                        for (int i = 0; i < jsonArray.length(); i++) { //종료 책 목록 훑는중
                            String dates = jsonArray.getJSONObject(i).getString("종료날짜");
                            int endyear = Integer.parseInt(dates.substring(0, 4));//2020
                            int endmonth = Integer.parseInt(dates.substring(5, 7));//10
                            int endday = Integer.parseInt(dates.substring(8,10)); //26
                            Log.e("엔드데이", String.valueOf(endday) );
                            if (endyear == year && endmonth == month && endday == day + 1) {
                                dailybooks++;
                            }
//                            else {
//                                dailybooks = 0;
//                            }
                        }
                        dailybookslist[day] = dailybooks;
                        DailyReading.add(new BarEntry(dailybookslist[day], day));

                    }
                    ArrayList day = new ArrayList();
                    for (int i = 1; i <= 31; i++) {
                        day.add(String.valueOf(i));
                    }

                    BarDataSet bardataset = new BarDataSet(DailyReading, "하루 단위 독서 권 수");
                    mBarChart2.animateY(2000);
                    BarData data = new BarData(day, bardataset);      // MPAndroidChart v3.X 오류 발생
                    bardataset.setColors(ColorTemplate.COLORFUL_COLORS);


                    mBarChart2.getXAxis().setDrawGridLines(false);
                    mBarChart2.getAxisLeft().setDrawGridLines(false);
                    mBarChart2.getAxisRight().setDrawGridLines(false);
                    mBarChart2.setDescription(null);
                    mBarChart2.setData(data);
                    mBarChart2.getXAxis().setDrawAxisLine(false);
                    mBarChart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    mBarChart2.getAxisRight().setDrawLabels(false);
                    mBarChart2.getAxisRight().setDrawAxisLine(false);
                    bardataset.setDrawValues(false);
                } catch (Exception e) {

                }

            }



            // 바 차트2 종료

        }


    }



}