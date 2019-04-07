package myteam.com.messenger;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import myteam.com.messenger.main.SectionsPagerAdapter;
import myteam.com.messenger.main.chats.ChatsFragment;
import myteam.com.messenger.main.people.PeopleFragment;
import myteam.com.messenger.main.profile.ProfileFragment;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SectionsPagerAdapter mViewPagerAdapter;
    TabLayout mTabLayout;
    ViewPager mViewPager;

    DatabaseReference reference;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mViewPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), this);
        mViewPagerAdapter.addFragment(new ChatsFragment());
        mViewPagerAdapter.addFragment(new PeopleFragment());
        mViewPagerAdapter.addFragment(new ProfileFragment());



        mViewPager = findViewById(R.id.view_pager);
        mViewPager.setAdapter(mViewPagerAdapter);
//        mViewPager.setOffscreenPageLimit(3);

        mTabLayout = findViewById(R.id.tab_layout);
        mTabLayout.setupWithViewPager(mViewPager);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    private void status(String status){
        reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }

}
