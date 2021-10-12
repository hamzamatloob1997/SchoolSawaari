package com.example.schoolsawari;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.schoolsawari.Map.FetchURL;
import com.example.schoolsawari.Map.TaskLoadedCallback;
import com.example.schoolsawari.helper.BookingModelClass;
import com.example.schoolsawari.helper.ModelLocation;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Tracking_Activity extends FragmentActivity implements OnMapReadyCallback, TaskLoadedCallback {
    BookingModelClass bookingModelClass;
    private GoogleMap mMap;
    LinearLayout layout1;
    Double lat,lng;
    TextView pickUpHome,dropOffHome,pickUpSchool,dropOffSchool;
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
    DatabaseReference databaseReference;
    String requestId;
    ModelLocation driverLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_);



//        SharedPreferences sharedPreferences=getSharedPreferences("CurrLoc",MODE_PRIVATE);
//        CurrLat=Double.valueOf(sharedPreferences.getString("CurrLat",""));
//        CurrLong=Double.valueOf(sharedPreferences.getString("CurrLong",""));



        //BookingModelClass bookingModelClass = getSavedObjectFromPreference(Tracking_Activity.this,"booking","driver",BookingModelClass.class);
        String uid = FirebaseAuth.getInstance().getUid();
        bookingModelClass=(BookingModelClass) getIntent().getSerializableExtra("ad");
        lat = bookingModelClass.getPickupLocation().getLatitude();
        lng = bookingModelClass.getPickupLocation().getLongitude();
        requestId= bookingModelClass.getRequestId();

        databaseReference = FirebaseDatabase.getInstance().getReference("DriverLocation");
        databaseReference.child(requestId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    driverLocation = new ModelLocation();
                    driverLocation=snapshot.getValue(ModelLocation.class);
                    place1 = new MarkerOptions().position(new LatLng(driverLocation.getLatitude(), driverLocation.getLongitude())).title("Driver");
                    new FetchURL(Tracking_Activity.this).execute(getUrl(place1.getPosition(), place2.getPosition(),"driving"), "driving");

                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ClinicLong=bookingModelClass.getDropOffLocation().getLongitude();
        ClinicLat=bookingModelClass.getDropOffLocation().getLatitude();

        place1 = new MarkerOptions().position(new LatLng(lat, lng)).title("Driver");
        place2 = new MarkerOptions().position(new LatLng(ClinicLat  , ClinicLong)).title("Destination");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);



        try {

            new FetchURL(this).execute(getUrl(place1.getPosition(), place2.getPosition(),"driving"), "driving");


        }catch (Exception e){
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

    public void doStuff(){
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


    }
}