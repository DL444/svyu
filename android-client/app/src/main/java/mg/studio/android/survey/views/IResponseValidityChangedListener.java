package mg.studio.android.survey.views;

/**
 * Represents a listener that responds when the validity of user response has changed.
 */
public interface IResponseValidityChangedListener {

    /**
     * The callback method executed when the validity of user response has changed.
     * @param valid True if user response is valid. False otherwise.
     */
    void onResponseValidityChanged(boolean valid);
}
