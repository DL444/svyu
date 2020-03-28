package mg.studio.android.survey.models;

import java.util.HashSet;

/**
 * Represents a response that consists of one or more choices.
 */
public final class MultiChoiceResponse implements IResponse {

    /**
     * {@inheritDoc}
     */
    @Override
    public QuestionType getType() {
        return QuestionType.Multiple;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasResponse() {
        return !responses.isEmpty();
    }

    /**
     * Gets an array of choices for this response.
     * @return An array of choices for this response.
     */
    public Integer[] getResponse() {
        return responses.toArray(new Integer[0]);
    }

    /**
     * Adds a choice if it currently does not exist. Otherwise, remove it.
     * @param choice The choice to add or remove.
     * @return True if the choice was added. False if it is removed.
     */
    public boolean setResponse(int choice) {
        if (responses.contains(choice)) {
            responses.remove(choice);
            return false;
        } else {
            responses.add(choice);
            return true;
        }
    }

    private HashSet<Integer> responses = new HashSet<>();
}
