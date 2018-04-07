package jeeryweb.satsang.Utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jeeryweb.satsang.Services.AlarmBroadcastReciever;

/**
 * Created by Debo#Paul on 4/1/2018.
 */

public class AlarmSetter {
    final private String TAG = "AlarmDebug";
    public SharedPreferenceManager sharedPref;
    Context _c;
    SimpleDateFormat sdfDate, sdf;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int requestCode, requestCode15;
    Intent intent;

    public AlarmSetter(Context c) {
        this._c = c;
        sharedPref = new SharedPreferenceManager(_c);
        requestCode = 234324243;
        requestCode15 = 230000000;
        sdfDate = new SimpleDateFormat("yyyy:MM:dd"); //for date
        sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm"); //for time

    }

    public void setAlarm(Boolean cont) throws ParseException {


        Log.e(TAG, "SerAlarm() called");

        if (sharedPref.isALarmDisabled()) {
            if (sharedPref.getflagA() != 0) {   //flag a says morning evening or not set 120
                cancelAlarm();
                sharedPref.setflagA(0);
            }
            Log.e(TAG, "Alarm is disabbled!. So return to call function");
            return;
        }
        alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
        intent = new Intent(_c, AlarmBroadcastReciever.class);
        alarmIntent = PendingIntent.getBroadcast(_c, requestCode, intent, 0);

        if (cont) {

            Log.e(TAG, "Contiious flag is set");
            if (sharedPref.getflagA() == 1) { //morning alreadyset
                Log.e(TAG, "Morning prayer ALarm was called");

                String s = sharedPref.getPrayTime().split(",")[1];
                int h = Integer.parseInt(s.split(":")[0]);
                int m = Integer.parseInt(s.split(":")[1]);
                long setTime;
                Date d = new Date();
                String currentDate = sdfDate.format(d);
                String temp = currentDate + " " + h + ":" + m;
                Date date = sdf.parse(temp);
                setTime = date.getTime();


                alarmMgr.set(AlarmManager.RTC_WAKEUP,
                        setTime, alarmIntent);

                sharedPref.setflagA(2); //now setting evening
                Toast.makeText(_c, "Alarm set for your next Evening time prayer", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Evening prayer alarm is being set!" + setTime);

            } else { //evening already set
                Log.e(TAG, "Evening prayer ALarm was called");
                alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
                alarmIntent = PendingIntent.getBroadcast(_c, requestCode, intent, 0);

                String s = sharedPref.getPrayTime().split(",")[0];
                int h = Integer.parseInt(s.split(":")[0]);
                int m = Integer.parseInt(s.split(":")[1]);

                long setTime;


                Date d = new Date();
                String currentDate = sdfDate.format(d);
                String temp = currentDate + " " + h + ":" + m;
                Date date = sdf.parse(temp);
                setTime = date.getTime();


                alarmMgr.set(AlarmManager.RTC_WAKEUP,
                        setTime, alarmIntent);

                sharedPref.setflagA(1);  //now setting in morning
                //  Toast.makeText(_c, "Alarm set for your next Morning time prayer",Toast.LENGTH_SHORT).show();
                Toast.makeText(_c, "Alarm set for your next Morning time prayer", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Morning prayer alarm is being set!" + setTime);
            }
            return;
        }

        if (sharedPref.getflagA() != 0) {
            cancelAlarm();
            sharedPref.setflagA(0);
        }

        Log.e(TAG, "In not continious part, i.e location changed or main activity opened!");


        Boolean isCloserToMorning = IsCloserToMorning();
        Log.e(TAG, "isclosertoMornign is  "+isCloserToMorning);
        int h, m;

        if (isCloserToMorning) {
            String s = sharedPref.getPrayTime().split(",")[0];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);
            sharedPref.setflagA(1);
            Toast.makeText(_c, "Alarm set for your next Morning time Prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "ALarm set for morning time");
        } else {
            String s = sharedPref.getPrayTime().split(",")[1];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);
            sharedPref.setflagA(2);
            Toast.makeText(_c, "Alarm set for your next Evening time Prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "ALarm set for Evening time");
        }

        long setTime;

        Date d = new Date();
        String currentDate = sdfDate.format(d);
        String temp = currentDate+" "+h+":"+m;

        Date date = sdf.parse(temp);
        setTime = date.getTime();

        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                setTime, alarmIntent);

        Log.e(TAG, "ALarm set at time " + setTime);
        Log.e(TAG, "-----------------------------------------------------------------------------------------------");

    }

    public void setAlarm15(Boolean cont) throws ParseException {


        Log.e(TAG, "SetAlarm15() function called");
        if (sharedPref.isALarmDisabled()) {
            if (sharedPref.getflagA15() != 0) {
                cancelAlarm15();
                sharedPref.setflagA15(0);
            }
            Log.e(TAG, " 15) Alarm not set as setting is disallowed");
            return;
        }
        alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
        alarmIntent = PendingIntent.getBroadcast(_c, requestCode15, intent, 0);
        if (cont) {
            Log.e(TAG, "Contniuos is "+cont);

            if (sharedPref.getflagA15() == 1) {
                String s = sharedPref.getPrayTime().split(",")[1];
                int h = Integer.parseInt(s.split(":")[0]);
                int m = Integer.parseInt(s.split(":")[1]);

                if (m >= 15) {
                    m = m - 15;
                } else {
                    int temp = 15 - m;
                    m = 60 - temp;
                    if (h == 0) {
                        h = 23;

                    } else {
                        h = h - 1;
                    }


                }
                long setTime;

                Date d = new Date();
                String currentDate = sdfDate.format(d);
                String temp = currentDate + " " + h + ":" + m;

                Date date = sdf.parse(temp);
                setTime = date.getTime();


                alarmMgr.set(AlarmManager.RTC_WAKEUP,
                        setTime, alarmIntent);

                sharedPref.setflagA(2);
                Toast.makeText(_c, "ALarm set for reminding you 15 mins early to your next Evening prayer",Toast.LENGTH_SHORT).show();
                Log.e(TAG,  "15) Evening alarm is set at " + setTime);
            } else {


                String s = sharedPref.getPrayTime().split(",")[0];
                int h = Integer.parseInt(s.split(":")[0]);
                int m = Integer.parseInt(s.split(":")[1]);

                if (m >= 15) {
                    m = m - 15;
                } else {
                    int temp = 15 - m;
                    m = 60 - temp;
                    if (h == 0) {
                        h = 23;

                    } else {
                        h = h - 1;
                    }
                }

                long setTime;


                Date d = new Date();
                String currentDate = sdfDate.format(d);
                String temp = currentDate + " " + h + ":" + m;
                Date date = sdf.parse(temp);
                setTime = date.getTime();


                alarmMgr.set(AlarmManager.RTC_WAKEUP,
                        setTime, alarmIntent);

                sharedPref.setflagA(1);
                Toast.makeText(_c, "ALarm set for reminding you 15 mins early to your next Morning prayer",Toast.LENGTH_SHORT).show();
                Log.e(TAG,  "15) Morning alarm is set at " + setTime);
            }
            return;
        }

        if (sharedPref.getflagA15() != 0) {
            cancelAlarm15();
            sharedPref.setflagA15(0);
        }
        Log.e(TAG, "15) Continious variable is False, i.e lcoation hasc changed or method has launched");


        Boolean isCloserToMorning = IsCloserToMorning15();
        Log.e(TAG, "15). isCLoserToMorning variable is "+isCloserToMorning);
        int h, m;

        if (isCloserToMorning) {
            String s = sharedPref.getPrayTime().split(",")[0];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);

            if (m >= 15) {
                m = m - 15;
            } else {
                int temp = 15 - m;
                m = 60 - temp;
                if (h == 0) {
                    h = 23;

                } else {
                    h = h - 1;
                }
            }

            sharedPref.setflagA15(1);
            Toast.makeText(_c, "ALarm set for reminding you 15 mins early to your next Morning prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG,  "15) Morning alarm is set");
        } else {
            String s = sharedPref.getPrayTime().split(",")[1];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);
            if (m >= 15) {
                m = m - 15;
            } else {
                int temp = 15 - m;
                m = 60 - temp;
                if (h == 0) {
                    h = 23;

                } else {
                    h = h - 1;
                }
            }
            sharedPref.setflagA15(2);
            Toast.makeText(_c, "ALarm set for reminding you 15 mins early to your next Evening prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG,  "15) Evening alarm is set at ");
        }

        long setTime;

        Date d = new Date();
        String currentDate = sdfDate.format(d);
        String temp = currentDate+" "+h+":"+m;
        Date date = sdf.parse(temp);
        setTime = date.getTime();

        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                setTime, alarmIntent);

        Log.e(TAG, "15). ALarm set at time " + setTime);
        Log.e(TAG, "-----------------------------------------------------------------------------------------------");

    }


    public boolean IsCloserToMorning() {

        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
        String currentTime = hourFormat.format(new Date());

        //current time
        String[] s = currentTime.split(":");
        int h = Integer.parseInt(s[0]);
        int m = Integer.parseInt(s[1]);

        //morning time
        String s1 = sharedPref.getPrayTime().split(",")[0];
        int h1 = Integer.parseInt(s1.split(":")[0]);
        int m1 = Integer.parseInt(s1.split(":")[1]);

        //evening time
        String s2 = sharedPref.getPrayTime().split(",")[1];
        int h2 = Integer.parseInt(s2.split(":")[0]);
        int m2 = Integer.parseInt(s2.split(":")[1]);

        Log.e(TAG, "Ismorning called with para "+h+" "+h1+" "+h2);
        //compare with evening times
        if (h > h2)
            return true;
        if (h == h2) {
            if (m > m2)
                return true;
            else
                return false;
        }

        if (h < h1) {
            return true;
        }
        if (h > h1) {
            return false;
        }
        if (h == h1) {
            if (m > m1) {
                return false;
            }
            return true;
        }

        return true;
    }

    public boolean IsCloserToMorning15() {

        SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
        String currentTime = hourFormat.format(new Date());

        String[] s = currentTime.split(":");
        int h = Integer.parseInt(s[0]);
        int m = Integer.parseInt(s[1]);

        String s1 = sharedPref.getPrayTime().split(",")[0];
        int h1 = Integer.parseInt(s1.split(":")[0]);
        int m1 = Integer.parseInt(s1.split(":")[1]);

        if (m1 >= 15) {
            m1 = m1 - 15;
        } else {
            int temp = 15 - m1;
            m1 = 60 - temp;
            if (h1 == 0) {
                h1 = 23;

            } else {
                h1 = h1 - 1;
            }
        }

        String s2 = sharedPref.getPrayTime().split(",")[1];
        int h2 = Integer.parseInt(s2.split(":")[0]);
        int m2 = Integer.parseInt(s2.split(":")[1]);

        if (m2 >= 15) {
            m2 = m2 - 15;
        } else {
            int temp = 15 - m2;
            m2 = 60 - temp;
            if (h2 == 0) {
                h2 = 23;

            } else {
                h2 = h2 - 1;
            }
        }
        Log.e(TAG, "Ismorning called with para "+h+" "+h1+" "+h2);
        //compare with evening times
        if (h > h2)
            return true;
        if (h == h2) {
            if (m > m2)
                return true;
            else
                return false;
        }

        if (h < h1) {
            return true;
        }
        if (h > h1) {
            return false;
        }
        if (h == h1) {
            if (m > m1) {
                return false;
            }
            return true;
        }

        return true;
    }


    public void cancelAlarm() {
        AlarmManager alarmManager = (AlarmManager) _c.getSystemService(_c.ALARM_SERVICE);
        Intent myIntent = new Intent(_c, AlarmBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                _c, requestCode, myIntent,
                0);

        alarmManager.cancel(pendingIntent);
        Log.e(TAG, "Alarm is cancelled");
    }


    public void cancelAlarm15() {
        AlarmManager alarmManager = (AlarmManager) _c.getSystemService(_c.ALARM_SERVICE);
        Intent myIntent = new Intent(_c, AlarmBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                _c, requestCode15, myIntent,
                0);

        alarmManager.cancel(pendingIntent);
        Log.e(TAG, "15). Alarm is cancelled");
    }

}
