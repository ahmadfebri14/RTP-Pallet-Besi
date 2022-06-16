package com.example.rtppalletbesi.Utils;
import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.appcompat.app.AppCompatActivity;

public class DeviceHelper {
    @SuppressLint("MissingPermission")
    public void vibrateDevice(int timeVibrate, Context apps){
        Vibrator v = (Vibrator) apps.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(timeVibrate, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(timeVibrate);
        }
    }
}
