package chenhao.lib.onecode.image.crop;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import chenhao.lib.onecode.OneCode;
import chenhao.lib.onecode.R;
import chenhao.lib.onecode.image.ImageUtil;
import chenhao.lib.onecode.utils.StringUtils;

public class ClipActivity extends Activity implements OnClickListener {
    ClipNewLayout mClipLayout;
    private boolean isFixed;
    private int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.onecode_clip_picture_activity);
        String path = getIntent().getStringExtra("path");
        isFixed = getIntent().getBooleanExtra("isFixed", false);
        width = getIntent().getIntExtra("width", 0);
        height = getIntent().getIntExtra("height", 0);
        if (StringUtils.isEmpty(path)) {
            finish();
        }
        mClipLayout = (ClipNewLayout) findViewById(R.id.clip_layout);
        mClipLayout.setInitData(width, height, isFixed);
        findViewById(R.id.clip_save).setOnClickListener(this);
        findViewById(R.id.clip_rotate).setOnClickListener(this);
        findViewById(R.id.clip_cancel).setOnClickListener(this);
        mClipLayout.setSourceImage(path, getWindow());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mClipLayout.onDestory();
    }

    private void clipBitmap() {
        if (null!= OneCode.getConfig()){
            Bitmap bitmap = mClipLayout.getBitmap();
            String path = OneCode.getConfig().getCachePath() + System.currentTimeMillis() + ".jpg";
            if (bitmap != null) {
                ImageUtil.saveMyBitmap(bitmap, path, 90);
                bitmap.recycle();
                System.gc();
            }
            setResult(RESULT_OK, new Intent().putExtra("data", path));
        }
        finish();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.clip_cancel) {
            finish();
        } else if (v.getId() == R.id.clip_save) {
            clipBitmap();
        } else if (v.getId() == R.id.clip_rotate) {
            if (mClipLayout != null) {
                mClipLayout.rotate(90.0f);
            }
        }
    }

}
