package com.tpnet.spantextview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.text.style.UnderlineSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TextView富文本类
 * Created by Litp on 2017/2/23.
 */
@SuppressLint("AppCompatCustomView")
public class SpanTextView extends TextView implements View.OnClickListener {


    private int mDefaultHintColor = 0x6633B5E5;    //点击时候的背景色，默认为淡绿色

    private final String mAddText = "拁"; //添加 替换为图片的文字、创建完整Span时候的文字

    private boolean mIsOpenMarquees; //跑马灯

    private boolean mLinkHit;  //是否点击了局部链接

    //记录图片和文本的点击事件
    private Map<String,ClickImageSpan> mClickImageSpan;


    public final static int FRONT_IMAGE = 0x1;         //在文字之前
    public final static int REPLACE_IMAGE = 0x2;     //替换文字为照片
    public final static int AFTER_IMAGE = 0x3;         //在文字之后
    @IntDef({FRONT_IMAGE, REPLACE_IMAGE, AFTER_IMAGE})
    public @interface TEXT_POSITION_FLAG {
    }


    public final static int AUTO_SIZE = 0x64;         //原来的图片大小
    public final static int TEXT_SIZE = 0x65;         //文字的高度
    public final static int APPOINT_SIZE = 0x66;   //指定大小，在后面添加指定的宽高数组
    @IntDef({AUTO_SIZE, TEXT_SIZE, APPOINT_SIZE})
    public @interface IMAGE_SIZE_FLAG {}

    public SpanTextView(Context context) {
        this(context,null);
    }


    public SpanTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mClickImageSpan = new HashMap<>();
    }


    protected void onFinishInflate() {
        super.onFinishInflate();
        if (Build.VERSION.SDK_INT >= 16) {
            this.mDefaultHintColor = getHighlightColor();
        }
    }

    //格式化文本
    public void setFormatText(@StringRes int formatText, Object... text) {
        setFormatText(getResources().getString(formatText), text);
    }

    //格式化文本
    public void setFormatText(String formatText, Object... text) {
        setText(String.format(formatText, text));
    }


    //格式化文本,map
    public void setFormatText(@StringRes int formatRes, Map<String, String> map) {
        StringBuffer text = new StringBuffer();
        for (String key : map.keySet()) {
            text.append(map.get(key));
            text.append(" ");
        }

        setFormatText(formatRes, text.toString());
    }

    /**
     * 格式化文本
     *
     * @param formatText
     * @param text
     * @param color
     */
    public void setFormatText(@StringRes int formatText, @ColorInt int color, Object... text) {
        setFormatText(getResources().getString(formatText), text);
        setSpanTextColor(formatText, color);
    }


    public int getmDefaultHintColor() {
        return mDefaultHintColor;
    }

    public void setmDefaultHintColor(@ColorInt int mDefaultHintColor) {
        this.mDefaultHintColor = mDefaultHintColor;
    }

    /*--------------- 文字颜色  --------------*/
    public ForegroundColorSpan setSpanTextColor(int start, @ColorInt int color) {
        return setSpanTextColor(start, getText().length(), color);
    }

    public ForegroundColorSpan setSpanTextColor(String str, @ColorInt int color) {
        int[] size = getStartAndEnd(str);
        return setSpanTextColor(size[0], size[1], color);
    }

    /**
     * 设置文字颜色
     *
     * @param start 开始的位置
     * @param end   结束的位置
     * @param color 要设置的颜色
     */
    public ForegroundColorSpan setSpanTextColor(int start, int end, @ColorInt int color) {
        ForegroundColorSpan span = new ForegroundColorSpan(color);
        setSpann(createSpan(getText(), span, start, end));
        return span;
    }


    /*--------------- 文字大小  --------------*/
    public AbsoluteSizeSpan setSpanTextSize(int start, int textSize) {
        return setSpanTextSize(start, getText().length(), textSize);
    }

    public AbsoluteSizeSpan setSpanTextSize(String str, int textSize) {
        int[] size = getStartAndEnd(str);
        return setSpanTextSize(size[0], size[1], textSize);
    }

    /**
     * 设置文字颜色
     *
     * @param start    开始的位置
     * @param end      结束的位置
     * @param textSize 要设置的大小，单位px，请自行根据sp转换
     */
    public AbsoluteSizeSpan setSpanTextSize(int start, int end, int textSize) {
        AbsoluteSizeSpan span = new AbsoluteSizeSpan(textSize);
        setSpann(createSpan(getText(), span, start, end));
        return span;
    }



    /*--------------- 文字背景  --------------*/

    public RadiusBackgroundSpan setSpanTextBack(String str, @ColorInt int color) {
        int[] size = getStartAndEnd(str);
        return setSpanTextBack(size[0], size[1], color, 0);
    }

    public RadiusBackgroundSpan setSpanTextBack(String str, @ColorInt int color, int radius) {
        int[] size = getStartAndEnd(str);
        return setSpanTextBack(size[0], size[1], color, radius);
    }

    public RadiusBackgroundSpan setSpanTextBack(int start, int end, @ColorInt int color) {
        return setSpanTextBack(start, end, color, 0);
    }

    /**
     * 设置文字的背景
     *
     * @param start  开始的位置
     * @param end    结束的位置
     * @param color  要设置的颜色
     * @param radius 背景的圆角像素，单位px,直角就是0
     */
    public RadiusBackgroundSpan setSpanTextBack(int start, int end, @ColorInt int color, int radius) {
        RadiusBackgroundSpan span = new RadiusBackgroundSpan(color, radius);
        setSpann(createSpan(getText(),span , start, end));
        return span;
    }


    /*--------------- 文字链接  --------------*/
    public ClickTextSpan setSpanTextLink(String text, String sign) {
        return setSpanTextLink(text, sign, true);
    }

    public ClickTextSpan setSpanTextLink(String text, String sign, boolean isUnderLineVisiable) {
        return setSpanTextLink(text, sign, isUnderLineVisiable, 0);
    }

    public ClickTextSpan setSpanTextLink(String text, String sign, boolean isUnderLineVisiable, @ColorInt int color) {
        int[] array = getStartAndEnd(text);
        return setSpanTextLink(array[0], array[1], sign, isUnderLineVisiable, color);
    }

    public ClickTextSpan setSpanTextLink(int start, int end, String sign) {
        return setSpanTextLink(start, end, sign, true, 0);
    }

    /**
     * 设置文字链接
     *
     * @param start               开始的位置
     * @param end                 结束的位置
     * @param sign                设置链接对应的标识，供给回调的时候判断
     * @param isUnderLineVisiable 是否显示下划线
     * @param color               文字的颜色
     */
    public ClickTextSpan setSpanTextLink(int start, int end, String sign, boolean isUnderLineVisiable, @ColorInt int color) {
        try {
            //防止字符不存在异常

            ClickTextSpan span = new ClickTextSpan(new ClickTextListener(getText().subSequence(start, end), start, sign), isUnderLineVisiable, color);
            setSpann(
                    createSpan(getText(),
                            span,
                            start,
                            end));
            setMovementMethod(LocalLinkMovementMethod.getInstance());
            return span;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    @Deprecated
    public void setSpanLink(String text, String sign) {
        setSpanLink(text, sign, true);
    }

    @Deprecated
    public void setSpanLink(String text, String sign, boolean isUnderLineVisiable) {
        setSpanLink(text, sign, isUnderLineVisiable, 0);
    }

    @Deprecated
    public void setSpanLink(String text, String sign, boolean isUnderLineVisiable, @ColorInt int color) {
        int[] array = getStartAndEnd(text);
        setSpanLink(array[0], array[1], sign, isUnderLineVisiable, color);
    }

    @Deprecated
    public void setSpanLink(int start, int end, String sign) {
        setSpanLink(start, end, sign, true, 0);

    }

    @Deprecated
    public void setSpanLink(int start, int end, String sign, boolean isUnderLineVisiable, @ColorInt int color) {
        try {
            //防止字符不存在异常
            setSpann(
                    createSpan(getText(),
                            new ClickTextSpan(new ClickTextListener(getText().subSequence(start, end), start, sign), isUnderLineVisiable, color),
                            start,
                            end));
            setMovementMethod(LocalLinkMovementMethod.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*--------------- 文字下横线  --------------*/
    public UnderlineSpan setUnderLine(String text, boolean isVisiable) {
        int[] array = getStartAndEnd(text);
        return setUnderLine(array[0], array[1], isVisiable);
    }

    public UnderlineSpan setUnderLine(int start, int end, boolean isVisiable) {
        UnderlineSpan span;
        if (isVisiable) {
            span =  new UnderlineSpan();
            setSpann(createSpan(getText(),span, start, end));
            return span;
        }
        span = new NOUnderlineSpan();
        setSpann(createSpan(getText(), span, start, end));
        return span;
    }


    /**
     * 设置图片点击事件
     *
     * @param position 图片的位置，一个文本位置
     * @param sign     图片的标识
     */
    private void setImageLinkSpan(int position, String sign) {


        if (!TextUtils.isEmpty(sign) && position != -1) {

            ClickImageSpan span = new ClickImageSpan(new ClickImageListener(position, sign));

            setSpann(
                    createSpan(getText(),
                            span ,
                            position,
                            position == 0 ? 1: position));
            setMovementMethod(LocalLinkMovementMethod.getInstance());


            mClickImageSpan.put(sign,span);


        }

    }


    /*--------------- 添加图片到最后面  --------------*/

    public CenterImageSpan addImageToLast(@DrawableRes int imgId, String... sign) {
        return addImageToLast(imgId, AUTO_SIZE, new int[2], sign);
    }

    public CenterImageSpan addImageToLast(@DrawableRes int imgId, @IMAGE_SIZE_FLAG int sizeFlag, String... sign) {
        return addImageToLast(imgId, sizeFlag, new int[2], sign);
    }

    public CenterImageSpan addImageToLast(@DrawableRes int imgId, @IMAGE_SIZE_FLAG int sizeFlag, int[] size, String... sign) {
        Drawable drawable = getResources().getDrawable(imgId);
        return addImageToLast(drawable, sizeFlag, size, sign);
    }

    public CenterImageSpan addImageToLast(Drawable drawable, @IMAGE_SIZE_FLAG int sizeFlag, int[] size, String... sign) {

        CenterImageSpan span = null;

        if (drawable != null) {

            size = calcDrawableSize(drawable, sizeFlag, size);

            drawable.setBounds(0, 0, size[0], size[1]);


            span = new CenterImageSpan(drawable);

            addSpann(true, createNormalSpan(span));

            if (sign.length > 0) {
                setImageLinkSpan(getText().length(), sign[0]);
            }
        }

        return span;
    }

    /*--------------- 添加图片到最前面 --------------*/
    public CenterImageSpan addImageToFirst(@DrawableRes int imgId, String... sign) {
        return addImageToFirst(imgId, AUTO_SIZE, sign);
    }


    public CenterImageSpan addImageToFirst(@DrawableRes int imgId, @IMAGE_SIZE_FLAG int sizeFlag, String... sign) {
        return addImageToFirst(imgId, sizeFlag, new int[2], sign);
    }

    public CenterImageSpan addImageToFirst(@DrawableRes int imgId, @IMAGE_SIZE_FLAG int sizeFlag, int[] size, String... sign) {
        Drawable drawable = getResources().getDrawable(imgId);
        return addImageToFirst(drawable, sizeFlag, size, sign);
    }


    public CenterImageSpan addImageToFirst(Drawable drawable, @IMAGE_SIZE_FLAG int sizeFlag, int[] size, String... sign) {

        CenterImageSpan span = null;


        if (drawable != null) {

            size = calcDrawableSize(drawable, sizeFlag, size);

            drawable.setBounds(0, 0, size[0], size[1]);

            //先在前面加一个字，再替换为图片
            addSpann(false, new SpannableString(mAddText), new SpannableString(getText()));

            span = new CenterImageSpan(drawable);

            //替换刚刚添加的文字
            setSpann(createSpan(getText(), span, 0, 1));

            if (sign != null && sign.length > 0) {
                setImageLinkSpan(0, sign[0]);
            }

        }

        return span;
    }


    /*--------------- 把文字替换成图片  --------------*/

    public List<CenterImageSpan> replaceTextToImage(String text, @DrawableRes int imgId) {
        return replaceTextToImage(text, imgId, AUTO_SIZE, new int[2]);
    }


    public List<CenterImageSpan> replaceTextToImage(String text, @DrawableRes int imgId, @IMAGE_SIZE_FLAG int sizeFlag, int[] size, String... sign) {
        return replaceTextToImage(text, getResources().getDrawable(imgId), sizeFlag, size, sign);
    }

    /**
     * 替换文本为图片，全部替换
     *
     * @param text     将被替换的文字
     * @param drawable 要换成的图片drawable
     * @param sizeFlag 图片显示的大小
     * @param size     固定图片时候要设置的宽高
     */
    public List<CenterImageSpan> replaceTextToImage(String text, Drawable drawable, @IMAGE_SIZE_FLAG int sizeFlag, int[] size, String... sign) {

        List<CenterImageSpan> list = new ArrayList<>();

        if (!TextUtils.isEmpty(text) || drawable != null) {

            Pattern pattern = Pattern.compile(text);
            Matcher matcher = pattern.matcher(getText());

            size = calcDrawableSize(drawable, sizeFlag, size);
            drawable.setBounds(0, 0, size[0], size[1]);



            //循环替换
            while (matcher.find()) {
                CenterImageSpan span = new CenterImageSpan(drawable);
                setSpann(createSpan(getText(),
                        span,
                        matcher.start(),
                        matcher.end()));
                list.add(span);
            }

        }
        return list;


    }


    public void setImage(@DrawableRes int imgId, String text, @TEXT_POSITION_FLAG int flag, String... sign) {
        setImage(imgId, text, flag, AUTO_SIZE, new int[2], sign);
    }

    public void setImage(@DrawableRes int imgId, String text, @TEXT_POSITION_FLAG int flag, @IMAGE_SIZE_FLAG int sizeFlag, int[] size, String... sign) {
        setImage(getResources().getDrawable(imgId), text, flag, sizeFlag, size, sign);
    }

    /**
     * 插入图片到指定位置，两个字符串的中间
     *
     * @param drawable 图片资源
     * @param text     要操作的text
     * @param flag     图片插入模式,前中后
     * @param sizeFlag 图片显示的大小
     * @param size     固定图片时候要设置的宽高
     */
    public void setImage(Drawable drawable, String text, @TEXT_POSITION_FLAG int flag, @IMAGE_SIZE_FLAG int sizeFlag, int[] size, String... sign) {
        switch (flag) {
            case FRONT_IMAGE:
                insertImage(drawable, getStartAndEnd(text)[0], sizeFlag, size, sign);
                break;
            case REPLACE_IMAGE:
                replaceTextToImage(text, drawable, sizeFlag, size, sign);
                break;
            case AFTER_IMAGE:
                insertImage(drawable, getStartAndEnd(text)[1], sizeFlag, size, sign);
                break;
        }
    }


    /*------------  插入图片-------------*/
    public void insertImage(@DrawableRes int imgId, int insertPosition, @IMAGE_SIZE_FLAG int sizeFlag, int[] size, String... sign) {
        insertImage(getResources().getDrawable(imgId), insertPosition, sizeFlag, size, sign);
    }

    /**
     * 插入图片到指定位置
     *
     * @param drawable       图片Drawable
     * @param insertPosition 要插入的位置，0为从开头，getText.length()为最后
     * @param sizeFlag       图片显示的大小
     * @param size           固定图片时候要设置的宽高
     */
    public void insertImage(Drawable drawable, int insertPosition, @IMAGE_SIZE_FLAG int sizeFlag, int[] size, String... sign) {

        if (drawable != null) {

            if (insertPosition == 0) {
                //开始
                addImageToFirst(drawable, sizeFlag, size);
            } else if (insertPosition == getText().length()) {
                //在后面添加
                addImageToLast(drawable, sizeFlag, size);
            } else {

                size = calcDrawableSize(drawable, sizeFlag, size);
                drawable.setBounds(0, 0, size[0], size[1]);
                //先在中间添加一个文字
                addSpann(false,
                        new SpannableString(getText().subSequence(0, insertPosition)),
                        new SpannableString(mAddText),
                        new SpannableString(getText().subSequence(insertPosition, getText().length()))
                );
                //把加上去的文字替换为图片
                setSpann(createSpan(getText(), new CenterImageSpan(drawable), insertPosition, insertPosition + 1));

                if (sign != null && sign.length > 0) {
                    setImageLinkSpan(insertPosition, sign[0]);
                }

            }
        }


    }


    /**
     * 根据Flag计算要显示的图片的 宽高
     *
     * @param drawable 图片
     * @param sizeFlag 标识
     * @return 返回计算好的宽高
     */
    private int[] calcDrawableSize(Drawable drawable, @IMAGE_SIZE_FLAG int sizeFlag, int[]... s) {
        int[] size = new int[2];
        switch (sizeFlag) {
            case AUTO_SIZE:
                size[0] = drawable.getIntrinsicWidth();
                size[1] = drawable.getIntrinsicHeight();
                break;
            case APPOINT_SIZE:
                //指定宽高没有设置的话抛出异常
                if (s == null || s.length <= 0 || s[0].length <= 1) {
                    throw new IllegalStateException("指定宽高状态下，需要添加参数设置宽高");
                } else if (s[0][0] < 0 || s[0][1] < 0) {
                    throw new IllegalStateException("指定宽高状态下，宽或高不能为小于0");
                }
                break;
            case TEXT_SIZE:
                //文字的宽高度
                size[0] = getLineHeight() * drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight();
                size[1] = getLineHeight();
                break;
        }

        return size;
    }

    public SpannableString createSpan(CharSequence text, Object spann, int start, int end) {

        if (start < 0 || end <= 0 || spann == null) {
            return null;
        }

        SpannableString spannableString = new SpannableString(text);

        spannableString.setSpan(spann, start, end, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }


    /**
     * 创建完整替换的Span
     *
     * @param spann 要创建的Span
     * @return
     */
    public SpannableString createNormalSpan(Object spann) {
        SpannableString spannableString = new SpannableString(mAddText);
        spannableString.setSpan(spann, 0, mAddText.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        return spannableString;
    }

    public void setSpann(SpannableString spannableString) {
        if (spannableString == null) {
            return;
        }
        setText(spannableString);
    }

    /**
     * 在原有的基础上添加Spann在后面
     *
     * @param isContains      是否包含原来的内容
     * @param spannableString 要添加多个的spannableString
     */
    public SpannableStringBuilder addSpann(boolean isContains, SpannableString... spannableString) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        if (isContains) {
            builder.append(getText());
        }

        //循环添加在后面
        for (SpannableString string : spannableString) {
            builder.append(string);
        }
        setText(builder);
        return builder;
    }

    private int[] getStartAndEnd(String text) {
        int start = getText().toString().indexOf(text);
        return new int[]{start, text.length() + start};
    }

    /**
     * 清除所有效果
     */
    public void clearAllSpan() {

        int i = 0;

        //先清除图片添加的文本
        for (String s : mClickImageSpan.keySet()) {
            i += clearClickImageSpan(s,i);
        }
        setText(getText().toString());
    }

    /**
     * 清除列表span
     *
     * @param span 需要清除的span
     * @param <T>  span类型
     */
    public <T> void clearSpan(T span,T... spanMore) {
        SpannableString ss = (SpannableString) getText();
        ss.removeSpan(span);
        for (T s : spanMore) {
            ss.removeSpan(span);
        }
    }

    /**
     * 清除点击的span
     * @param sign 标识
     */
    public int clearClickImageSpan(String sign,int num){

        //清除图片添加的文字
        ClickImageSpan span = mClickImageSpan.get(sign);


        int position = span.clickListener.position - num;

        if(position == getText().length()){
            position--;
        }

        String str = String.valueOf(getText().charAt(position));

        Log.e("@@","次数："+sign+"   str:"+str);


        if(str.equals(mAddText)){
            SpannableString spannableFront = new SpannableString(getText().subSequence(0,position));
            SpannableString spannableAfter = new SpannableString(getText().subSequence(position + 1,getText().length()));
            addSpann(false,spannableFront,spannableAfter);

            return mAddText.length();

        }else{
            clearSpan(span);
        }

        return 0;

    }


    //没有省略字符时候的文本
    CharSequence allText = "";

    String mOmitText = "...展开全文";
    int showNum;  //显示的字符数
    int showColor;  //显示的字符颜色

    public void setOmit(int showNum) {
        setOmit(showNum, getResources().getColor(R.color.colorAccent));
    }


    /**
     * 省略显示字符
     *
     * @param showNum 要显示的字符数量
     */
    public void setOmit(int showNum, @ColorInt int color) {

        allText = getText();


        if (allText.length() < showNum) {
            //如果要显示文字超出总的长度，则隐藏2/3;
            showNum = allText.length() / 3;
        }

        this.showNum = showNum;

        this.showColor = color;

        reSetOmit(true);


        setOnClickListener(this);

        //添加展开全文
        //setSpanLink(mOmitText,omitSign);

    }

    //恢复缩放文本
    public void reSetOmit(boolean isScale) {
        if (isScale) {
            //设置省略字段
            setText(getText().subSequence(0, showNum));

            //添加展开全文
            addSpann(true, createSpan(mOmitText, new ForegroundColorSpan(showColor), 0, mOmitText.length()));

        } else {
            setText(allText);
        }
    }


    @Override
    public void onClick(View v) {


        //注意这里不能用文本equals，如果文本设置了Span会导致不相等
        if (allText.length() == getText().length()) {
            //收缩
            reSetOmit(true);
        } else {
            //伸展
            reSetOmit(false);
        }
    }


    private OnOmitClickListener mOnOmitClickListener;

    public interface OnOmitClickListener {
        /**
         * @param isOmit 收起还是伸展
         */
        void onOmitClick(boolean isOmit);
    }

    public void setOnOmitClickListener(OnOmitClickListener listener) {
        this.mOnOmitClickListener = listener;
    }


    public void setOnTextLinkClickListener(onTextLinkClickListener listener) {
        this.mTextLinkClickListener = listener;
    }

    public void setOnImageLinkClickListener(onImageLinkClickListener listener) {
        this.mImageLinkClickListener = listener;
    }

    private onTextLinkClickListener mTextLinkClickListener;
    private onImageLinkClickListener mImageLinkClickListener;

    /**
     * 点击的监听器
     */
    class ClickTextListener implements OnClickListener {
        private String sign = "";   //标识
        private String text = "";    //点击的文本
        private int position = -1;  //文本的起点位置

        ClickTextListener(String sign) {
            this.sign = sign;
        }

        ClickTextListener(CharSequence text, String sign) {
            this.sign = sign;
            this.text = String.valueOf(text);
        }

        ClickTextListener(CharSequence text, int position, String sign) {
            this.sign = sign;
            this.position = position;
            this.text = String.valueOf(text);
        }


        public String getSign() {
            return this.sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public void onClick(View v) {

            //回调点击事件
            mTextLinkClickListener.onTextLinkClick(v, this.text, position, this.sign);

            //实现点击显示一下背景
            //显示100毫秒背景后 变回透明
            postDelayed(new Runnable() {
                public void run() {
                    setHighlightColor(Color.TRANSPARENT);
                    setHighlightColor(mDefaultHintColor);
                }
            }, 100);
        }
    }


    class ClickImageListener implements OnClickListener {
        private String sign = "";
        private int position = -1;

        ClickImageListener(int position) {
            this.position = position;
        }

        ClickImageListener(int position, String sign) {
            this.sign = sign;
            this.position = position;
        }


        public String getSign() {
            return this.sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public void onClick(View v) {

            //回调点击事件
            mImageLinkClickListener.onImageLinkClick(v, position, this.sign);

        }
    }


    /**
     * 点击的Span
     */
    class ClickTextSpan extends ClickableSpan {

        private ClickTextListener clickListener; //点击监听器
        private int textColor = 0;           //文本颜色
        private boolean isUnderLineVisiable = true;  //是否显示下划线

        ClickTextSpan(OnClickListener listener) {
            this.clickListener = (ClickTextListener) listener;
            this.isUnderLineVisiable = this.isUnderLineVisiable;
        }

        ClickTextSpan(OnClickListener listener, boolean isUnderLineVisiable) {
            this.clickListener = (ClickTextListener) listener;
            this.isUnderLineVisiable = isUnderLineVisiable;
        }

        ClickTextSpan(OnClickListener listener, boolean isUnderLineVisiable, int textColor) {
            this.clickListener = (ClickTextListener) listener;
            this.isUnderLineVisiable = isUnderLineVisiable;
            this.textColor = textColor;
        }

        public void onClick(View view) {
            this.clickListener.onClick(view);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            if (this.textColor != 0) {
                ds.setColor(this.textColor);
            } else {
                ds.setColor(ds.linkColor);
            }
            ds.setUnderlineText(this.isUnderLineVisiable);
            ds.clearShadowLayer();
        }
    }


    class ClickImageSpan extends ClickableSpan {

        private ClickImageListener clickListener; //点击监听器

        ClickImageSpan(OnClickListener listener) {
            this.clickListener = (ClickImageListener) listener;
        }

        public void onClick(View view) {
            this.clickListener.onClick(view);
        }

    }


    /**
     * 取消下划线类
     */
    @SuppressLint({"ParcelCreator"})
    private class NOUnderlineSpan extends UnderlineSpan {
        NOUnderlineSpan() {
        }

        public void updateDrawState(TextPaint ds) {
            ds.setUnderlineText(false);
        }
    }

    /**
     * 圆角背景类
     */
    @SuppressLint({"ParcelCreator"})
    class RadiusBackgroundSpan extends ReplacementSpan {
        private int mColor;
        private int mRadius;
        private int mSize;

        public RadiusBackgroundSpan(@ColorInt int color, int radius) {
            this.mColor = color;
            this.mRadius = radius;
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
            this.mSize = (int) (paint.measureText(text, start, end) + ((float) (this.mRadius * 2)));
            return this.mSize;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
            int color = paint.getColor();
            paint.setColor(this.mColor);
            paint.setAntiAlias(true);
            canvas.drawRoundRect(new RectF(x, ((float) y) + paint.ascent(), ((float) this.mSize) + x, ((float) y) + paint.descent()), (float) this.mRadius, (float) this.mRadius, paint);
            paint.setColor(color);
            canvas.drawText(text, start, end, x + ((float) this.mRadius), (float) y, paint);
        }
    }


    /**
     * 图片和文字居中类
     */
    public class CenterImageSpan extends ImageSpan {

        public CenterImageSpan(Context context, final int drawableRes) {
            super(context, drawableRes);
        }

        public CenterImageSpan(Drawable drawables) {
            super(drawables);
        }

        @Override
        public int getSize(Paint paint, CharSequence text, int start, int end, FontMetricsInt fm) {
            Drawable d = getDrawable();
            Rect rect = d.getBounds();
            if (fm != null) {
                FontMetricsInt fmPaint = paint.getFontMetricsInt();
                int fontHeight = fmPaint.bottom - fmPaint.top;
                int drHeight = rect.bottom - rect.top;

                int top = drHeight / 2 - fontHeight / 4;
                int bottom = drHeight / 2 + fontHeight / 4;

                fm.ascent = -bottom;
                fm.top = -bottom;
                fm.bottom = top;
                fm.descent = top;
            }
            return rect.right;
        }

        @Override
        public void draw(@NonNull Canvas canvas, CharSequence text,
                         int start, int end, float x,
                         int top, int y, int bottom, @NonNull Paint paint) {
            //要显示的Drawable
            Drawable b = getDrawable();
            // font metrics of text to be replaced
            FontMetricsInt fm = paint.getFontMetricsInt();
            int transY = (y + fm.descent + y + fm.ascent) / 2 - b.getBounds().bottom / 2;

            canvas.save();
            canvas.translate(x, transY);
            b.draw(canvas);
            canvas.restore();
        }
    }


    /**
     * 设置跑马灯效果
     *
     * @param isMarquees 是否开启
     */
    public void setMarquee(boolean isMarquees) {
        if (isMarquees) {
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
            setFocusable(true);
            setFocusableInTouchMode(true);
        } else {
            setEllipsize(TextUtils.TruncateAt.END);
            setFocusable(false);
            setFocusableInTouchMode(false);
        }
        this.mIsOpenMarquees = isMarquees;
    }


    @Override
    public boolean isFocused() {
        return mIsOpenMarquees || super.isFocused();
    }


    //屏蔽点击
    @Override
    public boolean performClick() {
        if (mLinkHit) {
            return true;
        }
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mLinkHit = false;
        return super.onTouchEvent(event);
    }

    //设置的局部的点击之后，拦截View的点击事件
    public static class LocalLinkMovementMethod extends LinkMovementMethod {

        static LocalLinkMovementMethod sInstance;


        public static LocalLinkMovementMethod getInstance() {
            if (sInstance == null)
                sInstance = new LocalLinkMovementMethod();

            return sInstance;
        }

        @Override
        public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(
                        off, off, ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                    } else {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }

                    if (widget instanceof SpanTextView) {
                        ((SpanTextView) widget).mLinkHit = true;
                    }
                    return true;
                } else {
                    Selection.removeSelection(buffer);
                    Touch.onTouchEvent(widget, buffer, event);
                    return false;
                }
            }
            return Touch.onTouchEvent(widget, buffer, event);
        }
    }


    public interface onTextLinkClickListener {
        /**
         * 所有的文本点击回调方法
         *
         * @param view 本身的Textview
         * @param text 单击的文本
         * @param sign 点击事件的标识
         */
        void onTextLinkClick(View view, String text, int position, String sign);
    }

    public interface onImageLinkClickListener {
        /**
         * 所有图片的点击回调方法
         *
         * @param view     本身的Textview
         * @param position 点击的图片的位置
         * @param sign     点击事件的标识
         */
        void onImageLinkClick(View view, int position, String sign);
    }


}
