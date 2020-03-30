package mg.studio.android.survey.views;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mg.studio.android.survey.R;
import mg.studio.android.survey.viewmodels.IQuestionViewModel;

public final class ComposerListAdapter extends RecyclerView.Adapter<ComposerListItem> {

    public ComposerListAdapter() {
        this(new ArrayList<IQuestionViewModel>());
    }

    public ComposerListAdapter(ArrayList<IQuestionViewModel> questions) {
        source = questions;
    }

    @NonNull
    @Override
    public ComposerListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout root = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.composer_list_item_layout, parent, false);
        return new ComposerListItem(root);
    }

    @Override
    public void onBindViewHolder(@NonNull ComposerListItem holder, int position) {
        IQuestionViewModel question = source.get(position);
        holder.setHeader(question.getQuestion());
        holder.setCaption(question.getDescription());
    }

    @Override
    public int getItemCount() {
        return source.size();
    }

    private ArrayList<IQuestionViewModel> source;
}
