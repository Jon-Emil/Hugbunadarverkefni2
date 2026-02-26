package is.hbv601g.gamecatalog;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import is.hbv601g.gamecatalog.pages.all_games.AllGamesFragment;
import is.hbv601g.gamecatalog.storage.TokenManager;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private TokenManager tokenManager;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tokenManager = new TokenManager(this);
        navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);

        // 每次抽屉打开时，根据登录状态显示/隐藏 Log In 菜单项
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(android.view.View drawerView) {
                MenuItem loginItem = navigationView.getMenu().findItem(R.id.navigation_login);
                if (loginItem != null) {
                    loginItem.setVisible(tokenManager.getToken() == null);
                }
            }
        });

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);

        NavController navController = navHostFragment.getNavController();

        appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_login,
                R.id.navigation_search_games,
                R.id.navigation_profile
        ).setOpenableLayout(drawerLayout)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        NavigationUI.setupWithNavController(navigationView, navController);

        /*

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.nav_view), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.fragmentContainer,
                            AllGamesFragment.newInstance(1)
                    )
                    .commit();
        }
         */

    }

    //Code gotten from https://developer.android.com/guide/navigation/integrations/ui#action_bar
    @Override
    public boolean onSupportNavigateUp(){
        NavController navController =
                Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();

    }
}