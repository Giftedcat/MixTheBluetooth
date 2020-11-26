package com.hc.basiclibrary.viewBasic.manage;

import android.util.SparseArray;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.hc.basiclibrary.R;
import com.hc.basiclibrary.viewBasic.BasFragment;
import com.hc.basiclibrary.viewBasic.tool.IMessageInterface;

import java.util.ArrayList;

import java.util.List;


public class BasFragmentManage {

    private List<FragmentMessage> mFragments = new ArrayList<>();
    private int mViewId;
    private int mPreviousFragmentLocation = -1; //之前fragment数组的位置
    private SparseArray<BasFragment> mArray = new SparseArray<>();
    private List<Integer> mFragmentOrder = new ArrayList<>();
    private FragmentActivity mActivity;

    public BasFragmentManage(int viewId, FragmentActivity activity){
        mViewId = viewId;
        mActivity = activity;
    }

    //添加所有的fragment，按照从左到右的顺序,不然会抛出异常
    public IMessageInterface addFragment(int id,BasFragment fragment){
        int length = mArray.size();
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        mArray.put(id,fragment);
        if (length < mArray.size()){

            mFragments.add(new FragmentMessage(id,fragment));
            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transaction.add(mViewId,fragment);
            transaction.commit();//提交

            for (Integer integer : mFragmentOrder) {
                if (integer == id){
                    return getInterface(id);
                }
            }
            mFragmentOrder.add(id);
        }
        hideFragment(transaction);//隐藏所有的Fragment
        return fragment;
    }

    public void showFragment(int id){
        if (id == getFragmentId())
            return;
        final FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        if (getFragmentId() != -1)
            transaction.setCustomAnimations(selectAnim(id), 0);
        else
            selectAnim(id);
        hideFragment(transaction);//隐藏所有的Fragment
        for (final FragmentMessage mFragment : mFragments) {
            if (mFragment.getViewId() == id){
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        transaction.show(mFragment.getFragment());//展示指定的Fragment
                        transaction.commit();//提交
                        mFragment.setHide(false);//设置为非隐藏
                    }
                });
                return;
            }
        }
        throw new SecurityException("请先加载id为"+id+"的fragment,请调用addFragment()");
    }


    private IMessageInterface getInterface(int id){
        for (FragmentMessage mFragment : mFragments) {
            if (mFragment.getViewId() == id){
                return (IMessageInterface) mFragment;
            }
        }
        return null;
    }



    //隐藏所有
    private void hideFragment(FragmentTransaction transaction){
        for (FragmentMessage mFragment : mFragments) {
            transaction.hide(mFragment.getFragment());
            mFragment.setHide(true);//设置标志为隐藏
        }
    }

    //删除一个fragment后，要把他重新加载尽来
    public void delete(int viewId){
        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        for (FragmentMessage mFragment : mFragments) {
            if (mFragment.getViewId() == viewId){
                transaction.remove(mFragment.getFragment());
                mFragments.remove(mFragment);
                transaction.commit();
                break;
            }
        }
    }

    //获取没有被隐藏的Fragment
    private int getFragmentId(){
        for (FragmentMessage mFragment : mFragments) {
            if (!mFragment.getHide()){
                return mFragment.getViewId();
            }
        }
        return -1;
    }


    private int selectAnim(int id) {
        boolean isRight = false;
        if (mPreviousFragmentLocation == -1){
            mPreviousFragmentLocation = getArrayLocation(id);
        }else {
            isRight = mPreviousFragmentLocation < getArrayLocation(id);
            mPreviousFragmentLocation = getArrayLocation(id);
        }

        if (isRight)
            return R.anim.fragment_right;
        else
            return R.anim.fragment_left;
    }


    private int getArrayLocation(int id){
        for(int i= 0;i<mFragmentOrder.size();i++){
            if (mFragmentOrder.get(i) == id){
                return i;
            }
        }
        return -1;
    }


    private class FragmentMessage{
        private int mViewId;
        private BasFragment mFragment;
        private boolean mHide = true;//初始为隐藏
        FragmentMessage(int viewId, BasFragment fragments){
            this.mFragment = fragments;
            this.mViewId = viewId;
        }

        BasFragment getFragment() {
            return mFragment;
        }

        int getViewId() {
            return mViewId;
        }

        void setHide(boolean mHide) {
            this.mHide = mHide;
        }

        boolean getHide(){
            return mHide;
        }
    }



}
