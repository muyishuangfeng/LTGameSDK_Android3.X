package com.gnetop.ltgame.core.base;

import android.view.View;

import com.gnetop.ltgame.core.R;

import java.util.Calendar;

public abstract class BaseAppActivity extends BaseActivity {

    //时间间隔
    private static final long NO_DOUBLE_CLICK_TIME = 2 * 1000;


    @Override
    protected int getFragmentContentId() {
        return R.id.lyt_container;
    }


    /**
     * 重复点击判断
     * @param view
     * @return
     */
    protected boolean isDoubleClick(View view) {
        Object tag = view.getTag(view.getId());
        long beforeTimeMiles = tag != null ? (long) tag : 0;
        long timeInMillis = Calendar.getInstance().getTimeInMillis();
        view.setTag(view.getId(), timeInMillis);
        return timeInMillis - beforeTimeMiles < NO_DOUBLE_CLICK_TIME;
    }






}
