package com.example.words;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class WordsFragment extends Fragment {
    private WordViewModel wordViewModel;
    private RecyclerView mRv1;
    private MyAdapter myAdapter1, myAdapter2;
    private FloatingActionButton mFABToAddFragment;
    private LiveData<List<Word>> filteredWords;
    private static final String VIEW_TYPE_SHP = "view_type_shp";
    private static final String IS_USING_CARD_VIEW = "is_using_card_view";

    public WordsFragment() {
        // Required empty public constructor
        // 默认不显示工具条，改为显示
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_cleaerdata:
                // 对话窗口
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("清空数据");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        wordViewModel.deleteAllWords();
                    }
                });

                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.create();
                builder.show();
                break;
            // 切换视图
            case R.id.mi_switchviewtype:
                SharedPreferences sp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
                boolean viewType = sp.getBoolean(IS_USING_CARD_VIEW, false);
                SharedPreferences.Editor editor = sp.edit();
                if (viewType) {
                    mRv1.setAdapter(myAdapter1);
                    editor.putBoolean(IS_USING_CARD_VIEW, false);
                } else {
                    mRv1.setAdapter(myAdapter2);
                    editor.putBoolean(IS_USING_CARD_VIEW, true);
                }
                editor.apply();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    // 申明绑定工具条
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.main_menu, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.app_bar_search).getActionView();
        searchView.setMaxWidth(700);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 查询
            @Override
            public boolean onQueryTextChange(String newText) {
                String pattern = newText.trim();
                // 移除原有观察
                filteredWords.removeObservers(requireActivity());
                filteredWords = wordViewModel.findWordsWithPattern(pattern);
                filteredWords.observe(requireActivity(), new Observer<List<Word>>() {
                    @Override
                    public void onChanged(List<Word> words) {
                        int temp = myAdapter1.getItemCount();
                        if (temp != words.size()) {
                            myAdapter1.notifyDataSetChanged();
                            myAdapter2.notifyDataSetChanged();
                        }
                    }
                });
                return true;
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_words, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        wordViewModel = ViewModelProviders.of(requireActivity()).get(WordViewModel.class);
        mRv1 = requireActivity().findViewById(R.id.rv_1);
        mRv1.setLayoutManager(new LinearLayoutManager(requireActivity()));
        myAdapter1 = new MyAdapter(false, wordViewModel);
        myAdapter2 = new MyAdapter(true, wordViewModel);
        mRv1.setItemAnimator(new DefaultItemAnimator() {
            @Override
            public void onAnimationFinished(@NonNull RecyclerView.ViewHolder viewHolder) {
                super.onAnimationFinished(viewHolder);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRv1.getLayoutManager();
                if (linearLayoutManager != null) {
                    int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastPostion = linearLayoutManager.findLastVisibleItemPosition();
                    for (int i = firstPosition; i < lastPostion; i++) {
                        MyAdapter.MyViewHolder holder = (MyAdapter.MyViewHolder) mRv1.findViewHolderForAdapterPosition(i);
                        if (holder != null) {
                            holder.mTvNumber.setText(String.valueOf(i + 1));

                        }
                    }
                }
            }
        });
        SharedPreferences sp = requireActivity().getSharedPreferences(VIEW_TYPE_SHP, Context.MODE_PRIVATE);
        boolean viewType = sp.getBoolean(IS_USING_CARD_VIEW, false);
        if (viewType) {
            mRv1.setAdapter(myAdapter2);

        } else {
            mRv1.setAdapter(myAdapter1);
        }

//        mRv1.setAdapter(myAdapter1);
        filteredWords = wordViewModel.getAllWordsLive();
        filteredWords.observe(requireActivity(), new Observer<List<Word>>() {
            @Override
            public void onChanged(List<Word> words) {
                int temp = myAdapter1.getItemCount();
                if (temp != words.size()) {
                    // 单个添加 有动画
                    //myAdapter1.notifyItemInserted(0);
                    //myAdapter2.notifyItemInserted(0);
                    // 整体刷新 开销大
                    //myAdapter1.notifyDataSetChanged();
                    //myAdapter2.notifyDataSetChanged();
                    mRv1.smoothScrollBy(0, -200);
                    myAdapter1.submitList(words);
                    myAdapter2.submitList(words);
                }
            }
        });
        mFABToAddFragment = requireActivity().findViewById(R.id.fab_toaddfragment);
        mFABToAddFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_wordsFragment_to_addFragment);
            }
        });
    }

}
