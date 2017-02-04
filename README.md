# SpanTextView
TextView需要局部操作：点击某些局部文字的事件、颜色、大小、下划线、指定位置插入图片、添加图片等等，如果没经过封装，你需要在activty上写一堆的view操作代码。

简单封装一下之后，我在TextView要设置第3-5的字母点击事件 和 “链接1”字符点击事件，代码只需要如下

```
tvTest.setSpanLink("链接1",SIGN_ONE);
tvTest.setSpanLink(3,5,SIGN_TWO);

//点击回调监听器
tvTest.setOnLinkClickListener(new SpanTextView.onLinkClickListener() {
    @Override
    public void onLinkClick(View view, String sign) {
        String toast = "";
        switch (sign){  //根据sign来判断点击的字符串
            case SIGN_ONE:
                toast = "链接1";
                break;
            case SIGN_TWO:
                toast = "3-5";
                break;
        }
        Toast.makeText(MainActivity.this,"点击了"+toast,Toast.LENGTH_SHORT).show();
    }

});
```


又例如替换图片，插入图片，代码只需要如下:

```
//添加图片到最前面,文字的高度
tvTest.setImageToFirst(R.drawable.one, SpanTextView.ImageSizeFlag.TEXT_SIZE);

//添加图片到中间(中间添加的后面)
tvTest.setImage(R.drawable.two,"中间添加", SpanTextView.ImageFlag.AFTER_IMAGE);

//添加图片到最后面
tvTest.setImageToLast(R.drawable.four);

//替换文本为图片
//tvTest.setImage(R.drawable.three,"图片标识", SpanTextView.ImageFlag.REPLACE_IMAGE);
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

图片支持插入、替换、居中、可设置宽高。

封装了一些常用的，如果你需要的功能封装里面没有，你可以使用下面的代码进行设置Span

```
//设置其他的Span
tvTest.setSpann(tvTest.createSpan(new TypefaceSpan("sans-serif"),1,5));
```

还有其他的功能，具体请查看源码方法
