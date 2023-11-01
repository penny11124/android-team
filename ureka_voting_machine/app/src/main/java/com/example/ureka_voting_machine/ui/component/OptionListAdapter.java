package com.example.ureka_voting_machine.ui.component;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ureka_voting_machine.R;
import com.example.ureka_voting_machine.model.voting.Option;

import java.util.List;

public class OptionListAdapter extends RecyclerView.Adapter<OptionListAdapter.ViewHolder> {
    private List<Option> list;
    private boolean nextQuestion;

    public OptionListAdapter() {
    }

    public OptionListAdapter(List<Option> options) {
        this.list = options;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private Button btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btn = (Button) itemView.findViewById(R.id.option_btn);
        }
    }

    @NonNull
    @Override
    public OptionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.voting_option, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        holder.btn.setText(String.valueOf(list.get(position).optionNum));
        holder.btn.setText(list.get(position).text);

        holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion = true;
                list.get(position).optionNum++;
                notifyDataSetChanged();
            }
        });
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
