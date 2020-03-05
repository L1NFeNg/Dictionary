package com.example.words;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

class WordRepository {
    private LiveData<List<Word>> allWordsLive;
    private WordDao wordDao;

    WordRepository(Context context) {
        WordDatabase wordDatabase = WordDatabase.getDatabase(context.getApplicationContext());
        wordDao = wordDatabase.getWordDao();
        allWordsLive = wordDao.getAllWordsLive();
    }

    void insertWords(Word... words) {
        new InsertAsynTask(wordDao).execute(words);
    }

    void updateWords(Word... words) {
        new UpdateAsynTask(wordDao).execute(words);
    }

    void deleteWords(Word... words) {
        new DeleteAsynTask(wordDao).execute(words);
    }

    void deleteAllWords(Word... words) {
        new DeleteAllAsynTask(wordDao).execute();
    }

    LiveData<List<Word>> getAllWordsLive() {
        return allWordsLive;
    }

    LiveData<List<Word>> findWordsWithPattern(String pattern) {
        return wordDao.findWordsWithPattern("%" + pattern + "%");
    }

    static class InsertAsynTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        InsertAsynTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.insertWords(words);
            return null;
        }
    }

    static class UpdateAsynTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        UpdateAsynTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.updateWords(words);
            return null;
        }
    }

    static class DeleteAsynTask extends AsyncTask<Word, Void, Void> {
        private WordDao wordDao;

        DeleteAsynTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Word... words) {
            wordDao.deleteWords(words);
            return null;
        }
    }

    static class DeleteAllAsynTask extends AsyncTask<Void, Void, Void> {
        private WordDao wordDao;

        DeleteAllAsynTask(WordDao wordDao) {
            this.wordDao = wordDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wordDao.deleteAllWords();
            return null;
        }
    }
}
