package com.example.ureka_voting_machine.ui.setting;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.ureka_voting_machine.databinding.FragmentSettingBinding;
import com.example.ureka_voting_machine.model.voting.Option;
import com.example.ureka_voting_machine.model.voting.Question;
import com.example.ureka_voting_machine.model.voting.Voting;
import com.example.ureka_voting_machine.ui.MainViewModel;
import com.example.ureka_voting_machine.ui.component.OptionListAdapter;
import com.example.ureka_voting_machine.ureka.CommandType;

import java.util.ArrayList;
import java.util.List;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    private TextView textTitle;
    private RecyclerView optionList;
    private OptionListAdapter adapter;
    private EditText settingTitle;
    private EditText settingOption;
    private Button addBtn;
    private Button addQuestionBtn;
    private Button doneBtn;

    private List<Question> questions = new ArrayList<>();
    private List<Option> options = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // view model
        SettingViewModel settingViewModel =
                new ViewModelProvider(this).get(SettingViewModel.class);
        MainViewModel mainViewModel = new ViewModelProvider(getActivity()).get(MainViewModel.class);
        // go to wait fragment if voting state is default
        NavController navController = NavHostFragment.findNavController(this);

        mainViewModel.getStatus().observe(getViewLifecycleOwner(), new Observer<CommandType>() {
            @Override
            public void onChanged(CommandType state) {
                if (CommandType.SETTING != state) {
                    navController.navigate(R.id.navigation_ble);
                }
            }
        });

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textTitle = binding.textTitle;
        optionList = binding.optionList;
        settingTitle = binding.settingTitle;
        settingOption = binding.settingOption;
        addBtn = binding.addBtn;
        addQuestionBtn = binding.addQuestionBtn;
        doneBtn = binding.doneBtn;

        textTitle.setText(settingTitle.getText());

        optionList.setLayoutManager(new LinearLayoutManager(getActivity()));
        optionList.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        adapter = new OptionListAdapter(options);
        optionList.setAdapter(adapter);

        settingTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                textTitle.setText(s);
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                options.add(new Option(settingOption.getText().toString()));
                adapter.notifyItemChanged(options.size() - 1);
            }
        });

        addQuestionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                questions.add(new Question(textTitle.getText().toString(), new ArrayList<>(options)));
                options.clear();
                textTitle.setText("");
                settingOption.setText("");
                settingTitle.setText("");
                adapter.notifyDataSetChanged();
            }
        });
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                options.clear();
                textTitle.setText("");
                settingOption.setText("");
                settingTitle.setText("");
                adapter.notifyDataSetChanged();
                mainViewModel.setQuestionList(questions);
                mainViewModel.setVoting(new Voting());
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}