package com.example.ash.energymonitor;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session;
import com.example.ash.energymonitor.dataRetrieval.CostCalc;
import com.example.ash.energymonitor.dataRetrieval.HistoricalDataManager;
import com.example.ash.energymonitor.dataRetrieval.DataCompiler;
import com.example.ash.energymonitor.dataRetrieval.DropboxRetrieval;
import com.example.ash.energymonitor.widgetLib.WidgetDrawer;
import com.example.ash.energymonitor.widgetLib.WidgetView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.Arrays;


/**
 * An activity representing a list of Dashboards. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link DashboardDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 * <p/>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link DashboardListFragment} and the item details
 * (if present) is a {@link DashboardDetailFragment}.
 * <p/>
 * This activity also implements the required
 * {@link DashboardListFragment.Callbacks} interface
 * to listen for item selections.
 */
public class DashboardListActivity extends Activity
        implements DashboardListFragment.Callbacks {



    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    //variables for allowing live update
    private int updateInterval = 25000;
    private Handler dataUpdateHandler = new Handler();

    //Variables for History update
    private int historyUpdateInterval = 30000;
    private Handler historyDataUpdateHandler = new Handler();



////   these are the dropbox variables for old app
//    final static private String APP_KEY = "62hdf2xcvsnggyf";
//   final static private String APP_SECRET = "68ogil29378xo5h";

    //dropbox files stuff
    public String dataTemp;

    public File file;
    public File historyFile;

    //Data Variables as custom class objects
    public DataCompiler currentValues;
    public HistoricalDataManager.HistoricalNodeData historicalValues;
    public int globalSpinnerValue;
    public int[] feedIds;

    //File management variables
    public String historyFileName = "thequeue.dat";
    public String fileName ="output.txt";
    public String historyFilePath = "Feed Data/" + historyFileName;
    String filePath = "Feed Data/" + fileName;

    public FileOutputStream outputStream;
    public FileOutputStream historyFileOutputStream;
    public HistoricalDataManager historicalDataManager;


    //the dropbox variables for the new app
    final static private String APP_KEY = "hhojeauh7nonqmx";
    final static private String APP_SECRET = "lxueohe4zq8ai1x";

    final static private Session.AccessType ACCESS_TYPE = Session.AccessType.DROPBOX;
    private DropboxAPI<AndroidAuthSession> mDBApi;

    //variables for reference
    Context mContext;
    String selectedItem = null;
    int dashboardSelected;
//    private Handler periodicHandler = new Handler();
//    private Handler

//variables that might need to be changed
    public static final int HOUSE_A_NUM = 4;
    public static final int HOUSE_B_NUM = 5;

    public static final int[] HOUSE_A_MAIN_IDS = new int[]{20,22};
    public static final int[] HOUSE_A_APP_IDS = new int[]{8,9};

    public static final int[] HOUSE_B_MAIN_IDS = new int[]{2,5};
    public static final int[] HOUSE_B_APP_IDS = new int[]{28,26,27,12};



    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_list);

        mContext = getApplicationContext();

        if (findViewById(R.id.dashboard_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((DashboardListFragment) getFragmentManager()
                    .findFragmentById(R.id.dashboard_list))
                    .setActivateOnItemClick(true);

            //this is the dropbox code
            AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
            AndroidAuthSession session = new AndroidAuthSession(appKeys);
           // AndroidAuthSession session = buildSession();
            try {
                mDBApi = new DropboxAPI<AndroidAuthSession>(session);
                // MyActivity below should be your activity class name
                //Starts the dropbox authentication process
                mDBApi.getSession().startOAuth2Authentication(DashboardListActivity.this);
                String[] arg = new String[3];
            }catch(Exception e){
                Log.d("Proto", "Auth session error" + e);
            }
        }

        //starts the updater and retrievals
        startRepeatingTask();
        historicalDataManager = new HistoricalDataManager();

    }


    @Override
    protected void onStop(){
        super.onStart();
    stopRepeatingTask();
    }

    @Override
    protected void onStart(){
        super.onStart();
        startRepeatingTask();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // Required to complete auth, sets the access token on the session
                mDBApi.getSession().finishAuthentication();
                //fileName = "output.txt";
                String accessToken = mDBApi.getSession().getOAuth2AccessToken();

                String fileAndroidPath = this.getApplicationContext().getFilesDir() +"/" + fileName;
                filePath = "Feed Data/" + fileName;
                Log.d("Proto", fileAndroidPath);

                String historyFileAndroidPath = this.getApplicationContext().getFilesDir() +"/" + historyFileName;
                historyFilePath = "Feed Data/" + historyFileName;
                //String filePath = "Dropbox/Feed Data/" + fileName;
               // Log.d("Proto", "this is the file path " + filePath);

                //String cachePath = mContext.getCacheDir().getAbsolutePath() + "/" + fileName;
                //new DataActivity().execute(cachePath);
                try {
                    file = new File(fileAndroidPath);
                    //file.mkdir();
                    file.getParentFile().mkdir();
                    historyFile = new File(historyFileAndroidPath);
                    historyFile.getParentFile().mkdir();
                    //if (parent != null) parent.mkdirs();
                    //historyFile.createNewFile();
                    //historyFile.mkdir();
                    //Log.d("Proto", "this is the file name " + file.getName());

//                    File mFile=new File(Environment.getExternalStorageDirectory().getPath()+fileName);
//                    mFile.mkdir();
                    deleteFile(historyFileName);
                    deleteFile(fileName);
                    historyFileOutputStream = openFileOutput(historyFileName , Context.MODE_PRIVATE);
                    outputStream = openFileOutput(fileName , Context.MODE_PRIVATE);


                    //historyFileOutputStream = new FileOutputStream(historyFile);
                    //DropboxAPI.DropboxFileInfo info = mDBApi.getFile("/" + fileName, null, outputStream, null);
                    DropboxRetrieval dropboxRetrieval = new DropboxRetrieval(file, filePath, outputStream, mDBApi);
                    this.dataTemp = DropboxRetrieval.readFromFile(dropboxRetrieval.execute().get());
                    this.currentValues = new DataCompiler(this.dataTemp);
                    DropboxRetrieval historyFileRetrieval = new DropboxRetrieval(historyFile, historyFilePath, historyFileOutputStream, mDBApi);

                    //for historical file
                    File tempFile = historyFileRetrieval.execute().get();
                    String historyText = DropboxRetrieval.readFromFile(tempFile);
                    //String  = historyFileRetrieval.readFromFile()
                    //Log.d("Proto", "This is the name: " + tempFile.getName() + ", " + this.dataTemp);

                    //FileInputStream fileInputStream = new FileInputStream(tempFile);

                    //FileInputStream fileInputStream = openFileInput(historyFileName);
                    //ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                    //Class.forName("com.example.ash.energymonitor.dataRetrieval.HistoricalDataManager");
                    //Object o = objectInputStream.readObject();
                    //historicalDataManager = (com.example.ash.energymonitor.dataRetrieval.HistoricalDataManager) objectInputStream.readObject();
                    Log.i("Feeds", historyText);
                     historicalDataManager = new HistoricalDataManager(historyText);
                    runnableHelper();
                    //dropboxRetrieval.run();

                  // Log.i("Proto", "Got it homme!");
                }catch(Exception e){
                    Log.i("Proto", "File not found", e);
                }
               // Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }

    /**Starts the background threads for updating data
     */
    void startRepeatingTask(){
        updaterRunnable.run();
        updateHistoryRunnable.run();

    }

    /**Stops the background threads for updating data
     */
    void stopRepeatingTask(){
        dataUpdateHandler.removeCallbacks(updaterRunnable);
        historyDataUpdateHandler.removeCallbacks(updateHistoryRunnable);
    }

    /**
     * Callback method from {@link DashboardListFragment.Callbacks}
     * indicating that the item with the given ID was selected.
     */
    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(DashboardDetailFragment.ARG_ITEM_ID, id);
            DashboardDetailFragment fragment = new DashboardDetailFragment();
            fragment.setArguments(arguments);
            getFragmentManager().beginTransaction()
                    .replace(R.id.dashboard_detail_container, fragment)
                    .commit();

        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, DashboardDetailActivity.class);
            detailIntent.putExtra(DashboardDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }

        //if(id.equals.(""))
    }


    private AndroidAuthSession buildSession() {
        AppKeyPair appKeyPair = new AppKeyPair(APP_KEY,
                APP_SECRET);
        AndroidAuthSession session;

        String[] stored = null;
        if (stored != null) {
            AccessTokenPair accessToken = new AccessTokenPair(stored[0],
                    stored[1]);
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE,
                    accessToken);
        } else {
            session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
        }

        return session;
    }


    //Runnable for the current reading file
    Runnable updaterRunnable = new Runnable(){@Override public void run(){try{runnableHelper();}catch(Exception e){}dataUpdateHandler.postDelayed(updaterRunnable, updateInterval);}};

    //Runnable for the History file
    Runnable updateHistoryRunnable = new Runnable(){@Override public void run(){ try{historyRunnableHelper();}catch(Exception e){}historyDataUpdateHandler.postDelayed(updateHistoryRunnable, historyUpdateInterval);}};



    /**This is a helper function for the runnable to fetch the data
     * It uses the data retrieval package.
     *
     * @return
     */
    private String runnableHelper(){

        String valueTemp = "";
        String nameTemp = "";
        int tempId =0;
        try {
            file = new File(file.getPath());

            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            DropboxRetrieval dropboxRetrieval = new DropboxRetrieval(file, filePath, outputStream, mDBApi);
            this.dataTemp = DropboxRetrieval.readFromFile(dropboxRetrieval.execute().get());
            this.currentValues = new DataCompiler(this.dataTemp);
            this.feedIds = this.currentValues.getFeedIds();
            tempId =  this.feedIds[this.globalSpinnerValue];
            //valueTemp =  this.currentValues.getValueFromName(this.selectedItem);
            valueTemp = ((DataCompiler.LiveData) this.currentValues.storedLiveDataTable.get(this.feedIds[this.globalSpinnerValue])).value;
            nameTemp= ((DataCompiler.LiveData) this.currentValues.storedLiveDataTable.get(this.feedIds[this.globalSpinnerValue])).name;
            //Log.d("Proto", "does this work? for regular");
        }catch(Exception e){
            //Do something about network connection
            //Log.d("Proto", "is this it?" +e);
        }

        if(this.dashboardSelected ==1){
            Log.d("Dash", "Does this run here?");

        }else if(this.dashboardSelected == 2){
            WidgetView dial = (WidgetView) findViewById(R.id.dash2Dial);

            dial.config.singleData = Float.parseFloat(valueTemp);
            HistoricalDataManager.HistoricalNodeData tempNode = this.historicalDataManager.historicalData.get(this.feedIds[this.globalSpinnerValue]);

            //Log.d("Proto", "Does this run with value? + " + this.historicalDataManager.historicalData.get(this.globalSpinnerValue).values[]);
            dial.config.minAndMax = WidgetDrawer.getMinAndMax(tempNode.values[tempNode.ID_WEEK]);
            dial.config.minAndMax[0] = 0f;
            Log.d("Proto", "Spinner selection: " + this.globalSpinnerValue + ". Name: " + nameTemp   +" id " +tempId + " other id? " + tempNode.nodeId + " Value: " + valueTemp);
            //Log.d("Proto", "This is the feed Ids: " + Arrays.toString(tempNode.values[1]));
            // Log.d("Proto", "Does this run? " + valueTemp + "This is the max " + dial.config.minAndMax[1]);
            dial.invalidate();

        }else if(this.dashboardSelected ==3){
            TextView textView = (TextView) findViewById(R.id.liveSelectTextValue);
            //Log.d("Proto", "This is the value: " + this.dataCompilerTemp.getValueFromName(this.selectedItem));
            textView.setText(valueTemp);
            textView.invalidate();
        }

        return null;
    }


    private String historyRunnableHelper(){
        try{

            historyFile = new File(historyFile.getPath());
            historyFileOutputStream = openFileOutput(historyFileName, Context.MODE_PRIVATE);
            DropboxRetrieval dropboxRetrieval = new DropboxRetrieval(historyFile, historyFilePath, historyFileOutputStream, mDBApi);
            File tempFile = dropboxRetrieval.execute().get();
            String historyText = DropboxRetrieval.readFromFile(tempFile);
            this.historicalDataManager = new HistoricalDataManager(historyText);

            //Log.d("Proto", "History thread updated. This is for HouseA " + Arrays.toString(this.historicalDataManager.historicalData.get(this.globalSpinnerValue).values[2]));


        }catch(Exception e){
            Log.d("Proto", "Runnable failed + " + e);

        }


        return null;
    }


    public void onRadioButtonClicked(View view){
        DashboardDetailFragment f = (DashboardDetailFragment) getFragmentManager().findFragmentById(R.id.dashboard_detail_container);
        //String buttonText = "" + ((RadioButton) view).getText();
        try {
            int viewId = view.getId();
            if (f.dashboardSelected == 2) {
                WidgetView runGraph = (WidgetView) findViewById(R.id.dash2RunGraph);

                switch (viewId) {
                    case R.id.hourRadioButton:
                        runGraph.config.timesArray = historicalValues.times[historicalValues.ID_HOUR];
                        runGraph.config.valueArray = historicalValues.values[historicalValues.ID_HOUR];
                        //runGraph.config.widgetData = historicalValues.lastHour;
                        break;
                    case R.id.dayRadioButton:
                        runGraph.config.timesArray = historicalValues.times[historicalValues.ID_DAY];
                        runGraph.config.valueArray = historicalValues.values[historicalValues.ID_DAY];
                        //runGraph.config.widgetData = historicalValues.lastDay;
                        break;
                    case R.id.weekRadioButton:
                        runGraph.config.timesArray = historicalValues.times[historicalValues.ID_WEEK];
                        runGraph.config.valueArray = historicalValues.values[historicalValues.ID_WEEK];
                        //runGraph.config.widgetData = historicalValues.lastWeek;
                        break;
                    case R.id.monthRadioButton:
                        runGraph.config.timesArray = historicalValues.times[historicalValues.ID_MONTH];
                        runGraph.config.valueArray = historicalValues.values[historicalValues.ID_MONTH];
                        //runGraph.config.widgetData = historicalValues.lastMonth;
                        break;
                    case R.id.yearRadioButton:
                        runGraph.config.timesArray = historicalValues.times[historicalValues.ID_YEAR];
                        runGraph.config.valueArray = historicalValues.values[historicalValues.ID_YEAR];
                        //runGraph.config.widgetData = historicalValues.lastYear;
                        break;
                    //case R.id.decadeRadioButton:

                        //runGraph.config.widgetData = historicalValues.last
                        //break;
                    default:
                        break;
                }
                runGraph.invalidate();

                boolean b = f.getActivity().equals(this);
               // Log.d("Proto", "Im in love with the COCO! " + b);


            }
        }catch(Exception e){

        }
    }

    public void onChangeData(View view) {
        WidgetView runGraph = (WidgetView) findViewById(R.id.runGraph_lbw);
        WidgetView dial = (WidgetView) findViewById(R.id.dial_lbw);
        WidgetView barGraph = (WidgetView) findViewById(R.id.barGraph_lbw);
        try {
            //HistoricalDataManager.HistoricalNodeData selectedNode =

            if (this.dashboardSelected == HOUSE_A_NUM) {

                if (view.getId() == findViewById(R.id.totalText_lbl).getId()) {
                    int length = historicalDataManager.historicalData.get(HOUSE_A_MAIN_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH].length;
                    float[] combinedValues = new float[length];
                    for (int i = 0; i < combinedValues.length; i++) {
                        combinedValues[i] = historicalDataManager.historicalData.get(HOUSE_A_MAIN_IDS[0]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH][i] + historicalDataManager.historicalData.get(HOUSE_A_MAIN_IDS[1]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH][i];
                    }
                    float[] weekValues = new float[historicalDataManager.historicalData.get(HOUSE_A_MAIN_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK].length];
                    for (int i = 0; i < weekValues.length; i++) {
                        weekValues[i] = historicalDataManager.historicalData.get(HOUSE_A_MAIN_IDS[0]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK][i] + historicalDataManager.historicalData.get(HOUSE_A_MAIN_IDS[1]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK][i];
                        //Log.d("BarGraph", " " + combinedValues[i]);
                    }


                    runGraph.config.timesArray = historicalDataManager.historicalData.get(HOUSE_A_MAIN_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    runGraph.config.valueArray = combinedValues;
                    dial.config.singleData = Float.parseFloat(currentValues.getValueFromId("" + HOUSE_A_MAIN_IDS[0])) + Float.parseFloat(currentValues.getValueFromId("" + HOUSE_A_MAIN_IDS[1]));

                    //barGraph.config.valueArray = barGraphValues;
                    //Log.d("BarGraph", "what is going on " + Arrays.toString(barGraph.config.namesArray));
                    barGraph.config.valueArray = CostCalc.getWeeklyCost(historicalDataManager.historicalData.get(HOUSE_A_MAIN_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK], weekValues, barGraph.config);
                    ;

                } else if (view.getId() == findViewById(R.id.washingText_lbl).getId()) {
                    runGraph.config.timesArray = historicalDataManager.historicalData.get(HOUSE_A_APP_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    runGraph.config.valueArray = historicalDataManager.historicalData.get(HOUSE_A_APP_IDS[0]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    dial.config.singleData = Float.parseFloat(currentValues.getValueFromId("" + HOUSE_A_APP_IDS[0]));
                    try {
                        barGraph.config.valueArray = CostCalc.getWeeklyCost(historicalDataManager.historicalData.get(HOUSE_A_APP_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK],
                                historicalDataManager.historicalData.get(HOUSE_A_APP_IDS[0]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK], barGraph.config);
                    }catch(Exception e){
                        barGraph.config.valueArray = null;
                    }

                } else if (view.getId() == findViewById(R.id.fridgeText_lbl).getId()) {
                    runGraph.config.timesArray = historicalDataManager.historicalData.get(HOUSE_A_APP_IDS[1]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    runGraph.config.valueArray = historicalDataManager.historicalData.get(HOUSE_A_APP_IDS[1]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    dial.config.singleData = Float.parseFloat(currentValues.getValueFromId("" + HOUSE_A_APP_IDS[1]));
                    try {
                        barGraph.config.valueArray = CostCalc.getWeeklyCost(historicalDataManager.historicalData.get(HOUSE_A_APP_IDS[1]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK],
                                historicalDataManager.historicalData.get(HOUSE_A_APP_IDS[1]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK], barGraph.config);
                    }catch(Exception e){
                        barGraph.config.valueArray = null;
                    }

                }


            } else if (this.dashboardSelected == HOUSE_B_NUM) {
                if (view.getId() == findViewById(R.id.totalText_lbl).getId()) {
                    int length = historicalDataManager.historicalData.get(HOUSE_B_MAIN_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH].length;
                    float[] combinedValues = new float[length];
                    for (int i = 0; i < combinedValues.length; i++) {
                        combinedValues[i] = historicalDataManager.historicalData.get(HOUSE_B_MAIN_IDS[0]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH][i] + historicalDataManager.historicalData.get(HOUSE_B_MAIN_IDS[1]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH][i];
                    }
                    float[] weekValues = new float[historicalDataManager.historicalData.get(HOUSE_B_MAIN_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK].length];
                    for (int i = 0; i < weekValues.length; i++) {
                        weekValues[i] = historicalDataManager.historicalData.get(HOUSE_B_MAIN_IDS[0]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK][i] + historicalDataManager.historicalData.get(HOUSE_B_MAIN_IDS[1]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK][i];

                    }
                    runGraph.config.timesArray = historicalDataManager.historicalData.get(HOUSE_B_MAIN_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    runGraph.config.valueArray = combinedValues;
                    dial.config.singleData = Float.parseFloat(currentValues.getValueFromId("" + HOUSE_B_MAIN_IDS[0])) + Float.parseFloat(currentValues.getValueFromId("" + HOUSE_B_MAIN_IDS[1]));


                    barGraph.config.valueArray = CostCalc.getWeeklyCost(historicalDataManager.historicalData.get(HOUSE_B_MAIN_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK], weekValues, barGraph.config);


                } else if (view.getId() == findViewById(R.id.washingText_lbl).getId()) {

                    runGraph.config.timesArray = historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    runGraph.config.valueArray = historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[0]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    dial.config.singleData = Float.parseFloat(currentValues.getValueFromId("" + HOUSE_B_APP_IDS[0]));
                    try {
                        barGraph.config.valueArray = CostCalc.getWeeklyCost(historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[0]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK],
                                historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[0]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK], barGraph.config);
                    }catch(Exception e){
                        barGraph.config.valueArray = null;
                    }
                } else if (view.getId() == findViewById(R.id.fridgeText_lbl).getId()) {
                    runGraph.config.timesArray = historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[1]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    runGraph.config.valueArray = historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[1]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    dial.config.singleData = Float.parseFloat(currentValues.getValueFromId("" + HOUSE_B_APP_IDS[1]));
                    try {
                        barGraph.config.valueArray = CostCalc.getWeeklyCost(historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[1]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK],
                                historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[1]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK], barGraph.config);
                    }catch(Exception e){
                        barGraph.config.valueArray = null;
                    }
                } else if (view.getId() == findViewById(R.id.microwaveText_lbl).getId()) {
                    runGraph.config.timesArray = historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[2]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    runGraph.config.valueArray = historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[2]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    dial.config.singleData = Float.parseFloat(currentValues.getValueFromId("" + HOUSE_B_APP_IDS[2]));
                    try {
                        barGraph.config.valueArray = CostCalc.getWeeklyCost(historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[2]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK],
                                historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[2]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK], barGraph.config);
                    }catch(Exception e){
                        barGraph.config.valueArray = null;
                    }
                } else if (view.getId() == findViewById(R.id.evrText_lbl).getId()) {
                    runGraph.config.timesArray = historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[3]).times[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    runGraph.config.valueArray = historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[3]).values[HistoricalDataManager.HistoricalNodeData.ID_MONTH];
                    dial.config.singleData = Float.parseFloat(currentValues.getValueFromId("" + HOUSE_B_APP_IDS[3]));
                    try {
                        barGraph.config.valueArray = CostCalc.getWeeklyCost(historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[3]).times[HistoricalDataManager.HistoricalNodeData.ID_WEEK],
                                historicalDataManager.historicalData.get(HOUSE_B_APP_IDS[3]).values[HistoricalDataManager.HistoricalNodeData.ID_WEEK], barGraph.config);
                    }catch(Exception e){
                        barGraph.config.valueArray = null;
                    }
                }
            } else {

            }
            runGraph.invalidate();
            dial.invalidate();
            barGraph.invalidate();

            Log.d("something", "This is dash " + this.dashboardSelected + " ID  " + view.getId() + " Other id " + findViewById(R.id.washingText_lbl).getId());
        }catch(Exception e){
            runGraph.invalidate();
            dial.invalidate();
            barGraph.invalidate();
        }
    }
}


