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
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;

/*
 * RecyclerView adapter for displaying a list of users.
 * Used by UserListFragment to show either a followers list or a following list.
 * Each row shows the user's circular profile picture, username, and a single-line
 * description. Tapping anywhere on a row triggers OnUserClickListener with the
 * user's ID, which the fragment uses to navigate to OtherUserProfileFragment.
 * The profile picture URL may be null, empty, or the literal string "null"
 * (a legacy quirk from older JSON parsing). All three cases fall back to a
 * system placeholder drawable rather than attempting a Glide load that would fail.
 */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    /* Callback for row taps. navigation stays in the fragment. */
    public interface OnUserClickListener {
        void onUserClick(long userId);
    }

    private final List<SimpleUserEntity> users = new ArrayList<>();
    private OnUserClickListener listener;

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    /* Replaces the current list with newUsers and refreshes the RecyclerView. */
    public void setData(List<SimpleUserEntity> newUsers) {
        users.clear();
        users.addAll(newUsers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        SimpleUserEntity user = users.get(position);

        holder.username.setText(user.getUsername());
        holder.description.setText(user.getDescription() != null ? user.getDescription() : "");

        // backend can send null or the string "null" for users without a pic
        String pictureUrl = user.getProfilePictureURL();
        boolean valid = pictureUrl != null && !pictureUrl.isEmpty() && !"null".equalsIgnoreCase(pictureUrl);

        if (valid) {
            Glide.with(holder.itemView.getContext())
                    .load(pictureUrl)
                    .circleCrop()
                    .placeholder(android.R.drawable.ic_menu_myplaces)
                    .error(android.R.drawable.ic_menu_myplaces)
                    .into(holder.profilePicture);
        } else {
            holder.profilePicture.setImageResource(android.R.drawable.ic_menu_myplaces);
        }

        // whole row is tappable, not just a button
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onUserClick(user.getId());
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    /* ViewHolder caches references to the three views in item_user.xml. */
    static class UserViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePicture;
        TextView username;
        TextView description;

        UserViewHolder(View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.userItemProfilePicture);
            username = itemView.findViewById(R.id.userItemUsername);
            description = itemView.findViewById(R.id.userItemDescription);
        }
    }
}