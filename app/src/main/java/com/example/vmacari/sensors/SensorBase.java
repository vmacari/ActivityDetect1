package com.example.vmacari.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;

import java.util.List;

/**
 * Created by vmacari on 1/27/15.
 */
public abstract class SensorBase {

    private SensorManager mSensorManager;
    public abstract boolean isAvailable ();


    /**
     *
     * @param context
     */
    public SensorBase(Context context) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    /**
     *
     * @return
     */
    public List<Sensor> getSensorsList () {
        return mSensorManager.getSensorList(Sensor.TYPE_ALL);
    }

    /**
     *
     * @return
     * @param typeMagneticField
     */
    public Sensor getSensor(int typeOfSensor) {
        return mSensorManager.getDefaultSensor(typeOfSensor);
    }
}
