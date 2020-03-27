package mg.studio.android.survey.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Pair;

import java.util.ArrayList;

/**
 * Singleton class of the abstraction of a key-value database.
 */
public final class DbClient extends SQLiteOpenHelper {

    /**
     * Gets the singleton instance of DbClient class.
     * @param appContext The application context to create or open database in.
     * @return The singleton instance.
     */
    public static DbClient getInstance(Context appContext) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new DbClient(appContext.getApplicationContext());
                }
            }
        }
        return instance;
    }

    /**
     * The method called when the database is first created.
     * This method does nothing. It's up to applications to decide their creation policies.
     * @param db {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db) { }

    /**
     * The method called when the database is updated to a newer version.
     * This method does nothing. It's up to applications to decide their versioning logic.
     * @param db {@inheritDoc}
     * @param oldVersion {@inheritDoc}
     * @param newVersion {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    /**
     * Creates a collection for key-value storage.
     * @param collectionName The name of the collection.
     */
    public void createCollection(String collectionName) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("create table if not exist " + collectionName + " ( rowKey varchar(100), value text )");
        db.close();
    }

    /**
     * Creates or updates a key-value pair.
     * @param collection The collection in which to perform upsert operation.
     * @param key The key of the pair to insert or update.
     * @param value The value of the pair.
     */
    public void upsert(String collection, String key, String value) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vals = new ContentValues();
        vals.put("value", value);
        int rows = db.update(collection, vals, "rowKey == ?", new String[] { key });
        if (rows == 0)
        {
            vals.put("rowKey", key);
            db.insert(collection, null, vals);
        }
        db.close();
    }

    /**
     * Gets the value corresponding to a key.
     * @param collection The collection containing the key-value pair.
     * @param key The key of the pair.
     * @return The value of the pair.
     */
    public String getOne(String collection, String key) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(collection, new String[] { "value" }, "rowKey == ?", new String[] { key }, null, null, null);
        if (cursor.moveToNext()) {
            String value = cursor.getString(cursor.getColumnIndexOrThrow("value"));
            cursor.close();
            db.close();
            return value;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    /**
     * Gets all key-value pairs in a collection.
     * @param collection The collection to get key-value pairs from.
     * @return An ArrayList containing Pair elements, each element represents a key-value pair found.
     */
    public ArrayList<Pair<String, String>> getAll(String collection) {
        ArrayList<Pair<String, String>> result = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(collection, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String key = cursor.getString(cursor.getColumnIndexOrThrow("rowKey"));
            String value = cursor.getString(cursor.getColumnIndexOrThrow("value"));
            result.add(new Pair<>(key, value));
        }
        cursor.close();
        db.close();
        return result;
    }

    /**
     * Deletes a key-value pair from a collection.
     * @param collection The collection to delete key-value pair from.
     * @param key The key of the pair to delete.
     */
    public void delete(String collection, String key) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(collection, "rowKey == ?", new String[] { key });
        db.close();
    }

    /**
     * Deletes all key-value pairs from a collection.
     * @param collection The collection to delete key-value pairs from.
     */
    public void clear(String collection) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(collection, null, null);
        db.close();
    }

    private DbClient(Context context) {
        super(context, "svyu.db", null, 1);
    }

    private static volatile DbClient instance;
    private static final Object lock = new Object();
}
