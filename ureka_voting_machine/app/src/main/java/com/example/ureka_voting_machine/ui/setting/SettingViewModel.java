package com.example.ureka_voting_machine.ui.setting;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.ureka_voting_machine.model.AppDatabase;
import com.example.ureka_voting_machine.model.voting.Option;
import com.example.ureka_voting_machine.model.voting.OptionDao;
import com.example.ureka_voting_machine.model.voting.Question;
import com.example.ureka_voting_machine.model.voting.QuestionDao;

import java.util.List;

public class SettingViewModel extends AndroidViewModel {

    private MutableLiveData<List<Question>> questions = new MutableLiveData<>();
    private MutableLiveData<List<Option>> options = new MutableLiveData<>();

    // room
    private QuestionDao questionDao;
    private OptionDao optionDao;
    private AppDatabase db;

    public SettingViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<Question>> getQuestions() {
        return questions;
    }

    public LiveData<List<Option>> getOptions() {
        return options;
    }
}