package is.hbv601g.gamecatalog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.color.MaterialColors;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.entities.review.SimpleReviewEntity;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    // Used in BaseProfileFragment: clicking any review navigates to that game's page.
    public interface OnReviewClickListener {
        void onReviewClick(String gameTitle);
    }

    // Used in SpecificGameFragment: clicking your own review opens the edit/delete flow.
    public interface OnEditReviewClickListener {
        void onReviewClick(SimpleReviewEntity review);
    }

    private final List<SimpleReviewEntity> reviews = new ArrayList<>();
    private String loggedInUsername = null;
    private boolean collapsed = true;
    private static final int PREVIEW_COUNT = 3;

    private OnEditReviewClickListener editClickListener;
    private OnReviewClickListener navClickListener;

    public void setOnReviewClickListener(OnEditReviewClickListener listener) {
        this.editClickListener = listener;
    }

    public void setOnNavClickListener(OnReviewClickListener listener) {
        this.navClickListener = listener;
    }

    public void setData(List<SimpleReviewEntity> newReviews) {
        reviews.clear();
        reviews.addAll(newReviews);
        notifyDataSetChanged();
    }

    public void setLoggedInUsername(String username) {
        this.loggedInUsername = username;
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        SimpleReviewEntity review = reviews.get(position);

        holder.title.setText(review.getTitle());
        holder.rating.setText(review.getRating() + "/100");
        holder.text.setText(review.getText());
        holder.gameTitle.setText(review.getGameTitle());

        boolean isUserReview = loggedInUsername != null &&
                loggedInUsername.equals(review.getAuthor());
        MaterialCardView card = (MaterialCardView) holder.itemView;
        if (!isUserReview) {
            card.setCardBackgroundColor(MaterialColors.getColor(card, com.google.android.material.R.attr.colorSurface));
        }

        // navClickListener (profile page): all reviews are tappable to navigate to the game.
        // editClickListener (game detail page): only own review is tappable to edit/delete.
        if (navClickListener != null) {
            holder.itemView.setOnClickListener(v -> navClickListener.onReviewClick(review.getGameTitle()));
        } else if (isUserReview && editClickListener != null) {
            holder.itemView.setOnClickListener(v -> editClickListener.onReviewClick(review));
        } else {
            holder.itemView.setOnClickListener(null);
            holder.itemView.setForeground(null);
        }
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public int getItemCount() {
        if (collapsed) {
            return Math.min(PREVIEW_COUNT, reviews.size());
        } else { return reviews.size(); }
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
        notifyDataSetChanged();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView rating;
        TextView text;
        TextView gameTitle;

        ReviewViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.reviewTitle);
            rating = itemView.findViewById(R.id.reviewRating);
            text = itemView.findViewById(R.id.reviewText);
            gameTitle = itemView.findViewById(R.id.reviewGameTitle);
        }
    }
}
