package edu.greenriver.sdev.androidgametest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ScoreDAO {
    private SQLiteDatabase database;

    public ScoreDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void insertScore(String userName, int score) {
        ContentValues values = new ContentValues();
        values.put("userName", userName);
        values.put("score", score);
        database.insert("scores", null, values);
    }

    public List<HighScore> getHighScores() {
        List<HighScore> highScores = new ArrayList<>();

        // Query the database to get high scores
        Cursor cursor = database.query(
                "scores", // table name
                new String[]{"userName", "score"}, // columns
                null,
                null,
                null,
                null,
                "score DESC", // order by score in descending order
                "10" // limit to the top 10 scores
        );

        // Iterate over the cursor and add scores to the list
        while (((Cursor) cursor).moveToNext()) {
            //TODO:LEFT OFF HERE FIX
            String userName = cursor.getString(cursor.getColumnIndex("userName"));
            int score = cursor.getInt(cursor.getColumnIndex("score"));

            highScores.add(new HighScore(userName, score));
        }

        // Close the cursor
        cursor.close();

        return highScores;
    }
}
