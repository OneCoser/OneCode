package com.chenhao.onecode;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import butterknife.BindView;
import chenhao.lib.onecode.base.BaseFragment;
import chenhao.lib.onecode.view.FilletBtView;

/**
 * 所属项目：OneCode
 * 创建日期：2017/8/9
 * 创建人：onecode
 * 修改日期：2017/8/9
 * 修改人：onecode
 * 描述：PageItemFragment
 */

public class PageItemFragment extends BaseFragment {

    @BindView(R.id.page_name)
    FilletBtView pageName;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return View.inflate(getActivity(),R.layout.app_fragment_page,null);
    }

    @Override
    public void initView() {
        pageName.setText(null!=getArguments()?getArguments().getString("name","NoName"):"NoName");
    }

    @Override
    public String getPageName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void systemStatusAction(int status) {

    }

}
