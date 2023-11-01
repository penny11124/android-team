package com.example.ureka_voting_machine.model;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.ureka_voting_machine.model.device.DeviceDB;
import com.example.ureka_voting_machine.model.device.DeviceDao;
import com.example.ureka_voting_machine.model.ticket.TicketDB;
import com.example.ureka_voting_machine.model.ticket.TicketDao;
import com.example.ureka_voting_machine.model.voting.Option;
import com.example.ureka_voting_machine.model.voting.OptionDao;
import com.example.ureka_voting_machine.model.voting.Question;
import com.example.ureka_voting_machine.model.voting.QuestionDao;
import com.example.ureka_voting_machine.model.voting.Voting;
import com.example.ureka_voting_machine.model.voting.VotingDao;

@Database(entities = {DeviceDB.class, TicketDB.class, Voting.class, Question.class, Option.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract DeviceDao deviceDao();

    public abstract TicketDao ticketDao();

    public abstract VotingDao votingDao();

    public abstract QuestionDao questionDao();

    public abstract OptionDao optionDao();

}