package com.example.ash.energymonitor;

import android.app.ActionBar;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;


import com.example.ash.energymonitor.dashboard.DashboardConfig;
import com.example.ash.energymonitor.dataRetrieval.CostCalc;
import com.example.ash.energymonitor.dataRetrieval.DataCompiler;
import com.example.ash.energymonitor.dataRetrieval.HistoricalDataManager;
import com.example.ash.energymonitor.widgetLib.WidgetDrawer;
import com.example.ash.energymonitor.widgetLib.WidgetView;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;


/**
 * A fragment representing a single Dashboard detail screen.
 * This fragment is either contained in a {@link DashboardListActivity}
 * in two-pane mode (on tablets) or a {@link DashboardDetailActivity}
 * on handsets.
 */
public class DashboardDetailFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    //for updating the UI
    public View curRootView;
    public int dashboardSelected;
    public DataCompiler data;
    public String spinnerSelection= null;


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DashboardConfig.DummyItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public DashboardDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DashboardConfig.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_dashboard_detail, container, false);
        //this.curRootView = rootView;
        String[] applianceNames = new String[1];
        try {
            this.data = new DataCompiler(((DashboardListActivity) this.getActivity()).dataTemp);

            Log.d("Proto", ((DashboardListActivity) this.getActivity()).dataTemp);
            applianceNames = data.getFeedNames();


            // Show the dummy content as text in a TextView.
            if (mItem != null) {
                if (mItem.toString().equals("Overview")) {
                    this.dashboardSelected = 1;
                    ((DashboardListActivity) getActivity()).dashboardSelected = 1;
                    rootView = inflater.inflate(R.layout.overview, container, false);
                    overviewCostCalc(rootView);
                    //tester only
//                    HistoricalDataManager.HistoricalNodeData historicalNodeData = ((DashboardListActivity) this.getActivity()).historicalDataManager.historicalData.get(20);
//                    float cost = CostCalc.getTotalCost(historicalNodeData.times[historicalNodeData.ID_MONTH], historicalNodeData.values[historicalNodeData.ID_MONTH]);
//                    Log.d("Proto", "Here is the cost before " + cost);
//                    ((TextView) rootView.findViewById(R.id.houseACost)).setText("$" + cost);
//                    Log.d("Proto", "Here is the cost " + cost);
//                    CostCalc.testCostCalc();


//               View f =   getActivity().findViewById(R.id.dashboard_list);
//               // ListFragment fragment = (ListFragment) getFragmentManager().findFragmentById(R.id.dashboard_list);
//
//               LinearLayout.LayoutParams layoutParams =(LinearLayout.LayoutParams) f.getLayoutParams();
//               layoutParams.width = 0;
//               f.setLayoutParams(layoutParams);
                }else if (mItem.toString().equals("Feeds")) {
                    this.dashboardSelected = 2;
                    ((DashboardListActivity) getActivity()).dashboardSelected = 2;
                    rootView = inflater.inflate(R.layout.feed1, container, false);
                    try {
                        Spinner dash2Spin = (Spinner) rootView.findViewById(R.id.dash2Spin);
                        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, applianceNames);
                        dash2Spin.setAdapter(adapter_state);
                        dash2Spin.setOnItemSelectedListener(this);
                        dash2Spin.setSelection(1);
                        //Log.d("Proto", "Does it come here?" );
                    } catch (Exception e) {
                    }
                }else if (mItem.toString().equals("Dashboard 3")) {
                    this.dashboardSelected = 3;
                    ((DashboardListActivity) getActivity()).dashboardSelected = 3;
                    rootView = inflater.inflate(R.layout.live_select, container, false);
                    //rootView = inflater.inflate(R.layout.tabhost, container, false);
                    //this.data = new DataCompiler(DataCompiler.LiveData.TEST_DATA);

                    //String[] state= {"Andra Pradesh","Arunachal Pradesh","Assam","Bihar","Haryana","Himachal Pradesh", "Jammu and Kashmir", "Jharkhand","Karnataka", "Kerala","Tamil Nadu"};


                    //ArrayAdapter (Context context, int resource, List<T> objects)

//                ArrayAdapter<CharSequence> adapter_state = ArrayAdapter.createFromResource(this.getActivity(),
//                        R.array.planets_array, R.layout.live_select);

//                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item, state);
//                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                s.setAdapter(dataAdapter);
                    try {
                        //spinner = new Spinner(this.getActivity());
                        ArrayAdapter<String> adapter_state = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, applianceNames);
                        Spinner spinner = (Spinner) rootView.findViewById(R.id.liveSelectSpin);
                        spinner.setOnItemSelectedListener(this);

                        spinner.setAdapter(adapter_state);
                        //spinner.setVerticalScrollbarPosition(2);

                    } catch (Exception e) {
                        Log.d("Proto", "" + e);
                    }
                }else if(mItem.toString().equals("House A")){
                    this.dashboardSelected = 4;
                    ((DashboardListActivity) getActivity()).dashboardSelected = 4;
                    rootView = inflater.inflate(R.layout.house_b, container, false);
                    //int[] mains = new int[]{20,22};
                    int[] mains = DashboardListActivity.HOUSE_A_MAIN_IDS;
                    int[] ids = DashboardListActivity.HOUSE_A_APP_IDS;
                    houseACalc(rootView, mains, ids);

                }else if(mItem.toString().equals("House B")){
                    this.dashboardSelected = 5;
                    ((DashboardListActivity) getActivity()).dashboardSelected = 5;
                    rootView = inflater.inflate(R.layout.house_b, container, false);
                    int[] mains = DashboardListActivity.HOUSE_B_MAIN_IDS;
                    int[] ids = DashboardListActivity.HOUSE_B_APP_IDS;
                    houseBCalc(rootView, mains, ids);
                }


                //((TextView) rootView.findViewById(R.id.dashboard_detail)).setText(mItem.content);
            }
        }catch(Exception e){
        //this.data = new DataCompiler(DataCompiler.LiveData.TEST_DATA);
            Log.d("Proto", "Fragment loadout failed " + e);
    }

    this.curRootView = rootView;
        return rootView;
    }

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        DashboardListActivity activity = (DashboardListActivity) getActivity();
        if(this.dashboardSelected ==1) {

        }else if(this.dashboardSelected ==2){
            try {

                ((DashboardListActivity) getActivity()).globalSpinnerValue = pos;
                //Getting the data
                spinnerSelection = (String) parent.getItemAtPosition(pos);
                ((DashboardListActivity) this.getActivity()).selectedItem = spinnerSelection;
               //Log.d("Proto", "In fragment and selection is " + (pos+1));
                //TextView curValue = (TextView) this.curRootView.findViewById(R.id.liveSelectTextValue);
                this.data = new DataCompiler(((DashboardListActivity) this.getActivity()).dataTemp);
                String currentValue = this.data.getValueFromName(spinnerSelection);
                //this gets the data from the Historical Data Library

                HistoricalDataManager.HistoricalNodeData nodeDataManager = (HistoricalDataManager.HistoricalNodeData)
                        ((DashboardListActivity) this.getActivity()).historicalDataManager.historicalData.get(activity.feedIds[activity.globalSpinnerValue]);

                Log.d("Proto", "This is the feed Id  " +activity.feedIds[activity.globalSpinnerValue]);
                ((DashboardListActivity) this.getActivity()).historicalValues = nodeDataManager;
                //Enumeration<Float> enumeration = nodeDataManager.lastDay.elements();

                //Log.d("Proto", "Something: " + enumeration.nextElement() + ", " + enumeration.nextElement());



                //setting the widget view's data to the data from the file for last day

                WidgetView runGraph =  ((WidgetView) this.curRootView.findViewById(R.id.dash2RunGraph));

                   //runGraph.config.widgetData =((DashboardListActivity) this.getActivity()).historicalDataManager.historicalData.get( (pos +1)).lastDay;
                //runGraph.config.widgetData = nodeDataManager.lastWeek;

                //Sets the default configuration
                runGraph.config.timesArray = nodeDataManager.times[nodeDataManager.ID_WEEK];
                runGraph.config.valueArray = nodeDataManager.values[nodeDataManager.ID_WEEK];
                RadioButton radioButton = (RadioButton) this.getActivity().findViewById(R.id.weekRadioButton);
                radioButton.setChecked(true);
                String valueTemp =  ((DashboardListActivity)this.getActivity()).currentValues.getValueFromName(((DashboardListActivity)this.getActivity()).selectedItem);
                WidgetView dial = (WidgetView) this.getActivity().findViewById(R.id.dash2Dial);
                dial.config.singleData = Float.parseFloat(valueTemp);
                dial.config.minAndMax = WidgetDrawer.getMinAndMax(nodeDataManager.values[nodeDataManager.ID_WEEK]);
                dial.config.minAndMax[0] = 0f;
                dial.invalidate();
                runGraph.invalidate();

            }catch(Exception e){
                Log.d("Proto", "its here in the fragrment " + e.toString());
            }
        }else if(this.dashboardSelected ==3){
            // An item was selected. You can retrieve the selected item using
            spinnerSelection = (String) parent.getItemAtPosition(pos);
            TextView curValue = (TextView) this.curRootView.findViewById(R.id.liveSelectTextValue);
            this.data = new DataCompiler(((DashboardListActivity) this.getActivity()).dataTemp);
            String s = this.data.getValueFromName(spinnerSelection);
            Log.d("Proto", "Something: " + pos);
            ((DashboardListActivity) this.getActivity()).selectedItem = spinnerSelection;
            curValue.setText(s);
        }else{

        }

    }



    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
//        String name = (String) parent.getItemAtPosition();
//        TextView curValue = (TextView) this.curRootView.findViewById(R.id.liveSelectTextValue);
//        this.data = new DataCompiler(((DashboardListActivity) this.getActivity()).dataTemp);
//        String s = this.data.getValueFromName(name);
//        curValue.setText(s);
    }

    public void overviewCostCalc(View inputView){
        int[] dataIndexes = new int[]{26,9,8,28,27,16};
        int[] viewIdIndexes = new int[]{ R.id.fridgeCost, R.id.dishWasherCost, R.id.fanCoilCost, R.id.washingMachineCost, R.id.microwaveCost, R.id.coffeeMakerCost};


        HistoricalDataManager historicalDataManager = ((DashboardListActivity) this.getActivity()).historicalDataManager;
        HistoricalDataManager.HistoricalNodeData historicalNodeData =  historicalDataManager.historicalData.get(dataIndexes[0]);
        Log.d("Error", "this is all the ids " + viewIdIndexes.toString());
        for(int i =0; i<dataIndexes.length;i++){
            try {
                historicalNodeData = historicalDataManager.historicalData.get(dataIndexes[i]);
            float cost = CostCalc.getTotalCost(historicalNodeData.times[historicalNodeData.ID_MONTH], historicalNodeData.values[historicalNodeData.ID_MONTH]);
                String costString = String.format("%.2f", cost);
            ((TextView) inputView.findViewById(viewIdIndexes[i])).setText("$" + costString);
                Log.d("Error", "the id is " + viewIdIndexes[i] + " and the node id is " + dataIndexes[i]);
            }catch(Exception e){
                Log.d("Error", "the error is " + e);
            }
        }

         historicalNodeData =  historicalDataManager.historicalData.get(2);
        float cost1 = CostCalc.getTotalCost(historicalNodeData.times[historicalNodeData.ID_MONTH], historicalNodeData.values[historicalNodeData.ID_MONTH]);
        historicalNodeData =  historicalDataManager.historicalData.get(5);
        float cost2 = CostCalc.getTotalCost(historicalNodeData.times[historicalNodeData.ID_MONTH], historicalNodeData.values[historicalNodeData.ID_MONTH]);
        Log.d("Proto", "Here is the cost before " + cost1 + " other " +cost2 );
        float costTotalHouseB = cost1+cost2;
        String costString = String.format("%.2f", costTotalHouseB);
        ((TextView) inputView.findViewById(R.id.houseBCost)).setText("$" + costString);

        historicalNodeData =  historicalDataManager.historicalData.get(20);
         cost1 = CostCalc.getTotalCost(historicalNodeData.times[historicalNodeData.ID_MONTH], historicalNodeData.values[historicalNodeData.ID_MONTH]);
        historicalNodeData =  historicalDataManager.historicalData.get(22);
         cost2 = CostCalc.getTotalCost(historicalNodeData.times[historicalNodeData.ID_MONTH], historicalNodeData.values[historicalNodeData.ID_MONTH]);
        Log.d("Proto", "Here is the cost before " + cost1 + " other " +cost2 );
         float costTotalHouseA = cost1+cost2;
         costString = String.format("%.2f", costTotalHouseA);
        ((TextView) inputView.findViewById(R.id.houseACost)).setText("$" + costString);




        WidgetView compareView = (WidgetView) inputView.findViewById(R.id.houseCompare);
        Hashtable<String, Float> compareData = new Hashtable<String, Float>();
        compareView.config.namesArray = new String[]{"House A", "House B"};
        compareView.config.valueArray = new float[]{costTotalHouseA, costTotalHouseB};
        //compareData.put("House A", costTotalHouseA);
        //compareData.put("House B", costTotalHouseB);

        compareView.config.setWidgetData(compareData);
        compareView.config.units = "$";
        compareView.invalidate();
        //compareData.put("House A", )
        //compareView.config.widgetData = null;


    }

    public void houseACalc(View inputView, int[] mains, int[] ids){
        houseBCalc(inputView, mains, ids);
        ((TextView) inputView.findViewById(R.id.houseB_title)).setText("House A");
        ((TextView) inputView.findViewById(R.id.washingText_lbl)).setText("Fan Coil");
        ((TextView) inputView.findViewById(R.id.fridgeText_lbl)).setText("Dishwasher");


        ((TextView) inputView.findViewById(R.id.microwaveText_lbl)).setText("");
        ((TextView) inputView.findViewById(R.id.evrText_lbl)).setText("");
        ((TextView) inputView.findViewById(R.id.microwave_lbc)).setText("");
        ((TextView) inputView.findViewById(R.id.erv_lbc)).setText("");

    }


    public void houseBCalc(View inputView, int[] mains, int[] ids){


        TextView totalView =(TextView) inputView.findViewById(R.id.total_lbc);
        HistoricalDataManager historicalDataManager = ((DashboardListActivity) this.getActivity()).historicalDataManager;
        DataCompiler dataCompiler = ((DashboardListActivity) this.getActivity()).currentValues;

        float cost1 = CostCalc.getTotalCost( historicalDataManager.historicalData.get(mains[0]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH], historicalDataManager.historicalData.get(mains[0]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH]);
        float cost2 = CostCalc.getTotalCost( historicalDataManager.historicalData.get(mains[1]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH], historicalDataManager.historicalData.get(mains[1]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH]);

        //String cost = String.format("%.2f","$" + (cost1+cost2) );
        //totalView.setText("WTF");
        totalView.setText("$" + String.format("%.2f",(cost1+cost2) ));

         //ids = new int[]{28,26,27,12};
        int [] viewIds = new int[]{R.id.washingMaching_lbc,R.id.fridge_lbc, R.id.microwave_lbc, R.id.erv_lbc};
        //Log.d("newView", "Is it here?" );
        for(int i = 0; i<ids.length;i++){
            float cost = CostCalc.getTotalCost(historicalDataManager.historicalData.get(ids[i]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH],historicalDataManager.historicalData.get(ids[i]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH]);
            TextView tempView = (TextView) inputView.findViewById(viewIds[i]);
            tempView.setText("$" + String.format("%.2f", cost));
            }


        WidgetView runGraph = (WidgetView) inputView.findViewById(R.id.runGraph_lbw);
        WidgetView dial = (WidgetView) inputView.findViewById(R.id.dial_lbw);
        WidgetView barGraph = (WidgetView) inputView.findViewById(R.id.barGraph_lbw);

        int length = historicalDataManager.historicalData.get(mains[0]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH].length;
        float[] combinedValues = new float[length];
        float[] weekValues = new float[historicalDataManager.historicalData.get(mains[0]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK].length];
        for(int i = 0; i< weekValues.length; i++){
            weekValues[i] = historicalDataManager.historicalData.get(mains[0]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK][i] +historicalDataManager.historicalData.get(mains[1]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK][i];
            //Log.d("BarGraph", " " + combinedValues[i]);
        }
        for(int i = 0; i< combinedValues.length; i++){
            combinedValues[i] = historicalDataManager.historicalData.get(mains[0]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH][i] +historicalDataManager.historicalData.get(mains[1]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH][i];
        }

        runGraph.config.timesArray = historicalDataManager.historicalData.get(mains[0]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
        runGraph.config.valueArray = combinedValues;

        dial.config.singleData = Float.parseFloat(dataCompiler.getValueFromId("" + mains[0])) + Float.parseFloat(dataCompiler.getValueFromId("" + mains[1]));
        dial.config.minAndMax = new float[]{0f, WidgetDrawer.getMinAndMax(combinedValues)[1]};

        //float[] barGraphValues = new float[]{4f,5f,10f,15f,3f};
        //Log.d("BarGraph", "what is going on " + Arrays.toString(historicalDataManager.historicalData.get(2).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK]));
        float[] daysCalc = CostCalc.getWeeklyCost(historicalDataManager.historicalData.get(mains[0]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK], weekValues, barGraph.config);
        //barGraph.config.valueArray = barGraphValues;
        //Log.d("BarGraph", "what is going on " + Arrays.toString(barGraph.config.namesArray));
        barGraph.config.valueArray = daysCalc;

        barGraph.invalidate();
        runGraph.invalidate();
        dial.invalidate();
    }



}


