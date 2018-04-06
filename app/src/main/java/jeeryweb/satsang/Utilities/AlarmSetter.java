package jeeryweb.satsang.Utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import jeeryweb.satsang.Services.AlarmBroadcastReciever;

/**
 *
 * Created by Debo#Paul on 4/1/2018.
 *
 */

public class AlarmSetter {
    public SharedPreferenceManager sharedPref;
    Context _c;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int requestCode;

    final private String TAG="AlarmDebug";
    public AlarmSetter(Context c){
        this._c = c;
        sharedPref = new SharedPreferenceManager(_c);
        requestCode = 234324243;

    }

    public void setAlarm(Boolean cont) throws ParseException {

        Log.e(TAG, "alarm manager called");
        if( sharedPref.isALarmDisabled()){
            if(sharedPref.getflagA()!=0){
                cancelAlarm();
                sharedPref.setflagA(0);
            }
            Log.e(TAG, "alarm not set as setting disallowed");
            return;
        }
        if(cont){

            if(sharedPref.getflagA()==1){

                alarmMgr = (AlarmManager)_c.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
                alarmIntent = PendingIntent.getBroadcast(_c, requestCode, intent, 0);

                String s = sharedPref.getPrayTime().split(",")[1];
                int h = Integer.parseInt(s.split(":")[0]);
                int m = Integer.parseInt(s.split(":")[1]);

                String alarmTimeString = h+":"+m;
                LocalTime alarmTime = LocalTime.parse(alarmTimeString);
                ZoneId zone = ZoneId.of("Asia/Kolkata");
                long alarmTimeMillis = LocalDate.now(zone)
                        .atTime(alarmTime)
                        .atZone(zone)
                        .toInstant()
                        .toEpochMilli();

                alarmMgr.set(AlarmManager.RTC_WAKEUP,
                        alarmTimeMillis, alarmIntent);

                sharedPref.setflagA(2);
                //Toast.makeText(_c, "Alarm set for your next Evening time prayer",Toast.LENGTH_SHORT).show();
                Log.e(TAG, h + "evening alarm called at "+Calendar.getInstance().getTimeInMillis());
            }
            else{
                alarmMgr = (AlarmManager)_c.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
                alarmIntent = PendingIntent.getBroadcast(_c, requestCode, intent, 0);

                String s = sharedPref.getPrayTime().split(",")[0];
                int h = Integer.parseInt(s.split(":")[0]);
                int m = Integer.parseInt(s.split(":")[1]);

                String alarmTimeString = h+":"+m;
                LocalTime alarmTime = LocalTime.parse(alarmTimeString);
                ZoneId zone = ZoneId.of("Asia/Kolkata");
                long alarmTimeMillis = LocalDate.now(zone)
                        .atTime(alarmTime)
                        .atZone(zone)
                        .toInstant()
                        .toEpochMilli();

                alarmMgr.set(AlarmManager.RTC_WAKEUP,
                        alarmTimeMillis, alarmIntent);

                sharedPref.setflagA(1);
              //  Toast.makeText(_c, "Alarm set for your next Morning time prayer",Toast.LENGTH_SHORT).show();
                Log.e(TAG, h + "morning alarm called at "+Calendar.getInstance().getTimeInMillis());
            }
            return;
        }

        if(sharedPref.getflagA()!=0){
            cancelAlarm();
            sharedPref.setflagA(0);
        }
    Log.e(TAG, "not continious part");


        Boolean isCloserToMorning = isCloser();
        int h,m;

        if(isCloserToMorning){
            String s = sharedPref.getPrayTime().split(",")[0];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);
            sharedPref.setflagA(1);
            //Toast.makeText(_c, "Alarm set for your next Morning time Prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "morning alarm called at on loc changed "+h);
        }
        else{
            String s = sharedPref.getPrayTime().split(",")[1];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);
            sharedPref.setflagA(2);
            //Toast.makeText(_c, "Alarm set for your next Evening time Prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "evening alarm called at on loc changed "+h);
        }
        String alarmTimeString = h+":"+m;
        LocalTime alarmTime = LocalTime.parse(alarmTimeString);
        ZoneId zone = ZoneId.of("Asia/Kolkata");
        long alarmTimeMillis = LocalDate.now(zone)
                .atTime(alarmTime)
                .atZone(zone)
                .toInstant()
                .toEpochMilli();

        alarmMgr = (AlarmManager)_c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
        alarmIntent = PendingIntent.getBroadcast(_c, requestCode, intent, 0);
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                alarmTimeMillis,  alarmIntent);

        Log.e(TAG, "at "+Calendar.getInstance().getTimeInMillis());

    }

    public boolean isCloser(){


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String currentTime = simpleDateFormat.format(new Date());

        String[] s = currentTime.split(":");
        int h = Integer.parseInt(s[0]);
        int m = Integer.parseInt(s[1]);

        String s1 = sharedPref.getPrayTime().split(",")[0];
        int h1 = Integer.parseInt(s1.split(":")[0]);
        int m1 = Integer.parseInt(s1.split(":")[1]);

        String s2 = sharedPref.getPrayTime().split(",")[1];
        int h2 = Integer.parseInt(s2.split(":")[0]);
        int m2 = Integer.parseInt(s2.split(":")[1]);

        Log.e(TAG, "isMorning called "+h+" "+h1+" "+h2);
        //compare with evening times
        if(h>h2)
            return true;
        if(h==h2){
            if(m>m2)
                return true;
            else
                return false;
        }

        if(h<h1){
            return true;
        }
        if(h>h1){
            return false;
        }
        if(h==h1){
            if(m>m1){
                return false;
            }
            return true;
        }

        return true;
    }





    public void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) _c.getSystemService(_c.ALARM_SERVICE);
        Intent myIntent = new Intent(_c, AlarmBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                _c, requestCode, myIntent,
                0);

        alarmManager.cancel(pendingIntent);
        Log.e("AlarmFuck", "Alarm cancelled");
    }

    private int processPrayingTimeData() {
        String s1 = sharedPref.getPrayTime().split(",")[0];
        String s2 = sharedPref.getPrayTime().split(",")[1];

        int hour  = Integer.parseInt(s1.split(":")[0]);
        int min = Integer.parseInt(s1.split(":")[1]);


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm");
        String currentTime = simpleDateFormat.format(new Date());

        int hour1  = Integer.parseInt(currentTime.split(":")[0]);
        int min1 = Integer.parseInt(currentTime.split(":")[1]);

        return (hour - hour1)*3600 +  (min-min1)*60;

    }
}
