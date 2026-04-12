package is.hbv601g.gamecatalog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import is.hbv601g.gamecatalog.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.game.ListedGameEntity;

public class GameAdapter
        extends RecyclerView.Adapter<GameAdapter.GameViewHolder> {

    private final List<ListedGameEntity> games = new ArrayList<>();
    private final OnGameClickListener listener;

    public GameAdapter(OnGameClickListener listener) {
        this.listener = listener;
    }

    public void setData(List<ListedGameEntity> newGames) {
        games.clear();
        games.addAll(newGames);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game, parent, false);

        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull GameViewHolder holder,
            int position
    ) {
        ListedGameEntity game = games.get(position);

        holder.gameTitleText.setText(game.getTitle());
        holder.gameImage.setTransitionName("game_cover_" + game.getId());

        holder.itemView.setOnClickListener(v ->
                listener.onGameClick(game, holder.gameImage)
        );

        Glide.with(holder.itemView)
             .load(game.getCoverImage())
             .placeholder(android.R.drawable.ic_menu_gallery)
             .error(android.R.drawable.ic_menu_report_image)
             .centerCrop()
             .into(holder.gameImage);
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {

        private ImageView gameImage;
        private TextView gameTitleText;

        GameViewHolder(View itemView) {
            super(itemView);

            gameImage = itemView.findViewById(R.id.gameImage);
            gameTitleText = itemView.findViewById(R.id.gameTitle);
        }
    }

    public interface OnGameClickListener {
        void onGameClick(ListedGameEntity game, ImageView coverImage);
    }
}
