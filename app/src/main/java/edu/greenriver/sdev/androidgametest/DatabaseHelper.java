package edu.greenriver.sdev.androidgametest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "game_scores.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the table to store user names and scores
        String createTableQuery = "CREATE TABLE scores (id INTEGER PRIMARY KEY AUTOINCREMENT, userName TEXT, score INTEGER)";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Handle database upgrades (if needed)
    }

    // Method to get high scores
    public Cursor getHighScores() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM scores ORDER BY score DESC";
        return db.rawQuery(query, null);
    }
}
