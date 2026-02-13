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

import is.hbv601g.gamecatalog.entities.genre.SimpleGenreEntity;

public class GenreAdapter
        extends RecyclerView.Adapter<GenreAdapter.GenreViewHolder> {

    private final List<SimpleGenreEntity> genres = new ArrayList<>();

    public void setData(List<SimpleGenreEntity> newGenres) {
        genres.clear();
        genres.addAll(newGenres);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre, parent, false);

        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull GenreViewHolder holder,
            int position
    ) {
        SimpleGenreEntity genre = genres.get(position);

        holder.title.setText(genre.getTitle());
        holder.count.setText(String.valueOf(genre.getGameAmount()));
    }

    @Override
    public int getItemCount() {
        return genres.size();
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        TextView count;

        GenreViewHolder(View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.genreTitle);
            count = itemView.findViewById(R.id.genreCount);
        }
    }
}

