package is.hbv601g.gamecatalog.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.entities.genre.ListedGenreEntity;

public class GenreParamAdapter extends RecyclerView.Adapter<GenreParamAdapter.GenreViewHolder> {

    private List<ListedGenreEntity> genreList;
    private Set<String> selectedGenres = new HashSet<>();

    public GenreParamAdapter(List<ListedGenreEntity> genreList) {
        this.genreList = genreList;
    }

    public Set<String> getSelectedGenres() {
        return selectedGenres;
    }

    public void updateData(List<ListedGenreEntity> newGenres) {
        this.genreList = newGenres;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GenreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_genre_param, parent, false);
        return new GenreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenreViewHolder holder, int position) {
        ListedGenreEntity genre = genreList.get(position);
        String displayText = genre.getTitle() + " " + genre.getGameAmount();
        holder.chip.setText(displayText);

        holder.chip.setOnCheckedChangeListener(null);
        holder.chip.setChecked(selectedGenres.contains(genre.getTitle()));

        holder.chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedGenres.add(genre.getTitle());
            } else {
                selectedGenres.remove(genre.getTitle());
            }
        });
    }

    @Override
    public int getItemCount() {
        return genreList.size();
    }

    static class GenreViewHolder extends RecyclerView.ViewHolder {
        Chip chip;

        public GenreViewHolder(@NonNull View itemView) {
            super(itemView);
            chip = (Chip) itemView;
        }
    }
}
