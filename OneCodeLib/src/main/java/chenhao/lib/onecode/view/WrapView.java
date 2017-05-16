package chenhao.lib.onecode.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by onecode on 15/11/4.
 */
public class WrapView extends ViewGroup {

    private int maxWidth,maxLine=0;
    private int VIEW_SPACE=5;

    public WrapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        VIEW_SPACE=(int)(5*getResources().getDisplayMetrics().density);
    }

    public void setMaxLine(int line){
        maxLine=line;
    }

    public void setViewSpace(int space){
        VIEW_SPACE=space;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        maxWidth = MeasureSpec.getSize(widthMeasureSpec);
        int childCount = getChildCount();
        int x = 0;
        int y = 0;
        int nowLine = 0;
        ArrayList<View> killV=new ArrayList<View>();
        for (int index = 0; index < childCount; index++) {
            View child = getChildAt(index);
            if (child.getVisibility() != View.GONE) {
                child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                x += width + (index==0?0:VIEW_SPACE);
                y = nowLine * height + height;
                if (x > maxWidth) {
                    x = width;
                    nowLine++;
                    y = nowLine * height + height;
                }
                if (maxLine>0&&nowLine>=maxLine){
                    killV.add(child);
                }
            }
        }
        setMeasuredDimension(nowLine==0?x:maxWidth, y+(nowLine*VIEW_SPACE));
        if (killV.size()>0){
            for (View v:killV){
                removeView(v);
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int childCount = getChildCount();
        maxWidth = r - l;
        int x = 0;
        int y = 0;
        int nowLine = 0;
        ArrayList<View> killV=new ArrayList<View>();
        for (int i = 0; i < childCount; i++) {
            View child = this.getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                int width = child.getMeasuredWidth();
                int height = child.getMeasuredHeight();
                x += width + (i==0?0:VIEW_SPACE);
                y = nowLine * (height + VIEW_SPACE) + height;
                if (x > maxWidth) {
                    x = width;
                    nowLine++;
                    y = nowLine * (height + VIEW_SPACE) + height;
                }
                child.layout(x - width, y - height, x, y);
            }
            if (maxLine>0&&nowLine>=maxLine){
                killV.add(child);
            }
        }
        if (killV.size()>0){
            for (View v:killV){
                removeView(v);
            }
        }
    }
}
