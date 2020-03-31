package mg.studio.android.survey.views;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import mg.studio.android.survey.R;
import mg.studio.android.survey.viewmodels.ChoiceQuestionViewModelBase;

public final class ComposerChoiceQuestionView extends ComposerQuestionViewBase {

    public static ComposerChoiceQuestionView createInstance(ChoiceQuestionViewModelBase question, boolean isUpdate, int index) {
        ComposerChoiceQuestionView fragment = new ComposerChoiceQuestionView();
        Bundle args = new Bundle();
        args.putSerializable("question", question);
        args.putBoolean("isUpdate", isUpdate);
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (ChoiceQuestionViewModelBase) getArguments().getSerializable("question");
            options = question.options();
            isUpdate = getArguments().getBoolean("isUpdate");
            index = getArguments().getInt("index");
        }
        adapter = new OptionListAdapter(options, itemClickListener);
        adapter.registerAdapterDataObserver(observer);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.composer_choice_view, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EditText title = getView().findViewById(R.id.composerQuestionInput);
        title.addTextChangedListener(textWatcher);

        FloatingActionButton confirmBtn = getView().findViewById(R.id.composerConfirmBtn);
        confirmBtn.setOnClickListener(confirmListener);
        boolean hasQuestion = question.hasQuestion();
        confirmBtn.setEnabled(hasQuestion);
        confirmBtn.setTranslationY(hasQuestion ? 0.0f : 500.0f);
        if (hasQuestion) {
            title.setText(question.getQuestion());
        }

        RecyclerView recyclerView = getView().findViewById(R.id.optionListView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        ItemTouchHelper gestureHelper = new ItemTouchHelper(gestureCallback);
        gestureHelper.attachToRecyclerView(recyclerView);

        emptyIndicator = getView().findViewById(R.id.optionEmptyHint);
        emptyIndicator.setVisibility(options.size() == 0 ? View.VISIBLE : View.GONE);

        Button addOptionBtn = getView().findViewById(R.id.addOptionBtn);
        addOptionBtn.setOnClickListener(addOptionListener);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) { }
        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().isEmpty()) {
                setConfirmButtonEnabled(false);
            } else {
                setConfirmButtonEnabled(true);
            }
        }
    };

    private Button.OnClickListener addOptionListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            showOptionEditor("", -1, new IOptionEditorAction() {
                @Override
                public void onConfirm(String option, int index) {
                    options.add(option);
                    adapter.notifyItemInserted(options.size() - 1);
                }
            });
        }
    };

    private Button.OnClickListener confirmListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText title = ComposerChoiceQuestionView.this.getView().findViewById(R.id.composerQuestionInput);
            question.setQuestion(title.getText().toString());
            for (int i = 0; i < question.options().size(); i++) {
                if (question.options().get(i).trim().isEmpty()) {
                    question.options().remove(i);
                    i--;
                }
            }
            ComposerChoiceQuestionView.this.getQuestionOperationCompleteCallback().onQuestionOperationComplete(question, isUpdate, index);
        }
    };

    private IItemClickCallback itemClickListener = new IItemClickCallback() {
        @Override
        public void onItemClicked(int index) {
            String oldOption = options.get(index);
            showOptionEditor(oldOption, index, new IOptionEditorAction() {
                @Override
                public void onConfirm(String option, int i) {
                    options.set(i, option);
                    adapter.notifyItemChanged(i);
                }
            });
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
            int index = viewHolder.getAdapterPosition();
            options.remove(index);
            adapter.notifyItemRemoved(index);
        }

        @Override
        public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
            OptionListItem item = (OptionListItem) viewHolder;
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
            OptionListItem item = (OptionListItem) viewHolder;
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
                ComposerChoiceQuestionView.this.emptyIndicator.setVisibility(View.VISIBLE);
            } else {
                ComposerChoiceQuestionView.this.emptyIndicator.setVisibility(View.GONE);
            }
        }
    };

    private void setConfirmButtonEnabled(boolean enabled) {
        FloatingActionButton btn = getView().findViewById(R.id.composerConfirmBtn);
        if (btn.isEnabled() == enabled) {
            return;
        }
        if (enabled) {
            btn.setEnabled(true);
        } else {
            btn.setEnabled(false);
        }
        float value = enabled ? 0.0f : 500.0f;
        ObjectAnimator animation = ObjectAnimator.ofFloat(btn, "translationY", value).setDuration(200);
        if (enabled) {
            animation.setInterpolator(new OvershootInterpolator());
        } else {
            animation.setInterpolator(new AnticipateInterpolator());
        }
        animation.start();
    }

    private void moveItem(int oldIndex, int newIndex) {
        String item = options.get(oldIndex);
        options.remove(oldIndex);
        options.add(newIndex, item);
        adapter.notifyItemMoved(oldIndex, newIndex);
    }

    private void showOptionEditor(String option, final int index, final IOptionEditorAction action) {
        View editorRoot = LayoutInflater.from(getContext()).inflate(R.layout.option_edit_layout, null, false);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setView(editorRoot);
        final EditText editor = editorRoot.findViewById(R.id.optionEditText);
        editor.setText(option);
        dialogBuilder.setCancelable(true)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String newOption = editor.getText().toString();
                                action.onConfirm(newOption, index);
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        dialogBuilder.create().show();
    }

    private interface IOptionEditorAction {
        void onConfirm(String option, int index);
    }

    private boolean isUpdate;
    private int index;
    private ChoiceQuestionViewModelBase question;
    private ArrayList<String> options;
    private OptionListAdapter adapter;
    private TextView emptyIndicator;
}
