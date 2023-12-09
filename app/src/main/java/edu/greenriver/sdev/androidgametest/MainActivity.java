package edu.greenriver.sdev.androidgametest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button startGame;
    Button viewHighScores; // Add a button for viewing high scores

    ImageButton imageButtonEasy;
    ImageButton imageButtonMedium;
    ImageButton imageButtonHard;

    EditText editTextName;

    int difficulty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startGame = findViewById(R.id.buttonStartGame);
        viewHighScores = findViewById(R.id.buttonViewHighScores); // Initialize the new button
        editTextName = findViewById(R.id.editTextName);

        imageButtonEasy = findViewById(R.id.imageButtonEasy);
        imageButtonMedium = findViewById(R.id.imageButtonMedium);
        imageButtonHard = findViewById(R.id.imageButtonHard);

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the button click, navigate to the MainGame activity
                startGame();
            }
        });

        viewHighScores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the button click, navigate to the HighScoresActivity
                viewHighScores();
            }
        });

        imageButtonEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                difficulty = 0;
            }
        });

        imageButtonMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                difficulty = 1;
            }
        });

        imageButtonHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                difficulty = 2;
            }
        });
    }

    private void startGame() {
        // Get the username from the EditText
        String username = editTextName.getText().toString();

        // Start the MainGame activity
        Intent intent = new Intent(MainActivity.this, MainGame.class);

        // Put the username as an extra in the Intent
        intent.putExtra("USERNAME", username);

        intent.putExtra("DIFFICULTY", difficulty);

        startActivity(intent);
    }

    private void viewHighScores() {
        // Start the HighScoresActivity
        Intent intent = new Intent(MainActivity.this, HighScoresActivity.class);
        startActivity(intent);
    }
}