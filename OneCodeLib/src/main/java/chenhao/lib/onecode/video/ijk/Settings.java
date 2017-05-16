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
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Settings {
    private Context mAppContext;
    private SharedPreferences mSharedPreferences;

    public Settings(Context context) {
        mAppContext = context.getApplicationContext();
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(mAppContext);
    }

    public boolean getEnableBackgroundPlay() {
        return mSharedPreferences.getBoolean("pref.enable_background_play", false);
    }

    public boolean getUsingMediaCodec() {
        return mSharedPreferences.getBoolean("pref.using_media_codec", false);
    }

    public boolean getUsingMediaCodecAutoRotate() {
        return mSharedPreferences.getBoolean("pref.using_media_codec_auto_rotate", false);
    }

    public boolean getUsingOpenSLES() {
        return mSharedPreferences.getBoolean("pref.using_opensl_es", false);
    }

    public String getPixelFormat() {
        return mSharedPreferences.getString("pref.pixel_format", "");
    }

    public boolean getEnableNoView() {
        return mSharedPreferences.getBoolean("pref.enable_no_view", false);
    }

    public boolean getEnableSurfaceView() {
        return mSharedPreferences.getBoolean("pref.enable_surface_view", false);
    }

    public boolean getEnableTextureView() {
        return mSharedPreferences.getBoolean("pref.enable_texture_view", false);
    }

    public boolean getEnableDetachedSurfaceTextureView() {
        return mSharedPreferences.getBoolean("pref.enable_detached_surface_texture", false);
    }

    public String getLastDirectory() {
        return mSharedPreferences.getString("", "/");
    }

    public void setLastDirectory(String path) {
        mSharedPreferences.edit().putString("", path).apply();
    }
}
