package edu.greenriver.sdev.androidgametest;

import android.content.Context;
import android.widget.ImageView;

public class Player {
    private ImageView imageView;
    private int lives;

    public Player(Context context) {
        imageView = new ImageView(context);
        lives = 3; // Initialize the player with 3 lives, for example
        // You can also set other properties or behaviors here for the ImageView
    }

    public ImageView getImageView() {
        return imageView;
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        lives--;
        // Add any additional logic for when the player loses a life
    }

    // Add other custom methods or properties as needed
}