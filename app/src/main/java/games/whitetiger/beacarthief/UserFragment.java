package games.whitetiger.beacarthief;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class UserFragment extends Fragment {

    View myView;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private PagerTabStrip mPagerTabStrip;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.fragment_user, container, false);

        // Create the adapter that will return a fragment for each of the
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) myView.findViewById(R.id.container);
        mPagerTabStrip = (PagerTabStrip) myView.findViewById(R.id.pager_header);
        mPagerTabStrip.setTabIndicatorColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.setCurrentItem(0);

        return myView;
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int NUM_ITEMS = 2;

        public SectionsPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: // Fragment # 0 - This will show FirstFragment
                    return ProfileFragment.newInstance();
                case 1: // Fragment # 0 - This will show FirstFragment different title
                    return FinanceFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        /**
         * Returns the page title for the top indicator
         * @param position
         * @return String
         */
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_profile);
                case 1:
                    return getString(R.string.title_finances);
            }
            return null;
        }
    }
}
