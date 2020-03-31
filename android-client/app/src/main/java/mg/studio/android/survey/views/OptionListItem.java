package mg.studio.android.survey.views;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import mg.studio.android.survey.R;

public final class OptionListItem extends RecyclerView.ViewHolder {
    public OptionListItem(@NonNull ConstraintLayout root) {
        super(root);
        this.root = root;
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickCallback != null) {
                    clickCallback.onItemClicked(index);
                }
            }
        });
        option = root.findViewById(R.id.optionText);
    }

    public void setOption(String option) {
        this.option.setText(option);
    }

    public void setBackground(@ColorInt int color) {
        root.setBackgroundColor(color);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setClickCallback(IItemClickCallback callback) {
        clickCallback = callback;
    }

    private final ConstraintLayout root;
    private final TextView option;
    private int index;
    private IItemClickCallback clickCallback;
}
