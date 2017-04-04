# SpanTextView

![效果图](http://img.blog.csdn.net/20170404120219201?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvbml1Yml0aWFucGluZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)


TextView需要局部操作：点击某些局部文字的事件、颜色、大小、下划线、指定位置插入图片、添加图片、缩略显示等等，如果没经过封装，你需要在activty上写一堆的view操作代码。

简单封装一下之后，我在TextView要设置第3-5的字母点击事件 和 “链接1”字符点击事件，代码只需要如下

```
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
```


又例如替换图片，插入图片，代码只需要如下:

```
        //添加图片到最前面,文字的高度
        tvTest.addImageToFirst(R.drawable.one, SpanTextView.TEXT_SIZE);

        //添加图片到中间(中间添加的后面)
        tvTest.setImage(R.drawable.two, "中间添加", SpanTextView.AFTER_IMAGE);

        //添加图片到最后面
        tvTest.addImageToLast(R.drawable.four);

        //替换文本为图片
        //tvTest.setImage(R.drawable.three,"图片标识", SpanTextView.REPLACE_IMAGE);
        tvTest.replaceTextToImage("图片标识",R.drawable.three);
```

又例如其他操作 背景、颜色、下划线等等:


```
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
        

        
```


又例如设置省略文字:


```
        //设置省略文字，只显示100个
        tvTestTwo.setOmit(100);

```
还有其他的功能，具体请查看源码方法
