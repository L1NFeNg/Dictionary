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
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class MyAdapter extends ListAdapter<Word, MyAdapter.MyViewHolder> {

    private boolean isUseCardView;
    private WordViewModel wordViewModel;

    MyAdapter(boolean isUseCardView, WordViewModel wordViewModel) {
        super(new DiffUtil.ItemCallback<Word>() {
            @Override
            // 比较ID是否相同
            public boolean areItemsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return oldItem.getId() == newItem.getId();
            }

            @Override
            public boolean areContentsTheSame(@NonNull Word oldItem, @NonNull Word newItem) {
                return (oldItem.getWord().equals(newItem.getWord())
                        && oldItem.getChineseMeaning().equals(newItem.getChineseMeaning())
                        && oldItem.isChineseInVisible() == newItem.isChineseInVisible());
            }
        });
        this.isUseCardView = isUseCardView;
        this.wordViewModel = wordViewModel;
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
        final Word word = getItem(position);
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
    public void onViewAttachedToWindow(@NonNull MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.mTvNumber.setText(String.valueOf(holder.getAdapterPosition() + 1));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTvNumber;
        TextView mTvEnglish;
        TextView mTvChinese;
        Switch mSwIsChineseInvisible;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvNumber = itemView.findViewById(R.id.tv_number);
            mTvEnglish = itemView.findViewById(R.id.tv_english);
            mTvChinese = itemView.findViewById(R.id.tv_chinese);
            mSwIsChineseInvisible = itemView.findViewById(R.id.sw_isChineseInvisible);
        }
    }
}