package mg.studio.android.survey.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Represents a survey model.
 */
public final class SurveyModel implements Serializable {

    /**
     * Gets the ID of the survey.
     * @return The ID of the survey.
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the survey.
     * @param id The ID to set to.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the number of questions in the survey.
     * @return The number of questions in the survey.
     */
    public int getLength() {
        return _questions.size();
    }

    /**
     * Gets the ArrayList object containing questions in the survey.
     * @return The ArrayList object containing questions in the survey.
     */
    public ArrayList<IQuestion> questions() {
        return _questions;
    }

    private String id;
    private ArrayList<IQuestion> _questions = new ArrayList<>();
}
