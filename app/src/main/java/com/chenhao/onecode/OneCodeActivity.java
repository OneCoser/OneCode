package com.chenhao.onecode;

import android.os.Bundle;

import chenhao.lib.onecode.base.BaseActivity;

/**
 * 所属项目：OneCode
 * 创建日期：2017/5/16
 * 创建人：onecode
 * 修改日期：2017/5/16
 * 修改人：onecode
 * 描述：OneCodeActivity
 */

public class OneCodeActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_onecode);
    }

    @Override
    public String getPageName() {
        return null;
    }

    @Override
    protected void reLoad(int status) {

    }
}
