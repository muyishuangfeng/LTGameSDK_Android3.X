package com.gnetop.ltgame.core.util;

import android.graphics.Bitmap;


/**
 * Describe : Bitmap 辅助 32kb = 32768
 * 获取指定大小图片的流程
 * 1. decode options outWidth,outHeight
 * 2. 利用bitmap的宽高，通过 w*h 小于 maxSize 大致计算目标图片宽高,
 * 这里 maxSize 指的是 byte[] length，利用宽高计算略有差异，
 * 这样做有两个好处，一个是不需要将整个图片 decode 到内存，只拿到 32kb 多一点的图片，
 * 第二个是可以尽快接近目标图片的大小,减少后续细节调整的次数
 * 经过此步之后拿到的 bitmap 会稍微大于 32kb
 * 3. 细节调整，利用 matrix.scale 每次缩小为原来的 0.9，循环接近目标大小
 */
class BitmapUtil {


    static void recyclerBitmaps(Bitmap... bitmaps) {
        try {
            for (Bitmap bitmap : bitmaps) {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
