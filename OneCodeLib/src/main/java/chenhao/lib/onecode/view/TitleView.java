package chenhao.lib.onecode.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import chenhao.lib.onecode.R;

/**
 * Created by onecode on 16/2/22.
 */
public class TitleView extends RelativeLayout {

    public static final int SHOW_NONE=0;
    public static final int SHOW_ICON=1;
    public static final int SHOW_TEXT=2;

    public static final int ACTION_TITLE_CLICK=0;
    public static final int ACTION_LEFT_CLICK=1;
    public static final int ACTION_RIGHT_CLICK=2;

    private final int ICON_DEFAULT_LEFT= R.drawable.onecode_red_dian_icon;
    private final int ICON_DEFAULT_RIGHT=R.drawable.onecode_red_dian_icon;

    public TitleView(Context context) {
        super(context);
        init(null,0);
    }

    public TitleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs,0);
    }

    public TitleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    private ImageView leftIcon,rightIcon;
    private TextView leftText,rightText,title;

    private void init(AttributeSet attrs, int defStyle){
        float dp=getResources().getDisplayMetrics().density;
        LayoutParams titleParams=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        LayoutParams leftParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        leftParams.addRule(ALIGN_PARENT_LEFT);
        LayoutParams rightParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        rightParams.addRule(ALIGN_PARENT_RIGHT);

        title=new TextView(getContext());
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        title.setEllipsize(TextUtils.TruncateAt.END);
        title.setGravity(Gravity.CENTER);
        title.setSingleLine();
        title.setPadding((int)(50*dp),0,(int)(50*dp),0);
        title.setBackgroundColor(Color.TRANSPARENT);
        this.addView(title,titleParams);

        leftIcon=new ImageView(getContext());
        leftIcon.setContentDescription("");
        leftIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.addView(leftIcon,leftParams);

        rightIcon=new ImageView(getContext());
        rightIcon.setContentDescription("");
        rightIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.addView(rightIcon,rightParams);

        leftText=new TextView(getContext());
        leftText.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        leftText.setEllipsize(TextUtils.TruncateAt.END);
        leftText.setGravity(Gravity.CENTER);
        leftText.setSingleLine();
        this.addView(leftText,leftParams);

        rightText=new TextView(getContext());
        rightText.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        rightText.setEllipsize(TextUtils.TruncateAt.END);
        rightText.setGravity(Gravity.CENTER);
        rightText.setSingleLine();
        this.addView(rightText,rightParams);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TitleView, defStyle, 0);
        setTextIcon(a.getString(R.styleable.TitleView_tv_titleText),
                a.getString(R.styleable.TitleView_tv_leftText),
                a.getString(R.styleable.TitleView_tv_rightText),
                a.getResourceId(R.styleable.TitleView_tv_leftIcon,ICON_DEFAULT_LEFT),
                a.getResourceId(R.styleable.TitleView_tv_rightIcon,ICON_DEFAULT_RIGHT));
        setItemBgColor(a.getColor(R.styleable.TitleView_tv_itemBgColor, Color.TRANSPARENT));
        setShow(a.getInt(R.styleable.TitleView_tv_leftShow,SHOW_NONE),a.getInt(R.styleable.TitleView_tv_rightShow,SHOW_NONE));
        setLeftPading(a.getDimension(R.styleable.TitleView_tv_leftPadingLR,(int)(10*dp)),a.getDimension(R.styleable.TitleView_tv_leftPadingTB,(int)(10*dp)));
        setRightPading(a.getDimension(R.styleable.TitleView_tv_rightPadingLR,(int)(10*dp)),a.getDimension(R.styleable.TitleView_tv_rightPadingTB,(int)(10*dp)));

        title.setTextColor(a.getColor(R.styleable.TitleView_tv_titleTextColor, Color.parseColor("#333333")));
        leftText.setTextColor(a.getColor(R.styleable.TitleView_tv_leftTextColor, Color.parseColor("#333333")));
        rightText.setTextColor(a.getColor(R.styleable.TitleView_tv_rightTextColor, Color.parseColor("#333333")));

        float ts=a.getInteger(R.styleable.TitleView_tv_titleTextSize,0);
        if (ts>0){
            title.setTextSize(TypedValue.COMPLEX_UNIT_SP,ts);
        }
        float ls=a.getInteger(R.styleable.TitleView_tv_leftTextSize,0);
        if (ls>0){
            leftText.setTextSize(TypedValue.COMPLEX_UNIT_SP,ls);
        }
        float rs=a.getInteger(R.styleable.TitleView_tv_rightTextSize,0);
        if (rs>0){
            rightText.setTextSize(TypedValue.COMPLEX_UNIT_SP,rs);
        }

        title.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=viewAction){
                    viewAction.onAction(ACTION_TITLE_CLICK);
                }
            }
        });
        leftIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=viewAction){
                    viewAction.onAction(ACTION_LEFT_CLICK);
                }
            }
        });
        leftText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=viewAction){
                    viewAction.onAction(ACTION_LEFT_CLICK);
                }
            }
        });
        rightIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=viewAction){
                    viewAction.onAction(ACTION_RIGHT_CLICK);
                }
            }
        });
        rightText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null!=viewAction){
                    viewAction.onAction(ACTION_RIGHT_CLICK);
                }
            }
        });
        setOnTitleViewAction(new OnTitleViewAction() {
            @Override
            public void onAction(int action) {
                if (action==ACTION_LEFT_CLICK&&getContext() instanceof Activity){
                    ((Activity)getContext()).onBackPressed();
                }
            }
        });
    }

    public void setTitle(String t){
        title.setText(t);
    }

    public void setTextIcon(String t, String lt, String rt, int li, int ri){
        title.setText(t);
        leftText.setText(lt);
        rightText.setText(rt);
        leftIcon.setImageResource(li!=0?li:ICON_DEFAULT_LEFT);
        rightIcon.setImageResource(ri!=0?ri:ICON_DEFAULT_RIGHT);
    }

    public TextView getTitleTextView(){
        return title;
    }

    public TextView getLeftTextView(){
        return leftText;
    }

    public TextView getRightTextView(){
        return rightText;
    }

    public void setItemBgColor(int itemBgColor){
        leftText.setBackgroundColor(itemBgColor);
        leftIcon.setBackgroundColor(itemBgColor);
        rightText.setBackgroundColor(itemBgColor);
        rightIcon.setBackgroundColor(itemBgColor);
    }

    public void setLeftPading(float leftPadingLR,float leftPadingTB){
        leftText.setPadding((int)leftPadingLR,(int)leftPadingTB,(int)leftPadingLR,(int)leftPadingTB);
        leftIcon.setPadding((int)leftPadingLR,(int)leftPadingTB,(int)leftPadingLR,(int)leftPadingTB);
    }

    public void setRightPading(float rightPadingLR,float rightPadingTB){
        rightText.setPadding((int)rightPadingLR,(int)rightPadingTB,(int)rightPadingLR,(int)rightPadingTB);
        rightIcon.setPadding((int)rightPadingLR,(int)rightPadingTB,(int)rightPadingLR,(int)rightPadingTB);
    }

    public void setShow(int leftShow,int rightShow){
        leftIcon.setVisibility(leftShow==SHOW_ICON?VISIBLE:GONE);
        leftText.setVisibility(leftShow==SHOW_TEXT?VISIBLE:GONE);
        rightIcon.setVisibility(rightShow==SHOW_ICON?VISIBLE:GONE);
        rightText.setVisibility(rightShow==SHOW_TEXT?VISIBLE:GONE);
    }

    public interface OnTitleViewAction{
        void onAction(int action);
    }

    private OnTitleViewAction viewAction;
    public void setOnTitleViewAction(OnTitleViewAction action){
        this.viewAction=action;
    }

}
