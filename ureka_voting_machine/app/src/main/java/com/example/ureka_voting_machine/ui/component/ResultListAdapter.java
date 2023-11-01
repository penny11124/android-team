package com.example.ureka_voting_machine.ui.component;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ureka_voting_machine.R;
import com.example.ureka_voting_machine.model.voting.Option;

import java.util.List;

public class ResultListAdapter extends RecyclerView.Adapter<ResultListAdapter.ViewHolder> {
    private List<Option> list;
    private boolean nextQuestion;

    public ResultListAdapter() {
    }

    public ResultListAdapter(List<Option> options) {
        this.list = options;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView optionTitle;
        private TextView optionNum;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            optionTitle = (TextView) itemView.findViewById(R.id.option_title);
            optionNum = (TextView) itemView.findViewById(R.id.option_num);
        }
    }

    @NonNull
    @Override
    public ResultListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.voting_result, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.optionTitle.setText(list.get(position).text);
        holder.optionNum.setText(Integer.toString(list.get(position).optionNum));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<Option> getList() {
        return list;
    }

    public boolean isNextQuestion() {
        return nextQuestion;
    }
}
