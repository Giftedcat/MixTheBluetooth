package com.hc.basiclibrary.viewBasic.manage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.viewpager.widget.ViewPager;

import com.hc.basiclibrary.viewBasic.manage.assist.FixedSpeedScroller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ViewPagerManage {

    private List<Fragment> mFragmentList;
    private ViewPager mViewPager;

    public ViewPagerManage(ViewPager viewPager){
        mViewPager = viewPager;
    }

    public Fragment addFragment(Fragment fragment){
        if (mFragmentList == null)
            mFragmentList = new ArrayList<>();
        mFragmentList.add(fragment);
        return fragment;
    }

    public FragmentAdapter getAdapter(){
        return new FragmentAdapter(((FragmentActivity)mViewPager.getContext()).getSupportFragmentManager(),3).setList(mFragmentList);
    }

    //控制点击移动的速度
    public void setDuration(int delayTime){
        try {
            Class clazz=Class.forName("androidx.viewpager.widget.ViewPager");
            Field f=clazz.getDeclaredField("mScroller");
            FixedSpeedScroller fixedSpeedScroller=new FixedSpeedScroller(mViewPager.getContext(),new LinearOutSlowInInterpolator());
            fixedSpeedScroller.setmDuration(delayTime);
            f.setAccessible(true);
            f.set(mViewPager,fixedSpeedScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setPositionListener(final PositionListener listener){
        ViewPager.OnPageChangeListener changeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (listener != null){
                    listener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
        mViewPager.addOnPageChangeListener(changeListener);
    }


    class FragmentAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragmentList;

        FragmentAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        FragmentAdapter setList(List<Fragment> list){
            fragmentList = list;
            return this;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    public interface PositionListener{
        void onPageSelected(int position);
    }

}
