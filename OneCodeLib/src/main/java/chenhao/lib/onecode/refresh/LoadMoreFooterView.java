package chenhao.lib.onecode.refresh;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import chenhao.lib.onecode.view.aviloading.AVLoadingIndicatorView;

public class LoadMoreFooterView extends LinearLayout {

    public final static int STATE_LAODING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;

    private int p;
    private TextView textView;
    private AVLoadingIndicatorView progressView;

    public LoadMoreFooterView(Context context) {
        super(context);
        initView();
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }
    public void initView(){
        removeAllViews();
        setGravity(Gravity.CENTER);
        this.setOrientation(HORIZONTAL);
        float dp=getResources().getDisplayMetrics().density;
        p=(int)(5*dp);
        setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        progressView = new AVLoadingIndicatorView(this.getContext());
        addView(progressView);
        textView=new TextView(getContext());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP,12);
        addView(textView);
        textView.setVisibility(GONE);
    }

    public void setViewStyle(int style,int color,String noMoreTxt,int noMoreTxtColor) {
        if (null!=progressView){
            progressView.setIndicatorColor(color);
            progressView.setIndicatorId(style);
        }
        if (null!=textView){
            textView.setText(noMoreTxt);
            textView.setTextColor(noMoreTxtColor);
        }
    }

    public void  setState(int state) {
        switch(state) {
            case STATE_LAODING:
                textView.setPadding(0,0,0,0);
                textView.setVisibility(GONE);
                progressView.setVisibility(View.VISIBLE);
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_COMPLETE:
                textView.setPadding(0,0,0,0);
                textView.setVisibility(GONE);
                progressView.setVisibility(GONE);
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_NOMORE:
                if (null!=textView.getText()&&textView.getText().length()>0){
                    textView.setPadding(0,p,0,p);
                    textView.setVisibility(VISIBLE);
                }else{
                    textView.setVisibility(GONE);
                }
                progressView.setVisibility(View.GONE);
                this.setVisibility(View.VISIBLE);
                break;
        }
    }

}
