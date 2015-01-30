package com.example.vmacari.sensors;

import android.content.Context;
import android.hardware.Sensor;

/**
 * Created by vmacari on 1/27/15.
 */
public class SensorBaseMagnetometer extends SensorBase {

    public SensorBaseMagnetometer(Context context) {
        super (context);
    }


    @Override
    public boolean isAvailable() {
        return getSensor(Sensor.TYPE_MAGNETIC_FIELD) != null;
    }
}
