package chenhao.lib.onecode.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.Button;

public class AlertItemView extends Button {
	private int defaultColor= Color.parseColor("#cfcfcf");
	private int pressColor= Color.parseColor("#5fc6ff");
	private OnClickListener clickListener;
	public AlertItemView(Context context) {
		super(context);
	}
	public AlertItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public AlertItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		this.clickListener=l;
	}
	
	public void setColor(int defaultColor,int pressColor){
        this.setTextColor(defaultColor);
        this.defaultColor=defaultColor;
        this.pressColor=pressColor;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			setTextColor(pressColor);
			break;
		case MotionEvent.ACTION_UP:
			setTextColor(defaultColor);
			if (null!=clickListener) {
				clickListener.onClick(this);
			}
			break;
		default:
			break;
		}
		return true;
	}
	
}
