package grupp3.iths.se.wineanddineparalell.activities;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import grupp3.iths.se.wineanddineparalell.fragments.ProfileFragment;
import grupp3.iths.se.wineanddineparalell.R;
import grupp3.iths.se.wineanddineparalell.fragments.SearchFragment;
import grupp3.iths.se.wineanddineparalell.fragments.WishListFragment;
import grupp3.iths.se.wineanddineparalell.fragments.AddFragment;
import grupp3.iths.se.wineanddineparalell.fragments.AppInfoFragment;

public class MainActivity extends AppCompatActivity {

    private SearchFragment searchFragment;
    private ProfileFragment profileFragment;
    private WishListFragment wishListFragment;
    private AddFragment addFragment;
    private AppInfoFragment appInfoFragment;


    private ImageButton infoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Bound BottomNavigationView & FrameLayout to variable.
        BottomNavigationView mMainNav;
        FrameLayout mMainFrame;

        // Bound MainFrame and MainNav to MainActivity class.
        mMainFrame = findViewById(R.id.main_frame);
        mMainNav = findViewById(R.id.main_nav);

        // Making an instance of every fragment.
        searchFragment = new SearchFragment();
        profileFragment = new ProfileFragment();
        wishListFragment = new WishListFragment();
        addFragment = new AddFragment();

        // TODO: Delete when LoginActivity sends you to SearchFragment after checked current user status.
        // Set so it will start the SearchFragment and not MainActivity when logged in.


        openFragmentWithAnimation(searchFragment);

        // Setup all navigation buttons to redirect to chosen fragment.
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_search:
                        openFragmentWithAnimation(searchFragment);
                        return true;
                    case R.id.nav_profile:
                        openFragmentWithAnimation(profileFragment);
                        return true;
                    case R.id.nav_wishlist:
                        openFragmentWithAnimation(wishListFragment);
                        return true;
                    case R.id.nav_add:
                        openFragmentWithAnimation(addFragment);
                        return true;
                    default:
                        return false;
                }
            }
        });

        appInfoFragment = new AppInfoFragment();

        infoBtn = findViewById(R.id.info_ib);
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragmentWithAnimation(appInfoFragment);
            }
        });


    }

    /**
     * Take an instance of a fragment and replace the fragment currently showing with the called one.
     * @param fragment
     */
    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.commit();
    }

    /**
     * Change fragment using an animation. Called with the help of enter_from_right and exit_to_right.xml files.
     * @param fragment
     */
    public void openFragmentWithAnimation(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_right);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.main_frame, fragment).commit();
    }
}
