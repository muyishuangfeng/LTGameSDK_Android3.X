package com.gnetop.ltgame.core.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

public class UrlUtils {

    private static UrlUtils sInstance;

    private UrlUtils() {
    }

    public static UrlUtils getInstance() {
        if (sInstance == null) {
            synchronized (UrlUtils.class) {
                if (sInstance == null) {
                    sInstance = new UrlUtils();
                }
            }
        }
        return sInstance;
    }

    /**
     * 打开网页
     */
    public void loadUrl(Activity activity, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        activity.startActivity(intent);
    }
}
