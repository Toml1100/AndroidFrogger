package edu.greenriver.sdev.androidgametest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;
import android.view.Display;
import android.widget.TextView;

// For example, in MainGame.java
public class MainGame extends AppCompatActivity {

    private int CAR_SPEED = 5;
    private int LOG_SPEED = 7;
    private ImageView playerCharacter;
    private ImageView leftCar;
    private ImageView rightCar;
    private ImageView leftCar2;
    private ImageView rightCar2;
    private ImageView logRow1Col1;
    private ImageView logRow1Col2;

    private ImageView turtleGroup;
    private ImageView turtleGroup2;
    private ImageView logRow2Col1;
    private ImageView logRow2Col2;
    private ImageView backgroundImage;
    private ImageView deadFrog;
    private ImageView drowningFrog;
    private final int PLAYER_INITIAL_X = 620;
    private final int PLAYER_INITIAL_Y = 1630;
    private int lives = 3;
    private HighScore highscore;
    private int currentScore = 0;
    float screenWidth;
    float screenHeight;
    private CountDownTimer gameTimer;
    TextView textViewLives;
    TextView score;
    private long gameTimeRemaining = 60000;
    boolean onLog;
    boolean onTurtle;

    private Button startGameButton;
    private final int WATER_LOWER_BOUND = 755;
    private final int WATER_UPPER_BOUND = 405;
    private final Handler carHandler = new Handler();
    private List<ImageView> cars = new ArrayList<>();
    private List<ImageView> logs = new ArrayList<>();
    private List<ImageView> turtles = new ArrayList<>();
    private ScoreDAO scoreDAO;
    private String username;
    private int difficulty;

    //Local Variables
    ImageButton arrowUp;
    ImageButton arrowDown;
    ImageButton arrowLeft;
    ImageButton arrowRight;

    // Define arrow buttons for other directions

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);

        //Set high score object score intially 0
        highscore = new HighScore("Thomas", 0);

        //Score object database handler
        scoreDAO = new ScoreDAO(this);

        username = getIntent().getStringExtra("USERNAME");

        difficulty = getIntent().getIntExtra("DIFFICULTY", 0);

        //Change speeds per difficulty
        if (difficulty == 0){
            CAR_SPEED = 5;
            LOG_SPEED = 7;
        } else if (difficulty == 1){
            CAR_SPEED = 8;
            LOG_SPEED = 10;
        } else {
            CAR_SPEED = 11;
            LOG_SPEED = 13;
        }

        // Get the screen width and height
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        int actionBarHeight = getSupportActionBar() != null ? getSupportActionBar().getHeight() : 0;



        //Set the views
        leftCar = findViewById(R.id.leftCar);
        leftCar.setY(100 + actionBarHeight);
        leftCar.setX(0);
        rightCar = findViewById(R.id.rightCar);
        rightCar.setY(200 + actionBarHeight);
        rightCar.setX(250);
        leftCar2 = findViewById(R.id.leftCar2);
        leftCar2.setY(300 + actionBarHeight);
        leftCar2.setX(500);
        rightCar2 = findViewById(R.id.rightCar2);
        rightCar2.setY(375 + actionBarHeight);
        rightCar2.setX(750);
        logRow1Col1 = findViewById(R.id.logRow1Col1);
        logRow1Col1.setY(450 + actionBarHeight);
        logRow1Col1.setX(100);
        logRow1Col1.setTag("right");
        logRow1Col2 = findViewById(R.id.logRow1Col2);
        logRow1Col2.setY(450 + actionBarHeight);
        logRow1Col2.setX(1200);
        logRow1Col2.setTag("right");
        turtleGroup = findViewById(R.id.turtleGroup);
        turtleGroup.setY(515 + actionBarHeight);
        turtleGroup.setX(400);
        turtleGroup2 = findViewById(R.id.turtleGroup2);
        turtleGroup2.setY(515 + actionBarHeight);
        turtleGroup2.setX(-300);
        logRow2Col1 = findViewById(R.id.logRow2Col1);
        logRow2Col1.setY(600 + actionBarHeight);
        logRow2Col1.setX(100);
        logRow2Col1.setTag("left");
        logRow2Col2 = findViewById(R.id.logRow2Col2);
        logRow2Col2.setY(600 + actionBarHeight);
        logRow2Col2.setX(1200);
        logRow2Col2.setTag("left");
        arrowUp = findViewById(R.id.arrowUp);
        arrowDown = findViewById(R.id.arrowDown);
        arrowLeft = findViewById(R.id.arrowLeft);
        arrowRight = findViewById(R.id.arrowRight);
        backgroundImage = findViewById(R.id.backgroundImage);
        playerCharacter = findViewById(R.id.playerCharacter);
        playerCharacter.setY(50);

        //Set Key Movement Listeners
        PlayerMovement playerMovement = new PlayerMovement(playerCharacter);
        arrowUp.setOnClickListener(playerMovement);
        arrowDown.setOnClickListener(playerMovement);
        arrowLeft.setOnClickListener(playerMovement);
        arrowRight.setOnClickListener(playerMovement);

        //Add car to cars list to enable collision detection
        cars.add(leftCar);
        cars.add(rightCar);
        cars.add(leftCar2);
        cars.add(rightCar2);
//        cars.add(leftCar3);

        //Add logs to list of logs to enable detection
        logs.add(logRow1Col1);
        logs.add(logRow1Col2);
        logs.add(logRow2Col1);
        logs.add(logRow2Col2);

        //Add turtles to list to enable detection
        turtles.add(turtleGroup);
        turtles.add(turtleGroup2);

        //Hit by car
        deadFrog = findViewById(R.id.deadFrog);

        //Drowning
        drowningFrog = findViewById(R.id.drownedFrog);

        // Find the button by its ID
        startGameButton = findViewById(R.id.startGameButton);

        textViewLives = findViewById(R.id.textViewLives);

        score = findViewById(R.id.scoreTextView);

        // Click to start the game listener
        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Start the game loop
                start();
                initializeGameTimer();
            }
        });

    }

    //This is the game loop
    private void start() {
        carHandler.post(new Runnable() {
            @Override
            public void run() {
                startGameButton.setVisibility(View.GONE);
                Log.d("PlayerXCoordinate", "Player X Coordinate: " + playerCharacter.getX());
                if (isCollision(playerCharacter, cars)) {
                    // Check lives
                    // If out of lives, end the game
                    // Else restart and subtract a life
                    deadFrog.setVisibility(View.VISIBLE);

                    arrowUp.setEnabled(false);
                    arrowDown.setEnabled(false);
                    arrowLeft.setEnabled(false);
                    arrowRight.setEnabled(false);
                    // Use Handler to hide the deadFrog image after a delay
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // Hide the deadFrog image after the delay
                            deadFrog.setVisibility(View.GONE);

                            arrowUp.setEnabled(true);
                            arrowDown.setEnabled(true);
                            arrowLeft.setEnabled(true);
                            arrowRight.setEnabled(true);
                        }
                    }, 2000);

                    // Reset Player Subtract a life
                    subtractLife();

                }

                //Is the player in the water area?
                if (isPlayerInWater(playerCharacter)) {
                    //IS player on a log
                    for (ImageView log : logs ) {
                        onLog = false;
                        //check if character on the log
                        if(isOnLog(playerCharacter,log)){
                            onLog = true;
                            //check if log is moving right or left
                            if(log.getTag() == "right"){
                                //Move character to the right
                                playerCharacter.setX(playerCharacter.getX() + LOG_SPEED);
                            } else if (log.getTag() == "left"){
                                //Move character to the left
                                playerCharacter.setX(playerCharacter.getX() - LOG_SPEED);
                            }
                            break;
                        }
                    }

                    //onLog has been set to true or false

                    //check if on turtle
                    for (ImageView turtle : turtles) {
                        if(isOnTurtle(playerCharacter, turtle)){
                            onTurtle = true;
                            break;
                        } else {
                            onTurtle = false;
                        }
                    }

                    if(!onTurtle && !onLog){
                        // Player is in the water area
                        subtractLife();
                        Log.d("WaterArea", "Player is in the water area");
                        drowningFrog.setVisibility(View.VISIBLE);
                        arrowUp.setEnabled(false);
                        arrowDown.setEnabled(false);
                        arrowLeft.setEnabled(false);
                        arrowRight.setEnabled(false);
                        // Use Handler to hide the drowningFrog image after a delay
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // Hide the drowningFrog image after the delay
                                drowningFrog.setVisibility(View.GONE);

                                arrowUp.setEnabled(true);
                                arrowDown.setEnabled(true);
                                arrowLeft.setEnabled(true);
                                arrowRight.setEnabled(true);
                            }
                        }, 2000);

                    }

                }

                //Check if player is going off the screen
                if(isOffScreen(playerCharacter,screenWidth,screenHeight)){
                    //End of life
                    subtractLife();
//                    playerCharacter.setY(0);
                } else {
                    //Check if player has made it across the water safely
                    if(playerCharacter.getY() < WATER_UPPER_BOUND ) {
                        playerSafelyCrossedWater();
                    }
                }





                // Move car objects
                moveCar(leftCar, 1, leftCar.getX(), leftCar.getY(), 5);
                moveCar(rightCar, -1, leftCar.getX() + 250, rightCar.getY(), CAR_SPEED);
                moveCar(leftCar2, 1, rightCar.getX() + 125, leftCar2.getY(), CAR_SPEED);
                moveCar(rightCar2, -1, leftCar2.getX() + 500, rightCar2.getY(), CAR_SPEED);

                // Move log objects
                moveCar(logRow1Col1, 1, logRow1Col1.getX(), logRow1Col1.getY(), LOG_SPEED);
                moveCar(logRow1Col2, 1, logRow1Col2.getX(), logRow1Col2.getY(), LOG_SPEED);
                moveCar(logRow2Col1, -1, logRow2Col1.getX(), logRow2Col1.getY(), LOG_SPEED);
                moveCar(logRow2Col2, -1, logRow2Col2.getX(), logRow2Col2.getY(), LOG_SPEED);

                // Schedule the next update
                carHandler.post(this);
            }
        });
    }

    private void subtractLife(){
        playerCharacter.setY(PLAYER_INITIAL_Y);
        playerCharacter.setX(PLAYER_INITIAL_X);
        lives -= 1;
        if(lives >= 0){
            textViewLives.setText("Lives: " + (lives));
        } else {
            gameOver("death");
        }

    }
    private boolean isCollision(ImageView playerCharacter, List<ImageView> cars) {
        // Get the position and dimensions of the player character (Frogger)
        float froggerX = playerCharacter.getX();
        float froggerY = playerCharacter.getY();
        float froggerWidth = playerCharacter.getWidth();
        float froggerHeight = playerCharacter.getHeight();

        for (ImageView car : cars) {
            // Get the position and dimensions of the current car
            float carX = car.getX();
            float carY = car.getY();
            float carWidth = car.getWidth();
            float carHeight = car.getHeight();

            // Check for collision between the current car and Frogger
            if (carX < froggerX + froggerWidth &&
                    carX + carWidth > froggerX &&
                    carY < froggerY + froggerHeight &&
                    carY + carHeight > froggerY) {
                // Collision detected with at least one car
                return true;
            }
        }

        // No collision detected with any of the cars
        return false;
    }

    private boolean isOnLog(ImageView playerCharacter, ImageView log) {
        // Get the position and dimensions of the player character (Frogger)
        float froggerX = playerCharacter.getX();
        float froggerY = playerCharacter.getY();
        float froggerWidth = playerCharacter.getWidth();
        float froggerHeight = playerCharacter.getHeight();

        // Get the position and dimensions of the log
        float logX = log.getX();
        float logY = log.getY();
        float logWidth = log.getWidth();
        float logHeight = log.getHeight();

        // Check for overlap between the player character and the log
        if (froggerX + (froggerWidth/2) > logX &&
                (froggerX + froggerWidth/2)  < logX + logWidth &&
                froggerY + froggerHeight > logY &&
                froggerY < logY + logHeight) {
            // Overlap detected, player is on the log
            return true;
        }

        // No overlap, player is not on the log
        return false;
    }

    private boolean isOnTurtle(ImageView playerCharacter, ImageView turtle) {
        // Get the position and dimensions of the player character (Frogger)
        float froggerX = playerCharacter.getX();
        float froggerY = playerCharacter.getY();
        float froggerWidth = playerCharacter.getWidth();
        float froggerHeight = playerCharacter.getHeight();

        // Get the position and dimensions of the turtle
        float turtleX = turtle.getX();
        float turtleY = turtle.getY();
        float turtleWidth = turtle.getWidth();
        float turtleHeight = turtle.getHeight();

        // Check for overlap between the player character and the turtle
        if (froggerX + froggerWidth > turtleX &&
                froggerX < turtleX + turtleWidth &&
                froggerY + froggerHeight > turtleY &&
                froggerY < turtleY + turtleHeight) {
            // Overlap detected, player is on the turtle
            return true;
        }

        // No overlap, player is not on the turtle
        return false;
    }

    //Player in the water area
    private boolean isPlayerInWater(ImageView playerCharacter) {
        float froggerY = playerCharacter.getY();
        return froggerY <= WATER_LOWER_BOUND && froggerY >= WATER_UPPER_BOUND;
    }

    private void moveCar(ImageView car, int direction, float initialX, float initialY, float speed) {

        if (direction == 1) {
            // Moving to the right
            float newX = car.getX() + speed;

            // Check if the car has moved off the right side of the screen
            if (newX >= backgroundImage.getWidth()) {
                // If off the screen, wrap the car back to the left side
                newX = -car.getWidth(); // Set to the initial position off the left side
            }

            // Apply the new position to the car
            car.setX(newX);
        } else if (direction == -1) {
            // Moving to the left
            float newX = car.getX() - speed;

            // Check if the car has moved off the left side of the screen
            if (newX + car.getWidth() <= 0) {
                // If off the screen, wrap the car back to the right side
                newX = backgroundImage.getWidth(); // Set to the initial position off the right side
            }

            // Apply the new position to the car
            car.setX(newX);
        } else {
            // Set the initial position of the car
            car.setX(initialX);
            car.setY(initialY);
        }


    }

    private boolean isOffScreen(ImageView playerCharacter, float screenWidth, float screenHeight){
        if (playerCharacter.getX() < 0 - 5 || playerCharacter.getX() + playerCharacter.getWidth() > screenWidth + 15 ||
                playerCharacter.getY() < 0 - 5 || playerCharacter.getY() + playerCharacter.getHeight() > screenHeight + 15 ) {
            // Player is going off the screen, end life or take appropriate action
            return true; //true == off screen
        }
        return false; //false == on screen
    }

    private void resetGameObjects() {
        // Reset the positions of cars
        leftCar.setX(0);
        rightCar.setX(250);
        leftCar2.setX(500);
        rightCar2.setX(750);
        // Reset the positions of logs
        logRow1Col1.setX(100);
        logRow1Col2.setX(1200);
        logRow2Col1.setX(100);
        logRow2Col2.setX(1200);
        lives = 3;
        textViewLives.setText("Lives: " + (lives));

    }

    private void gameOver(String endCause) {
        // Display a game over dialog to the player
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over");

        //update score in the database
        highscore.setScore(currentScore);
        scoreDAO.insertScore(username, highscore.getScore());

        if(endCause == "death"){
            builder.setMessage("You ran out of lives! Retry the game?");
        } else {
            builder.setMessage("You ran out of time! Retry the game?");
        }

        builder.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (carHandler != null) {
                    carHandler.removeCallbacksAndMessages(null);  // Remove callbacks and messages
                }

                if (gameTimer != null) {
                    gameTimer.cancel();  // Stop the existing timer
                }
                resetGameObjects();
                start();
                initializeGameTimer();
            }
        });
        builder.setNegativeButton("Quit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle quitting the game or returning to the main menu
                // You can finish the activity or navigate to a different screen
                Intent intent = new Intent(MainGame.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        builder.setCancelable(false); // Prevent the dialog from being dismissed by tapping outside of it
        builder.show();
    }

    private void initializeGameTimer() {
        gameTimer = new CountDownTimer(gameTimeRemaining, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                // Update the timer display
                TextView timerTextView = findViewById(R.id.timerTextView);
                timerTextView.setText("Time: " + millisUntilFinished / 1000 + "s");
            }

            @Override
            public void onFinish() {
                // Handle the end of the game (e.g., show a game over dialog)
                gameOver("timeout");
            }
        };

        // Start the timer
        gameTimer.start();
    }

    private void playerSafelyCrossedWater() {
        // Handle the case when the player has safely crossed the water
        // For example, update the score, display a message, etc.
        if(difficulty == 0){
            currentScore += 10;
        } else if (difficulty == 1){
            currentScore += 15;
        } else {
            currentScore += 20;
        }
        score.setText("Score: " + currentScore);


        // Display a message (you can customize this based on your game)
        Log.d("Game", "Player has safely crossed the water!");

        // You might want to reset the player's position or perform other actions
        // depending on your game logic
        playerCharacter.setX(PLAYER_INITIAL_X);
        playerCharacter.setY(PLAYER_INITIAL_Y);
    }

}