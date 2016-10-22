package com.example.ash.energymonitor.widgetLib;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.example.ash.energymonitor.R;


import java.util.Hashtable;
import java.util.Stack;


/**
 * Created by Mehrdad on 12/17/2014.
 */
public class WidgetView extends View {

    public int gridHeight, gridWidth;

    public WidgetViewConfig config;

    public Paint paint;

    public WidgetViewConfig runConfig, compareConfig;


    public WidgetView(Context context, AttributeSet attrs){
       super(context, attrs);
       // super(context);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(
                attrs, R.styleable.WidgetView, 0, 0);


        String type ="";
        try{
           //String type = "" + typedArray.getInteger(R.styleable.WidgetView_widgetType, 0);
            type = typedArray.getString(R.styleable.WidgetView_widgetType);
            //String type = typedArray.getString(R.styleable.WidgetView_widgetTypes);
           Log.d("myTag", "okay whats up?" + type );
        }finally{
            typedArray.recycle();
        }

        this.config = new WidgetViewConfig(type);

        this.setBackgroundColor(Color.WHITE);

    }

    public void init(){
        this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        //this.runConfig =  new WidgetViewConfig("RunGraph");
        //this.compareConfig =
    }

    @Override
    protected void onSizeChanged(int w, int hm, int oldw, int oldh){

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        try {
            this.setMeasuredDimension((int) (this.config.location.right - this.config.location.left), (int) (this.config.location.bottom - this.config.location.top));
            //this.setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }catch(Exception e){

        }
    }


    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom){

    }



    //public void setDefaultConfig(){this.board[0][0].setType("Dial");}

    @Override
   protected void onDraw(Canvas canvas){
        init();
        this.config.drawWidget(canvas, paint);

   }
}
