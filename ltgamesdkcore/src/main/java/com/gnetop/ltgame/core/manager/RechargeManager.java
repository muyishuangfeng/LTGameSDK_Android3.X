package com.gnetop.ltgame.core.manager;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Intent;

import com.gnetop.ltgame.core.exception.LTGameError;
import com.gnetop.ltgame.core.exception.LTResultCode;
import com.gnetop.ltgame.core.impl.OnRechargeStateListener;
import com.gnetop.ltgame.core.model.RechargeObject;
import com.gnetop.ltgame.core.model.RechargeResult;
import com.gnetop.ltgame.core.model.Result;
import com.gnetop.ltgame.core.platform.GlobalPlatform;
import com.gnetop.ltgame.core.platform.IPlatform;
import com.gnetop.ltgame.core.platform.Target;

import java.lang.ref.WeakReference;

public class RechargeManager {

    public static final String TAG = RechargeManager.class.getSimpleName();
    private static LifecycleManager mManager;


    /**
     * 登录
     *
     * @param activity 发起登录的activity
     * @param target   目标平台
     * @param listener 登录回调
     */
    public static void recharge(Activity activity, int target, OnRechargeStateListener listener) {
        recharge(activity, target, null, listener);
    }

    /**
     * 发起登录
     *
     * @param activity 发起登录的 activity
     * @param target   目标平台
     * @param object   登录对象
     * @param listener 回调
     */
    public static void recharge(Activity activity, int target, RechargeObject object, OnRechargeStateListener listener) {
        if (mManager != null) {
            mManager.onHostActivityDestroy();
        }
        if (mManager == null) {
            mManager = new LifecycleManager();
        }
        mManager.prepareRecharge(activity, target, object, listener);
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
     * 开始支付
     */
   public static void actionRecharge(Activity activity) {
        if (mManager != null) {
            mManager.postRecharge(activity);
        }
    }


    /**
     * 生命周期管理
     */
    public static class LifecycleManager implements LifecycleObserver {

        private OnRechargeStateListener mStateListener;
        //实现类
        private OnRechargeStateRealize wrapListener;
        //实时目标
        private int currentTarget;
        //支付参数
        private RechargeObject mObject;

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
         * 准备支付，供外部调用
         *
         * @param activity 发起支付的 activity
         * @param target   目标平台
         * @param object   支付参数
         * @param listener 支付回调
         */
        private void prepareRecharge(Activity activity, @Target.RechargeTarget int target,
                                     RechargeObject object, OnRechargeStateListener listener) {
            if (activity instanceof LifecycleOwner) {
                Lifecycle lifecycle = ((LifecycleOwner) activity).getLifecycle();
                lifecycle.addObserver(this);
            }
            //开始支付
            listener.onState(activity, RechargeResult.stateOf(Result.STATE_RECHARGE_START));

            currentTarget = target;
            mObject = object;
            mStateListener = listener;
            originActivity = new WeakReference<>(activity);
            IPlatform platform = GlobalPlatform.newPlatformByTarget(activity, target);
            GlobalPlatform.savePlatform(platform);
            //没有安装
//            if (!platform.isInstall(activity)) {
//                listener.onState(originActivity.get(), RechargeResult.failOf(target,
//                        LTGameError.make(LTGameError.CODE_NOT_INSTALL)));
//            }
            //跳转
            Intent intent = new Intent(activity, platform.getUIKitClazz());
            intent.putExtra(GlobalPlatform.KEY_ACTION_TYPE, GlobalPlatform.ACTION_TYPE_RECHARGE);
            activity.startActivity(intent);
            activity.overridePendingTransition(0, 0);
        }


        /**
         * 激活支付，由透明 Activity 真正的激活支付
         *
         * @param activity 透明 activity
         */
        private void postRecharge(Activity activity) {
            //支付状态
            mStateListener.onState(originActivity.get(), RechargeResult.stateOf(Result.STATE_ACTIVE,
                    currentTarget));
            fakeActivity = new WeakReference<>(activity);
            //平台错误
            if (currentTarget == -1) {
                mStateListener.onState(activity, RechargeResult.
                        failOf(LTGameError.make(LTResultCode.SELF_PLATFORM_CODE, "login target error")));
                return;
            }
            //没有设置支付回调
            if (mStateListener == null) {
                mStateListener.onState(activity,
                        RechargeResult.failOf(currentTarget,
                                LTGameError.make(LTResultCode.SELF_PLATFORM_CODE, "没有设置 login listener")));
                return;
            }
            //创建的 platform 失效
            if (GlobalPlatform.getCurrentPlatform() == null) {
                mStateListener.onState(activity,
                        RechargeResult.failOf(currentTarget,
                                LTGameError.make(LTResultCode.SELF_PLATFORM_CODE, "创建的 platform 失效")));
                return;
            }

            wrapListener = new OnRechargeStateRealize(mStateListener);
            GlobalPlatform.getCurrentPlatform().recharge(activity, currentTarget, mObject, wrapListener);
        }

    }

    /**
     * 用于支付后回收资源
     */
    private static class OnRechargeStateRealize implements OnRechargeStateListener {

        OnRechargeStateListener mListener;

        OnRechargeStateRealize(OnRechargeStateListener listener) {
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
        public void onState(Activity activity, RechargeResult result) {
            if (mListener != null) {
                result.target = mManager.currentTarget;
                mListener.onState(getActivity(), result);
            }

            if (result.state == RechargeResult.STATE_RECHARGE_SUCCESS ||
                    result.state == RechargeResult.STATE_RECHARGE_CANCEL ||
                    result.state == RechargeResult.STATE_RECHARGE_FAILED) {
                if (mListener != null) {
                    mListener.onState(getActivity(), RechargeResult.completeOf(mManager.currentTarget));
                }
                mListener = null;
                clear();
            }
        }
    }

}
