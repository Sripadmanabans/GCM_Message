package com.sripadmanaban.gcmmessage;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * To receive the GCM message i guess
 * Created by Sripadmanaban on 1/19/2015.
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        ComponentName comp = new ComponentName(context.getPackageName(), GCMIntentService.class.getName());

        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);

        //send a local broadcast
        Intent localBroadCastIntent = new Intent(ConstantsHolder.TAG);
        localBroadCastIntent.putExtras(intent);
        LocalBroadcastManager.getInstance(context).sendBroadcast(localBroadCastIntent);
    }
}
