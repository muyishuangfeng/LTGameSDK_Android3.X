package com.gnetop.ltgame.core.impl;

import com.gnetop.ltgame.core.base.BaseEntry;
import com.gnetop.ltgame.core.model.ResultModel;

public interface OnAutoLoginCheckListener {


    void onCheckSuccess(BaseEntry<ResultModel> result);

    void onCheckFailed(int code);
}
