package com.example.ureka_voting_machine.ureka;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.room.Room;

import com.example.ureka_voting_machine.model.AppDatabase;
import com.example.ureka_voting_machine.model.voting.Voting;
import com.example.ureka_voting_machine.model.voting.VotingDao;
import com.logos.ticket_module.Command;
import com.logos.ticket_module.Execution;

import java.util.Arrays;

public class CommandExecution implements Execution {
    private final String TAG = "CommandExecution";

    private Application application;
    // room
    private AppDatabase db;
    private Voting voting;
    private VotingDao votingDao;


    public CommandExecution(Application application) {
        this.application = application;
        db = Room.databaseBuilder(this.application.getApplicationContext(), AppDatabase.class, "AppDatabase").build();
        votingDao = db.votingDao();
        voting = new Voting();
//        new getVoting().execute();
    }

    @Override
    public void executeCommand(Command command) {
        byte[] aByte = command.getByte();
        Log.d(TAG, "executeCommand: execute command!");
        if (Arrays.equals(aByte, CommandType.VOTING.name().getBytes())) {
            voting.votingId = 1;
            voting.state = 1;
            voting.commandType = CommandType.VOTING;
            Log.d(TAG, "executeCommand: voting");
        } else if (Arrays.equals(aByte, CommandType.SETTING.name().getBytes())) {
            voting.votingId = 1;
            voting.state = 2;
            voting.commandType = CommandType.SETTING;
            Log.d(TAG, "executeCommand: setting");
        } else if (Arrays.equals(aByte, CommandType.BILLING.name().getBytes())) {
            voting.votingId = 1;
            voting.state = 3;
            voting.commandType = CommandType.BILLING;
            Log.d(TAG, "executeCommand: billing");
        }
//        votingDao.insert(voting);
    }

    public Voting getVoting() {
        return voting;
    }

    private class getVoting extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            voting = votingDao.getFirstNow();
            if (voting == null) {
                // init voting
                Log.d(TAG, "CommandExecution: voting == null");
                voting = new Voting();
                votingDao.insert(voting);
            } else {
                Log.d(TAG, "CommandExecution: voting != null" + voting.votingId + ", " + voting.state + ", " + voting.commandType.name());
            }
            return null;
        }
    }

    public void setVoting(Voting voting) {
        this.voting = voting;
    }
}
