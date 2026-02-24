package is.hbv601g.gamecatalog.pages.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import is.hbv601g.gamecatalog.databinding.FragmentLoginBinding;
import is.hbv601g.gamecatalog.pages.search_games.SearchGamesViewModel;
import is.hbv601g.gamecatalog.services.AuthService;
import is.hbv601g.gamecatalog.services.NetworkService;

public class LogInFragment extends Fragment {

    private FragmentLoginBinding binding;
    private LogInViewModel viewModel;


    public LogInFragment() {
        // Required empty public constructor
    }


    //Code boilerplate from developer.android.com
    //Inflates the layout for this fragment
    @NonNull
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(LogInViewModel.class);

        NetworkService networkService = new NetworkService(requireContext());
        AuthService authService = new AuthService(networkService, requireContext());

        viewModel.init(authService);

        binding.EmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required override
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setEmail(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required override
            }
        });

        binding.PasswordInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // required override
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.setPassword(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // required override
            }
        });

        binding.LoginButton.setOnClickListener(v -> {
            viewModel.logIn();
        });

        binding.RegisterButton.setOnClickListener(v -> {
            viewModel.register();
        });

        // Code bits inspired from ChatGPT when trying to understand toasts
        viewModel.getLoginError().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.getLoginSuccess().observe(getViewLifecycleOwner(), success -> {
            if (Boolean.TRUE.equals(success)) {
                Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();

                // NAVIGATE HERE TO PERSONAL PROFILE
            }
        });

    }

    //Code from developer.android.com
    //Ensures binding is null when fragment is destroyed to avoid memory leaks
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
