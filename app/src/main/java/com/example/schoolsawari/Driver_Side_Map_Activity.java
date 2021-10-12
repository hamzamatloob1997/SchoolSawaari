package com.example.schoolsawari;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.schoolsawari.Map.FetchURL;
import com.example.schoolsawari.Map.TaskLoadedCallback;
import com.example.schoolsawari.helper.AttendanceModel;
import com.example.schoolsawari.helper.BookingModelClass;
import com.example.schoolsawari.helper.ModelLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Driver_Side_Map_Activity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {
    BookingModelClass bookingModelClass;
    private GoogleMap mMap;
    Double lat,lng;
    private LocationListener locationListener;
    private LocationManager locationManager;
    SupportMapFragment mapFragment;
    private final long MIN_TIME = 1000; // 1 second
    private final long MIN_DIST = 5; // 5 Meters

    private LatLng latLng;

    boolean startTracking=false;

    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;
    Double CurrLat, CurrLong;
    private MarkerOptions place1, place2;
    Button getDirection;
    private Polyline currentPolyline;
    Double ClinicLong,ClinicLat;
    DatabaseReference databaseReference ,attendanceReference,dateReference;
    FirebaseAuth firebaseAuth;
    ModelLocation currentLocation;
    String requestId;
    Dialog updateStatusDialog;
    RadioGroup status;
    Button update;
    String uid;
    AttendanceModel attendanceModel;
    String dId,pId,rId;
    String cDate,cTime;
    Calendar calendar;
    ArrayList<BookingModelClass> adsList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver__side__map_);

        updateStatusDialog = new Dialog(Driver_Side_Map_Activity.this, R.style.transparent_dialog);
        updateStatusDialog.setContentView(R.layout.update_status);
        updateStatusDialog.setCancelable(true);
        status = updateStatusDialog.findViewById(R.id.status);
        update = updateStatusDialog.findViewById(R.id.update);

       calendar = Calendar.getInstance();
       uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
       String currentDate = DateFormat.getDateInstance().format(calendar.getTime());
        attendanceModel = new AttendanceModel();
        dateReference = FirebaseDatabase.getInstance().getReference("Attendance");
        dateReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot did:snapshot.getChildren()){
                    if (uid.equals(did.getKey())) {
                        for (DataSnapshot date : did.getChildren()) {
                            if (currentDate.equals(date.getKey())) {
                                attendanceModel = date.getValue(AttendanceModel.class);
                                //Toast.makeText(Driver_Side_Map_Activity.this, "" + currentDate, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        SharedPreferences sharedPreferences=getSharedPreferences("CurrLoc",MODE_PRIVATE);
        CurrLat=Double.valueOf(sharedPreferences.getString("CurrLat",""));
        CurrLong=Double.valueOf(sharedPreferences.getString("CurrLong",""));

        //BookingModelClass bookingModelClass = getSavedObjectFromPreference(Driver_Side_Map_Activity.this,"booking","driver",BookingModelClass.class);
        String uid = FirebaseAuth.getInstance().getUid();
        bookingModelClass=(BookingModelClass) getIntent().getSerializableExtra("ad");
        lat = bookingModelClass.getPickupLocation().getLatitude();
        lng = bookingModelClass.getPickupLocation().getLongitude();
        requestId = bookingModelClass.getRequestId();
        ClinicLong=bookingModelClass.getDropOffLocation().getLongitude();
        ClinicLat=bookingModelClass.getDropOffLocation().getLatitude();
        dId = bookingModelClass.getDriverId();
        pId = bookingModelClass.getParentId();
        rId = bookingModelClass.getRequestId();




        place1 = new MarkerOptions().position(new LatLng(CurrLat, CurrLong)).title("Driver");
        place2 = new MarkerOptions().position(new LatLng(ClinicLat  , ClinicLong)).title("Destination");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);


        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                try {
                    if (startTracking){
                        try {
                            place1 = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Driver");
                            new FetchURL(Driver_Side_Map_Activity.this).execute(getUrl(place1.getPosition(), place2.getPosition(),"driving"), "driving");
                            databaseReference = FirebaseDatabase.getInstance().getReference("DriverLocation");
                            currentLocation = new ModelLocation();
                            currentLocation.setLatitude(location.getLatitude());
                            currentLocation.setLongitude(location.getLongitude());
                            databaseReference.child(requestId).setValue(currentLocation);
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(latLng).title("")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_driver));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                }
                catch (SecurityException e){
                    e.printStackTrace();
                }

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
        };
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        try {

            new FetchURL(this).execute(getUrl(place1.getPosition(), place2.getPosition(),"driving"), "driving");


        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DIST, locationListener);
        }
        catch (SecurityException e){
            e.printStackTrace();
        }






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
//        mMap = googleMap;
//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(lat, lng);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("")).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_current));
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,12.0f));


        int height = 100;
        int width = 100;
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.ic_bus);
        Bitmap b=bitmapdraw.getBitmap();
        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

        mMap = googleMap;
        Log.d("mylog", "Added Markers");
        mMap.addMarker(place1).setIcon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        mMap.addMarker(place2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place2.getPosition(),12.0f));

    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public void MoveOnBooking(View view) {
        Intent i = new Intent(Driver_Side_Map_Activity.this, Booking_Request_Activity.class);
        startActivity(i);
    }

    public void MoveOnProfile(View view) {
        Intent i = new Intent(Driver_Side_Map_Activity.this, Driver_Profile_Activity.class);
        startActivity(i);
    }
    public void MoveOnJobs(View view) {
        Intent i = new Intent(Driver_Side_Map_Activity.this,Driver_Jobs_Activity.class);
        startActivity(i);
    }
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        //parameters = "origin=27.658143,85.3199503&destination=27.667491,85.3208583&mode=driving";
        //"https://maps.googleapis.com/maps/api/directions/json?origin=27.658143,85.3199503&destination=27.667491,85.3208583&mode=driving&key=AIzaSyB9Fvl-byuMbmpJh17Af9B-TiSb37KSuw4
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.map_key);
        return url;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;


        return url;
    }
    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }
    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
        }
    }
    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }
    @SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    CurrLat =location.getLatitude();
                                    CurrLong =location.getLongitude();
                                    doStuff();
                                }
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }
    @SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }
    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            CurrLat =mLastLocation.getLatitude();
            CurrLong =mLastLocation.getLongitude();
            doStuff();
        }
    };
    public void doStuff(){
        //AIzaSyAHA6MDA7bqAS2Zh7H70spzB8hlpweu8nQ

        //Fakher Key
        //https://maps.googleapis.com/maps/api/directions/json?origin=30.667355,73.087326&destination=30.664611,73.084556&mode=driving&key=AIzaSyBE3ge-tsbW1BfxHC_uqWID1Zvs7xqkOkg

        //Ali Raza Key
        //https://maps.googleapis.com/maps/api/directions/json?origin=30.667355,73.087326&destination=30.664611,73.084556&mode=driving&key=AIzaSyAHA6MDA7bqAS2Zh7H70spzB8hlpweu8nQ


        //Nawaz Key
        //"https://maps.googleapis.com/maps/api/directions/json?origin=30.667355,73.087326&destination=30.664611,73.084556&mode=driving&key=AIzaSyB9Fvl-byuMbmpJh17Af9B-TiSb37KSuw4
        //  CurrLat=30.667355;
        // CurrLong=73.087326;
        try {
            databaseReference = FirebaseDatabase.getInstance().getReference("DriverLocation");
            currentLocation = new ModelLocation();
            currentLocation.setLatitude(CurrLat);
            currentLocation.setLongitude(CurrLong);
            databaseReference.child(requestId).setValue(currentLocation);
        }
        catch (Exception e){

        }
        if (ClinicLat==null || ClinicLong==null){
            Toast.makeText(this, "Can't get Doctor Location", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        place1 = new MarkerOptions().position(new LatLng(CurrLat, CurrLong)).title("Driver");
        place2 = new MarkerOptions().position(new LatLng(ClinicLat  , ClinicLong)).title("Destination");

        mapFragment.getMapAsync(this);
    }
    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }

    private void senNotification(String content) {
        String title=content;
        final String Legacy_SERVER_KEY = "AAAAWbhYjUM:APA91bFmHgvAxTetc1uCs_dxOOE4NpFYUrRGoNVPY0ca90YSIhTI0BEBqGhuoYFCOLtgZbzKbiYGcTTUpDpNp3-Kl6RS_M7MYaeiutXFgd6C3Crca6Y78DWT8K8NMVeRfy4ikKXQVAH0";
        JSONObject obj = null;
        JSONObject objData = null;
        JSONObject dataobjData = null;

        try {
            obj = new JSONObject();
            objData = new JSONObject();


            objData.put("title", title);
            objData.put("content", content);
//            objData.put("imageUrl","https://firebasestorage.googleapis.com/v0/b/admin-cricket-world-line.appspot.com/o/ic_launcher.png?alt=media&token=9c7d5eb5-007f-4091-8d36-d2a0dfcf7caa");
//            objData.put("gameUrl","https://firebasestorage.googleapis.com/v0/b/admin-cricket-world-line.appspot.com/o/ic_launcher.png?alt=media&token=9c7d5eb5-007f-4091-8d36-d2a0dfcf7caa");


            dataobjData = new JSONObject();
            dataobjData.put("text", content);
            dataobjData.put("title", title);
            dataobjData.put("content", content);
//            dataobjData.put("imageUrl","https://firebasestorage.googleapis.com/v0/b/admin-cricket-world-line.appspot.com/o/ic_launcher.png?alt=media&token=9c7d5eb5-007f-4091-8d36-d2a0dfcf7caa");
//            dataobjData.put("gameUrl","https://firebasestorage.googleapis.com/v0/b/admin-cricket-world-line.appspot.com/o/ic_launcher.png?alt=media&token=9c7d5eb5-007f-4091-8d36-d2a0dfcf7caa");


            obj.put("to", "/topics/all");
            obj.put("priority", "high");

            obj.put("notification", objData);
            obj.put("data", dataobjData);
            Log.e("!@rj@@@_PASS:>", obj.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.POST, "https://fcm.googleapis.com/fcm/send", obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("!_@@_SUCESS", response + "");
                        Toast.makeText(Driver_Side_Map_Activity.this, "Message sent"+response.toString(), Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("!_@@_Errors--", error + "");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "key=" + Legacy_SERVER_KEY);
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        int socketTimeout = 1000 * 60;// 60 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        jsObjRequest.setRetryPolicy(policy);
        requestQueue.add(jsObjRequest);


    }


    public void UpdateStatus(View view) {
        updateStatusDialog.show();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //rId = UUID.randomUUID().toString();

                    int selectedRadioButtonId = status.getCheckedRadioButtonId();
                    if (selectedRadioButtonId == -1) {
                        Toast.makeText(Driver_Side_Map_Activity.this, "Select an Option", Toast.LENGTH_SHORT).show();

                        return;
                    }else if (selectedRadioButtonId == R.id.PFH){
                        startTracking=true;
                        senNotification("Your child has been picked from home.");
                        cTime= new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                        attendanceModel.setPickHomeTime(cTime);
                        updateStatusDialog.dismiss();
                        try {
                            String url = getDirectionsUrl(new LatLng(lat,lng), new LatLng(bookingModelClass.getDropOffLocation().getLatitude(),bookingModelClass.getDropOffLocation().getLongitude()));


//                    DownloadTask downloadTask = new DownloadTask();
//
//                    Log.d("URL",url.toLowerCase());
//
//                    new  AlertDialog.Builder(Driver_Side_Map_Activity.this)
//                            .setMessage(url)
//                            .setCancelable(false)
//                            .show();
//                    // Start downloading json data from Google Directions API
                            //  downloadTask.execute(url);
                        }catch (Exception e){
//                   new  AlertDialog.Builder(Driver_Side_Map_Activity.this)
//                           .setMessage(e.getMessage())
//                           .setCancelable(false)
//                           .show();
                        }
                        // Getting URL to the Google Directions API



                        updateStatusDialog.dismiss();
                    }
                    else if (selectedRadioButtonId == R.id.DOS){
                        senNotification("Your child has been dropped at school.");
                        cTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                        attendanceModel.setDropSchoolTime(cTime);

                        updateStatusDialog.dismiss();
                    }
                    else if (selectedRadioButtonId == R.id.PUFS){
                        senNotification("Your has been picked from the School.");
                        cTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                        attendanceModel.setPickSchoolTime(cTime);

                        updateStatusDialog.dismiss();
                    }
                    else if (selectedRadioButtonId == R.id.DOH){
                        senNotification("Your chaild has been dropped at home.");
                       cTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
                        attendanceModel.setDropHomeTime(cTime);
                        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
                        String startingDate;
                        String days;
                        days = bookingModelClass.getDays();
                        startingDate = bookingModelClass.getDate().toString();
                        Calendar now=Calendar.getInstance();
                        try {
                            Date sDate=new SimpleDateFormat("dd/MM/yyyy").parse(startingDate);

                            now.setTime(sDate);
                            now.add(Calendar.DAY_OF_MONTH,Integer.parseInt(bookingModelClass.getDays()));

                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (currentDate.equalsIgnoreCase( new SimpleDateFormat("dd/MM/yyyy").format(now.getTime()))){

                            DatabaseReference dR = FirebaseDatabase.getInstance().getReference("Booking");
                            dR.child(bookingModelClass.getRequestId()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(v.getContext(), "Your Days are completed now!", Toast.LENGTH_LONG).show();
                                    }else {
                                        Toast.makeText(v.getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }



                        updateStatusDialog.dismiss();
                    }
                    else {

                        updateStatusDialog.dismiss();

                    }

                    cDate = DateFormat.getDateInstance().format(calendar.getTime());

                    attendanceReference = FirebaseDatabase.getInstance().getReference("Attendance");

                    attendanceModel.setDriverId(dId);
                    attendanceModel.setParentId(pId);
                    attendanceModel.setDate(cDate);
                    attendanceReference.child(dId).child(cDate.toString()).setValue(attendanceModel);
                }
                catch (Exception e){
                    Toast.makeText(Driver_Side_Map_Activity.this, "error"+e, Toast.LENGTH_LONG).show();
                    Log.i("Error",e.getMessage());
                }
            }
        });
    }
}