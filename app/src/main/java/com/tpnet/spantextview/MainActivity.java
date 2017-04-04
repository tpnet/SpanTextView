package com.tpnet.spantextview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends Activity {


    private SpanTextView tvTest;
    private SpanTextView tvTestTwo;

    final String TAG = this.getClass().getSimpleName();

    private final String SIGN_ONE = "one";
    private final String SIGN_TWO = "two";
    private final String SIGN_THREE = "three";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvTest = (SpanTextView) findViewById(R.id.tv_test);
        tvTestTwo = (SpanTextView) findViewById(R.id.tv_test_two);

        //设置文本大小
        tvTest.setSpanTextSize("大小",30);
        //设置文本背景
        tvTest.setSpanTextBack("普通背景", Color.BLUE);
        //设置文本圆角背景
        tvTest.setSpanTextBack("圆角背景", Color.GREEN,10);
        //设置文本颜色
        tvTest.setSpanTextColor("颜色",Color.RED);
        //设置文本下划线
        tvTest.setUnderLine("下划线",true);

        //自定义设置Span
        tvTest.setSpann(tvTest.createSpan(tvTest.getText(), new TypefaceSpan("sans-serif"), 1, 5));

        //添加图片到最前面,文字的高度
        tvTest.addImageToFirst(R.drawable.one, SpanTextView.TEXT_SIZE);

        //添加图片到中间(中间添加的后面)
        tvTest.setImage(R.drawable.two, "中间添加", SpanTextView.AFTER_IMAGE);

        //添加图片到最后面
        tvTest.addImageToLast(R.drawable.four);

        //替换文本为图片
        //tvTest.setImage(R.drawable.three,"图片标识", SpanTextView.REPLACE_IMAGE);
        tvTest.replaceTextToImage("图片标识",R.drawable.three);


        //设置点击事件
        tvTest.setSpanLink("链接1",SIGN_ONE,false,Color.GREEN);
        tvTest.setSpanLink("链接2",SIGN_TWO,false);
        tvTest.setSpanLink("链接3",SIGN_THREE);

        //点击事件回调监听
        tvTest.setOnLinkClickListener(new SpanTextView.onLinkClickListener() {
            @Override
            public void onLinkClick(View view, String text, String sign) {
                String toast = "";
                switch (sign){
                    case SIGN_ONE:
                        toast = "链接1";
                        break;
                    case SIGN_TWO:
                        toast = "链接2";

                        break;
                    case SIGN_THREE:
                        toast = "链接3";

                        break;
                }
                Toast.makeText(MainActivity.this,"点击了"+toast,Toast.LENGTH_SHORT).show();
                Log.e(TAG, "点击了"+toast);
            }


        });


        //设置省略文字，只显示100个
        tvTestTwo.setOmit(100);


        //设置省略文字，超出则显示1/3。缩放支持设置了span的文本
        //tvTest.setOmit(100);
        
        
    }
}
