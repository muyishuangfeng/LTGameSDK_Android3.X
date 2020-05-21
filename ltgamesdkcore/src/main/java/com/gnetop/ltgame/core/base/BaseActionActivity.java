package com.gnetop.ltgame.core.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gnetop.ltgame.core.platform.GlobalPlatform;
import com.gnetop.ltgame.core.platform.IPlatform;


/**
 * 激活登陆的通用 Activity
 */
public class BaseActionActivity extends Activity {

    public static final String TAG = BaseActionActivity.class.getSimpleName();
    //不是第一次运行
    private boolean mIsNotFirstResume = false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //for 微信
        if (getPlatform() != null) {
            getPlatform().handleIntent(this);
        }
        GlobalPlatform.dispatchAction(this, getIntent()
                .getIntExtra(GlobalPlatform.KEY_ACTION_TYPE, -1));
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (getPlatform() != null) {
            getPlatform().handleIntent(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsNotFirstResume) {
            if (getPlatform() != null) {
                getPlatform().handleIntent(this);
            }
            // 留在目标 app 后在返回会再次 resume
            checkFinish();
        } else {
            mIsNotFirstResume = true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getPlatform() != null) {
            getPlatform().onActivityResult(this, requestCode, resultCode, data);
        }
        checkFinish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalPlatform.release(this);
    }

    /**
     * 获取平台
     */
    private IPlatform getPlatform() {
        IPlatform iPlatform = GlobalPlatform.getCurrentPlatform();
        if (iPlatform == null) {
            checkFinish();
            return null;
        } else {
            return iPlatform;
        }
    }

    /**
     * 检查是否完成
     */
    public void checkFinish() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!isFinishing() && !isDestroyed()) {
                finish();
                overridePendingTransition(0, 0);
            }
        } else {
            if (!isFinishing()) {
                finish();
                overridePendingTransition(0, 0);
            }
        }
    }

    /**
     * 回调
     */
    protected void handleResp(Object object) {
        IPlatform iPlatform = getPlatform();
        if (iPlatform != null) {
            iPlatform.onResponse(object);
            Log.e("TAG","handleResp==="+object.toString());
        }
        checkFinish();
    }

}
