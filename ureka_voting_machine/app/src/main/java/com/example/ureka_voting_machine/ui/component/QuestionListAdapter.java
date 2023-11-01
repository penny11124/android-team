package com.example.ureka_voting_machine.ui.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ureka_voting_machine.R;
import com.example.ureka_voting_machine.model.voting.Question;

import java.util.List;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.ViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private List<Question> list;
    private Activity activity;

    public QuestionListAdapter() {
    }

    public QuestionListAdapter(List<Question> questions, Activity activity) {
        this.list = questions;
        this.activity = activity;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView questionTitle;
        private RecyclerView resultList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTitle = (TextView) itemView.findViewById(R.id.question_title);
            resultList = (RecyclerView) itemView.findViewById(R.id.result_list);
        }
    }

    @NonNull
    @Override
    public QuestionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.voting_question, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionListAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.questionTitle.setText(list.get(position).topic);
//        holder.resultList.setLayoutManager(new LinearLayoutManager(activity));
//        holder.resultList.addItemDecoration(new DividerItemDecoration(activity, DividerItemDecoration.VERTICAL));
        holder.resultList.setAdapter(new ResultListAdapter(list.get(position).getOptions()));
        holder.resultList.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<Question> getList() {
        return list;
    }

}
