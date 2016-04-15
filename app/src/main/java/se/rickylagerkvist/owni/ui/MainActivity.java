package se.rickylagerkvist.owni.ui;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import se.rickylagerkvist.owni.R;
import se.rickylagerkvist.owni.model.FireBaseUser;
import se.rickylagerkvist.owni.ui.ActivityFragment.ActivitiesFragment;
import se.rickylagerkvist.owni.ui.PeopleFragment.AddPeopleCardDialog;
import se.rickylagerkvist.owni.ui.PeopleFragment.PeopleFragment;
import se.rickylagerkvist.owni.ui.loginAndCreateUser.LoginActivity;
import se.rickylagerkvist.owni.utils.Constants;

public class MainActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FloatingActionButton fab;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private Firebase mFirebaseRef;
    private Menu menu;

    // icon for fab on tab changes
    int[] iconIntArray = {R.drawable.ic_people_white_24dp, R.drawable.ic_local_dining_white_24dp};
    int[] colorIntArray = {R.color.colorAccent, R.color.blueColor};

    private ValueEventListener mFirebaseRefListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set bottom margin equal to navBarHeight
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int navBarHeight = getNavigationBarHeight();
            findViewById(R.id.container).setPadding(0, 0, 0, navBarHeight);
        }

        // Set Firebase Context and connection String
        Firebase.setAndroidContext(this);
        mFirebaseRef = new Firebase(Constants.FIREBASE_URL_USERS + "/" + Constants.KEY_ENCODED_EMAIL);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // set icons for tabs (with two states: seletcted and unselected)
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_people_material);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_activities_material);
        // set tint color
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#FFFFFF"));

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // will select method depending on tab position
                FABClickOpenDialog(view);
            }
        });

        // set initial fab icon
        fab.setImageResource(R.drawable.ic_people_white_24dp);

        // change toolbar title, fab color, icon on tab selected with animation
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                animateFab(tab.getPosition());

                int position = tabLayout.getSelectedTabPosition();
                if (position == 0){
                    toolbar.setTitle("People");
                } else if (position == 1) {
                    toolbar.setTitle("Activities");
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
        int position = tabLayout.getSelectedTabPosition();

        if (position == 0){
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
        Snackbar.make(view, "AddActivity Card", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // set menu item to display name of user
        mFirebaseRefListener = mFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FireBaseUser mUser = dataSnapshot.getValue(FireBaseUser.class);
                MenuItem m = menu.findItem(R.id.log_out);
                m.setTitle("Log out " + mUser.getName());
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

    // fab animation, shrink fab and scale up again
    // from: http://stackoverflow.com/questions/31415742/how-to-change-floatingactionbutton-between-tabs/31418573
    protected void animateFab(final int position) {
        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
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
                    fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[position]));
                    fab.setImageDrawable(getResources().getDrawable(iconIntArray[position], null));
                }

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
    }

    private int getNavigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseRef.removeEventListener(mFirebaseRefListener);
    }
}