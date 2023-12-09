package edu.greenriver.sdev.androidgametest;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HighScoresAdapter extends RecyclerView.Adapter<HighScoresAdapter.ViewHolder> {

    private List<HighScore> highScores;
    private Context context;

    // Constructor
    public HighScoresAdapter(Context context, List<HighScore> highScores) {
        this.context = context;
        this.highScores = highScores;
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewRank;
        public TextView textViewUsername;
        public TextView textViewScore;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewRank = itemView.findViewById(R.id.textViewRank);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
            textViewScore = itemView.findViewById(R.id.textViewScore);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_high_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HighScore highScore = highScores.get(position);

        // Bind data to views
        holder.textViewRank.setText(String.valueOf(position + 1));
        holder.textViewUsername.setText(highScore.getUsername());
        holder.textViewScore.setText(String.valueOf(highScore.getScore()));
    }

    @Override
    public int getItemCount() {
        return highScores.size();
    }
}
