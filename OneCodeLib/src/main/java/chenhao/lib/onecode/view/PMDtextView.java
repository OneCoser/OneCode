package chenhao.lib.onecode.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by onecode on 16/5/31.
 * 跑马灯
 */
public class PMDtextView extends TextView {

    public PMDtextView(Context context) {
        super(context);
        init();
    }

    public PMDtextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PMDtextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
//        android:singleLine="true"
//        android:ellipsize="marquee"
//        android:focusable="true"
//        android:marqueeRepeatLimit="marquee_forever"
//        android:focusableInTouchMode="true"
//        android:scrollHorizontally="true"
        setSingleLine();
        setEllipsize(TextUtils.TruncateAt.MARQUEE);
        setFocusable(true);
        setMarqueeRepeatLimit(6);
        setFocusableInTouchMode(true);
        setHorizontallyScrolling(true);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
