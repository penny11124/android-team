package com.example.ureka_voting_machine.ui;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Room;

import com.example.ureka_voting_machine.model.AppDatabase;
import com.example.ureka_voting_machine.model.voting.Option;
import com.example.ureka_voting_machine.model.voting.OptionDao;
import com.example.ureka_voting_machine.model.voting.Question;
import com.example.ureka_voting_machine.model.voting.QuestionDao;
import com.example.ureka_voting_machine.model.voting.Voting;
import com.example.ureka_voting_machine.model.voting.VotingDao;
import com.example.ureka_voting_machine.ureka.CommandType;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private static final String TAG = "main view model";
    private MutableLiveData<CommandType> status = new MutableLiveData<>();
    private MutableLiveData<List<Question>> questions = new MutableLiveData<>();
    private List<Question> questionList = new ArrayList<>();

    private Application application;
    // room
    private QuestionDao questionDao;
    private OptionDao optionDao;
    private AppDatabase db;
    private Voting voting;
    private VotingDao votingDao;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

        db = Room.databaseBuilder(this.application.getApplicationContext(), AppDatabase.class, "AppDatabase").build();
        votingDao = db.votingDao();
        questionDao = db.questionDao();
        optionDao = db.optionDao();
        voting = new Voting();
//        status.setValue(CommandType.BILLING);
        status.setValue(voting.commandType);

        new getQuestions().execute();

        application.registerReceiver(gattCommandReceiver, new IntentFilter("COMMAND"));
    }

    private final BroadcastReceiver gattCommandReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String command = intent.getStringExtra("COMMAND");

            voting.commandType = CommandType.valueOf(command);
            status.setValue(CommandType.valueOf(command));

            Log.d(TAG, "onReceive: command: " + CommandType.valueOf(command).name());
        }
    };

    private class getQuestions extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            questionList = questionDao.getAllNow();
            questions.postValue(questionList);
            return null;
        }
    }

    public LiveData<CommandType> getStatus() {
        return status;
    }

    public void setStatus(CommandType commandType) {
        status.setValue(commandType);
    }

    public void setVoting(Voting voting) {
        this.voting = voting;
        status.setValue(voting.commandType);
//        votingDao.insert(voting);
    }

    public Voting getVoting() {
        return voting;
    }

    public void setQuestionList(List<Question> questionList) {
        this.questionList = questionList;
        new setQuestions(this.questionList).execute();
        questions.postValue(this.questionList);
    }

    @SuppressLint("StaticFieldLeak")
    private class setQuestions extends AsyncTask<Void, Void, Void> {
        private final List<Question> questions;

        public setQuestions(List<Question> questions) {
            this.questions = questions;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            questionDao.deleteAll();
            optionDao.deleteAll();
            for (int i = 0; i < this.questions.size(); i++) {
                Question q = this.questions.get(i);
                q.votingIdMap = 1;
                q.questionId = i + 1;
                questionDao.insert(q);
                List<Option> options = q.getOptions();
                for (int j = 0; j < options.size(); j++) {
                    Option o = options.get(j);
                    o.questionIdMap = i + 1;
                    o.optionPosition = j + 1;
                    o.optionNum = 0;
                    optionDao.insert(o);
                }
            }

            return null;
        }
    }

    public LiveData<List<Question>> getQuestions() {
        return questions;
//        return questionDao.getAll();
    }
}
