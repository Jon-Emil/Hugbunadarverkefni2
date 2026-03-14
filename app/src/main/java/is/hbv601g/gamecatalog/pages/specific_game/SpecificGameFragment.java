package is.hbv601g.gamecatalog.pages.specific_game;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.GenreAdapter;
import is.hbv601g.gamecatalog.adapters.ReviewAdapter;
import is.hbv601g.gamecatalog.database.CacheDatabase;
import is.hbv601g.gamecatalog.databinding.FragmentAllGamesBinding;
import is.hbv601g.gamecatalog.databinding.FragmentSpecificGameBinding;
import is.hbv601g.gamecatalog.entities.game.DetailedGameEntity;
import is.hbv601g.gamecatalog.entities.review.SimpleReviewEntity;
import is.hbv601g.gamecatalog.helpers.EmptyCallBack;
import is.hbv601g.gamecatalog.helpers.GameCollections;
import is.hbv601g.gamecatalog.helpers.ServiceCallback;
import is.hbv601g.gamecatalog.services.GameService;
import is.hbv601g.gamecatalog.services.NetworkService;
import is.hbv601g.gamecatalog.services.UserService;

public class SpecificGameFragment extends Fragment {

    private static final String ARG_GAME_ID = "game_id";

    private SpecificGameViewModel viewModel;

    private FragmentSpecificGameBinding binding;

    private GenreAdapter genreAdapter;

    private ReviewAdapter reviewAdapter;

    //Code boilerplate from developer.android.com
    //Inflates the layout for this fragment
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentSpecificGameBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        genreAdapter = new GenreAdapter();
        binding.genreRecycler.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );
        binding.genreRecycler.setAdapter(genreAdapter);


        reviewAdapter = new ReviewAdapter();
        binding.reviewRecycler.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );
        binding.reviewRecycler.setAdapter(reviewAdapter);

        //catch args missing error to prevent crash
        Bundle args = getArguments();
        if(args == null || !args.containsKey(ARG_GAME_ID)) {
            throw new IllegalArgumentException("Missing gameId argument");
        }
        long gameId = args.getLong(ARG_GAME_ID);

        NetworkService networkService = new NetworkService(requireContext());
        GameService gameService = new GameService(networkService);
        UserService userService = new UserService(networkService);

        viewModel = new ViewModelProvider(this).get(SpecificGameViewModel.class);

        CacheDatabase db = Room.databaseBuilder(
                requireContext(),
                CacheDatabase.class,
                "cache_database"
        ).build();

        viewModel.init(gameService, userService, gameId, db);
        viewModel.getGame().observe(getViewLifecycleOwner(), this::updateGameInfo);

        binding.favoriteButton.setVisibility(View.GONE);
        binding.wantToPlayButton.setVisibility(View.GONE);
        binding.havePlayedButton.setVisibility(View.GONE);

        viewModel.getUserAndGameExist().observe(getViewLifecycleOwner(), doExist -> {
            if (doExist) {
                binding.favoriteButton.setVisibility(View.VISIBLE);
                binding.wantToPlayButton.setVisibility(View.VISIBLE);
                binding.havePlayedButton.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsProcessingFavorites().observe(getViewLifecycleOwner(), isLoading -> {
            binding.favoriteButton.setEnabled(!isLoading);
            if (isLoading) {
                binding.favoriteButton.setText("Loading...");
            }
        });
        viewModel.getIsInFavorites().observe(getViewLifecycleOwner(), isFavorite -> {
            binding.favoriteButton.setText(getNewCollectionText(GameCollections.FAVORITE, isFavorite));
            // couple of unnecessary calls but it's a lightweight function so not a big deal.
            updateCollectionsAmount();
        });
        binding.favoriteButton.setOnClickListener(v -> {
            Boolean isFavorite = viewModel.getIsInFavorites().getValue();
            if (isFavorite != null ? isFavorite : false) {
                viewModel.removeFromCollection(GameCollections.FAVORITE);
            } else {
                viewModel.addToCollection(GameCollections.FAVORITE);
            }
        });

        viewModel.getIsProcessingWantToPlay().observe(getViewLifecycleOwner(), isLoading -> {
            binding.wantToPlayButton.setEnabled(!isLoading);
            if (isLoading) {
                binding.wantToPlayButton.setText("Loading...");
            }
        });
        viewModel.getIsInWantToPlay().observe(getViewLifecycleOwner(), isInWantToPlay -> {
            binding.wantToPlayButton.setText(getNewCollectionText(GameCollections.WANT_TO_PLAY, isInWantToPlay));
            // couple of unnecessary calls but it's a lightweight function so not a big deal.
            updateCollectionsAmount();
        });
        binding.wantToPlayButton.setOnClickListener(v -> {
            Boolean isInWantToPlay = viewModel.getIsInWantToPlay().getValue();
            if (isInWantToPlay != null ? isInWantToPlay : false) {
                viewModel.removeFromCollection(GameCollections.WANT_TO_PLAY);
            } else {
                viewModel.addToCollection(GameCollections.WANT_TO_PLAY);
            }
        });

        viewModel.getIsProcessingHasPlayed().observe(getViewLifecycleOwner(), isLoading -> {
            binding.havePlayedButton.setEnabled(!isLoading);
            if (isLoading) {
                binding.havePlayedButton.setText("Loading...");
            }
        });
        viewModel.getIsInHasPlayed().observe(getViewLifecycleOwner(), isInHasPlayed -> {
            binding.havePlayedButton.setText(getNewCollectionText(GameCollections.HAS_PLAYED, isInHasPlayed));
            // couple of unnecessary calls but it's a lightweight function so not a big deal.
            updateCollectionsAmount();
        });
        binding.havePlayedButton.setOnClickListener(v -> {
            Boolean isInHasPlayed = viewModel.getIsInHasPlayed().getValue();
            if (isInHasPlayed != null ? isInHasPlayed : false) {
                viewModel.removeFromCollection(GameCollections.HAS_PLAYED);
            } else {
                viewModel.addToCollection(GameCollections.HAS_PLAYED);
            }
        });

        //listeners for the review section
        binding.submitReviewButton.setOnClickListener(v -> {
            int rating = binding.reviewRatingPicker.getValue();
            String title = binding.reviewTitleInput.getText().toString().trim();
            String text = binding.reviewTextInput.getText().toString().trim();

            //so users can submit empty reviews
            if (title.isEmpty()) {
                binding.reviewTitleInput.setError("title empty");
                return;
            }

            if (text.isEmpty()) {
                binding.reviewTextInput.setError("review text empty");
                return;
            }

            submitReview(rating, text, title);
        });

        binding.reviewRatingPicker.setMinValue(0);
        binding.reviewRatingPicker.setMaxValue(100);

        reviewAdapter.setOnReviewClickListener(review -> {
            showEditReviewDialog(review);
        });




    }

    //for the popup for editing reviews
    private void showEditReviewDialog(SimpleReviewEntity review) {
        BottomSheetDialog dialog = new BottomSheetDialog(requireContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_edit_review, null);
        dialog.setContentView(view);

        NumberPicker ratingPicker = view.findViewById(R.id.editReviewRatingPicker);
        EditText titleInput = view.findViewById(R.id.editReviewTitleInput);
        EditText textInput = view.findViewById(R.id.editReviewTextInput);
        Button saveButton = view.findViewById(R.id.saveReviewButton);

        ratingPicker.setMinValue(0);
        ratingPicker.setMaxValue(100);
        ratingPicker.setValue(review.getRating());

        titleInput.setText(review.getTitle());
        textInput.setText(review.getText());

        saveButton.setOnClickListener(v -> {
            int newRating = ratingPicker.getValue();
            String newTitle = titleInput.getText().toString().trim();
            String newText = textInput.getText().toString().trim();

            updateReview(review.getId(), newRating, newText, newTitle);
            dialog.dismiss();
        });

        Button deleteButton = view.findViewById(R.id.deleteReviewButton);

        deleteButton.setOnClickListener(v -> {
            dialog.dismiss();
            showDeleteConfirmation(review);
        });


        dialog.show();
    }

    //Standart same as for deleting account and all delete operations
    private void showDeleteConfirmation(SimpleReviewEntity review) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Deleting Review!")
                .setMessage("Are you sure you want to delete your review?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteReview(review.getId());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }


    private void deleteReview(Long reviewId) {
        long gameId = getArguments().getLong("game_id");

        GameService gameService = new GameService(new NetworkService(requireContext()));

        gameService.deleteReview(gameId, reviewId, new EmptyCallBack() {

            //confirmint its deleted helped for debuging and ok leaving it here still
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Review deleted", Toast.LENGTH_SHORT).show();
                    viewModel.refreshGame();
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Failed to delete review", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }


    private void updateReview(Long reviewId, int rating, String text, String title) {
        long gameId = getArguments().getLong("game_id");

        GameService gameService = new GameService(new NetworkService(requireContext()));

        gameService.updateReview(gameId, reviewId, rating, text, title, new EmptyCallBack() {
            //confirmint its changed helped for debuging and ok leaving it here still
            @Override
            public void onSuccess() {
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "Review updated", Toast.LENGTH_SHORT).show();
                    viewModel.refreshGame();
                });
            }

            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Failed to update review", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }




    //submit method for revies
    private void submitReview(int rating, String text, String title) {
        long gameId = getArguments().getLong("game_id");

        GameService gameService = new GameService(new NetworkService(requireContext()));

        gameService.addReview(gameId, rating, text, title, new EmptyCallBack() {
            //detailed error handling along with handling network errors for the offline part
            @Override
            public void onError(Exception e) {
                requireActivity().runOnUiThread(() -> {

                    String raw = e.getMessage();
                    String message = raw; // fallback

                    // need to get the message because both errores return a 400.
                    try {
                        JSONObject json = new JSONObject(raw);
                        if (json.has("message")) {
                            message = json.getString("message");
                        }
                    } catch (Exception ignored) {

                    }

                    String lower = message.toLowerCase();

                    if (lower.contains("already")) {
                        Toast.makeText(requireContext(), "You already have a review for this game", Toast.LENGTH_LONG).show();
                    }
                    else if (lower.contains("required header 'authorization'") ||
                            lower.contains("authorization is not present") ||
                            lower.contains("unauthorized") ||
                            lower.contains("you must be logged in")) {
                        Toast.makeText(requireContext(), "You are not logged in", Toast.LENGTH_LONG).show();
                    }
                    else if (lower.contains("unable to resolve host") ||
                            lower.contains("failed to connect") ||
                            lower.contains("timeout")) {
                        Toast.makeText(requireContext(), "Offline", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(requireContext(), "Failed", Toast.LENGTH_LONG).show();
                    }


                    //for debugging
                    Log.e("ReviewError", "RAW ERROR: " + raw);
                });
            }


            @Override
            public void onSuccess() {
                binding.reviewTextInput.setText("");
                binding.reviewTitleInput.setText("");
                binding.reviewRatingPicker.setValue(0);

                //refresh the page to show something happened and reset the inputs
                viewModel.refreshGame();
            }
        });
    }



    private void updateGameInfo(DetailedGameEntity game) {
        String title = game.getTitle();
        binding.gameTitle.setText(title);
        binding.gameDescription.setText(game.getDescription());

        Glide.with(requireContext()).load(game.getCoverImage()).into(binding.gameImage);

        String formatted = game.getReleaseDate();
        try {
            SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Date date = parser.parse(game.getReleaseDate());
            formatted = DateFormat.getMediumDateFormat(getContext()).format(date);
        } catch (ParseException e) {
            // better parsing error handling inspired by Claude.
            Log.w("SpecificGameFragment", "Failed to parse release date: " + game.getReleaseDate(), e);
        }
        binding.gameReleaseDate.setText(formatted);

        String priceText = "$" + game.getPrice();
        binding.gamePrice.setText(priceText);

        binding.gameDeveloper.setText(game.getDeveloper());
        binding.gamePublisher.setText(game.getPublisher());

        Float averageRating = game.getAverageRating();
        String averageRatingText = averageRating == null ? "No Reviews" : String.valueOf(averageRating);
        binding.gameRating.setText(averageRatingText);

        binding.gameFavoriteAmount.setText(String.valueOf(game.getFavoriteOf().size()));
        binding.gameWantToPlayAmount.setText(String.valueOf(game.getWantToPlay().size()));
        binding.gameHavePlayedAmount.setText(String.valueOf(game.getHavePlayed().size()));

        genreAdapter.setData(game.getGenres());
        //get the reviews and stack own on top
        viewModel.getUserService().getLoggedInUsername(new ServiceCallback<String>() {
            @Override
            public void onSuccess(String username) {
                List<SimpleReviewEntity> reviews = new ArrayList<>(game.getReviews());

                boolean hasReview = userHasReview(reviews, username);

                if (hasReview) {
                    //hide review section if user has already submited a review
                    binding.reviewSubmitSection.setVisibility(View.GONE);
                } else {
                    //show if havent
                    binding.reviewSubmitSection.setVisibility(View.VISIBLE);
                }

                // the reorder
                reviews = reorderReviewsForUser(reviews, username);
                reviewAdapter.setLoggedInUsername(username);
                reviewAdapter.setData(reviews);
            }

            @Override
            public void onError(Exception e) {
                //not log in cant see review section
                binding.reviewSubmitSection.setVisibility(View.GONE);
                reviewAdapter.setData(game.getReviews());
            }
        });

    }

    private String getNewCollectionText(GameCollections selectedCollection, boolean isInCollection) {
        String newCollectionText = "";
        switch (selectedCollection) {
            case FAVORITE:
                newCollectionText = isInCollection ? "Unfavorite" : "Favorite";
                break;
            case WANT_TO_PLAY:
                newCollectionText = isInCollection ? "Remove From WantToPlay" : "Add to WantToPlay";
                break;
            case HAS_PLAYED:
                newCollectionText = isInCollection ? "Remove From HavePlayed" : "Add to HavePlayed";
                break;
        }
        return newCollectionText;
    }

    private void updateCollectionsAmount() {
        DetailedGameEntity game = viewModel.getGame().getValue();
        if (game == null) { return; }

        binding.gameFavoriteAmount.setText(String.valueOf(game.getFavoriteOf().size()));
        binding.gameWantToPlayAmount.setText(String.valueOf(game.getWantToPlay().size()));
        binding.gameHavePlayedAmount.setText(String.valueOf(game.getHavePlayed().size()));
    }

    //Code from developer.android.com
    //Ensures binding is null when fragment is destroyed to avoid memory leaks
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    //the method for stacking won view on top
    private List<SimpleReviewEntity> reorderReviewsForUser(
            List<SimpleReviewEntity> reviews,
            String username
    ) {
        if (username == null) return reviews;

        SimpleReviewEntity userReview = null;

        for (SimpleReviewEntity r : reviews) {
            if (username.equals(r.getAuthor())) {
                userReview = r;
                break;
            }
        }

        if (userReview != null) {
            reviews.remove(userReview);
            reviews.add(0, userReview);
        }

        return reviews;
    }

    //needed for know if user has review or not
    private boolean userHasReview(List<SimpleReviewEntity> reviews, String username) {
        if (username == null) return false;

        for (SimpleReviewEntity r : reviews) {
            if (username.equals(r.getAuthor())) {
                return true;
            }
        }
        return false;
    }



}

