package com.fonsview.localktv.tool;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScanSdReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)){           
             
        }else if(Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)){          
        }   
    }
}