package com.example.ureka_voting_machine.ui.component;

import com.example.ureka_voting_machine.model.voting.Option;

public class ListItem extends Option {
    private String text;

    public ListItem(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
