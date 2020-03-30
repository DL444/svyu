package mg.studio.android.survey.views;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import mg.studio.android.survey.R;

public final class ComposerListItem extends RecyclerView.ViewHolder {

    public ComposerListItem(@NonNull ConstraintLayout root) {
        super(root);
        header = root.findViewById(R.id.composerListItemHeader);
        caption = root.findViewById(R.id.composerListItemCaption);
    }

    public void setHeader(String header) {
        this.header.setText(header);
    }

    public void setCaption(String caption) {
        this.caption.setText(caption);
    }

    private final TextView header;
    private final TextView caption;
}
