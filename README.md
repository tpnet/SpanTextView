# SpanTextView
 

![最新效果图](https://github.com/tpnet/SpanTextView/blob/master/screen/GIF.gif)

TextView需要局部操作：点击某些局部文字的事件、颜色、大小、下划线、指定位置插入图片、添加图片、缩略显示、文字跑马灯等等，如果没经过封装，你需要在activty上写一堆的view操作代码。

简单封装一下之后，我在TextView要设置第3-5的字母点击事件 和 “链接1”字符点击事件，代码只需要如下

```
        //设置点击事件
        tvTest.setSpanLink("链接1",SIGN_ONE,false,Color.GREEN);
        tvTest.setSpanLink("链接2",SIGN_TWO,false);
        tvTest.setSpanLink("链接3",SIGN_THREE);
        
        //点击事件回调监听
        tvTest.setOnTextLinkClickListener(new SpanTextView.onTextLinkClickListener() {
            @Override
            public void onTextLinkClick(View view, String text,int position , String sign) {
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

如果你添加的图片需要点击事件，ok，满足你的,只需要在添加图片方法最后一个参数加上一个标识字符串常量即可，然后利用一个事件去监听，例如

```
        tvTest.addImageToFirst(R.drawable.one, SpanTextView.TEXT_SIZE,SIGN_PIC_ONE);
        //添加图片到中间位置 ("中间添加"的后面)
        tvTest.setImage(R.drawable.two, "中间添加", SpanTextView.AFTER_IMAGE,SIGN_PIC_TWO);
        //添加图片到最后面
        tvTest.addImageToLast(R.drawable.four,SIGN_PIC_THREE);
        
        // 图片点击回调事件
        tvTest.setOnImageLinkClickListener(new SpanTextView.onImageLinkClickListener() {
            @Override
            public void onImageLinkClick(View view, int position, String sign) {
                String toast = "";
                switch (sign){
                    case SIGN_PIC_ONE:
                        toast = "图片链接1-" + position;
                        break;
                    case SIGN_PIC_TWO:
                        toast = "图片链接2-" + position;

                        break;
                    case SIGN_PIC_THREE:
                        toast = "图片链接3-" + position;

                        //替换最后图片为图片1，
                        tvTest.replaceImageSpan(sign, getResources().getDrawable(R.drawable.one), SpanTextView.TEXT_SIZE, new int[2]);

                        break;
                    case SIGN_PIC_FOUR:
                        toast = "图片链接4-" + position;
                        break;
                }
                Toast.makeText(MainActivity.this,"点击了"+toast,Toast.LENGTH_SHORT).show();
                Log.e(TAG, "点击了"+toast);
            }
        });
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


设置省略文字:
```
        //文本开启跑马灯效果
        tvTextThree.setMarquee(true);
        //设置跑马灯的次数
        //tvTextThree.setMarqueeRepeatLimit(10);

```


设置文本跑马灯效果：

```
        //文本开启跑马灯效果
        tvTextThree.setMarquee(true);

```


清除效果：

```
    //清除单个效果，参数在设置的时候进行返回
    tvTest.clearSpan(mBackSpan);
    
    //清除全部效果
    tvTest.clearAllSpan();

```


还有其他的功能，具体请查看源码方法
