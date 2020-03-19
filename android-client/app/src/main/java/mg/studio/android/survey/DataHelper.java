package mg.studio.android.survey;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DataHelper extends SQLiteOpenHelper {
    public DataHelper(@Nullable Context context) {
        super(context, "DataBase.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(dropTableQuery);
        onCreate(db);
    }

    public void addResponse(int qid, QuestionType type, String response) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(qidColumnName, qid);
        switch (type) {
            case Single:
                values.put(typeColumnName, "single");
                break;
            case Multiple:
                values.put(typeColumnName, "multiple");
                break;
            case Text:
                values.put(typeColumnName, "text");
                break;
        }
        values.put(responseColumnName, response);
        db.insert(responseTableName, null, values);
        db.close();
    }

    public ResponseEntity[] getResponses() {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] cols = new String[] { typeColumnName, responseColumnName };
        Cursor cursor = db.query(responseTableName, cols, null, null, null, null, qidColumnName);
        int count = cursor.getCount();
        ResponseEntity[] responses = new ResponseEntity[count];
        for (int i = 0; i < count; i++) {
            cursor.moveToNext();
            String type = cursor.getString(cursor.getColumnIndexOrThrow(typeColumnName));
            String response = cursor.getString(cursor.getColumnIndexOrThrow(responseColumnName));
            responses[i] = new ResponseEntity(i, type, response);
        }
        cursor.close();
        return responses;
    }

    public void reset() {
        this.getWritableDatabase().execSQL(dropTableQuery);
        this.getWritableDatabase().execSQL(createTableQuery);
    }

    private static final String qidColumnName = "qid";
    private static final String typeColumnName = "type";
    private static final String responseColumnName = "answer";

    private static final String responseTableName = "q_answer";

    private static final String createTableQuery = "create table " + responseTableName + "(" +
            qidColumnName + " integer, " +
            typeColumnName + " varchar(100), " +
            responseColumnName + " varchar(100))";

    private static final String dropTableQuery = "drop table " + responseTableName;
}
