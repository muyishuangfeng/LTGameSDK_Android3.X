package com.gnetop.ltgame.core.ui;

import android.os.Parcel;
import android.os.Parcelable;

import com.gnetop.ltgame.core.R;

import me.yokeyword.fragmentation.anim.FragmentAnimator;

public class FadeInAnimator extends FragmentAnimator implements Parcelable {

    public FadeInAnimator() {
        enter = R.anim.hfragment_enter;
        exit = R.anim.hfragment_exit;
        popEnter = R.anim.hfragment_pop_enter;
        popExit = R.anim.hfragment_pop_exit;
    }

    private FadeInAnimator(Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FadeInAnimator> CREATOR = new Creator<FadeInAnimator>() {
        @Override
        public FadeInAnimator createFromParcel(Parcel in) {
            return new FadeInAnimator(in);
        }

        @Override
        public FadeInAnimator[] newArray(int size) {
            return new FadeInAnimator[size];
        }
    };
}
