package com.example.ureka_voting_machine.ui.voting;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ureka_voting_machine.R;
import com.example.ureka_voting_machine.databinding.FragmentBillingBinding;
import com.example.ureka_voting_machine.model.voting.Question;
import com.example.ureka_voting_machine.ui.MainViewModel;
import com.example.ureka_voting_machine.ui.component.QuestionListAdapter;
import com.example.ureka_voting_machine.ureka.CommandType;

import java.util.ArrayList;
import java.util.List;

public class BillingFragment extends Fragment {
    private static final String TAG = "billing fragment";
    private FragmentBillingBinding binding;
    private TextView title;
    private RecyclerView questionList;
    private QuestionListAdapter adapter;
    private List<Question> questions = new ArrayList<>();
    private VotingViewModel votingViewModel;
    private MainViewModel mainViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        votingViewModel =
                new ViewModelProvider(this).get(VotingViewModel.class);

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        NavController navController = NavHostFragment.findNavController(this);

        binding = FragmentBillingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        title = binding.textTitle;
        title.setText(R.string.title_billing);

        questionList = binding.questionList;
        questionList.setLayoutManager(new LinearLayoutManager(getActivity()));
        questionList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        mainViewModel.getQuestions().observe(getViewLifecycleOwner(), new Observer<List<Question>>() {
            @Override
            public void onChanged(List<Question> questionsList) {
                questions = questionsList;
                updateView();
            }
        });

        // go to wait if ticket not verified
        mainViewModel.getStatus().observe(getViewLifecycleOwner(), new Observer<CommandType>() {
            @Override
            public void onChanged(CommandType commandType) {
                if (CommandType.BILLING != commandType) {
                    navController.navigate(R.id.navigation_ble);
                }
            }
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateView() {
        if (null == questions) {
            return;
        }
        adapter = new QuestionListAdapter(questions, getActivity());
        questionList.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}