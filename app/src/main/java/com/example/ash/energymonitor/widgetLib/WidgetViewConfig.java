package com.example.ash.energymonitor.widgetLib;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Stack;

/**
 * Created by Mehrdad on 12/17/2014.
 */
public class WidgetViewConfig {
    //general types
    public String type, source, units;

    public RectF location;

    //a variable to contain the color configuration of each widget
    public Stack<Paint> colorList;

    //this is a variable for the run chart
    public int[] axisGrad;

    // this is a dummy object that initializes to dummy data
    public Hashtable widgetData;

    public String[] namesArray;

    //These are the arrays that keep the current data
    public long[] timesArray;
    public float[] valueArray;

    //this is used mainly for single widgets
    public float singleData;

    public float[] minAndMax;

    //Static variables
    public static final String RUN_GRAPH = "RunGraph", COMPARE_BINS = "CompareBins", DIAL ="Dial", BAR_GRAPH = "BarGraph";


    public WidgetViewConfig(String type){
        this.type = type;
        this.colorList = new Stack<Paint>();
        if(type==null){
            this.type="";
        }else if(type.equals(RUN_GRAPH)) {
            //Enumeration<Integer>  enumeration= colorList.elements();
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            p.setStyle(Paint.Style.STROKE);

            //p2 is the background color
            Paint p2 = new Paint();
           // p2.setColor(Color.parseColor("#C4C4C4"));
            p2.setColor(Color.WHITE);
            Paint p3 = new Paint();

            //main inner color
            p3.setColor(Color.parseColor("#f5e4b0"));
            p3.setStyle(Paint.Style.FILL);

            Paint p4 = new Paint();

            p4.setColor(Color.parseColor("#e8c83a"));


            //p3.setStyle(Paint.Style.STROKE);
            this.colorList.add(p);
            this.colorList.add(p2);
            this.colorList.add(p3);
            this.colorList.add(p4);
            Log.d("Frag", "IT runss!");
            this.axisGrad = new int[]{4,4};
            this.location = new RectF(0, 0, 700, 300);
        } else if(type.equals(COMPARE_BINS)){
            Paint p = new Paint();
            p.setColor(Color.BLACK);
            p.setStyle(Paint.Style.STROKE);
            p.setTextAlign(Paint.Align.CENTER);
            this.colorList.add(p);
            Paint p2 = new Paint();
            p2.setColor(Color.parseColor("#e8c83a"));
            this.colorList.add(p2);

            Hashtable<String, Float> data = new Hashtable<String, Float>();
            this.namesArray = new String[]{"Average usage", "Recomended"};
            this.valueArray = new float[]{15f, 10f};
//            data.put("Average Usage", 15.0f);
//            data.put("Recomended Usage", 10.0f );
//            data.put("Your Usage", 6.0f);
            this.location = new RectF(0,0, 500, 400);
            //this.location = new RectF(0,0, 400, 200);

            this.widgetData = data;

        }else if(type.equals(DIAL)){
            this.location = new RectF(0, 0, 300, 300);
        }else if(type.equals(BAR_GRAPH)){
            Paint p = new Paint();
            this.location = new RectF(0, 0, 700, 250);

            p.setColor(Color.parseColor("#2C25A8"));
            p.setTextSize(18);
            Paint p2 = new Paint();
            p2.setColor(Color.parseColor("#97d7fc"));
            Paint p3 = new Paint();
            p3.setColor(Color.parseColor("#2C25A8"));
            p3.setTextSize(16);
            this.colorList.add(p);
            this.colorList.add(p2);
            this.colorList.add(p3);
        }

        else{
            this.location = new RectF(0,0, 400, 200);

        }
    }




    public void setType(String type){
        this.type = type;
    }


    public void setWidgetData(Hashtable widgetData){
        this.widgetData = widgetData;
    }





    public void drawWidget(Canvas canvas, Paint paint){
       // Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Log.d("myTag", "type is: " +type);
        try {
        if(this.type.equals("Dial")){

            try {
                WidgetDrawer.drawDial(canvas, this.location, 6, singleData, this, paint);
            }catch(Exception e){
                WidgetDrawer.drawDial(canvas, this.location, 6, 0, null, paint);
            }

        }else if(this.type.equals("RunGraph")){
            WidgetDrawer.drawRunGraph(canvas,this.location, this.widgetData,paint,this);
        }else if(this.type.equals("CompareBins")){
            WidgetDrawer.drawCompareBins(canvas, this.location,  this.widgetData, this);
        }else if(this.type.equals("BarGraph")){
            WidgetDrawer.drawBarGraph(canvas,this.location,this);
        }
        }catch(Exception e){
            Log.d("Proto", "The error in config is: " +e);
            //WidgetDrawer.drawDial(canvas, this.location, 6, 0, null, paint);
        }




    }
    public class ColorConfig{
        public Stack<Color> colorSet;

        public ColorConfig(){

        }
        public ColorConfig(Stack<Color> colorSet){
            this.colorSet = colorSet;
        }
    }

}
