package se.rickylagerkvist.owni.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.FireBaseUser;
import se.rickylagerkvist.owni.ui.ActivityFragment.ActivitiesFragment;
import se.rickylagerkvist.owni.ui.ActivityFragment.AddActivityCardDialog;
import se.rickylagerkvist.owni.ui.PeopleFragment.AddPeopleCardDialog;
import se.rickylagerkvist.owni.ui.PeopleFragment.PeopleFragment;
import se.rickylagerkvist.owni.ui.loginAndCreateUser.LoginActivity;
import se.rickylagerkvist.owni.utils.Constants;

public class MainActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private FloatingActionButton mFab;
    private TabLayout mTabLayout;
    private Toolbar mToolbar;

    // icon and color for mFab on tab changes
    int[] iconIntArray = {R.drawable.ic_people_white_24dp, R.drawable.ic_local_dining_white_24dp};
    int[] colorIntArray = {R.color.colorAccent, R.color.colorAccent};

    // Firebase
    private ValueEventListener mFirebaseRefListener;
    private Firebase mFirebaseRef;

    private Firebase.AuthStateListener mFirebaseRefAuthListener;

    String mUserUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUserUid = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("USERUID", "defaultStringIfNothingFound");
        // if mUserUid has default value start LoginActivity
        Intent intent;
        if (mUserUid.equals("defaultStringIfNothingFound")) {
            intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        // set bottom margin equal to navBarHeight
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int navBarHeight = getNavigationBarHeight();
            findViewById(R.id.container).setPadding(0, 0, 0, navBarHeight);
        }

        mFirebaseRef = new Firebase(Constants.FIREBASE_URL_USERS + "/" + mUserUid);

        // listens for login state, if the user is logged out open LoginActivity
        mFirebaseRefAuthListener = mFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData == null || mUserUid == null) {
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("ENCODEDEMAIL", null).apply();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(sectionsPagerAdapter);

        // set icons for tabs (with two states: seletcted and unselected)
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_people_material);
        mTabLayout.getTabAt(1).setIcon(R.drawable.ic_activities_material);
        // set tint color
        mTabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // will select method depending on tab position
                FABClickOpenDialog(view);
            }
        });


        // change mToolbar title, mFab color, icon on tab selected with animation
        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());

                int position = mTabLayout.getSelectedTabPosition();
                if (position == 0) {
                    mToolbar.setTitle("People");
                } else if (position == 1) {
                    mToolbar.setTitle("Activities");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    // Checks witch tab is active and selects method
    private void FABClickOpenDialog(View view) {
        int position = mTabLayout.getSelectedTabPosition();

        if (position == 0) {
            showAddPeopleCardDialog(view);
        } else if (position == 1) {
            showAddActivityCardDialog(view);
        }
    }

    // Open dialog to add new PeopleCard
    public void showAddPeopleCardDialog(View view) {
        DialogFragment dialog = AddPeopleCardDialog.newInstance();
        dialog.show(MainActivity.this.getFragmentManager(), "AddPeopleCardDialog");
    }

    // Open dialog to add new ActivityCard
    public void showAddActivityCardDialog(View view) {
        DialogFragment dialog = AddActivityCardDialog.newInstance();
        dialog.show(MainActivity.this.getFragmentManager(), "AddActivitiesCardDialog");
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the mMenu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // set mMenu item to display name of user
        mFirebaseRefListener = mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FireBaseUser mUser = dataSnapshot.getValue(FireBaseUser.class);
                MenuItem m = menu.findItem(R.id.log_out);
                if (mUser != null) {
                    m.setTitle("Log out " + mUser.getName());
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.log_out) {
            mFirebaseRef.unauth();
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("ENCODEDEMAIL", null).apply();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            if (position == 0) {
                fragment = new PeopleFragment();
            } else if (position == 1) {
                fragment = new ActivitiesFragment();
            }

            return fragment;
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }

    // mFab animation, shrink mFab and scale up again
    // from: http://stackoverflow.com/questions/31415742/how-to-change-floatingactionbutton-between-tabs/31418573
    protected void animateFab(final int position) {
        mFab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(100);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mFab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[position]));
                    mFab.setImageDrawable(getResources().getDrawable(iconIntArray[position], null));
                }

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                mFab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFab.startAnimation(shrink);
    }

    // fixes problem with navbar overlapping our views
    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    // remove EventListener for better memory performance
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFirebaseRefListener != null && mFirebaseRefAuthListener != null) {
            mFirebaseRef.removeEventListener(mFirebaseRefListener);
            mFirebaseRef.removeAuthStateListener(mFirebaseRefAuthListener);
        }
    }
}