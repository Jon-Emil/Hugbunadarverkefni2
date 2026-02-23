package is.hbv601g.gamecatalog.pages.search_games;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import is.hbv601g.gamecatalog.adapters.GenreParamAdapter;
import is.hbv601g.gamecatalog.databinding.BottomSheetAdvancedSearchBinding;
import is.hbv601g.gamecatalog.entities.extras.AdvancedSearchParameters;

public class AdvancedSearchBottomSheet extends BottomSheetDialogFragment {

    private BottomSheetAdvancedSearchBinding binding;
    private GenreParamAdapter genreParamAdapter;
    private SearchGamesViewModel viewModel;

    // drag behavior was very inconsistent if it worked correctly or not
    // so we just disable it like this
    @Override
    public void onStart() {
        super.onStart();

        View bottomSheet = (View) getView().getParent();
        BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);

        behavior.setDraggable(false);
    }

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
        viewModel = new ViewModelProvider(requireActivity())
                .get(SearchGamesViewModel.class); // same instance of the view model as the fragment

        AdvancedSearchParameters savedParams = viewModel.getAdvancedSearchParameters();

        Float savedMinPrice = savedParams.getMinPrice();
        if (savedMinPrice != null) {
            binding.minPriceInput.setText(String.valueOf(savedMinPrice));
        }

        Float savedMaxPrice = savedParams.getMaxPrice();
        if (savedMaxPrice != null) {
            binding.maxPriceInput.setText(String.valueOf(savedMaxPrice));
        }

        String savedReleasedAfter = savedParams.getReleasedAfter();
        if (!savedReleasedAfter.isEmpty()) {
            binding.releasedAfterInput.setText(savedReleasedAfter);
        }

        String savedReleasedBefore = savedParams.getReleasedBefore();
        if (!savedReleasedBefore.isEmpty()) {
            binding.releasedBeforeInput.setText(savedReleasedBefore);
        }

        String savedDeveloper = savedParams.getDeveloper();
        if (!savedDeveloper.isEmpty()) {
            binding.developerInput.setText(savedDeveloper);
        }

        String savedPublisher = savedParams.getPublisher();
        if (!savedPublisher.isEmpty()) {
            binding.publisherInput.setText(savedPublisher);
        }

        binding.releasedAfterInput.setOnClickListener(v -> {
            showDatePicker(binding.releasedAfterInput);
        });

        binding.releasedAfterClearButton.setOnClickListener(v -> {
            binding.releasedAfterInput.setText("");
        });

        binding.releasedBeforeInput.setOnClickListener(v -> {
            showDatePicker(binding.releasedBeforeInput);
        });

        binding.releasedBeforeClearButton.setOnClickListener(v -> {
            binding.releasedBeforeInput.setText("");
        });

        binding.genreRecyclerView.setLayoutManager(new FlexboxLayoutManager(getContext()));
        genreParamAdapter = new GenreParamAdapter(
                viewModel.getGenres().getValue(),
                viewModel.getAdvancedSearchParameters().getGenres());
        binding.genreRecyclerView.setAdapter(genreParamAdapter);

        viewModel.getGenres().observe(getViewLifecycleOwner(), genres -> {
            genreParamAdapter.updateData(genres);
        });

        binding.applyButton.setOnClickListener(v -> {
            String minPriceString = binding.minPriceInput.getText().toString();
            String maxPriceString = binding.maxPriceInput.getText().toString();

            Float minPrice;
            Float maxPrice;

            if (minPriceString.isEmpty()) {
                minPrice = null;
            } else {
               minPrice = Float.valueOf(minPriceString);
            }

            if (maxPriceString.isEmpty()) {
                maxPrice = null;
            } else {
                maxPrice = Float.valueOf(maxPriceString);
            }

            String releasedAfter = binding.releasedAfterInput.getText().toString();
            String releasedBefore = binding.releasedBeforeInput.getText().toString();

            String developer = binding.developerInput.getText().toString();
            String publisher = binding.publisherInput.getText().toString();

            List<String> genres = new ArrayList<>(genreParamAdapter.getSelectedGenres());

            AdvancedSearchParameters params = new AdvancedSearchParameters(
                    minPrice,
                    maxPrice,
                    releasedAfter,
                    releasedBefore,
                    developer,
                    publisher,
                    genres
            );

            sendResult(params);
            dismiss();
        });
    }

    public Set<String> getSelectedGenres() {
        return genreParamAdapter.getSelectedGenres();
    }

    public interface FilterListener {
        void onFiltersApplied(AdvancedSearchParameters params);
    }

    private FilterListener listener;

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    private void sendResult(AdvancedSearchParameters params) {
        if (listener != null) {
            listener.onFiltersApplied(params);
        }
    }

    private void showDatePicker(EditText view) {
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

                    view.setText(date);
                },
                year, month, day
        );

        picker.show();
    }
}
