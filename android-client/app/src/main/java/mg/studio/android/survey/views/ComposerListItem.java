package mg.studio.android.survey.views;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import mg.studio.android.survey.R;

public final class ComposerListItem extends RecyclerView.ViewHolder {

    public ComposerListItem(@NonNull ConstraintLayout root) {
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
        header = root.findViewById(R.id.composerListItemHeader);
        caption = root.findViewById(R.id.composerListItemCaption);
    }

    public void setHeader(String header) {
        this.header.setText(header);
    }

    public void setCaption(String caption) {
        this.caption.setText(caption);
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
    private final TextView header;
    private final TextView caption;
    private int index;
    private IItemClickCallback clickCallback;
}
