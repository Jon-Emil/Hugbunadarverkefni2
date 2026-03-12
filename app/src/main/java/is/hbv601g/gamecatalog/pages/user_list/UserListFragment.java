package is.hbv601g.gamecatalog.pages.user_list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import is.hbv601g.gamecatalog.R;
import is.hbv601g.gamecatalog.adapters.UserAdapter;
import is.hbv601g.gamecatalog.databinding.FragmentUserListBinding;

/*
 * Shows a follower or following list for a given user.
 * Nav args: user_id (long), list_type ("followers" or "following").
 */
public class UserListFragment extends Fragment {

    private UserListViewModel viewModel;
    private FragmentUserListBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentUserListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(UserListViewModel.class);

        // Read navigation arguments passed by PersonalProfileFragment or OtherUserProfileFragment.
        long userId = getArguments() != null ? getArguments().getLong("user_id") : -1;
        String listType = getArguments() != null ? getArguments().getString("list_type", "followers") : "followers";

        // Set the title so the user knows which list they are viewing.
        binding.userListTitle.setText("followers".equals(listType) ? "Followers" : "Following");

        UserAdapter adapter = new UserAdapter();
        // Navigate to the tapped user's profile page.
        adapter.setOnUserClickListener(clickedUserId -> navigateToOtherProfile(clickedUserId));
        binding.userRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.userRecyclerView.setAdapter(adapter);

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading ->
                binding.userListProgressBar.setVisibility(loading ? View.VISIBLE : View.GONE));

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearErrorMessage();
            }
        });

        viewModel.getUsers().observe(getViewLifecycleOwner(), users -> {
            adapter.setData(users);
            // Show a friendly message instead of an empty RecyclerView.
            binding.emptyMessage.setVisibility(users.isEmpty() ? View.VISIBLE : View.GONE);
        });

        // Guard against missing arguments before triggering the network request.
        if (userId != -1) viewModel.loadUserList(userId, listType);
    }

    private void navigateToOtherProfile(long userId) {
        Bundle args = new Bundle();
        args.putLong("user_id", userId);
        // nav action has popUpTo so the back stack doesn't grow unboundedly
        Navigation.findNavController(requireView())
                .navigate(R.id.action_user_list_to_other_profile, args);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
