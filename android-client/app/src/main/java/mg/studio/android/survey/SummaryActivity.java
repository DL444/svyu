package mg.studio.android.survey;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class SummaryActivity extends AppCompatActivity {

    private SQLiteDatabase db;
    DataHelper dataHelper;

    private String type;
    private String answer;
    private StringBuilder result;

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

        /*
        *test
         */
        dataHelper=new DataHelper(this);

        db=dataHelper.getWritableDatabase();
        result = new StringBuilder();
        //Cursor cursor=db.rawQuery("select * from q_anwser",null);
        Cursor cursor=db.query("q_answer",null,null,null,null,null,null);
        if(cursor.moveToFirst()){
            do{
                type=cursor.getString(cursor.getColumnIndex("type"));
                answer=cursor.getString(cursor.getColumnIndex("answer"));

                result.append(type);
                result.append(":");
                result.append(answer);
                result.append(",");
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();
        String result_json=result.toString();
    }
}
