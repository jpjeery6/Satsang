package jeeryweb.satsang.Actvities;

import android.Manifest;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jeeryweb.satsang.BuildConfig;
import jeeryweb.satsang.Data.FileReader;
import jeeryweb.satsang.R;
import jeeryweb.satsang.Services.FetchAddressIntentService;
import jeeryweb.satsang.Services.LocationUpdaterService;
import jeeryweb.satsang.Utilities.AlarmSetter;
import jeeryweb.satsang.Utilities.ConstantsForGeocoding;
import jeeryweb.satsang.Utilities.SharedPreferenceManager;

public class MainActivity extends AppCompatActivity  {

//attributes****************************************************************************************
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String ADDRESS_REQUESTED_KEY = "address-request-pending";
    private static final String LOCATION_ADDRESS_KEY = "location-address";

   //Location objects
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLastLocation;
    /**
     * Tracks whether the user has requested an address. Becomes true when the user requests an
     * address and false when the address (or an error message) is delivered.
     */
    private boolean mAddressRequested;

    private String mAddressOutput;
    private String mAddressState;
    private String mPrayingTime;
    private String myLocationDistrict;
    /**
     * Receiver registered with this activity to get the response from FetchAddressIntentService.
     */
    private AddressResultReceiver mResultReceiver;

    //widgets
    private TextView mLocationAddressTextView, mStateNameView , mPrayerTimeView;
    private ProgressBar mProgressBar;
    private Switch aSwitch;

    public FileReader fileReader;
    public AlarmSetter alarmSetter;
    public SharedPreferenceManager sharedPref;
    mRecievrfromService mrecievrfromService;
    Context c;

//Methods*******************************************************************************************
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mResultReceiver = new AddressResultReceiver(new Handler());

        mLocationAddressTextView = (TextView) findViewById(R.id.location_address_view);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        mStateNameView = (TextView) findViewById(R.id.location_state);
        mPrayerTimeView = (TextView) findViewById(R.id.prayer_time_view);

        aSwitch  =(Switch)findViewById(R.id.simpleSwitch);

        /*  Initilaize classed */
        mFetchAddressButton = (Button) findViewById(R.id.fetch_address_button);
        mStateName = (TextView) findViewById(R.id.prayer_time_view);

        fileReader = new FileReader();
        sharedPref = new SharedPreferenceManager(this);
        mResultReceiver = new AddressResultReceiver(new Handler());
        mrecievrfromService = new mRecievrfromService();
        alarmSetter = new AlarmSetter(this);

        //set listener for swicth button

        aSwitch.setChecked(sharedPref.isALarmDisabled());

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
           public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                              if (bChecked) {
                                     sharedPref.setAlarmDisabled();

                                  try {
                                      alarmSetter.setAlarm(false);
                                  } catch (ParseException e) {
                                      e.printStackTrace();
                                  }
                              } else {
                                     sharedPref.unsetAlarmDisabled();
                                  try {
                                      alarmSetter.setAlarm(false);
                                  } catch (ParseException e) {
                                      e.printStackTrace();
                                  }

                                  }
                         }
      });


        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LocationUpdaterService.MY_ACTION);
        registerReceiver(mrecievrfromService, intentFilter);

        if(!isServiceRunning(LocationUpdaterService.class))
            startService(new Intent(getBaseContext(), LocationUpdaterService.class));

        c = this;
        // Set defaults, then update using values stored in the Bundle.
        mAddressRequested = false;
        myLocationDistrict = "";
        updateValuesFromBundle(savedInstanceState);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        updateUIWidgets();

    }
    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getAddress();
        }
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        unregisterReceiver(mrecievrfromService);
        super.onDestroy();
    }


    /**
     * Updates fields based on data stored in the bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Check savedInstanceState to see if the address was previously requested.
            if (savedInstanceState.keySet().contains(ADDRESS_REQUESTED_KEY)) {
                mAddressRequested = savedInstanceState.getBoolean(ADDRESS_REQUESTED_KEY);
            }
            // Check savedInstanceState to see if the location address string was previously found
            // and stored in the Bundle. If it was found, display the address string in the UI.
            if (savedInstanceState.keySet().contains(LOCATION_ADDRESS_KEY)) {
                myLocationDistrict = savedInstanceState.getString(LOCATION_ADDRESS_KEY);
                mLocationAddressTextView.setText(myLocationDistrict);
            }
        }
    }

    /**
     * Runs when user clicks the Fetch Address button.
     */
    @SuppressWarnings("unused")
    public void fetchAddressButtonHandler(View view) {
        if (mLastLocation != null) {
            startIntentService();
            Log.e("start","service started on button press");
            return;
        }
        // If we have not yet retrieved the user location, we process the user's request by setting
        // mAddressRequested to true. As far as the user is concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        updateUIWidgets();
    }

    /**
     * Creates an intent, adds location data to it as an extra, and starts the intent service for
     * fetching an address.
     */
    private void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);

        intent.putExtra(ConstantsForGeocoding.RECEIVER, mResultReceiver);
        intent.putExtra(ConstantsForGeocoding.LOCATION_DATA_EXTRA, mLastLocation);

        // Pass the result receiver as an extra to the service.
        intent.putExtra(ConstantsForGeocoding.RECEIVER, mResultReceiver);
        // Pass the location data as an extra to the service.
        intent.putExtra(ConstantsForGeocoding.LOCATION_DATA_EXTRA, mLastLocation);
        // Start the service. If the service isn't already running, it is instantiated and started
        // (creating a process for it if needed); if it is running then it remains running. The
        // service kills itself automatically once all intents are processed.

        startService(intent);
    }


    @SuppressWarnings("MissingPermission")
    private void getAddress() {
        Log.e(TAG, "in GetAddress");
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location == null) {
                            Log.w(TAG, "onSuccess:null");

                            showSnackbar("Cannot get location! Please try again later");
                            return;
                        }

                        mLastLocation = location;

                        // Determine whether a Geocoder is available.
                        if (!Geocoder.isPresent()) {
                            showSnackbar(getString(R.string.no_geocoder_available));
                            return;
                        }
                        // If the user pressed the fetch address button before we had the location,
                        // this will be set to true indicating that we should kick off the intent
                        // service after fetching the location.
                        if (mAddressRequested) {
                            startIntentService();
                        }
                        else {
                            startIntentService();
                            Log.e(TAG, "address not requested");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "getLastLocation:onFailure", e);
                    }
                });
    }

    /**

     * Updates the address in the UI.
     */
    private void displayAddressOutput() {
        if(mAddressState==null || mPrayingTime==null){
            mLocationAddressTextView.setText("Error in App. Please restart!");
            mStateNameView.setText("Error in App. Please restart!");
            mPrayerTimeView.setText("Error in App. Please restart!");
            return;
        }
        if(mAddressState=="NA" || mPrayingTime=="NA"){
            mLocationAddressTextView.setText(mAddressOutput);
            mStateNameView.setText("No Satasang mandir in your area");
            mPrayerTimeView.setText("Your old prayer time");
        }
        mLocationAddressTextView.setText(mAddressOutput);
        mStateNameView.setText(mAddressState);
        String format[] = mPrayingTime.split(",");
        String op = "Morning  "+format[0]+"\n"+"Evening "+format[1];
        mPrayerTimeView.setText(op);
    }

    /**
     * Toggles the visibility of the progress bar. Enables or disables the Fetch Address button.
     */
    private void updateUIWidgets() {
        if (mAddressRequested) {
            mProgressBar.setVisibility(ProgressBar.VISIBLE);

        } else {
            mProgressBar.setVisibility(ProgressBar.GONE);
        }
    }



    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save whether the address has been requested.
        savedInstanceState.putBoolean(ADDRESS_REQUESTED_KEY, mAddressRequested);

        // Save the address string.
        savedInstanceState.putString(LOCATION_ADDRESS_KEY, myLocationDistrict);
        super.onSaveInstanceState(savedInstanceState);
    }





    /* class for recieving data from Location updater Service

    */
    public class mRecievrfromService extends  BroadcastReceiver{


        @Override
        public void onReceive(Context arg0, Intent arg1) {

            try{
                mAddressOutput = arg1.getStringExtra("District");
                mPrayingTime = arg1.getStringExtra("PrayingTime");
                mAddressState = arg1.getStringExtra("State");
                Log.e("LocationUpdaterService", "Broadcast recievr worked");
                displayAddressOutput();
            }
            catch(Exception e){
                mAddressOutput = null;
                mPrayingTime = null;
                mAddressState = null;
                Log.e("LocationUpdaterService" , "Broadcast reciever did not work");
            }



        }

    }



//Class inside a class******************************************************************************
    /**
     * Receiver for data sent from FetchAddressIntentService.
     */
    private class AddressResultReceiver extends ResultReceiver {
        AddressResultReceiver(Handler handler) {
            super(handler);
        }
        /**
         *  Receives data sent from FetchAddressIntentService and updates the UI in MainActivity.
         */
        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string or an error message sent from the intent service.

            mAddressOutput = resultData.getString(ConstantsForGeocoding.RESULT_DATA_KEY);
            Log.e(TAG+":::","val "+mAddressOutput);

            if(mAddressOutput==null){
                mLocationAddressTextView.setText("Error occured! Please try again");
                return;
            }
            fileReader.read1(c);
            fileReader.read2(c);

            mAddressState = fileReader.queryWithDistrict(mAddressOutput);
            Log.e(TAG,"val "+mAddressState);
            if(mAddressState==null){
                mLocationAddressTextView.setText("Error occured! Please try again");
                return;
            }

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String currentDate = simpleDateFormat.format(new Date());
            int month = Integer.parseInt(currentDate.split("-")[1]);
            Log.e(TAG,"val "+currentDate+" "+month);

            mPrayingTime = fileReader.queryWithState(mAddressState,month);
            if(mPrayingTime==null){
                mLocationAddressTextView.setText("Error occured! Please try again");
                return;
            }

            if (resultCode == ConstantsForGeocoding.SUCCESS_RESULT) {
                if(!mAddressOutput.equals(sharedPref.getDistName())){
                    Log.e("AlarmFuck", "District changed 2");
                    Log.e(TAG+"::::ss", "val "+sharedPref.getDistName()+" "+sharedPref.getPrayTime()+" "+sharedPref.getStateName());
                    sharedPref.SaveStateName(mAddressState);
                    sharedPref.SaveDistName(mAddressOutput);
                    sharedPref.SavePrayTime(mPrayingTime);

                    try {
                        alarmSetter.setAlarm(false);
                    } catch (ParseException e) {
                        Log.e("AlarmFuck", "error in alarmservice");
                        e.printStackTrace();
                    }

                    Log.e(TAG, "val "+"prference saved in mainactivity");
                }
                else{

                }
                Log.e(TAG, "val "+"Address Found In mainactivity");

            myLocationDistrict = resultData.getString(ConstantsForGeocoding.RESULT_DATA_KEY);

            Log.e(TAG+":::", myLocationDistrict);
            fileReader.read1(c);
            fileReader.read2(c);

           String statenameAsInDatabase = fileReader.queryWithDistrict(myLocationDistrict);

           if(myLocationDistrict!=null)
               mLocationAddressTextView.setText(myLocationDistrict);

           if(statenameAsInDatabase!=null && statenameAsInDatabase.length()!=0 ) {

               Log.e("state name=", String.valueOf(statenameAsInDatabase.length()));

               SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
               String currentDate = simpleDateFormat.format(new Date());
               int month = Integer.parseInt(currentDate.split("-")[1]);

               //Log.e("current  date=", currentDate + " " + month);
               String prayingTime = fileReader.queryWithState(statenameAsInDatabase, month);

               mStateName.setText(statenameAsInDatabase + " " + prayingTime);
           }
           else {
               mStateName.setText("No Satsang Mandir nearby");
           }
            // Show a toast message if an address was found.
            if (resultCode == ConstantsForGeocoding.SUCCESS_RESULT) {
                showToast(getString(R.string.address_found));
            }
            displayAddressOutput();
            // Reset. Enable the Fetch Address button and stop showing the progress bar.
            mAddressRequested = false;
            updateUIWidgets();
        }
    }










//Permission Methods********************************************************************************
    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "val "+"Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    REQUEST_PERMISSIONS_REQUEST_CODE);
                        }
                    });

        } else {
            Log.i(TAG, "val "+"Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "val "+"onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "val "+"User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getAddress();
            } else {

                showSnackbar(R.string.permission_denied_explanation, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }

    }
    public void loadGoogleMap(View view){
        String co_or = "geo:"+mLastLocation.getLatitude()+','+mLastLocation.getLongitude()+"?q=Satsang";
        Uri gmmIntentUri = Uri.parse(co_or);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);

        }
    }


    private void showSnackbar(final String text) {
        View container = findViewById(android.R.id.content);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }


    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


}
