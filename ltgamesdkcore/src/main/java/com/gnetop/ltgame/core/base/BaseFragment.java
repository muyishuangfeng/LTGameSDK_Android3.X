package com.gnetop.ltgame.core.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gnetop.ltgame.core.ui.FadeInAnimator;

import me.yokeyword.fragmentation.SupportFragment;
import me.yokeyword.fragmentation.anim.FragmentAnimator;


public abstract class BaseFragment extends SupportFragment {
    //视图是否创建
    protected boolean isViewCreated = false;
    //数据是否加载完成
    private boolean isLoadDataCompleted;
    protected BaseActivity mActivity;
    View mRootView;
    public static final String ARG_NUMBER = "ARG_NUMBER";


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getFragmentId(), container, false);
            initView(mRootView);
            isViewCreated = true;
        }
        return mRootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getUserVisibleHint()) {
            lazyLoadData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreated && !isLoadDataCompleted) {
            lazyLoadData();
        } else {
            isLoadDataCompleted = false;
            isViewCreated = false;
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    protected int setStatusBarView() {
        return 0;
    }

    /**
     * 获取布局
     *
     * @return
     */
    protected abstract int getFragmentId();

    /**
     * 初始化布局
     *
     * @param view
     */
    protected abstract void initView(View view);

    /**
     * 懒加载
     */
    public void lazyLoadData() {
        isLoadDataCompleted = true;
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivity(Class<?> cls) {
        startActivity(cls, null);
    }

    /**
     * 通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, int requestCode) {
        startActivityForResult(cls, null, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivityForResult(Class<?> cls, Bundle bundle,
                                       int requestCode) {
        Intent intent = new Intent();
        intent.setClass(mActivity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 含有Bundle通过Class跳转界面
     **/
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(mActivity, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 线程
     *
     * @param r
     */
    public void runOnUIThread(Runnable r) {
        final Activity activity = mActivity;
        if (activity != null && r != null)
            activity.runOnUiThread(r);
    }


    @Override
    public void onStop() {
        super.onStop();
        hideSoftInput();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideSoftInput();
    }


    // 返回唯一的Activity实例
    public final BaseActivity getProxyActivity() {
        return mActivity;
    }


    @Override
    public void pop() {
        super.pop();
        hideSoftInput();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        // 设置默认Fragment动画  默认竖向(和安卓5.0以上的动画相同)
        //return super.onCreateFragmentAnimator();
        // 设置横向(和安卓4.x动画相同)
        //return new DefaultHorizontalAnimator();
        // 设置自定义动画
        return new FadeInAnimator();
    }


}
