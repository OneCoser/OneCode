package com.chenhao.onecode;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import butterknife.BindView;
import chenhao.lib.onecode.base.BaseActivity;
import chenhao.lib.onecode.view.SimlpViewPager;

/**
 * 所属项目：OneCode
 * 创建日期：2017/8/9
 * 创建人：onecode
 * 修改日期：2017/8/9
 * 修改人：onecode
 * 描述：OneCodePagesActivity
 */

public class OneCodePagesActivity extends BaseActivity {

    @BindView(R.id.onecode_pages)
    SimlpViewPager onecodePages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_onecode_pages);
        onecodePages.setAdapter(new PageItem(getSupportFragmentManager()));
    }

    private class PageItem extends FragmentPagerAdapter {

        public PageItem(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new PageItemFragment();
        }

        @Override
        public int getCount() {
            return 6;
        }
    }

    @Override
    public String getPageName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void systemStatusAction(int status) {

    }
}
