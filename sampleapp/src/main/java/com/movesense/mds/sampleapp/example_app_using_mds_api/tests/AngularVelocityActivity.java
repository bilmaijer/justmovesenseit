package com.movesense.mds.sampleapp.example_app_using_mds_api.tests;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.movesense.mds.Mds;
import com.movesense.mds.MdsException;
import com.movesense.mds.MdsNotificationListener;
import com.movesense.mds.MdsResponseListener;
import com.movesense.mds.MdsSubscription;
import com.movesense.mds.sampleapp.BleManager;
import com.movesense.mds.sampleapp.CanvasView;
import com.movesense.mds.sampleapp.ConnectionLostDialog;
import com.movesense.mds.sampleapp.MdsRx;
import com.movesense.mds.sampleapp.R;
import com.movesense.mds.sampleapp.example_app_using_mds_api.FormatHelper;
import com.movesense.mds.sampleapp.example_app_using_mds_api.logs.LogsManager;
import com.movesense.mds.sampleapp.example_app_using_mds_api.model.AngularVelocity;
import com.movesense.mds.sampleapp.example_app_using_mds_api.model.InfoResponse;
import com.movesense.mds.sampleapp.example_app_using_mds_api.model.MovesenseConnectedDevices;
import com.polidea.rxandroidble.RxBleDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnItemSelected;

public class AngularVelocityActivity extends AppCompatActivity implements BleManager.IBleConnectionMonitor {

    private final String LOG_TAG = AngularVelocityActivity.class.getSimpleName();
    private final String ANGULAR_VELOCITY_PATH = "Meas/Gyro/";
    private final String ANGULAR_VELOCITY_INFO_PATH = "/Meas/Gyro/Info";
    public static final String URI_EVENTLISTENER = "suunto://MDS/EventListener";
    private final List<String> spinnerRates = new ArrayList<>();
    @BindView(R.id.connected_device_name_textView) TextView mConnectedDeviceNameTextView;
    @BindView(R.id.connected_device_swVersion_textView) TextView mConnectedDeviceSwVersionTextView;
    @BindView(R.id.canvas) ImageView image;
    private String rate;
    private MdsSubscription mdsSubscription;

    private LineGraphSeries seriesX = new LineGraphSeries();
    private LineGraphSeries seriesY = new LineGraphSeries();
    private LineGraphSeries seriesZ = new LineGraphSeries();
    private LineGraphSeries seriesDeg = new LineGraphSeries();

    @BindView(R.id.switchSubscription) SwitchCompat switchSubscription;
    @BindView(R.id.spinner) Spinner spinner;
    @BindView(R.id.x_axis_textView) TextView xAxisTextView;
    @BindView(R.id.y_axis_textView) TextView yAxisTextView;
    @BindView(R.id.z_axis_textView) TextView zAxisTextView;
    @BindView(R.id.deg_axis_textView) TextView degAxisTextView;
    @BindView(R.id.graphView) GraphView graphView;
    private Dialog alertDialog;
    private LogsManager logsManager;
    private GraphView mAngularGraphView;

    private Paint paint = new Paint();
    private Paint paint2 = new Paint();
    private float initX = 100;
    private float initY = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_angular_velocity);
        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Angular Velocity");
        }

        logsManager = new LogsManager(this);

        alertDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.please_wait)
                .setMessage(R.string.loading_information)
                .create();


        mConnectedDeviceNameTextView.setText("Serial: " + MovesenseConnectedDevices.getConnectedDevice(0)
                .getSerial());

        mConnectedDeviceSwVersionTextView.setText("Sw version: " + MovesenseConnectedDevices.getConnectedDevice(0)
                .getSwVersion());

        xAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        yAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        zAxisTextView.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

        mAngularGraphView = (GraphView) findViewById(R.id.graphView);
        setUpGraphView();

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, spinnerRates);

        spinner.setAdapter(spinnerAdapter);

        // Display dialog
        alertDialog.show();

        paint.setARGB(1,0,0, 0);
        paint2.setARGB(1, 255, 0, 0);

        Mds.builder().build(this).get(MdsRx.SCHEME_PREFIX
                        + MovesenseConnectedDevices.getConnectedDevice(0).getSerial() + ANGULAR_VELOCITY_INFO_PATH,
                null, new MdsResponseListener() {
                    @Override
                    public void onSuccess(String data) {
                        Log.d(LOG_TAG, "onSuccess(): " + data);

                        // Hide dialog
                        alertDialog.dismiss();

                        InfoResponse infoResponse = new Gson().fromJson(data, InfoResponse.class);

                        for (Integer inforate : infoResponse.content.sampleRates) {
                            spinnerRates.add(String.valueOf(inforate));

                            // Set first rate as default
                            if (rate == null) {
                                rate = String.valueOf(inforate);
                            }
                        }

                        spinnerAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(MdsException error) {
                        Log.e(LOG_TAG, "onError(): ", error);

                        // Hide dialog
                        alertDialog.dismiss();
                    }
                });

        BleManager.INSTANCE.addBleConnectionMonitorListener(this);
    }

    @OnCheckedChanged(R.id.switchSubscription)
    public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            disableSpinner();

            // Clear Logcat
            logsManager.clearAdbLogcat();

            mdsSubscription = Mds.builder().build(this).subscribe(URI_EVENTLISTENER,
                    FormatHelper.formatContractToJson(MovesenseConnectedDevices.getConnectedDevice(0)
                            .getSerial(), ANGULAR_VELOCITY_PATH + rate), new MdsNotificationListener() {
                        @Override
                        public void onNotification(String data) {
                            Log.d(LOG_TAG, "onSuccess(): " + data);

                            AngularVelocity angularVelocity = new Gson().fromJson(
                                    data, AngularVelocity.class);

                            if (angularVelocity != null) {

                                AngularVelocity.Array arrayData = angularVelocity.body.array[0];

                                xAxisTextView.setText(String.format(Locale.getDefault(),
                                        "x: %.6f", arrayData.x));
                                yAxisTextView.setText(String.format(Locale.getDefault(),
                                        "y: %.6f", arrayData.y));
                                zAxisTextView.setText(String.format(Locale.getDefault(),
                                        "z: %.6f", arrayData.z));
                                degAxisTextView.setText(calcDegree(arrayData.x, arrayData.y, arrayData.z) + "");



                                try {
                                    drawPoint((float) arrayData.x, (float) arrayData.y);
                                    seriesX.appendData(
                                            new DataPoint(angularVelocity.body.timestamp, arrayData.x), false,
                                            200);
                                    seriesY.appendData(
                                            new DataPoint(angularVelocity.body.timestamp, arrayData.y), true,
                                            200);
                                    seriesZ.appendData(
                                            new DataPoint(angularVelocity.body.timestamp, arrayData.z), true,
                                            200);
                                } catch (IllegalArgumentException e) {
                                    Log.e(LOG_TAG, "GraphView error ", e);
                                }
                            }
                        }

                        @Override
                        public void onError(MdsException error) {
                            Log.e(LOG_TAG, "onError(): ", error);

                            Toast.makeText(AngularVelocityActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                            buttonView.setChecked(false);
                        }
                    });
        } else {
            enableSpinner();
            unSubscribe();

            // Save logs
            saveAdbLogsToFile(LOG_TAG);
        }
    }

    @OnItemSelected(R.id.spinner)
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        rate = spinnerRates.get(position);
    }

    private double calcDegree(double x, double y, double z) {
        double ayf=y/57.0;//Convert to rad
        double axf=x/57.0;//Convert to rad
        double xh=x*Math.cos(ayf)+y*Math.sin(ayf)*Math.sin(axf)-z*Math.cos(axf)*Math.sin(ayf);
        double yh=y*Math.cos(axf)+z*Math.sin(axf);

        double var_compass=Math.atan2(yh,xh) * (180 / Math.PI) -90; // angle in degrees
        if (var_compass>0){var_compass=var_compass-360;}
        var_compass=360+var_compass;

        return (var_compass);
    }

    private void drawPoint(Float x, Float y) {
        initX += x / 1000;
        initY += y / 1000;

        Bitmap bitmap = Bitmap.createBitmap(
                500, // Width
                300, // Height
                Bitmap.Config.ARGB_8888 // Config
        );

        // Initialize a new Canvas instance
        Canvas canvas = new Canvas(bitmap);

        // Draw a solid color to the canvas background
        canvas.drawColor(Color.LTGRAY);

        // Initialize a new Paint instance to draw the Circle
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);

        // Calculate the available radius of canvas
        int radius = 5;

                /*
                    public void drawCircle (float cx, float cy, float radius, Paint paint)
                        Draw the specified circle using the specified paint. If radius is <= 0, then
                        nothing will be drawn. The circle will be filled or framed based on the
                        Style in the paint.

                    Parameters
                        cx : The x-coordinate of the center of the circle to be drawn
                        cy : The y-coordinate of the center of the circle to be drawn
                        radius : The radius of the cirle to be drawn
                        paint : The paint used to draw the circle
                */
        // Finally, draw the circle on the canvas
        canvas.drawCircle(
                initX, // cx
                initY, // cy
                radius, // Radius
                paint // Paint
        );

        // Display the newly created bitmap on app interface
        image.setImageBitmap(bitmap);

    }


    private void setSeriesColor(@ColorRes int colorRes, LineGraphSeries series) {
        int color = getResources().getColor(colorRes);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setColor(color);
        series.setCustomPaint(paint);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unSubscribe();

        BleManager.INSTANCE.removeBleConnectionMonitorListener(this);
    }

    private void unSubscribe() {
        if (mdsSubscription != null) {
            mdsSubscription.unsubscribe();
            mdsSubscription = null;
        }
    }

    private void disableSpinner() {
        spinner.setEnabled(false);
    }

    private void enableSpinner() {
        spinner.setEnabled(true);
    }

    private void saveAdbLogsToFile(String logTag) {
        if (!logsManager.checkRuntimeWriteExternalStoragePermission(this, this)) {
            return;
        }

        logsManager.saveLogsToSdCard(logTag);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == LogsManager.REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            // if request is cancelled grantResults array is empty
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {

                    // Save logs
                    saveAdbLogsToFile(LOG_TAG);
                }
            }
        }
    }

    private void setUpGraphView() {
        mAngularGraphView.addSeries(seriesX);
        mAngularGraphView.addSeries(seriesY);
        mAngularGraphView.addSeries(seriesZ);
        seriesX.setDrawAsPath(true);
        seriesY.setDrawAsPath(true);
        seriesZ.setDrawAsPath(true);
        mAngularGraphView.getViewport().setXAxisBoundsManual(true);
        mAngularGraphView.getViewport().setMinX(0);
        mAngularGraphView.getViewport().setMaxX(10000);

        // Disable X axis label
        mAngularGraphView.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    return "";
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        setSeriesColor(android.R.color.holo_red_dark, seriesX);
        setSeriesColor(android.R.color.holo_green_dark, seriesY);
        setSeriesColor(android.R.color.holo_blue_dark, seriesZ);

    }

    @Override
    public void onDisconnect(RxBleDevice rxBleDevice) {
        Log.e(LOG_TAG, "onDisconnect: " + rxBleDevice.getName() + " " + rxBleDevice.getMacAddress());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setUpGraphView();
                ConnectionLostDialog.INSTANCE.showDialog(AngularVelocityActivity.this);
            }
        });
    }

    @Override
    public void onConnect(RxBleDevice rxBleDevice) {
        Log.e(LOG_TAG, "onConnect: " + rxBleDevice.getName() + " " + rxBleDevice.getMacAddress());
        ConnectionLostDialog.INSTANCE.dismissDialog();
    }
}
