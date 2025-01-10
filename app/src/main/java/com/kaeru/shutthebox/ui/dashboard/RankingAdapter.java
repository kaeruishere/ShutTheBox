package com.kaeru.shutthebox.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kaeru.shutthebox.R;
import java.util.List;
public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.RankingViewHolder> {

    private List<Ranking> rankingList;

    public RankingAdapter(List<Ranking> rankingList) {
        this.rankingList = rankingList;
    }

    @NonNull
    @Override
    public RankingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ranking, parent, false);
        return new RankingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RankingViewHolder holder, int position) {
        Ranking ranking = rankingList.get(position);
        holder.userNameTextView.setText(ranking.getUserName());
        holder.totalScoreTextView.setText(" : " + ranking.getTotalScore());
    }

    @Override
    public int getItemCount() {
        return rankingList.size();
    }

    public static class RankingViewHolder extends RecyclerView.ViewHolder {
        public TextView userNameTextView;
        public TextView totalScoreTextView;

        public RankingViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.userNameTextView);
            totalScoreTextView = itemView.findViewById(R.id.totalScoreTextView);
        }
    }
}
