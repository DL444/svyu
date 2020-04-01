package mg.studio.android.survey.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import mg.studio.android.survey.R;
import mg.studio.android.survey.models.IQuestion;
import mg.studio.android.survey.models.IResponse;
import mg.studio.android.survey.models.StarRateQuestion;
import mg.studio.android.survey.models.StarRateResponse;

public final class StarRateQuestionView extends QuestionViewBase {

    public static StarRateQuestionView createInstance(IQuestion question) {
        StarRateQuestionView fragment = new StarRateQuestionView();
        Bundle args = new Bundle();
        args.putSerializable("question", question);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            question = (StarRateQuestion) getArguments().getSerializable("question");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.question_star_rate_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView title = getView().findViewById(R.id.title);
        title.setText(question.getQuestion());

        RatingBar starBar = getView().findViewById(R.id.starBar);
        starBar.setOnRatingBarChangeListener(rateListener);
    }

    @Override
    public IResponse getResponse() {
        StarRateResponse response = new StarRateResponse();
        RatingBar starBar = getView().findViewById(R.id.starBar);
        response.setResponse(starBar.getRating());
        return response;
    }

    private RatingBar.OnRatingBarChangeListener rateListener = new RatingBar.OnRatingBarChangeListener() {
        @Override
        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
            if (rating > 0) {
                getValidityChangedCallback().onResponseValidityChanged(true);
            } else {
                getValidityChangedCallback().onResponseValidityChanged(false);
            }
        }
    };

    private StarRateQuestion question;
}
