package is.hbv601g.gamecatalog.pages.search_games;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import is.hbv601g.gamecatalog.adapters.GenreParamAdapter;
import is.hbv601g.gamecatalog.databinding.BottomSheetAdvancedSearchBinding;

public class AdvancedSearchBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetAdvancedSearchBinding binding;
    private GenreParamAdapter genreParamAdapter;

    @Override
    public View onCreateView(
            LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = BottomSheetAdvancedSearchBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Button applyBtn = binding.applyButton;

        // should make this a function call in the future
        binding.releasedAfterInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(
                    requireContext(),
                    (pickerView, selectedYear, selectedMonth, selectedDay) -> {

                        String date =
                                selectedYear + "-" +
                                (selectedMonth + 1) + "-" +
                                selectedDay;

                        binding.releasedAfterInput.setText(date);
                    },
                    year, month, day
            );

            picker.show();
        });

        binding.releasedBeforeInput.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(
                    requireContext(),
                    (pickerView, selectedYear, selectedMonth, selectedDay) -> {

                        String date =
                                selectedYear + "-" +
                                        (selectedMonth + 1) + "-" +
                                        selectedDay;

                        binding.releasedBeforeInput.setText(date);
                    },
                    year, month, day
            );

            picker.show();
        });

        List<String> genres = Arrays.asList(
                "Action", "Adventure", "RPG", "Shooter",
                "Horror", "Strategy", "Simulation",
                "Sports", "Puzzle", "Platformer"
        );

        binding.genreRecyclerView.setLayoutManager(new FlexboxLayoutManager(getContext()));
        genreParamAdapter = new GenreParamAdapter(genres);
        binding.genreRecyclerView.setAdapter(genreParamAdapter);

        applyBtn.setOnClickListener(v -> {

            sendResult();
            dismiss();
        });
    }

    public Set<String> getSelectedGenres() {
        return genreParamAdapter.getSelectedGenres();
    }

    public interface FilterListener {
        void onFiltersApplied();
    }

    private FilterListener listener;

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    private void sendResult() {
        if (listener != null) {
            listener.onFiltersApplied();
        }
    }
}
