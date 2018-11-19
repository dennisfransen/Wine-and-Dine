package grupp3.iths.se.wineanddineparalell;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private SearchFragment searchFragment;
    private ProfileFragment profileFragment;
    private WishListFragment wishListFragment;
    private AddFragment addFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bound BottomNavigationView to variable.
        BottomNavigationView mMainNav;

        // Bound MainFrame and MainNav to MainActivity class.
        mMainFrame = findViewById(R.id.main_frame);
        mMainNav = findViewById(R.id.main_nav);

        // Making an instance of every fragment.
        searchFragment = new SearchFragment();
        profileFragment = new ProfileFragment();
        wishListFragment = new WishListFragment();
        addFragment = new AddFragment();

        // Setup all navigation buttons to redirect to chosen fragment.
        mMainNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_search:
                        setFragment(searchFragment);
                        // Change background color of the button you have chosen. If wanted to:
                        // mMainNav.setItemBackgroundResource(R.color.colorPrimary);
                        return true;
                    case R.id.nav_profile:
                        setFragment(profileFragment);
                        return true;
                    case R.id.nav_wishlist:
                        setFragment(wishListFragment);
                        return true;
                    case R.id.nav_add:
                        setFragment(addFragment);
                        return true;
                    default:
                        return false;

                }
            }
        });
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame);
        fragmentTransaction.commit();
    }
}
