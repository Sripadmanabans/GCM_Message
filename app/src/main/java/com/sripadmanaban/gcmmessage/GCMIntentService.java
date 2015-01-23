package com.sripadmanaban.gcmmessage;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * This is a service
 * Created by Sripadmanaban on 12/2/2014.
 */
public class GCMIntentService extends IntentService implements ConstantsHolder
{
    public static final int NOTIFICATION_ID = 1;
    NotificationCompat.Builder builder;

    public GCMIntentService()
    {
        super("GcmIntentService");
        Log.i(TAG, "In service");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.i(TAG, "In handle");

        String message = null;
        String title = null;

        Bundle extra = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
        Log.i(TAG, messageType);
        Toast.makeText(getBaseContext(), messageType, Toast.LENGTH_LONG).show();


        if(!extra.isEmpty())
        {
            switch (messageType)
            {
                case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                    message = "Send Error " + extra.toString();
                    title = "Error";
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                    message = "Deleted Messages on Server : " + extra.toString();
                    title = "Delete";
                    break;
                case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                    message = extra.getString(MESSAGE_GCM);
                    title = extra.getString(EMAIL_GCM);
                    Log.i(TAG, "Received : " + message);
                    break;
                default:
                    message = "Dude you didn't catch me";
                    title = "Default";
                    break;
            }
        }

        sendNotification(message, title);

        GCMBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg, String title)
    {
        NotificationManager mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent destination = new Intent(this, MainActivity.class);

        PendingIntent contentIntent =
                PendingIntent.getActivity(this, 0, destination, PendingIntent.FLAG_UPDATE_CURRENT);

        builder = new NotificationCompat.Builder(this);

        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setContentTitle(title);
        builder.setStyle(new NotificationCompat.BigTextStyle().bigText(msg));
        builder.setContentText(msg);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);

        mNotificationManager.notify(NOTIFICATION_ID, builder.build());

    }
}
