package chenhao.lib.onecode.utils;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.widget.TextView;

/**
 * Created by onecode on 16/10/19.
 * 文字样式  如大小不、颜色一样的文字处理显示
 */
public class TxtStyle {

    private Context context;
    private String txt;
    private int lastLength=0;

    private SpannableString styledText;

    public TxtStyle(Context context, String txt) {
        this.context=context;
        this.txt = txt;
        if (StringUtils.isNotEmpty(txt)){
            this.styledText=new SpannableString(txt);
        }
    }

    public TxtStyle add(int length, int styleId){
        int rl=0;
        if (null!=styledText){
            try {
                int l=lastLength+length;
                rl=l>txt.length()?txt.length():l;
                styledText.setSpan(new TextAppearanceSpan(context, styleId),lastLength,rl, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        lastLength=rl;
        return this;
    }

    public void set(TextView view){
        if (null!=view&&null!=styledText){
            try {
                view.setText(styledText,TextView.BufferType.SPANNABLE);
            }catch (Exception e){
                e.printStackTrace();
                view.setText(txt);
            }
        }
    }

}
