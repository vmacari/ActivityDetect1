package com.example.vmacari.activitydetect1;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.vmacari.utils.ActivityChangedEvent;
import com.example.vmacari.utils.BusProvider;
import com.google.android.gms.location.DetectedActivity;
import com.squareup.otto.Subscribe;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.List;

public class GooglePlayServicesActivity extends Activity implements View.OnTouchListener {


    private static final int  MAX_HISTORY_LENGT = 50;
    private final String TAG = "GooglePlayServicesActivity";
    private DetectionRequester mDetectionRequester;
    private EditText etOutput;
    private List<ActivityChangedEvent> mActivityHistory = new ArrayList<>();
    private XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
    private XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    private GraphicalView mChartView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        etOutput = (EditText)findViewById(R.id.etOutput);

        mDetectionRequester = new DetectionRequester(getApplicationContext());

        mRenderer.addSeriesRenderer(getRendererWithColor (Color.RED,  "IN_VEHICLE"));
        mRenderer.addSeriesRenderer(getRendererWithColor (Color.BLUE,  "ON_FOOT"));
        mRenderer.addSeriesRenderer(getRendererWithColor (Color.GREEN,  "ON_BICYCLE"));
        mRenderer.addSeriesRenderer(getRendererWithColor (Color.BLACK,  "STILL"));
        mRenderer.addSeriesRenderer(getRendererWithColor (Color.GRAY,  "UNKNOWN"));
        mRenderer.addSeriesRenderer(getRendererWithColor (Color.YELLOW,  "TILTING"));
        mRenderer.addSeriesRenderer(getRendererWithColor (Color.CYAN,  "WALKING"));
        mRenderer.addSeriesRenderer(getRendererWithColor (Color.MAGENTA,  "RUNNING"));


        mRenderer.setBarSpacing(1);
        mRenderer.setGridColor(Color.RED);
        mRenderer.setShowLabels(true);
        mRenderer.setShowTickMarks(true);


        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins
        mRenderer.setPanEnabled(true, false);
        mRenderer.setYAxisMax(100);
        mRenderer.setYAxisMin(0);
        mRenderer.setShowGrid(true); // we show the grid




    }

    @Override
    public WindowManager getWindowManager() {
        return super.getWindowManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BusProvider.getInstance().unregister(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mDetectionRequester.connect();
    }


    @Override
    protected void onStop() {
        super.onStop();
        mDetectionRequester.disconnect();
    }


    private XYSeriesRenderer getRendererWithColor (int color, String decription) {

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(color);

        renderer.setDisplayBoundingPoints(true);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(3);

        renderer.setChartValuesTextSize(12);
        renderer.setHighlighted(true);
        renderer.setShowLegendItem(true);
        renderer.setDisplayChartValues(true);
        renderer.setDisplayBoundingPoints(true);

        return renderer;

    }

    @Subscribe
    public void receiveActivityReport (ActivityChangedEvent changeEvent) {

        Log.d(TAG, String.format("MaiFormReceived: %s", changeEvent.getDescription()));
        String initialText =  etOutput.getText().toString();

        String lines [] = initialText.split("\n");
        if (lines.length > 10) {
            StringBuilder sb = new StringBuilder();
            for (int i =1;i < lines.length; i ++ ) {
                sb.append(lines[i]);//.append("\n");
            }

            initialText = sb.toString();
        }

        etOutput.setText(initialText + "\n" + changeEvent.getDescription());
        if (etOutput.getText().length()  > 3) {
            etOutput.setSelection(etOutput.getText().length() - 2, etOutput.getText().length() - 1);
        }

        if (mActivityHistory.size() > MAX_HISTORY_LENGT) {
            mActivityHistory.remove(0); // remove first, FIFO
        }

        mActivityHistory.add(changeEvent);


        CategorySeries seriesInVehicle = new CategorySeries("IN_VEHICLE");
        CategorySeries seriesOnFoot = new CategorySeries("ON_FOOT");
        CategorySeries seriesOnBicycle = new CategorySeries("ON_BICYCLE");
        CategorySeries seriesStill = new CategorySeries("STILL");
        CategorySeries seriesUnknown = new CategorySeries("UNKNOWN");
        CategorySeries seriesTilting = new CategorySeries("TILTING");
        CategorySeries seriesWalking = new CategorySeries("WALKING");
        CategorySeries seriesRunning = new CategorySeries("RUNNING");


        for (ActivityChangedEvent ace : mActivityHistory) {
            seriesInVehicle.add(ace.getTypeString(), ace.getActivityType().getType() == DetectedActivity.IN_VEHICLE ? ace.getConfidence() : 0);
            seriesOnFoot.add(ace.getTypeString(), ace.getActivityType().getType() == DetectedActivity.ON_FOOT ? ace.getConfidence() : 0);
            seriesOnBicycle.add(ace.getTypeString(), ace.getActivityType().getType() == DetectedActivity.ON_BICYCLE ? ace.getConfidence() : 0);
            seriesStill.add(ace.getTypeString(), ace.getActivityType().getType() == DetectedActivity.STILL ? ace.getConfidence() : 0);
            seriesUnknown.add(ace.getTypeString(), ace.getActivityType().getType() == DetectedActivity.UNKNOWN ? ace.getConfidence() : 0);
            seriesTilting.add(ace.getTypeString(), ace.getActivityType().getType() == DetectedActivity.TILTING ? ace.getConfidence() : 0);
            seriesWalking.add(ace.getTypeString(), ace.getActivityType().getType() == DetectedActivity.WALKING ? ace.getConfidence() : 0);
            seriesRunning.add(ace.getTypeString(), ace.getActivityType().getType() == DetectedActivity.RUNNING ? ace.getConfidence() : 0);
        }

        mDataset.clear();
        //mDataset.addSeries(series.toXYSeries());
        mDataset.addSeries( seriesInVehicle.toXYSeries());
        mDataset.addSeries( seriesOnFoot.toXYSeries());
        mDataset.addSeries( seriesOnBicycle.toXYSeries());
        mDataset.addSeries( seriesStill.toXYSeries());
        mDataset.addSeries( seriesUnknown.toXYSeries());
        mDataset.addSeries( seriesTilting.toXYSeries());
        mDataset.addSeries( seriesWalking.toXYSeries());
        mDataset.addSeries( seriesRunning.toXYSeries());



        mChartView = ChartFactory.getBarChartView(this,
                mDataset, mRenderer, BarChart.Type.STACKED);

        mChartView.setClickable(true);
        mChartView.setHapticFeedbackEnabled(true);
        mChartView.setHovered(true);


        ((LinearLayout)findViewById(R.id.chart)).removeAllViews();
        ((LinearLayout)findViewById(R.id.chart)).addView(mChartView, 0);


        mChartView.setOnTouchListener(this);

        // series
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

           if (mChartView == null) {
               return false;
           }

        SeriesSelection seriesSelection = mChartView.getCurrentSeriesAndPoint();

        SimpleSeriesRenderer r = new SimpleSeriesRenderer();

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:{
                //Toast.makeText(this, "DOWN", Toast.LENGTH_SHORT) .show();
                if (seriesSelection != null) {
                    //seriesSelection.getXValue()

                }

            //    if(seriesSelection != null) touchedBar = seriesSelection.getPointIndex();
                break;
            }
            default: break;
        }

//        mRenderer.removeAllRenderers();
//        r.setColor(Color.RED);
//        r.setSelectedBar(ClickedBar);
//        mRenderer.addSeriesRenderer(r);
//        mChartView.repaint();

        return true;
    }
}
