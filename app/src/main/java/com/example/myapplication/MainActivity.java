package com.example.myapplication;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.myapplication.Fragment.OperateFragment;
import com.example.myapplication.Fragment.RealClientFragment;
import com.example.myapplication.Widget.TopLayout;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private OperateFragment mOperateFragment;
    private RealClientFragment mRealClientFragment;

    private List<Fragment> mFragmentList;

    private ViewPager mViewPager;
    private ViewPagerAdapter mAdapter;

    private TopLayout mOperateLayout;
    private TopLayout mRealClientLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.hide();
        }

        initId();
        initFragment();
        initList();
        setPagerAdapter();
        mOperateLayout.setSelectState();
        setViewPagerListener();
    }
    private void initId() {
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mOperateLayout = (TopLayout) findViewById(R.id.operate_layout);
        mRealClientLayout = (TopLayout) findViewById(R.id.realclient_layout);

        mOperateLayout.setOnClickListener(this);
        mRealClientLayout.setOnClickListener(this);
    }

    private void initFragment() {
        mOperateFragment=new OperateFragment();
        mRealClientFragment=new RealClientFragment();
    }

    private void initList() {
        mFragmentList=new ArrayList<>();
        mFragmentList.add(mOperateFragment);
        mFragmentList.add(mRealClientFragment);
    }

    private void setPagerAdapter() {
        FragmentManager fm=getSupportFragmentManager();
        mAdapter = new ViewPagerAdapter(fm);
        mViewPager.setAdapter(mAdapter);
    }

    private void setViewPagerListener() {
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                setBottomState(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.operate_layout:
                setBottomState(0);
                mViewPager.setCurrentItem(0);
                break;
            case R.id.realclient_layout:
                setBottomState(1);
                mViewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }
    private void setBottomState(int position) {
        switch (position){
            case 0:
                mOperateLayout.setSelectState();
                mRealClientLayout.setUnSelectState();
                break;
            case 1:
                mOperateLayout.setUnSelectState();
                mRealClientLayout.setSelectState();
                break;
            default:
                break;
        }
    }
 //   String s;
 //   int str =Integer.parseInt(String.valueOf(s.charAt(i)));

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }
}
