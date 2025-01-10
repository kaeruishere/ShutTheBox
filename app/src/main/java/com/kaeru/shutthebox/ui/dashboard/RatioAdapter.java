package com.kaeru.shutthebox.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kaeru.shutthebox.R;
import java.util.List;
public class RatioAdapter extends RecyclerView.Adapter<RatioAdapter.RatioViewHolder> {

    private final List<Ratio> ratioList;

    public RatioAdapter(List<Ratio> ratioList) {
        this.ratioList = ratioList;
    }

    @NonNull
    @Override
    public RatioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ratio, parent, false);
        return new RatioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RatioViewHolder holder, int position) {
        Ratio ratio = ratioList.get(position);
        holder.userNameTextView.setText(ratio.getUserName());
        holder.ratioTextView.setText(" : " + ratio.getRatio());
    }

    @Override
    public int getItemCount() {
        return ratioList.size();
    }

    public static class RatioViewHolder extends RecyclerView.ViewHolder {
        private final TextView userNameTextView;
        private final TextView ratioTextView;

        public RatioViewHolder(View itemView) {
            super(itemView);
            userNameTextView = itemView.findViewById(R.id.usernameTextView);
            ratioTextView = itemView.findViewById(R.id.ratioTextView);
        }
    }
}
