package com.example.readingnote;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.readingnote.Library.Book_ing_detail;
import com.example.readingnote.Library.EditNote;
import com.example.readingnote.Library.Library;
import com.example.readingnote.Library.Note_item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.Note_item_ViewHolder> {
    ArrayList<Note_item> NoteList;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor ediotr;
    int bookposition;


    //생성자
    public NoteAdapter(ArrayList<Note_item> noteList) {
        NoteList = noteList;

    }
    // 뷰홀더 클래스 시작
    static class Note_item_ViewHolder extends RecyclerView.ViewHolder{
        private TextView mSavednote_date, mSavednote_contents;
        private ImageView mSavednote_image, mSavednote_delete, mSavednote_edit;

        public Note_item_ViewHolder(@NonNull View itemView) {
            super(itemView);
            mSavednote_date = itemView.findViewById(R.id.Savednote_date);
            mSavednote_image = itemView.findViewById(R.id.Savednote_image);
            mSavednote_contents = itemView.findViewById(R.id.Savednote_contents);
            mSavednote_delete = itemView.findViewById(R.id.Note_delete);
            mSavednote_edit = itemView.findViewById(R.id.Note_edit);

        }


    }
    // 뷰홀더 클래스 끝

    //어댑터 연결 시작
    @NonNull
    @Override
    public Note_item_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        sharedPreferences = parent.getContext().getSharedPreferences("책",0);
        ediotr=sharedPreferences.edit();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_note_item, parent, false);
        Note_item_ViewHolder holder = new Note_item_ViewHolder(view);

        return holder;


    }

    @Override
    public void onBindViewHolder(@NonNull final Note_item_ViewHolder holder, final int position) {
        holder.mSavednote_date.setText(NoteList.get(position).Savednote_date);
        holder.mSavednote_contents.setText(NoteList.get(position).Savednote_contents);
        Log.e("이미지 값 확인",NoteList.get(position).Savednote_contents );
        if(NoteList.get(position).Savednote_image!=null && !NoteList.get(position).Savednote_image.isEmpty()){
            Log.e("이미지 값 확인",NoteList.get(position).Savednote_image );
        Uri uri = Uri.parse(NoteList.get(position).Savednote_image);
        holder.mSavednote_image.setImageURI(uri);

        }else{
            Log.e("이미지 값 확인", "이미지 값이 없다구" );
            holder.mSavednote_image.setVisibility(View.GONE);
        }


        //삭제 버튼 시작
        holder.mSavednote_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("");
                builder.setMessage("읽고 있는 책 목록에서 삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String json = sharedPreferences.getString("저장목록",null);
                                        Log.e("Note Adapter 저장목록 호출", json );
                                        Log.e("날짜",holder.mSavednote_date.getText().toString());
                                        Log.e("내용",holder.mSavednote_contents.getText().toString());
                                        try {
                                            JSONArray jsonArray = new JSONArray(json);
                                            int bookposition = 0; // savedbooklist의 인덱스임
                                            //노트의 날짜와 내용이 속한 노트가 속한 북디테일의 인덱스 값 찾기 : position
                                            for(int n=0; n<jsonArray.length(); n++){
                                                String notelist = jsonArray.getJSONObject(n).getString("노트목록");
                                                if(notelist.contains(holder.mSavednote_date.getText().toString())){
                                                    bookposition = n;
                                                }
                                        Log.e("notelist", notelist );

                                    }Log.e("포지션", Integer.toString(bookposition) );
                                    String notejson = jsonArray.getJSONObject(bookposition).getString("노트목록");
                                    JSONArray notearray = new JSONArray(notejson);
                                    notearray.remove(position);
                                    Log.e("notelist 삭제 후 노트목록",notearray.toString()); // 이제 이걸 저장 목록에 반영해야 함
                                    Log.e("삭제된 노트목록을 저장할 저장목록",jsonArray.getJSONObject(bookposition).toString());
                                    jsonArray.getJSONObject(bookposition).put("노트목록",notearray);
                                    Log.e("삭제된 노트목록을 저장할 저장목록2",jsonArray.getJSONObject(bookposition).getString("노트목록"));
                                    Log.e("전체 저장목록",jsonArray.toString());
                                    ediotr.putString("저장목록",jsonArray.toString());
                                    ediotr.apply();
                                    notifyItemRemoved(position);
                                    NoteList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyItemRangeChanged(position,NoteList.size());

//                    jsonArray.getJSONObject(bookposition).put("노트목록",notearray.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
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
        //삭제 버튼 끝

        //수정 버튼 시작
        holder.mSavednote_edit.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                String json = sharedPreferences.getString("저장목록",null);
                Log.e("Note Adapter 저장목록 호출", json );
                Log.e("날짜",holder.mSavednote_date.getText().toString());
                Log.e("내용",holder.mSavednote_contents.getText().toString());
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    bookposition = 0; // savedbooklist의 인덱스임
                    //노트의 날짜와 내용이 속한 노트가 속한 북디테일의 인덱스 값 찾기 : position
                    for(int n=0; n<jsonArray.length(); n++){
                        String notelist = jsonArray.getJSONObject(n).getString("노트목록");
                        if(notelist.contains(holder.mSavednote_date.getText().toString())){
                            bookposition = n;
                        }



                    }Log.e("포지션", Integer.toString(bookposition) );

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(v.getContext(), EditNote.class);
                String image = NoteList.get(position).Savednote_image;
                if(image != null){
                    intent.putExtra("이미지",image);
                    Log.e("저장 이미지",image);
                }
                String text = NoteList.get(position).Savednote_contents;
                if(text != null) {
                    intent.putExtra("글", text);
                    Log.e("저장 글",text);
                }
                intent.putExtra("노트포지션",position);
                intent.putExtra("북포지션",bookposition);
                v.getContext().startActivity(intent);
            }
        });
        //수정 버튼 끝
    }

    @Override
    public int getItemCount() {
        return NoteList.size();
    }
    //어댑터 연결 끝


}