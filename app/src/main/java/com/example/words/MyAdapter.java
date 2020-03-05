package com.example.words;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private List<Word> allWords = new ArrayList<>();
    private boolean isUseCardView;
    private WordViewModel wordViewModel;

    MyAdapter(boolean isUseCardView, WordViewModel wordViewModel) {
        this.isUseCardView = isUseCardView;
        this.wordViewModel = wordViewModel;
    }

    void setAllWords(List<Word> allWords) {
        this.allWords = allWords;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View itemView = null;
        if (isUseCardView) {
            itemView = layoutInflater.inflate(R.layout.cell_card_2, parent, false);
        } else {
            itemView = layoutInflater.inflate(R.layout.cell_normal_2, parent, false);
        }

        final MyViewHolder holder = new MyViewHolder(itemView);
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

        holder.mSwIsChineseInvisible.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Word word = (Word) holder.itemView.getTag(R.id.WORD_FOR_VIEW_HOLDER);
                if (isChecked) {
                    holder.mTvChinese.setVisibility(View.GONE);
                    word.setChineseInVisible(true);
                    wordViewModel.updateWords(word);
                } else {
                    holder.mTvChinese.setVisibility(View.VISIBLE);
                    word.setChineseInVisible(false);
                    wordViewModel.updateWords(word);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final Word word = allWords.get(position);
        holder.itemView.setTag(R.id.WORD_FOR_VIEW_HOLDER, word);
        holder.mTvNumber.setText(String.valueOf(word.getId()));
        holder.mTvEnglish.setText(word.getWord());
        holder.mTvChinese.setText(word.getChineseMeaning());
        if (word.isChineseInVisible()) {
            holder.mTvChinese.setVisibility(View.GONE);
            holder.mSwIsChineseInvisible.setChecked(true);
        } else {
            holder.mTvChinese.setVisibility(View.VISIBLE);
            holder.mSwIsChineseInvisible.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return allWords.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTvNumber;
        TextView mTvEnglish;
        TextView mTvChinese;
        Switch mSwIsChineseInvisible;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvNumber = itemView.findViewById(R.id.tv_number);
            mTvEnglish = itemView.findViewById(R.id.tv_english);
            mTvChinese = itemView.findViewById(R.id.tv_chinese);
            mSwIsChineseInvisible = itemView.findViewById(R.id.sw_isChineseInvisible);
        }
    }
}