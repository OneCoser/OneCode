/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package chenhao.lib.onecode.video.ijk;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.MediaController;

import java.util.ArrayList;

public class MediaControllerDefault extends MediaController implements IMediaController {
    private ActionBar mActionBar;

    public MediaControllerDefault(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MediaControllerDefault(Context context, boolean useFastForward) {
        super(context, useFastForward);
    }

    public MediaControllerDefault(Context context) {
        super(context);
    }

    public void setSupportActionBar(ActionBar actionBar) {
        mActionBar = actionBar;
        if (null!=mActionBar){
            if (isShowing()) {
                actionBar.show();
            } else {
                actionBar.hide();
            }
        }
    }

    @Override
    public void show() {
        super.show();
        if (null!=mActionBar){
            mActionBar.show();
        }
    }

    @Override
    public void hide() {
        super.hide();
        if (null!=mActionBar){
            mActionBar.hide();
        }
        if (null!=mShowOnceArray&&mShowOnceArray.size()>0){
            for (View view : mShowOnceArray){
                view.setVisibility(View.GONE);
            }
            mShowOnceArray.clear();
        }
    }

    private ArrayList<View> mShowOnceArray = new ArrayList<View>();

    @Override
    public void showOnce(View view) {
        if (null!=view){
            if (null==mShowOnceArray){
                mShowOnceArray=new ArrayList<>();
            }
            mShowOnceArray.add(view);
            view.setVisibility(View.VISIBLE);
            show();
        }
    }

}
