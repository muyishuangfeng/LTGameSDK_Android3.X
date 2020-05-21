package com.gnetop.ltgame.core.manager;


import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;

import com.gnetop.ltgame.core.exception.LTGameError;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnLoginStateListener;
import com.gnetop.ltgame.core.model.LoginObject;
import com.gnetop.ltgame.core.model.LoginResult;
import com.gnetop.ltgame.core.model.Result;
import com.gnetop.ltgame.core.platform.GlobalPlatform;
import com.gnetop.ltgame.core.platform.IPlatform;
import com.gnetop.ltgame.core.platform.Target;

import java.lang.ref.WeakReference;

/**
 * 登陆管理类，使用该类进行登陆操作
 */
public class LoginManager {

    public static final String TAG = LoginManager.class.getSimpleName();
    private static LifecycleManager mManager;

    /**
     * 登录
     *
     * @param activity 发起登录的activity
     * @param target   目标平台
     * @param listener 登录回调
     */
    public static void login(Activity activity, int target, OnLoginStateListener listener) {
        login(activity, target, null, listener);
    }

    /**
     * 发起登录
     *
     * @param activity 发起登录的 activity
     * @param target   目标平台
     * @param object   登录对象
     * @param listener 回调
     */
    public static void login(Activity activity, int target, LoginObject object, OnLoginStateListener listener) {
        if (mManager != null) {
            mManager.onHostActivityDestroy();
        }
        if (mManager == null) {
            mManager = new LifecycleManager();
        }
        mManager.prepareLogin(activity, target, object, listener);
    }


    /**
     * 清除
     */
    public static void clear() {
        if (mManager != null) {
            mManager.onHostActivityDestroy();
        }
        GlobalPlatform.release(null);
    }

    /**
     * 开始登录
     */
    public static void actionLogin(Activity activity) {
        if (mManager != null) {
            mManager.postLogin(activity);
        }
    }


    /**
     * 生命周期管理
     */
    public static class LifecycleManager implements LifecycleObserver {

        private OnLoginStateListener mStateListener;
        //实现类
        private OnLoginStateRealize wrapListener;
        //实时目标
        private int currentTarget;
        //登录参数
        private LoginObject mObject;

        private WeakReference<Activity> fakeActivity;
        //源activity
        private WeakReference<Activity> originActivity;

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onHostActivityDestroy() {
            onProcessFinish();
        }

        /**
         * 流程结束，回收资源
         */
        private void onProcessFinish() {
            if (wrapListener != null) {
                wrapListener.mListener = null;
            }
            if (fakeActivity != null) {
                GlobalPlatform.release(fakeActivity.get());
                fakeActivity.clear();
            } else {
                GlobalPlatform.release(null);
            }

            if (originActivity != null) {
                originActivity.clear();
            }
            mStateListener = null;
            wrapListener = null;
            fakeActivity = null;
            currentTarget = -1;
            mObject = null;
        }

        /**
         * 准备登录，供外部调用
         *
         * @param activity 发起登录的 activity
         * @param target   目标平台
         * @param object   登录参数
         * @param listener 登录回调
         */
        private void prepareLogin(Activity activity, @Target.LoginTarget int target,
                                  LoginObject object, OnLoginStateListener listener) {
            if (activity instanceof LifecycleOwner) {
                Lifecycle lifecycle = ((LifecycleOwner) activity).getLifecycle();
                lifecycle.addObserver(this);
            }
            //开始登录
            listener.onState(activity, LoginResult.stateOf(Result.STATE_START));

            currentTarget = target;
            mObject = object;
            mStateListener = listener;
            originActivity = new WeakReference<>(activity);
            IPlatform platform = GlobalPlatform.newPlatformByTarget(activity, target);
            GlobalPlatform.savePlatform(platform);
            //没有安装
//            if (!platform.isInstall(activity)) {
//                listener.onState(originActivity.get(), LoginResult.failOf(target, LTGameError.make(LTGameError.CODE_NOT_INSTALL)));
//            }
            //跳转
            Intent intent = new Intent(activity, platform.getUIKitClazz());
            intent.putExtra(GlobalPlatform.KEY_ACTION_TYPE, GlobalPlatform.ACTION_TYPE_LOGIN);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }

        /**
         * 激活登录，由透明 Activity 真正的激活登录
         *
         * @param activity 透明 activity
         */
        private void postLogin(Activity activity) {
            //登录状态
            mStateListener.onState(originActivity.get(), LoginResult.stateOf(Result.STATE_ACTIVE,
                    currentTarget));
            fakeActivity = new WeakReference<>(activity);
            //平台错误
            if (currentTarget == -1) {
                mStateListener.onState(activity, LoginResult.
                        failOf(LTGameError.make(LTResultCode.STATE_COMMON_ERROR, "login target error")));
                return;
            }
            //没有设置登录回调
            if (mStateListener == null) {
                mStateListener.onState(activity,
                        LoginResult.failOf(currentTarget,
                                LTGameError.make(LTResultCode.STATE_COMMON_ERROR, "没有设置 login listener")));
                return;
            }
            //创建的 platform 失效
            if (GlobalPlatform.getCurrentPlatform() == null) {
                mStateListener.onState(activity,
                        LoginResult.failOf(currentTarget,
                                LTGameError.make(LTResultCode.STATE_COMMON_ERROR, "创建的 platform 失效")));
                return;
            }

            wrapListener = new OnLoginStateRealize(mStateListener);
            GlobalPlatform.getCurrentPlatform().login(activity, currentTarget, mObject, wrapListener);
        }

    }

    /**
     * 用于登录后回收资源
     */
    private static class OnLoginStateRealize implements OnLoginStateListener {

        OnLoginStateListener mListener;

        public OnLoginStateRealize(OnLoginStateListener listener) {
            this.mListener = listener;
        }

        /**
         * 获取Activity
         *
         * @return activity
         */
        public Activity getActivity() {
            if (mManager != null && mManager.originActivity != null) {
                return mManager.originActivity.get();
            }
            return null;
        }


        @Override
        public void onState(Activity activity, LoginResult result) {
            if (mListener != null) {
                result.target = mManager.currentTarget;
                mListener.onState(getActivity(), result);
            }

            if (result.state == LoginResult.STATE_SUCCESS ||
                    result.state == LoginResult.STATE_CANCEL ||
                    result.state == LoginResult.STATE_FAIL) {
                if (mListener != null) {
                    mListener.onState(getActivity(), LoginResult.completeOf(mManager.currentTarget));
                }
                mListener = null;
                clear();
            }
        }

        @Override
        public void onLoginOut() {
            if (mListener!=null){
                mListener.onLoginOut();
            }

        }
    }

}
