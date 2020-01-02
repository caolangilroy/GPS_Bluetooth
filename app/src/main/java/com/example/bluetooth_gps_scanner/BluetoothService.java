package com.example.bluetooth_gps_scanner;
//Above is the package
// below android libraries are important.
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static androidx.core.app.ActivityCompat.startActivityForResult;

//Creating the bluetooth service class. It extends Service to give Service functionality.
public class BluetoothService extends Service {
    private final String TAG = BluetoothService.class.getSimpleName();
    private FirebaseDatabase database = FirebaseDatabase.getInstance();// creating a database object.
    private DatabaseReference myRef = database.getReference("locations");//getting a reference from the database

    public static boolean handlerNotify = false;
    private ArrayList<String> devList= new ArrayList<String>();
    private ArrayList<String> fullDevList= new ArrayList<String>();



    final Runnable r = new Runnable() {
        @Override
        public void run() {
            handlerNotify = false;
            devList.clear();

        }
    };




    /**
     * FOLLOWING LINES ARE FOR BINDING SERVICE TO
     * AN ACTIVITY
     */
    public class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }
    // Creating a binder object.
    private final IBinder mBinder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent)
    {
        return mBinder;
    }
    //////////////////Unsure how this works. Its a function to get the location listener and it returns the location listener. where is this being created?
    public LocationListener getLocationListener() {
        return locationListener;

    }





    //Here the location listener method is created.
    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location)
        {
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            String key =  myRef.push().getKey();
            LocationData locationData = new LocationData(latLng.latitude, latLng.longitude);
            //myRef.child(key).setValue(locationData);
            Log.i("PRP","Location Changed");

            handlerNotify = true;
            scanDevices(BluetoothService.this);
            r.run();


        }


        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
        public void scanDevices(Context context){

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            context.registerReceiver(mReceiver, filter);
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(handlerNotify==true){
                bluetoothAdapter.startDiscovery();
            }
            else{
                //dont scan
                System.out.println("not scanning");
            }
        }
        };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                devList.add(deviceName);
                fullDevList.add(deviceName);

                Log.i("Device Name: " , "device " + deviceName);
                Log.i("deviceHardwareAddress " , "hard"  + deviceHardwareAddress);
            }
        }
    };


}
