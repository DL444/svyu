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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
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
        adapter = new ComposerListAdapter(questions, itemClickListener);
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

        ItemTouchHelper gestureHelper = new ItemTouchHelper(gestureCallback);
        gestureHelper.attachToRecyclerView(recyclerView);

        speedDial = getView().findViewById(R.id.addQuestionMain);
        speedDial.inflate(R.menu.add_question_speed_dial);
        speedDial.setOnActionSelectedListener(addQuestionListener);

        emptyIndicator = getView().findViewById(R.id.composerNoQuetionsHint);
        emptyIndicator.setVisibility(questions.size() == 0 ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IQuestionOperationRequestListener) {
            questionOperationRequestListener = (IQuestionOperationRequestListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement IQuestionOperationRequestListener.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        questionOperationRequestListener = null;
    }

    public void addQuestion(IQuestionViewModel question) {
        questions.add(question);
        adapter.notifyItemInserted(questions.size() - 1);
    }

    public void updateQuestion(int index) {
        adapter.notifyItemChanged(index);
    }

    private IItemClickCallback itemClickListener = new IItemClickCallback() {
        @Override
        public void onItemClicked(int index) {
            questionOperationRequestListener.onUpdateQuestionRequested(questions.get(index), index);
        }
    };

    private SpeedDialView.OnActionSelectedListener addQuestionListener = new SpeedDialView.OnActionSelectedListener() {
        @Override
        public boolean onActionSelected(SpeedDialActionItem actionItem) {
            ComposerListFragment.this.speedDial.close();
            switch (actionItem.getId()) {
                case R.id.singleChoiceQuestonAdd:
                    questionOperationRequestListener.onAddQuestionRequested(QuestionType.Single);
                    return true;
                case R.id.multiChoiceQuestionAdd:
                    questionOperationRequestListener.onAddQuestionRequested(QuestionType.Multiple);
                    return true;
                case R.id.textQuestionAdd:
                    questionOperationRequestListener.onAddQuestionRequested(QuestionType.Text);
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

    private ItemTouchHelper.Callback gestureCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP | ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            moveItem(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            recentDeletionIndex = viewHolder.getAdapterPosition();
            recentDeletion = questions.get(recentDeletionIndex);
            questions.remove(recentDeletionIndex);
            adapter.notifyItemRemoved(recentDeletionIndex);

            Snackbar removeSnack = Snackbar.make(ComposerListFragment.this.getView(), R.string.questionRemoveHint, Snackbar.LENGTH_LONG);
            removeSnack.setAction(R.string.undo, undoListender);
            removeSnack.show();
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            ComposerListItem item = (ComposerListItem) viewHolder;
            switch (actionState) {
                case ItemTouchHelper.ACTION_STATE_IDLE:
                    item.setBackground(getResources().getColor(R.color.alt));
                    break;
                case ItemTouchHelper.ACTION_STATE_SWIPE:
                    item.setBackground(getResources().getColor(R.color.danger));
                    break;
                case ItemTouchHelper.ACTION_STATE_DRAG:
                    item.setBackground(getResources().getColor(R.color.altMediumHigh));
                    break;
            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            ComposerListItem item = (ComposerListItem) viewHolder;
            item.setBackground(getResources().getColor(R.color.alt));
        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }

        @Override
        public boolean isItemViewSwipeEnabled() {
            return true;
        }
    };

    private View.OnClickListener undoListender = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recentDeletion == null) {
                return;
            }
            questions.add(recentDeletionIndex, recentDeletion);
            adapter.notifyItemInserted(recentDeletionIndex);
            recentDeletion = null;
        }
    };

    private void moveItem(int oldIndex, int newIndex) {
        IQuestionViewModel item = questions.get(oldIndex);
        questions.remove(oldIndex);
        questions.add(newIndex, item);
        adapter.notifyItemMoved(oldIndex, newIndex);
    }

    private IQuestionViewModel recentDeletion;
    private int recentDeletionIndex;

    private SpeedDialView speedDial;
    private ArrayList<IQuestionViewModel> questions;
    private ComposerListAdapter adapter;
    private TextView emptyIndicator;

    private IQuestionOperationRequestListener questionOperationRequestListener;
}
