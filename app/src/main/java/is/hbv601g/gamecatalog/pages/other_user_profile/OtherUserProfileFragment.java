package is.hbv601g.gamecatalog.pages.other_user_profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import is.hbv601g.gamecatalog.databinding.FragmentOtherUserProfileBinding;
import is.hbv601g.gamecatalog.pages.BaseProfileFragment;

/*
 * Public profile screen for another user (read-only).
 * Nav arg: user_id (long). Rendering logic is all in BaseProfileFragment.
 */
public class OtherUserProfileFragment extends BaseProfileFragment {

    private OtherUserProfileViewModel viewModel;
    private FragmentOtherUserProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentOtherUserProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(OtherUserProfileViewModel.class);

        //catch args missing error to prevent crash
        Bundle args = getArguments();
        if(args == null || !args.containsKey("user_id")) {
            throw new IllegalArgumentException("Missing userId argument");
        }
        long userId = args.getLong("user_id");

        //long userId = getArguments() != null ? getArguments().getLong("user_id") : -1;

        initSharedViews(view);

        viewModel.init(userId);


        //Button logic, similar to how collection buttons work in specific game
        binding.followButton.setVisibility(View.GONE);
        viewModel.getUserAndProfilexist().observe(getViewLifecycleOwner(), doExist -> {
            if (doExist) {
                binding.followButton.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getIsProcessingFollow().observe(getViewLifecycleOwner(), isProcessing -> {
            binding.followButton.setEnabled(!isProcessing);

            if(isProcessing){
                binding.followButton.setText("Loading...");
            }
        });

        viewModel.getIsFollowing().observe(getViewLifecycleOwner(), isFollowing -> {
            binding.followButton.setText(isFollowing ? "Unfollow" : "Follow");
        });

        binding.followButton.setOnClickListener(v ->{
            Boolean isFollowing = viewModel.getIsFollowing().getValue();
            if(isFollowing != null ? isFollowing : false){
                viewModel.unfollowUser(userId);
            }
            else{
                viewModel.followUser(userId);
            }
        });



        viewModel.getIsLoading().observe(getViewLifecycleOwner(), this::setLoading);

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Toast.makeText(requireContext(), error, Toast.LENGTH_LONG).show();
                viewModel.clearErrorMessage();
            }
        });

        viewModel.getUser().observe(getViewLifecycleOwner(), this::bindUser);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
