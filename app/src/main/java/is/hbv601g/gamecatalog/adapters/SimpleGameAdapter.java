package is.hbv601g.gamecatalog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.entities.game.SimpleGameEntity;

public class SimpleGameAdapter extends RecyclerView.Adapter<SimpleGameAdapter.CoverViewHolder> {

    // Show two rows of three before collapsing into "+X more".
    private static final int PREVIEW_COUNT = 6;
    private static final int TYPE_GAME = 0;
    private static final int TYPE_MORE = 1;

    public interface OnGameClickListener {
        void onGameClick(long gameId);
    }

    // Called when the "+x more" cell is tapped, so the host can sync its expand button state.
    public interface OnExpandClickListener {
        void onExpandClick();
    }

    private final List<SimpleGameEntity> games = new ArrayList<>();
    private boolean collapsed = true;
    private OnGameClickListener listener;
    private OnExpandClickListener expandListener;

    public void setOnGameClickListener(OnGameClickListener listener) {
        this.listener = listener;
    }

    public void setOnExpandClickListener(OnExpandClickListener listener) {
        this.expandListener = listener;
    }

    public void setData(List<SimpleGameEntity> newGames) {
        games.clear();
        games.addAll(newGames);
        notifyDataSetChanged();
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (collapsed && games.size() > PREVIEW_COUNT && position == PREVIEW_COUNT) {
            return TYPE_MORE;
        }
        return TYPE_GAME;
    }

    @Override
    public int getItemCount() {
        // Collapsed: show PREVIEW_COUNT covers + 1 overflow cell; expanded: show all.
        if (collapsed && games.size() > PREVIEW_COUNT) {
            return PREVIEW_COUNT + 1;
        }
        return games.size();
    }

    @NonNull
    @Override
    public CoverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_cover, parent, false);
        return new CoverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoverViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_MORE) {
            // Gray placeholder cell showing "+N" centered; no image, no title strip.
            int remaining = games.size() - PREVIEW_COUNT;
            holder.coverImage.setImageDrawable(null);
            holder.moreLabel.setText("+" + remaining);
            holder.moreLabel.setVisibility(View.VISIBLE);
            holder.title.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                setCollapsed(false);
                if (expandListener != null) expandListener.onExpandClick();
            });
        } else {
            SimpleGameEntity game = games.get(position);
            holder.moreLabel.setVisibility(View.GONE);
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(game.getTitle());

            // Load cover with Glide; fall back to the card's colorSurfaceVariant background.
            String url = game.getCoverImage();
            boolean hasImage = url != null && !url.isEmpty() && !"null".equalsIgnoreCase(url);
            if (hasImage) {
                Glide.with(holder.itemView.getContext())
                        .load(url)
                        .centerCrop()
                        .into(holder.coverImage);
            } else {
                holder.coverImage.setImageDrawable(null);
            }

            holder.itemView.setOnClickListener(v -> {
                if (listener != null) listener.onGameClick(game.getId());
            });
        }
    }

    static class CoverViewHolder extends RecyclerView.ViewHolder {
        ImageView coverImage;
        TextView moreLabel;
        TextView title;

        CoverViewHolder(View itemView) {
            super(itemView);
            coverImage = itemView.findViewById(R.id.gameCoverImage);
            moreLabel = itemView.findViewById(R.id.moreLabel);
            title = itemView.findViewById(R.id.gameCoverTitle);
        }
    }
}