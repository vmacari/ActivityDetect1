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


        // Now we create the renderer
        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(Color.BLUE);

        renderer.setDisplayBoundingPoints(true);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(3);

        renderer.setChartValuesTextSize(12);
        renderer.setHighlighted(true);
        renderer.setShowLegendItem(true);
        renderer.setDisplayChartValues(true);
        renderer.setDisplayBoundingPoints(true);

        mRenderer.addSeriesRenderer(renderer);

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


        CategorySeries series = new CategorySeries("Activity");

        for (ActivityChangedEvent ace : mActivityHistory) {
            series.add(ace.getTypeString(), ace.getConfidence());
        }

        mDataset.clear();
        mDataset.addSeries(series.toXYSeries());

        mChartView = ChartFactory.getBarChartView(this,
                mDataset, mRenderer, BarChart.Type.DEFAULT);

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
