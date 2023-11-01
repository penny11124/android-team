package com.example.ureka_voting_machine.model;

import androidx.room.TypeConverter;

import com.example.ureka_voting_machine.model.voting.Option;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class Converters {
    @TypeConverter
    public static ArrayList<Option> fromString(String value) {
        Type listType = new TypeToken<ArrayList<Option>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArrayList(ArrayList<Option> list) {
        Gson gson = new Gson();
        String json = gson.toJson(list);
        return json;
    }
}
