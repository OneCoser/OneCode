package chenhao.lib.onecode.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PinYinSideBar extends View {

	private OnTouchingLetterChangedListener onTouchingLetterChangedListener;
	private int choose = -1;// 选中
	private Paint paint = new Paint();
	private TextView mTextDialog;
	private List<String> b;
	private boolean isPress;
	private int textSize=12;

	public PinYinSideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public PinYinSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public PinYinSideBar(Context context) {
		super(context);
	}

	public void initData(List<String> data, TextView mTextDialog, OnTouchingLetterChangedListener listener) {
		initData(12,data,mTextDialog,listener);
	}

	public void initData(int textSize, List<String> data, TextView mTextDialog, OnTouchingLetterChangedListener listener) {
		this.textSize=textSize;
		this.mTextDialog = mTextDialog;
		this.onTouchingLetterChangedListener = listener;
		if (null!=data&&data.size()>0){
			this.b=data;
		}
		invalidate();
	}

	private void checkB(){
		if (null==b||b.size()<=0){
			b=new ArrayList<>();
			b.add("A"); b.add("B"); b.add("C"); b.add("D"); b.add("E"); b.add("F"); b.add("G");
			b.add("H"); b.add("I"); b.add("J"); b.add("K"); b.add("L"); b.add("M"); b.add("N");
			b.add("O"); b.add("P"); b.add("Q"); b.add("R"); b.add("S"); b.add("T"); b.add("U");
			b.add("V"); b.add("W"); b.add("X"); b.add("Y"); b.add("Z"); b.add("#");
		}
	}

	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		checkB();
		int height = getHeight();
		int width = getWidth();
		int singleHeight = height / b.size();

		for (int i = 0; i < b.size(); i++) {
			paint.setColor(Color.rgb(33, 65, 98));
			paint.setColor(Color.parseColor("#888888"));
			paint.setTypeface(Typeface.DEFAULT_BOLD);
			paint.setAntiAlias(true);
			paint.setTextSize(textSize*getResources().getDisplayMetrics().density);
			if (i == choose||isPress) {
				paint.setColor(Color.WHITE);
				if (i==choose){
					paint.setFakeBoldText(true);
				}
			}
			float xPos = width / 2 - paint.measureText(b.get(i)) / 2;
			float yPos = singleHeight * i + singleHeight;
			canvas.drawText(b.get(i), xPos, yPos, paint);
			paint.reset();
		}

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		final int action = event.getAction();
		final float y = event.getY();
		final int oldChoose = choose;
		final int c = (int) (y / getHeight() * b.size());// 点击y坐标所占总高度的比例*b数组的长度就等于点击b中的个数.

		switch (action) {
		case MotionEvent.ACTION_UP:
			isPress=false;
			setBackgroundColor(Color.TRANSPARENT);
			choose = -1;
			invalidate();
			if (mTextDialog != null) {
				mTextDialog.setVisibility(View.INVISIBLE);
			}
			break;
		default:
			isPress=true;
			setBackgroundColor(Color.parseColor("#66000000"));
			if (oldChoose != c) {
				if (c >= 0 && c < b.size()) {
					if (onTouchingLetterChangedListener != null) {
						onTouchingLetterChangedListener.onTouchingLetterChanged(b.get(c));
					}
					if (mTextDialog != null) {
						mTextDialog.setText(b.get(c));
						mTextDialog.setVisibility(View.VISIBLE);
					}
					choose = c;
					invalidate();
				}
			}
			break;
		}
		return true;
	}

	public interface OnTouchingLetterChangedListener {
		void onTouchingLetterChanged(String s);
	}

}
