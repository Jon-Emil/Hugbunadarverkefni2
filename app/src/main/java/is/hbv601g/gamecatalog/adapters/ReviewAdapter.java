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

    private final List<SimpleReviewEntity> reviews = new ArrayList<>();
    private String loggedInUsername = null;
    private boolean collapsed = true;
    private static final int PREVIEW_COUNT = 3;

    public interface onReviewClickListener{
        void onReviewClick(SimpleReviewEntity review);
    }

    private onReviewClickListener clickListener;

    public void setOnReviewClickListener(onReviewClickListener listener) {
        this.clickListener = listener;
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
        // own review er þegar kominn með eigin looks format, breytum aðeins litinum á other people´s reviewum. sjaum hvernig þetta lookar.
        if (!isUserReview) {card.setCardBackgroundColor(MaterialColors.getColor(card, com.google.android.material.R.attr.colorSurface));}
        // makes only own review is clickable
        if (isUserReview) {
            holder.itemView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onReviewClick(review);
                }
            });

        } else {
            // disables click for other reviews
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
    public void setCollapsed (boolean collapsed) {
        this.collapsed = collapsed;
        notifyDataSetChanged();
    } // notify app the current status of the collapse status of review block.

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