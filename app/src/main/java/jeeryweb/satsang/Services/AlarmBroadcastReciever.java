package jeeryweb.satsang.Services;

import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jeeryweb.satsang.Actvities.MainActivity;
import jeeryweb.satsang.R;
import jeeryweb.satsang.Utilities.AlarmSetter;
import jeeryweb.satsang.Utilities.SharedPreferenceManager;


public class AlarmBroadcastReciever extends BroadcastReceiver {
   MediaPlayer mp;
   AlarmSetter alarmSetter;
   SharedPreferenceManager sh;
   @Override
   public void onReceive(Context context, Intent intent) {

       alarmSetter = new AlarmSetter(context);
       sh = new SharedPreferenceManager(context);

       SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
       String currentTime = simpleDateFormat.format(new Date());

       Log.e("AlarmFuck", "Alarm called "+currentTime);


       NotificationCompat.Builder builder =
               new NotificationCompat.Builder(context)
                       .setSmallIcon(R.drawable.ic_satsang)
                       .setContentTitle("Satsang")
                       .setContentText("The time is " + currentTime)
                       .setStyle(new NotificationCompat.BigTextStyle())
                       .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true);
       builder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;

       Intent notificationIntent = new Intent(context, MainActivity.class);
       PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
               PendingIntent.FLAG_UPDATE_CURRENT);
       builder.setContentIntent(contentIntent);

       // Add as notification
       NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
       manager.notify(R.string.FM_NOTIFICATION_ID, builder.build());

       mp=MediaPlayer.create(context, R.raw.alarm );
//       mp.start();
      // mp.release();
//       Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();

       try {
           alarmSetter.setAlarm(true);
       } catch (ParseException e) {
           Log.e("AlarmFuck", "error in alarmservice");
           e.printStackTrace();
       }

   }
}
