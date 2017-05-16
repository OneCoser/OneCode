package chenhao.lib.onecode.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class AutoImageView extends ImageView {

	public AutoImageView(Context context) {
		super(context);
	}
	
	public AutoImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public AutoImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		try {
			if (null!=bm) {
				LayoutParams params=this.getLayoutParams();
				if (null!=params) {
					double ih=bm.getHeight();
					double iw=bm.getWidth();
					double ihw=ih/iw;
					params.height=(int)(getWidth()*ihw);
					this.setScaleType(ScaleType.FIT_XY);
					this.setLayoutParams(params);
				}
			}
		} catch (Exception e) {
		}
		super.setImageBitmap(bm);
	}


}
