package mg.studio.android.survey.models;

import java.io.Serializable;
import java.util.ArrayList;

public final class SurveyModel implements Serializable {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLength() {
        return _questions.size();
    }

    public ArrayList<IQuestion> questions() {
        return _questions;
    }

    private int id;
    private ArrayList<IQuestion> _questions = new ArrayList<>();
}
