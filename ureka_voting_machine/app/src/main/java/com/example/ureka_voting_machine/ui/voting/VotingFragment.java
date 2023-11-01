package com.example.ureka_voting_machine.ui.voting;

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
import com.example.ureka_voting_machine.databinding.FragmentVotingBinding;
import com.example.ureka_voting_machine.model.voting.Option;
import com.example.ureka_voting_machine.model.voting.Question;
import com.example.ureka_voting_machine.ui.MainViewModel;
import com.example.ureka_voting_machine.ui.component.OptionListAdapter;
import com.example.ureka_voting_machine.ureka.CommandType;

import java.util.ArrayList;
import java.util.List;

public class VotingFragment extends Fragment {

    private static final String TAG = "voting fragment";
    private FragmentVotingBinding binding;
    private TextView title;
    private RecyclerView optionList;
    private OptionListAdapter adapter;
    private RecyclerView.AdapterDataObserver adapterDataObserver;
    private List<Question> questions = new ArrayList<>();
    private VotingViewModel votingViewModel;
    private MainViewModel mainViewModel;
    private int questionNum = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        votingViewModel =
                new ViewModelProvider(this).get(VotingViewModel.class);

        mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);

        NavController navController = NavHostFragment.findNavController(this);

        binding = FragmentVotingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        title = binding.textTitle;
        optionList = binding.optionList;
        optionList.setLayoutManager(new LinearLayoutManager(getActivity()));
        optionList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        adapterDataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (adapter.isNextQuestion()) {
                    questionNum++;
                    updateView();
                }
            }
        };

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
                if (CommandType.VOTING != commandType) {
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

    public void updateView() {
        if (null == questions) {
            return;
        }
        if (questions.size() > questionNum) {
            adapter = new OptionListAdapter(questions.get(questionNum).getOptions());
            adapter.registerAdapterDataObserver(adapterDataObserver);
            optionList.setAdapter(adapter);
            title.setText(questions.get(questionNum).topic);
            adapter.notifyDataSetChanged();
        } else {
            Log.d(TAG, "updateView: question is empty");
            for (Question q :
                    questions) {
                Log.d(TAG, "question: " + q.topic);
                for (Option o :
                        q.getOptions()) {
                    Log.d(TAG, "option: " + o.optionId + o.text + "-" + o.optionNum);
                }
            }
            votingViewModel.storeVotingResult(questions);
            mainViewModel.getVoting().commandType = CommandType.DEFAULT;
            mainViewModel.setVoting(mainViewModel.getVoting());
        }
    }
}