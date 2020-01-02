package com.example.bluetooth_gps_scanner;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetooth_gps_scanner.LocationData;
import com.example.bluetooth_gps_scanner.R;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

//Creating a Device List Activity class which extends AppCompatActivity
public class DeviceListActivity extends AppCompatActivity
{
    private final String TAG = DeviceListActivity.class.getSimpleName();//tag created for the class.
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference myRef = database.getReference("locations");
    private ListView listView;//creates a listview variable.
    private DeviceListAdapter deviceListAdapter;//creates a device list adaptor.

    //an onCreate method is created
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//calls this method from the parent class
        setContentView(R.layout.device_list_view);//sets content view.
        listView = findViewById(R.id.deviceListView);//sets List view (device list view)
        deviceListAdapter = new DeviceListAdapter(this, 0);//device listener variable is set to this new object.
        listView.setAdapter(deviceListAdapter);//this sets device listener adaptor as the adaptor within the listview object.

        //this is reading for events on the screen within the listview.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

//        myRef.addValueEventListener(valueEventListener);
        myRef.addChildEventListener(childEventListener);
    }

    //Creating a Device Listener adaptor class which is used above^, It extends an array adapter.
    public class DeviceListAdapter extends ArrayAdapter<LocationData>
    {

        //Creates an Array List of type Location data and naming it Location Devices.
        private ArrayList<LocationData> locationDevices;
        private Context context; //Creates a context object

        //Creates a device list adaptor method which takes in the context set above, and an integer called resourceInt
        public DeviceListAdapter(Context context, int resourceInt) {

            super(context, resourceInt);//Calls the parent class with the same parameters
            this.context = context;
            locationDevices = new ArrayList<>();//creating an empty and unconfigured arraylist(ie the type contents of the list havnt been declared yet)
        }

        //adds location to location List and then notification sent.
        @Override
        public void add(LocationData location) {
            locationDevices.add(location);
            notifyDataSetChanged();
        }

        //Get data method returns data at desired position.
        @Override
        public LocationData getItem(int position) {
            return locationDevices.get(position);
        }

        //this method counts the number of entries in the list via .size().
        @Override
        public int getCount() {
            return locationDevices.size();
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;//this creates a view object

            if(view == null)
            {
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                view = inflater.inflate(R.layout.device_list_entry, null);
                ViewHolder viewHolder = new ViewHolder(view);
                view.setTag(viewHolder);
            }

            LocationData device = locationDevices.get(position);//creating a location data object from the data in the array list at requested position
            ViewHolder holder = (ViewHolder)view.getTag();//Creating a viewHolder object which casts the view.getTag to viewHolder type. - this is defined below.
            holder.deviceName.setText(device.deviceName);//functions are defined below.
            holder.addressView.setText(device.deviceAddress);
            holder.deviceType.setText(""+device.deviceType);

            if(device.deviceType == 1)
            {
                holder.imageView.setImageDrawable(getDrawable(R.drawable.ic_heartbeat));
            }
            else
            {
                holder.imageView.setImageDrawable(getDrawable(R.drawable.ic_laptop_windows_black_24dp));
            }
            return view;
        }
    }

    public class ViewHolder
    {
        public TextView deviceName;
        public TextView addressView;
        public TextView deviceType;
        public ImageView imageView;


        public ViewHolder(View view)
        {
            deviceName = view.findViewById(R.id.deviceName);
            addressView = view.findViewById(R.id.macAddress);
            deviceType = view.findViewById(R.id.deviceType);
            imageView = view.findViewById(R.id.imageView);
        }
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
        {
        }
    };

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s)
        {
            LocationData locationEntry = dataSnapshot.getValue(LocationData.class);
            if (locationEntry.deviceName != null)
            {
                deviceListAdapter.add(locationEntry);
            }
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }

    };
}