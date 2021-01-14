package com.example.myapplication.Widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;

/**
 * <pre>
 *     author : 孟东风
 *     time   : 2019/12/11
 *     project_name  :TeachingBox4
 *     desc   :
 */
public class TopLayout extends LinearLayout {
    private  ImageView mImageView;
    private TextView mTextView;

    public TopLayout(Context context) {
        this(context, null);
    }

    public TopLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

/*    context通过调用obtainStyledAttributes方法来获取一个TypeArray，然后由该TypeArray来对属性进行设置
    obtainStyledAttributes方法有三个，我们最常用的是有一个参数的obtainStyledAttributes(int[] attrs)，其参数直接styleable中获得
    TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.MyView);
    调用结束后务必调用recycle()方法，否则这次的设定会对下次的使用造成影响*/

    public TopLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopLayout, defStyleAttr, 0);
        int iconId = typedArray.getResourceId(R.styleable.TopLayout_top_icon, R.drawable.operate_selector);
        String text = typedArray.getString(R.styleable.TopLayout_text);
        typedArray.recycle();
        mImageView.setImageResource(iconId);
        mTextView.setText(text);
    }

    private void initView(Context context) {
        View view= LayoutInflater.from(context).inflate(R.layout.top_layout,null);
        mImageView = (ImageView) view.findViewById(R.id.image_view);
        mTextView = (TextView) view.findViewById(R.id.text_view);
        addView(view);
    }

    public void setSelectState() {
        mImageView.setSelected(true);
        mTextView.setSelected(true);
    }

    public void setUnSelectState() {
        mImageView.setSelected(false);
        mTextView.setSelected(false);
    }
}
