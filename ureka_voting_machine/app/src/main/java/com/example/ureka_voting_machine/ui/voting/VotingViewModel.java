package com.example.ureka_voting_machine.ui.voting;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.room.Room;

import com.example.ureka_voting_machine.model.AppDatabase;
import com.example.ureka_voting_machine.model.device.DeviceDB;
import com.example.ureka_voting_machine.model.device.DeviceDao;
import com.example.ureka_voting_machine.model.device.DeviceStorage;
import com.example.ureka_voting_machine.model.voting.Question;
import com.example.ureka_voting_machine.model.voting.QuestionDao;

import java.util.List;

public class VotingViewModel extends AndroidViewModel {

    DeviceStorage deviceStorage;
    LiveData<DeviceDB> deviceDB;
    AppDatabase db;
    DeviceDao deviceDao;
    QuestionDao questionDao;

    public VotingViewModel(@NonNull Application application) {
        super(application);
        db = Room.databaseBuilder(application.getApplicationContext(),
                AppDatabase.class, "AppDatabase").build();
        deviceDao = db.deviceDao();

        deviceDB = deviceDao.getFirst();

        questionDao = db.questionDao();
    }

    public void storeVotingResult(List<Question> questionList) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                for (Question q :
                        questionList) {
                    questionDao.insert(q);
                }
            }
        });
    }
}