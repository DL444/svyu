package mg.studio.android.survey.views;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import mg.studio.android.survey.R;

public final class OptionListAdapter extends RecyclerView.Adapter<OptionListItem> {

    public OptionListAdapter(IItemClickCallback itemClickCallback) {
        this(new ArrayList<String>(), itemClickCallback);
    }

    public OptionListAdapter(ArrayList<String> options,
                             IItemClickCallback itemClickCallback) {
        source = options;
        clickCallback = itemClickCallback;
    }

    @NonNull
    @Override
    public OptionListItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ConstraintLayout root = (ConstraintLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.option_list_item_layout, parent, false);
        return new OptionListItem(root);
    }

    @Override
    public void onBindViewHolder(@NonNull OptionListItem holder, int position) {
        holder.setOption(source.get(position));
        holder.setIndex(position);
        holder.setClickCallback(clickCallback);
    }

    @Override
    public int getItemCount() {
        return source.size();
    }

    private ArrayList<String> source;
    private IItemClickCallback clickCallback;
}
