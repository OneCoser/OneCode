package chenhao.lib.onecode.utils;

import android.graphics.Bitmap;
import com.facebook.imagepipeline.request.BasePostprocessor;

public class FuzzyPostprocessor extends BasePostprocessor {

    private int blur=30,brightness=-66;

    public FuzzyPostprocessor() {
        this.blur=30;
        this.brightness=-66;
    }

    public FuzzyPostprocessor(int blur, int brightness) {
        this.blur = blur;
        this.brightness = brightness;
    }

    @Override
    public String getName() {
        return "FuzzyPostprocessor";
    }

    @Override
    public void process(Bitmap bitmap) {
        Clog.i("FuzzyPostprocessor");
        try{
            if (brightness!=0&&blur>0){
                FileUtils.doBrightness(FileUtils.doBlur(bitmap,blur),brightness);
            }else if(brightness!=0){
                FileUtils.doBrightness(bitmap,brightness);
            }else if(blur>0){
                FileUtils.doBlur(bitmap,blur);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
