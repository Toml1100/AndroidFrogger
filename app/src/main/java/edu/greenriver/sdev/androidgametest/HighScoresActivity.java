package edu.greenriver.sdev.androidgametest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HighScoresActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HighScoresAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        Context context = this;

        // Assuming you have a method to retrieve high scores from the database
        List<HighScore> highScores = getHighScoresFromDatabase(context);

        // Set up RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Create and set the adapter
        adapter = new HighScoresAdapter(this, highScores);
        recyclerView.setAdapter(adapter);
    }

    // Replace this method with your actual database retrieval logic
    private List<HighScore> getHighScoresFromDatabase(Context context) {
        List<HighScore> highScores = new ArrayList<>();

        // Assuming you have a DatabaseHelper instance
        DatabaseHelper dbHelper = new DatabaseHelper(context);

        // Retrieve high scores using the getHighScores method
        Cursor cursor = dbHelper.getHighScores();

        // Check if the cursor is not null and if moveToFirst returns true
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Retrieve column indices
                int userNameIndex = cursor.getColumnIndex("userName");
                int scoreIndex = cursor.getColumnIndex("score");

                // Check if the indices are valid
                if (userNameIndex >= 0 && scoreIndex >= 0) {
                    // Retrieve values using indices
                    String userName = cursor.getString(userNameIndex);
                    int score = cursor.getInt(scoreIndex);

                    // Add scores to the list
                    highScores.add(new HighScore(userName, score));
                }
            } while (cursor.moveToNext());
        }

        // Close the cursor
        if (cursor != null) {
            cursor.close();
        }

        return highScores;
    }
}
