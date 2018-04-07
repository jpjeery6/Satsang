package jeeryweb.satsang.Utilities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jeeryweb.satsang.Services.AlarmBroadcastReciever;

/**
 * Created by Debo#Paul on 4/1/2018.
 */

public class AlarmSetter {
    final private String TAG = "AlarmDebug";
    public SharedPreferenceManager sharedPref;
    Context _c;
    SimpleDateFormat sdfDate;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    private int requestCode, requestCode15;

    public AlarmSetter(Context c) {
        this._c = c;
        sharedPref = new SharedPreferenceManager(_c);
        requestCode = 234324243;
        requestCode15 = 230000000;
        sdfDate = new SimpleDateFormat("yyyy:MM:dd");

    }

    public void setAlarm(Boolean cont) throws ParseException {


        Log.e(TAG, "alarm manager called");
        if (sharedPref.isALarmDisabled()) {
            if (sharedPref.getflagA() != 0) {   //flag a says morning evening or not set 120
                cancelAlarm();
                sharedPref.setflagA(0);
            }
            Log.e(TAG, "alarm not set as setting disallowed");
            return;
        }
        if (cont) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm");
            // sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

            if (sharedPref.getflagA() == 1) { //morning alreadyset

                alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
                alarmIntent = PendingIntent.getBroadcast(_c, requestCode, intent, 0);

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
                //Toast.makeText(_c, "Alarm set for your next Evening time prayer",Toast.LENGTH_SHORT).show();
                Log.e(TAG, h + "evening alarm called at " + setTime);
            } else { //evening already set
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
                Log.e(TAG, h + "morning alarm called at " + Calendar.getInstance().getTimeInMillis());
            }
            return;
        }

        if (sharedPref.getflagA() != 0) {
            cancelAlarm();
            sharedPref.setflagA(0);
        }
        Log.e(TAG, "not continious part");


        Boolean isCloserToMorning = isCloser();
        int h, m;

        if (isCloserToMorning) {
            String s = sharedPref.getPrayTime().split(",")[0];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);
            sharedPref.setflagA(1);
            //Toast.makeText(_c, "Alarm set for your next Morning time Prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "morning alarm called at on loc changed " + h);
        } else {
            String s = sharedPref.getPrayTime().split(",")[1];
            h = Integer.parseInt(s.split(":")[0]);
            m = Integer.parseInt(s.split(":")[1]);
            sharedPref.setflagA(2);
            //Toast.makeText(_c, "Alarm set for your next Evening time Prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "evening alarm called at on loc changed " + h);
        }


        //run on first time or location change
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm");
        //sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        long setTime;

        Date d = new Date();
        String currentDate = sdfDate.format(d);
        String temp = currentDate+" "+h+":"+m;

        Date date = sdf.parse(temp);
        setTime = date.getTime();


        alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
        alarmIntent = PendingIntent.getBroadcast(_c, requestCode, intent, 0);
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                setTime, alarmIntent);

        Log.e(TAG, "at " + setTime);

    }

    public void setAlarm15(Boolean cont) throws ParseException {


        Log.e(TAG, "alarm manager called");
        if (sharedPref.isALarmDisabled()) {
            if (sharedPref.getflagA15() != 0) {
                cancelAlarm15();
                sharedPref.setflagA15(0);
            }
            Log.e(TAG, "alarm not set as setting disallowed");
            return;
        }
        if (cont) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm");
            // sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

            if (sharedPref.getflagA15() == 1) {

                alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
                alarmIntent = PendingIntent.getBroadcast(_c, requestCode15, intent, 0);

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
                //Toast.makeText(_c, "Alarm set for your next Evening time prayer",Toast.LENGTH_SHORT).show();
                Log.e(TAG, h + "evening alarm called at " + setTime);
            } else {
                alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
                Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
                alarmIntent = PendingIntent.getBroadcast(_c, requestCode15, intent, 0);

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
                //  Toast.makeText(_c, "Alarm set for your next Morning time prayer",Toast.LENGTH_SHORT).show();
                Log.e(TAG, h + "morning alarm called at " + setTime);
            }
            return;
        }

        if (sharedPref.getflagA15() != 0) {
            cancelAlarm15();
            sharedPref.setflagA15(0);
        }
        Log.e(TAG, "not continious part");


        Boolean isCloserToMorning = isCloser15();
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
            //Toast.makeText(_c, "Alarm set for your next Morning time Prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "morning alarm called at on loc changed " + h);
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
            //Toast.makeText(_c, "Alarm set for your next Evening time Prayer",Toast.LENGTH_SHORT).show();
            Log.e(TAG, "evening alarm called at on loc changed " + h);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy:MM:dd HH:mm");
        //sdf.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        long setTime;

        Date d = new Date();
        String currentDate = sdfDate.format(d);
      String temp = currentDate+" "+h+":"+m;
        Date date = sdf.parse(temp);
        setTime = date.getTime();


        alarmMgr = (AlarmManager) _c.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(_c, AlarmBroadcastReciever.class);
        alarmIntent = PendingIntent.getBroadcast(_c, requestCode15, intent, 0);
        alarmMgr.set(AlarmManager.RTC_WAKEUP,
                setTime, alarmIntent);

        Log.e(TAG, "at " + setTime);

    }


    public boolean isCloser() {


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());

        String[] s = currentTime.split(":");
        int h = Integer.parseInt(s[0]);
        int m = Integer.parseInt(s[1]);

        String s1 = sharedPref.getPrayTime().split(",")[0];
        int h1 = Integer.parseInt(s1.split(":")[0]);
        int m1 = Integer.parseInt(s1.split(":")[1]);

        String s2 = sharedPref.getPrayTime().split(",")[1];
        int h2 = Integer.parseInt(s2.split(":")[0]);
        int m2 = Integer.parseInt(s2.split(":")[1]);

        //Log.e(TAG, "isMorning called " + h + " " + h1 + " " + h2);
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

    public boolean isCloser15() {


        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String currentTime = sdf.format(new Date());

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

        //Log.e(TAG, "isMorning called " + h + " " + h1 + " " + h2);
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
        Log.e("AlarmFuck", "Alarm cancelled");
    }


    public void cancelAlarm15() {
        AlarmManager alarmManager = (AlarmManager) _c.getSystemService(_c.ALARM_SERVICE);
        Intent myIntent = new Intent(_c, AlarmBroadcastReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                _c, requestCode15, myIntent,
                0);

        alarmManager.cancel(pendingIntent);
        Log.e("AlarmDebug", "Alarm cancelled");
    }

}
