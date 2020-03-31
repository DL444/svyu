package mg.studio.android.survey.views;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialView;

import java.util.ArrayList;

import mg.studio.android.survey.R;
import mg.studio.android.survey.models.QuestionType;
import mg.studio.android.survey.viewmodels.IQuestionViewModel;

public class ComposerListFragment extends Fragment {

    public ComposerListFragment() { }

    public static ComposerListFragment newInstance() {
        return newInstance(new ArrayList<IQuestionViewModel>());
    }

    public static ComposerListFragment newInstance(ArrayList<IQuestionViewModel> questions) {
        ComposerListFragment fragment = new ComposerListFragment();
        Bundle args = new Bundle();
        args.putSerializable("questions", questions);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            questions = (ArrayList<IQuestionViewModel>) getArguments().getSerializable("questions");
        } else {
            questions = new ArrayList<>();
        }
        adapter = new ComposerListAdapter(questions);
        adapter.registerAdapterDataObserver(observer);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_composer_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = getView().findViewById(R.id.composerListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        speedDial = getView().findViewById(R.id.addQuestionMain);
        speedDial.inflate(R.menu.add_question_speed_dial);
        speedDial.setOnActionSelectedListener(addQuestionListener);

        emptyIndicator = getView().findViewById(R.id.composerNoQuetionsHint);
        emptyIndicator.setVisibility(questions.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IAddQuestionRequestListener) {
            addQuestionRequestListener = (IAddQuestionRequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IAddQuestionRequestListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        addQuestionRequestListener = null;
    }

    public void addQuestion(IQuestionViewModel question) {
        questions.add(question);
        adapter.notifyItemInserted(questions.size() - 1);
    }

    private SpeedDialView.OnActionSelectedListener addQuestionListener = new SpeedDialView.OnActionSelectedListener() {
        @Override
        public boolean onActionSelected(SpeedDialActionItem actionItem) {
            ComposerListFragment.this.speedDial.close();
            switch (actionItem.getId()) {
                case R.id.singleChoiceQuestonAdd:
                    addQuestionRequestListener.onAddQuestionRequested(QuestionType.Single);
                    return true;
                case R.id.multiChoiceQuestionAdd:
                    addQuestionRequestListener.onAddQuestionRequested(QuestionType.Multiple);
                    return true;
                case R.id.textQuestionAdd:
                    addQuestionRequestListener.onAddQuestionRequested(QuestionType.Text);
                    return true;
                default:
                    return false;
            }
        }
    };

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            checkEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            checkEmpty();
        }

        private void checkEmpty() {
            if (adapter.getItemCount() == 0) {
                ComposerListFragment.this.emptyIndicator.setVisibility(View.VISIBLE);
            } else {
                ComposerListFragment.this.emptyIndicator.setVisibility(View.GONE);
            }
        }
    };

    private SpeedDialView speedDial;
    private ArrayList<IQuestionViewModel> questions;
    private ComposerListAdapter adapter;
    private TextView emptyIndicator;

    private IAddQuestionRequestListener addQuestionRequestListener;
}
