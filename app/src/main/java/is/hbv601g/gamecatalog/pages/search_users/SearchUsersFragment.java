package is.hbv601g.gamecatalog.pages.search_users;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.UserAdapter;
import is.hbv601g.gamecatalog.databinding.FragmentSearchUsersBinding;
import is.hbv601g.gamecatalog.entities.user.SimpleUserEntity;
import is.hbv601g.gamecatalog.services.NetworkService;
import is.hbv601g.gamecatalog.services.UserService;

public class SearchUsersFragment extends Fragment {

    private SearchUsersViewModel viewModel;

    private FragmentSearchUsersBinding binding;
    private UserAdapter userAdapter;

    public SearchUsersFragment() {
        //Required empty constructor
    }

    //Code boilerplate from developer.android.com
    //Inflates the layout for this fragment
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentSearchUsersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userAdapter = new UserAdapter();
        userAdapter.setOnUserClickListener(clickedUserId -> navigateToOtherProfile(clickedUserId));
        binding.userRecycler.setLayoutManager(
                new LinearLayoutManager(
                        requireContext(),
                        LinearLayoutManager.VERTICAL,
                        false
                )
        );
        binding.userRecycler.setAdapter(userAdapter);

        //Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(SearchUsersViewModel.class);

        NetworkService networkService = new NetworkService(requireContext());
        UserService userService = new UserService(networkService);

        viewModel.init(userService);

        String[] modes = {"username", "following", "reviewAmount", "favoriteAmount", "wantsToPlayAmount", "hasPlayedAmount"};

        ArrayAdapter<String> selectorAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                modes
        );

        binding.sortBySelector.setAdapter(selectorAdapter);

        binding.titleInput.addTextChangedListener(new TextWatcher() {
                                                      @Override
                                                      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                                          // required override
                                                      }

                                                      @Override
                                                      public void onTextChanged(CharSequence s, int start, int before, int count) {
                                                          viewModel.setUsernameParam(s.toString());
                                                      }

                                                      @Override
                                                      public void afterTextChanged(Editable s) {
                                                          // required override
                                                      }
                                                  }
        );

        binding.sortBySelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView parent, View view, int position, long id) {

                String selected = modes[position];
                viewModel.setSortBy(selected);
            }

            @Override
            public void onNothingSelected(AdapterView parent) {
                // required override
            }
        });

        binding.sortReverseToggleButton
                .setOnCheckedChangeListener(((buttonView, isChecked) -> {
                    viewModel.setSortReverse(isChecked);
                }));

        binding.searchButton.setOnClickListener(v -> {
            viewModel.refreshPage();
        });

        binding.nextPage.setOnClickListener(v -> {
            viewModel.nextPage();
        });

        binding.previousPage.setOnClickListener(v -> {
            viewModel.previousPage();
        });

        viewModel.getUsers().observe(getViewLifecycleOwner(), this::updateUsersInfo);

        // this calls updatePageInfo twice if both maxPage and currentPage change
        // but that only happens if new users are added while browsing which is unlikely
        viewModel.getCurrentPage().observe(getViewLifecycleOwner(), this::updatePageInfo);
        viewModel.getPageAmount().observe(getViewLifecycleOwner(), this::updatePageInfo);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.nextPage.setEnabled(!isLoading);
            binding.previousPage.setEnabled(!isLoading);
            binding.searchButton.setEnabled(!isLoading);
            if (isLoading) {
                binding.loadingIndicator.setVisibility(View.VISIBLE);
                binding.userRecycler.setAlpha(0.3f);
            } else {
                binding.loadingIndicator.setVisibility(View.GONE);
                binding.userRecycler.setAlpha(1f);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            binding.userListErrorMessage.setText(errorMessage);
            binding.userRecycler.setVisibility(View.GONE);
            binding.loadingIndicator.setVisibility(View.GONE);
            binding.userListErrorContainer.setVisibility(View.VISIBLE);
            binding.previousPage.setEnabled(false);
            binding.nextPage.setEnabled(false);
            binding.searchButton.setEnabled(false);
        });

        binding.userListRetryButton.setOnClickListener(v -> {
            binding.userListErrorContainer.setVisibility(View.GONE);
            binding.userRecycler.setVisibility(View.VISIBLE);
            viewModel.refreshPage();
        });
    }

    public void updateUsersInfo(List<SimpleUserEntity> users) {
        userAdapter.setData(users);
        binding.userRecycler.scrollToPosition(0);
        String pageDisplay = "Page " + viewModel.getCurrentPage();
        binding.pageDisplay.setText(pageDisplay);
    }

    public void updatePageInfo(Integer ignored) {
        Integer currentPage = viewModel.getCurrentPage().getValue();
        Integer pageAmount = viewModel.getPageAmount().getValue();

        String pageAmountDisplay = pageAmount == null ? "?" : pageAmount.toString();
        String currentPageDisplay = currentPage == null ? "?" : currentPage.toString();

        String pageDisplayText = currentPageDisplay + " / " + pageAmountDisplay;
        binding.pageDisplay.setText(pageDisplayText);
    }

    //Code from developer.android.com
    //Ensures binding is null when fragment is destroyed to avoid memory leaks
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void navigateToOtherProfile(long userId) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", userId);
        // nav action has popUpTo so the back stack doesn't grow unboundedly
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(R.id.navigation_other_user_profile, bundle);
    }
}
