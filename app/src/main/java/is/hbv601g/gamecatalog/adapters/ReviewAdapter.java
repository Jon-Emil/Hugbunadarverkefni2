package is.hbv601g.gamecatalog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import is.hbv601g.gamecatalog.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import is.hbv601g.gamecatalog.entities.review.SimpleReviewEntity;

public class ReviewAdapter
        extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private final List<SimpleReviewEntity> reviews = new ArrayList<>();

    public void setData(List<SimpleReviewEntity> newReviews) {
        reviews.clear();
        reviews.addAll(newReviews);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_review, parent, false);

        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ReviewViewHolder holder,
            int position
    ) {
        SimpleReviewEntity review = reviews.get(position);

        holder.title.setText(review.getTitle());
        holder.author.setText(review.getAuthor());
        String ratingText = "Rating: " + String.valueOf(review.getRating());
        holder.rating.setText(ratingText);
        holder.text.setText(review.getText());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView author;
        TextView rating;
        TextView text;

        ReviewViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.reviewTitle);
            author = itemView.findViewById(R.id.reviewAuthor);
            rating = itemView.findViewById(R.id.reviewRating);
            text = itemView.findViewById(R.id.reviewText);
        }
    }
}


