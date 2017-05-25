package chenhao.lib.onecode.image;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import chenhao.lib.onecode.base.BaseBigImageActivity;
import chenhao.lib.onecode.utils.StringUtils;

/**
 * Created by onecode on 16/5/2.
 * 看大图
 */
public class SimlpBigImageActivity extends BaseBigImageActivity {

    public static void show(Activity a, Image data) {
        if (null != data && StringUtils.isNotEmpty(data.checkUrl())) {
            ArrayList<Image> images = new ArrayList<>();
            images.add(data);
            show(a,images,0);
        }
    }

    public static void show(Activity a, ArrayList<Image> images, int showIndex) {
        if (null != images && images.size() > 0) {
            Intent intent = new Intent(a, SimlpBigImageActivity.class);
            intent.putParcelableArrayListExtra("images", images);
            intent.putExtra("showIndex", showIndex);
            a.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<Image> list=getIntent().getParcelableArrayListExtra("images");
        showImages(list,getIntent().getIntExtra("showIndex",0));
    }

    @Override
    public boolean onLongClickImage(String image) {
        return false;
    }

    @Override
    public String getPageName() {
        return this.getClass().getSimpleName();
    }

    @Override
    protected void systemStatusAction(int status) {

    }

}
