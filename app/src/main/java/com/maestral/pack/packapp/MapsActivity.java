package com.maestral.pack.packapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.RelativeLayout;

import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maestral.pack.packapp.API.PackApi;
import com.maestral.pack.packapp.models.Member;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private BluetoothAdapter mBtAdapter;
    private String beacon1Address = "EC:11:27:2A:56:B3";
    private String beacon2Address = "EC:11:27:29:B5:63";
    private boolean scanRunning;
    private int mInterval = 1000;
    private int secondsPassed = 0;
    private Handler mHandler;
    private Handler mGroupsUpdateHandler;
    private List<String> visibleDevices = new ArrayList<>();
    private boolean beaconOneModelShown;
    private static final int MY_LOCATION_PERMISSION = 1;
    private static final String TAG = "PackApp";
    private GoogleMap mMap;
    private GoogleApiClient mGoogleClient;

    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    private PackApi mAPI;

    private List<Member> mRetreivedMembers;

    Runnable mGroupsUpdater = new Runnable() {
        @Override
        public void run() {
            try {
                Log.v(TAG, "MapsActivity:::::::::::::::::::::::::::::::: Polling group data");
                Call<List<Member>> getMembersCall = mAPI.getMembers();
                getMembersCall.enqueue(new Callback<List<Member>>() {
                    @Override
                    public void onResponse(Call<List<Member>> call, Response<List<Member>> response) {
                        Log.v(TAG, "MapsActivity:::::::::::::::::::: Get Members response" + response.body());
                        parseMembers(response.body());
                    }

                    @Override
                    public void onFailure(Call<List<Member>> call, Throwable t) {
                        Log.e(TAG, t.toString());
                    }
                });
            }
            catch (Exception e){
                Log.e(TAG, e.toString());
            }
            finally {
                mGroupsUpdateHandler.postDelayed(mGroupsUpdater, 5000);
            }
        }
    };


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                System.out.println("repeating task");
//                if (!scanRunning) {
                visibleDevices.clear();
                secondsPassed = 0;
                doBeaconDiscovery();
                System.out.println("scanning will be run now");
//                }
            } catch (Exception e) {
                Log.v(TAG, e.getMessage());
            } finally {
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    private boolean mPanicActive = false;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            boolean newDeviceFound = BluetoothDevice.ACTION_FOUND.equals(action);
//            boolean discoveryFinished = BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action);
            if (newDeviceFound) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                try {
                    checkIfBeaconTwo(device);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
//            if (discoveryFinished) {
//                finishDiscovery();
//            }
        }

        private void checkIfBeaconTwo(BluetoothDevice device) throws IOException {
            if (device.getAddress().equals(beacon2Address)) {
                mPanicActive = true;
                Call<String> updatePanicCall = mAPI.updatePanic(true, "irfanka");

                updatePanicCall.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Log.v(TAG, "Panic Alert Successful");
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable throwable) {
                        Log.v(TAG, "Panic Alert Not Successful");
                    }
                });
            }
        }
    };

//    private void finishDiscovery() {
//        System.out.println("discovery finished");
//        boolean beaconOneFound = false;
//        boolean beaconTwoFound = false;
//
//        for(String detectedDeviceAddress : visibleDevices){
//            if(detectedDeviceAddress.equals(beacon1Address)){
//                beaconOneFound = true;
//            }
//            if(detectedDeviceAddress.equals(beacon2Address)){
//                beaconTwoFound = true;
//            }
//        }
//        scanRunning = false;
//    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mHandler = new Handler();
        if (mGoogleClient == null) {
            mGoogleClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }



        streamLocation();


        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();

        if (!mBtAdapter.isEnabled()) {
            if (!mBtAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }

        streamLocation();
        setUpBluetoothEvents();
        initDiscoveryService();


        mAPI = PackApi.retrofit.create(PackApi.class);

        double[] location = new double[]{0.0, 0.0, 0.0};

        Member newMember = new Member("irfanka", "Irfan", "Kahvedzic", Boolean.TRUE, location);

        Call<Member> createMemberCall = mAPI.createMember(newMember);


        mGroupsUpdateHandler = new Handler();

        startPollingGroups();


        try {
            Log.v(TAG, "MapsActivity::::::::::::::::::::::::::::::::::::::: Making a POST request");
            createMemberCall.enqueue(new Callback<Member>() {
                @Override
                public void onResponse(Call<Member> call, Response<Member> response) {
                    Log.v(TAG, "MapsActivity::::::::::::::::::::::::::::::: Response: " + response.raw());
                }

                @Override
                public void onFailure(Call<Member> call, Throwable t) {
                    Log.e(TAG, "Error");
                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }


    }

    private void startPollingGroups() {
        mGroupsUpdater.run();
    }


    private void parseMembers(List<Member> members) {
        if (members == null) return;
        mRetreivedMembers = members;


        RelativeLayout root = (RelativeLayout) findViewById(R.id.maps_root_layout);

        for (int i = 0; i < members.size(); i++) {
            CircularImageView memberAvatar = new CircularImageView(this);
            memberAvatar.setId(i + 1);
            int avatarResourceId = getResources().getIdentifier("avatar_" + (i + 1), "drawable", getPackageName());
            memberAvatar.setImageResource(avatarResourceId);
            root.addView(memberAvatar);

            memberAvatar.getLayoutParams().height = 100;
            memberAvatar.getLayoutParams().width = 100;


            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) memberAvatar.getLayoutParams();

            if (i > 0) {
                params.addRule(RelativeLayout.BELOW, i - 1);
            }

            params.setMargins(60, 40, 0, 0);


        }


    }


    @Override
    protected void onStart() {
        mGoogleClient.connect();
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopRepeatingTask();
    }

    private void initDiscoveryService() {
        startRepeatingTask();
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    @Override
    protected void onStop() {
        mGoogleClient.disconnect();
        super.onStop();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_LOCATION_PERMISSION);
        } else {
            mMap.setMyLocationEnabled(true);
        }

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

    }

    public void clicked_settings(View view) {
        Intent settingsScreen = new Intent(MapsActivity.this, SettingsActivity.class);
        startActivity(settingsScreen);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_LOCATION_PERMISSION: {
                //If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        mMap.setMyLocationEnabled(true);
                    } catch (SecurityException e) {
                        // This is just to get rid of compiler warnings
                    }
                    streamLocation();
                    Log.d(TAG, "::::::::::::::::::::::::::::::::::::::::::::::::Permission granted");
                } else {
                    Log.d(TAG, "::::::::::::::::::::::::::::::::::::::::::::::::Permission denied");
                }
            }
        }
    }

    //==============================================================================================
    // Sending GPS location to the API
    //==============================================================================================

    private void streamLocation() {
        Log.v(TAG, "MapsActivity:::::::::::::::::::::::::::::::::::Setting up location streaming");
        createLocationRequest();
        getCurrentLocationSettings();
    }

    private void createLocationRequest() {
        Log.v(TAG, "MapsActivity:::::::::::::::::::::::::::::::::::Making a LocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    private void getCurrentLocationSettings() {
        // Get current location settings
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        // Check whether the current location settings are satisfied (GPS enabled)
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleClient, builder.build());


        // Prompt the user to change location settings (if needed)
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                final LocationSettingsStates states = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // GPS is already ON
                        Log.v(TAG, "MapsActivity:::::::::::::::::::::: GPS is ON");
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed by showing
                        // the user a dialog.
                        // status.startResolutionForResult(OuterClass.this, REQUEST_CHECK_SETTINGS);
                        Log.e(TAG, "MapsActivity:::::::::::::::::::::: GPS is OFF");
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG, "MapsActivity:::::::::::::::::::::: GPS is OFF and cannot be turned ON");
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.v(TAG, "MapsActivity:::::::::::::::::::::::::::::::::Google API client onConnected");
        Log.v(TAG, "MapsActivity:::::::::::::::::::::::::::::::::Getting the last known GPS location");
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleClient);
            if (mLastLocation != null) {
                String lat = String.valueOf(mLastLocation.getLatitude());
                String lng = String.valueOf(mLastLocation.getLongitude());
                Log.v(TAG, "MapsActivity::::::::::::::::::Last known location: " + lat + " " + lng);

                // Initiate location streaming
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleClient, mLocationRequest, this);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Security Exception getting the last known location:" + e.getMessage());
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "MapsActivity:::::::::::::::::::::::::::::::::Google API client onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v(TAG, "MapsActivity:::::::::::::::::::::::::::::::::Google API client onConnectionFailed");
    }


    @Override
    public void onLocationChanged(Location location) {
        String loc = String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude());
        Log.v(TAG, "MapsActivity:::::::::::::::::::::::::::::::: Location update: " + loc);
        double[] locArray = new double[]{
                location.getLatitude(),
                location.getLongitude(),
                location.getAltitude(),
                location.getSpeed(),
                location.getBearing(),
                location.getAccuracy(),
                location.getTime()
        };
        try {
            Self.getInstance().member.geoLocation = locArray;
            Call<String> updateLocationCall = mAPI.updateLocation(locArray, Self.getInstance().member.userName);

            updateLocationCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    Log.v(TAG, "MapsActivity::::::::::::::::::::::: UpdateLocation response: " + response.headers() + " " + response.body());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG, "Error: " + t.toString());

                }
            });
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void setUpBluetoothEvents() {
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    private void doBeaconDiscovery() {
        mBtAdapter.startDiscovery();
        scanRunning = true;
    }

}
