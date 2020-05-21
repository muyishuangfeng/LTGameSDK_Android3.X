package com.gnetop.ltgame.core.ui.progress;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.gnetop.ltgame.core.util.DensityUtils;


/**
 * 加载进度条
 *
 * @author
 * @since 2016年11月28日
 */
public class MyProgress extends ProgressBar {

    /**
     * 动画对象
     **/
//	private AnimationDrawable anim = null;
    public MyProgress(Context context) {
        super(context);
        setIndeterminateDrawable();
    }

    /**
     * <p>
     * Constructors:
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param context
     * @param attrs
     */
    public MyProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
//		anim = (AnimationDrawable) this.getDrawable();
//		anim.start();
        setIndeterminateDrawable();
    }

    private void setIndeterminateDrawable() {
//        setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_anim));
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        widthMeasureSpec = DensityUtils.Dp2px(getContext(), 20);
        heightMeasureSpec = DensityUtils.Dp2px(getContext(), 20);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    //
//	/**
//	 *
//	 * @Methods: show
//	 * @Description: 显示进度条
//	 * @throws
//	 */
//	public void show() {
//		this.setVisibility(View.VISIBLE);
//	}
//
//	/**
//	 *
//	 * @Methods: dismiss
//	 * @Description: 隐藏进度条
//	 * @throws
//	 */
//	public void dismiss() {
//		this.setVisibility(View.GONE);
//	}
//
//	@Override
//	public void onWindowFocusChanged(boolean hasWindowFocus) {
//		if (anim == null) {
//			anim = (AnimationDrawable) this.getDrawable();
//		}
//		if (anim.isRunning()) {
//			anim.stop();
//		} else {
//			anim.start();
//		}
//	}

}
