package myteam.com.messenger.main;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import myteam.com.messenger.chats.ChatsFragment;
import myteam.com.messenger.main.people.PeopleFragment;
import myteam.com.messenger.main.profile.ProfileFragment;

/**
 * Returns a fragment corresponding to one of the sections/tabs/pages.
 */


public class SectionsPagerAdapter extends FragmentPagerAdapter {
    // Sparse array to keep track of registered fragments in memory
    private SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private final Context mContext;

    public SectionsPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }


    @Override
    public Fragment getItem(int position) {
//        switch (position) {
//            case 1:
//                return ChatsFragment.newInstance(position);
//            case 2:
//                return ExpenseFragment.newInstance(position);
//            default:
//                return PlanFragment.newInstance(position);
//        }
        return registeredFragments.valueAt(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 1:
                return mContext.getString(PeopleFragment.TITLE_ID);
            case 2:
                return mContext.getString(ProfileFragment.TITLE_ID);
            default:
                return mContext.getString(ChatsFragment.TITLE_ID);
        }
    }

    @Override
    public int getCount() {
        return registeredFragments.size();
    }

    public void addFragment(Fragment fragment){
        registeredFragments.put(getCount(),fragment);
    }

//    // Register the fragment when the item is instantiated
//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        registeredFragments.put(position, fragment);
//        return fragment;
//    }
//
//    // Unregister when the item is inactive
//    @Override
//    public void destroyItem(ViewGroup container, int position, Object object) {
//        registeredFragments.remove(position);
//        super.destroyItem(container, position, object);
//    }
//
//    // Returns the fragment for the position (if instantiated)
//    public Fragment getRegisteredFragment(int position) {
//        return registeredFragments.get(position);
//    }

}
