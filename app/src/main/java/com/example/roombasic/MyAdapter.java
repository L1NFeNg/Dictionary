package com.example.roombasic;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    List<Word> allWords = new ArrayList<>();
    boolean isUseCardView;

    public MyAdapter(boolean isUseCardView) {
        this.isUseCardView = isUseCardView;
    }

    public void setAllWords(List<Word> allWords) {
        this.allWords = allWords;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = null;
        if (isUseCardView) {
            itemView = layoutInflater.inflate(R.layout.cell_card, parent, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.cell_normal, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Word word = allWords.get(position);
        holder.mTvNumber.setText(String.valueOf(word.getId()));
        holder.mTvEnglish.setText(word.getWord());
        holder.mTvChinese.setText(word.getChineseMeaning());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://translate.google.cn/?hl=zh-CN&tab=TT#view=home&op=translate&sl=en&tl=zh-CN&text="
                        + holder.mTvEnglish.getText());
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                holder.itemView.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return allWords.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTvNumber;
        TextView mTvEnglish;
        TextView mTvChinese;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvNumber = itemView.findViewById(R.id.tv_number);
            mTvEnglish = itemView.findViewById(R.id.tv_english);
            mTvChinese = itemView.findViewById(R.id.tv_chinese);
        }
    }

}
