package mg.studio.android.survey;

import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SummaryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.summary_layout);
        ArrayList<ISurveyResponse> responses = (ArrayList<ISurveyResponse>)getIntent().getSerializableExtra(getPackageName() + ".responses");
        Survey survey = (Survey)getIntent().getSerializableExtra(getPackageName() + ".survey");
        LinearLayout rootLayout = findViewById(R.id.summaryLayout);

        for (int i = 0; i < survey.getLength(); i++) {
            String question = survey.getQuestions()[i].getQuestion();

            TextView textView = new TextView(new ContextThemeWrapper(this, R.style.SummaryTitle), null, 0);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.marginXS));
            textView.setLayoutParams(layoutParams);
            textView.setText(question);
            rootLayout.addView(textView);

            textView = new TextView(new ContextThemeWrapper(this, R.style.SummaryItem), null, 0);
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, 0, 0, getResources().getDimensionPixelSize(R.dimen.marginM));
            textView.setLayoutParams(layoutParams);
            textView.setText(responses.get(i).getResponse());
            rootLayout.addView(textView);
        }
    }
}
