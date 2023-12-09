package edu.greenriver.sdev.androidgametest;

import android.view.View;

public class PlayerMovement implements View.OnClickListener {
    private static final int PLAYER_SPEED = 175;
    private final View playerCharacter;

    public PlayerMovement(View playerCharacter) {
        this.playerCharacter = playerCharacter;
    }

    @Override
    public void onClick(View v) {
        int currentX = (int) playerCharacter.getX();
        int currentY = (int) playerCharacter.getY();
        int newX = currentX;
        int newY = currentY;

        if (v.getId() == R.id.arrowUp) {
            newY -= PLAYER_SPEED;
        } else if (v.getId() == R.id.arrowDown) {
            newY += PLAYER_SPEED;
        } else if (v.getId() == R.id.arrowLeft) {
            newX -= PLAYER_SPEED;
        } else if (v.getId() == R.id.arrowRight) {
            newX += PLAYER_SPEED;
        }

        // Apply the new position to the player image
        playerCharacter.setX(newX);
        playerCharacter.setY(newY);

        // Optionally, you can add boundary checks to ensure the player stays within the screen bounds.
    }
}







