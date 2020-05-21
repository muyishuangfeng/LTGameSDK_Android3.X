package com.gnetop.ltgame.core.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.view.Window;

import java.util.List;

import me.yokeyword.fragmentation_swipeback.SwipeBackActivity;


public abstract class BaseActivity extends SwipeBackActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(getViewId());
        initView();
        initData();

    }

    /**
     * 绑定布局
     */
    protected abstract int getViewId();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 获取绑定的fragmentID
     */
    protected abstract int getFragmentContentId();


    /**
     * 绑定控件
     */
    public <T extends View> T $(int resId) {
        return (T) super.findViewById(resId);
    }

    /**
     * 页面跳转
     *
     * @param clz
     */
    public void startActivity(Class<?> clz) {
        startActivity(new Intent(getApplicationContext(), clz));
    }

    /**
     * 携带数据的页面跳转
     *
     * @param clz
     * @param bundle
     */
    public void startActivity(Class<?> clz, Bundle bundle) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 跳转
     *
     * @param clz           具体Activity
     * @param mAgreementUrl 用户协议
     * @param mPrivacyUrl   隐私协议
     */
    public void startActivity(Class<?> clz, String mAgreementUrl, String mPrivacyUrl) {
        Intent intent = new Intent();
        intent.setClass(this, clz);
        Bundle bundle = new Bundle();
        bundle.putString("mAgreementUrl", mAgreementUrl);
        bundle.putString("mPrivacyUrl", mPrivacyUrl);
        intent.putExtra("bundleData", bundle);
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        for (int indext = 0; indext < fragmentManager.getFragments().size(); indext++) {
            Fragment fragment = fragmentManager.getFragments().get(indext); //找到第一层Fragment
            if (fragment == null)
                Log.w(TAG, "Activity result no fragment exists for index: 0x"
                        + Integer.toHexString(requestCode));
            else
                handleResult(fragment, requestCode, resultCode, data);
        }
    }

    /**
     * 递归调用，对所有的子Fragment生效
     */
    private void handleResult(Fragment fragment, int requestCode, int resultCode, Intent data) {
        fragment.onActivityResult(requestCode, resultCode, data);//调用每个Fragment的onActivityResult
        List<Fragment> childFragment = fragment.getChildFragmentManager().getFragments(); //找到第二层Fragment
        for (Fragment f : childFragment)
            if (f != null) {
                handleResult(f, requestCode, resultCode, data);
            }
    }


    /**
     * 添加fragment
     */
    public void addFragment(BaseFragment fragment, boolean addToBackStack, boolean allowAnimation) {
        loadRootFragment(getFragmentContentId(), fragment,
                addToBackStack, allowAnimation);
    }


}


