package org.techtown.Constellation.work;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class IntentReceiverzz extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            // Set the alarm here.
            Log.d(TAG, "알람실행됨");
            SharedPreferences sharedPref = context.getSharedPreferences("IFile",MODE_PRIVATE);
            int re = sharedPref.getInt("index",0);
            re++;

            SharedPreferences sharedPref1 = context.getSharedPreferences("Wr_AcFile",MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedPref1.edit();
            editor1.remove("OP");
            editor1.commit();

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("index_",re);
            editor.commit();

    }

}