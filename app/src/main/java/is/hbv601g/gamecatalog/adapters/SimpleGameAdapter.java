package is.hbv601g.gamecatalog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.entities.game.SimpleGameEntity;

public class SimpleGameAdapter extends RecyclerView.Adapter<SimpleGameAdapter.SimpleGameViewHolder> {

    public interface OnGameClickListener {
        void onGameClick(long gameId);
    }

    private final List<SimpleGameEntity> games = new ArrayList<>();
    private OnGameClickListener listener;

    public void setOnGameClickListener(OnGameClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<SimpleGameEntity> newGames) {
        games.clear();
        games.addAll(newGames);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleGameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_simple_game, parent, false);
        return new SimpleGameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleGameViewHolder holder, int position) {
        SimpleGameEntity game = games.get(position);
        holder.title.setText(game.getTitle());
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onGameClick(game.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class SimpleGameViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        SimpleGameViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.simpleGameTitle);
        }
    }
}