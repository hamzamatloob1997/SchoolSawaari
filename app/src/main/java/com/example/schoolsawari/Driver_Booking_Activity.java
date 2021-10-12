package com.example.schoolsawari;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.schoolsawari.helper.BookingModelClass;
import com.example.schoolsawari.helper.DriverModelClass;
import com.example.schoolsawari.helper.ModelLocation;
import com.example.schoolsawari.helper.SingleShotLocationProvider;
import com.google.android.libraries.places.api.Places;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.schoolsawari.StoryBoarding.MY_PERMISSIONS_REQUEST_LOCATION;


public class Driver_Booking_Activity extends AppCompatActivity {
    DriverModelClass driverModelClass;
    CircleImageView image;
    TextView name, number;
    String dbName, dbNumber, driverId;
    EditText noOfDays, pickUp, dropOff;
    ModelLocation location;
    BookingModelClass bookingModelClass;
    int PICK_UP_REQUEST = 111;
    int DROP_OFF_REQUEST = 112;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    Button sendRequest;
    ProgressDialog progressDialog;


    Location loc;
    LocationManager locationManager;
    SingleShotLocationProvider.GPSCoordinates mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver__booking_);
        name = findViewById(R.id.name);
        number = findViewById(R.id.number);
        image = findViewById(R.id.image);
        noOfDays = findViewById(R.id.noOfDays);
        pickUp = findViewById(R.id.pickUp);
        dropOff = findViewById(R.id.dropOff);
        sendRequest = findViewById(R.id.sendRequest);

        Places.initialize(getApplicationContext(),"AIzaSyBj6vRMayrF07Lt4YKjPBGrEJbxWCLiDuI");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCancelable(false);

        driverModelClass = (DriverModelClass) getIntent().getSerializableExtra("driver");
        Picasso.get().load(driverModelClass.getProfileImageUrl()).into(image);
        //Toast.makeText(this, ""+driverModelClass, Toast.LENGTH_SHORT).show();
        dbName = driverModelClass.getName();
        dbNumber = driverModelClass.getNumber();
        driverId = driverModelClass.getId();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);

        }
        locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        SingleShotLocationProvider.requestSingleUpdate(this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.d("Location", "my location is " + location.toString());
                        mLocation=location;
                    }
                });

        name.setText(dbName);
        number.setText(dbNumber);


        bookingModelClass = new BookingModelClass();
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Booking");
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                updateUI(user);
                String Id = user.getUid();
                if (TextUtils.isEmpty(pickUp.getText().toString())){
                    Toast.makeText(Driver_Booking_Activity.this, "Select Pick Up Location", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(dropOff.getText().toString())){
                    Toast.makeText(Driver_Booking_Activity.this, "Select Drop off Location", Toast.LENGTH_SHORT).show();
                }
                else if (TextUtils.isEmpty(noOfDays.getText().toString())){
                    Toast.makeText(Driver_Booking_Activity.this, "Enter No of Kids", Toast.LENGTH_SHORT).show();
                }
                else {
                    String requestId = UUID.randomUUID().toString();
                    //ParentModelClass parentModelClass = getSavedObjectFromPreference(Driver_Booking_Activity.this,"profile","parent",ParentModelClass.class);

                    progressDialog.show();
                    bookingModelClass.setParentId(Id);
                    bookingModelClass.setDriverId(driverId);
                    bookingModelClass.setDays(noOfDays.getText().toString());
                    bookingModelClass.setAccepted(false);
                    bookingModelClass.setRequestId(requestId);
                    databaseReference.child(requestId).setValue(bookingModelClass);
                    progressDialog.dismiss();
                    Toast.makeText(Driver_Booking_Activity.this, "Send Request Successfully", Toast.LENGTH_SHORT).show();
                    pickUp.setText(null);
                    dropOff.setText(null);
                    noOfDays.setText(null);
                }

            }
        });

    }

    private void updateUI(FirebaseUser user) {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101) {

        }

        if (requestCode == PICK_UP_REQUEST) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    // String address = data.getStringExtra(MapUtility.ADDRESS);
                    double currentLatitude = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    double currentLongitude = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    Bundle completeAddress =data.getBundleExtra("fullAddress");
                    pickUp.setText(new StringBuilder().append("addressline2: ").append
                            (completeAddress.getString("addressline2")).toString());
                    location =new ModelLocation();
                    location.setLongitude(currentLongitude);
                    location.setLatitude(currentLatitude);
                    bookingModelClass.setPickupLocation(location);
                    /* data in completeAddress bundle
                    "fulladdress"
                    "city"
                    "state"
                    "postalcode"
                    "country"
                    "addressline1"
                    "addressline2"
                     */
                    // txtAddress.setText();

                    Log.d("Location",new StringBuilder().append("addressline2: ").append
                            (completeAddress.getString("addressline2")).append("\ncity: ").append
                            (completeAddress.getString("city")).append("\npostalcode: ").append
                            (completeAddress.getString("postalcode")).append("\nstate: ").append
                            (completeAddress.getString("state")).toString());

                    // txtLatLong.setText();
                    Log.d("LatLong",new StringBuilder().append("Lat:").append(currentLatitude).append
                            ("  Long:").append(currentLongitude).toString());

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else if (requestCode == DROP_OFF_REQUEST){

            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    // String address = data.getStringExtra(MapUtility.ADDRESS);
                    double currentLatitude = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    double currentLongitude = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    Bundle completeAddress =data.getBundleExtra("fullAddress");
                    dropOff.setText(new StringBuilder().append("addressline2: ").append
                            (completeAddress.getString("addressline2")).toString());
                    location =new ModelLocation();
                    location.setLongitude(currentLongitude);
                    location.setLatitude(currentLatitude);
                    bookingModelClass.setDropOffLocation(location);
                    /* data in completeAddress bundle
                    "fulladdress"
                    "city"
                    "state"
                    "postalcode"
                    "country"
                    "addressline1"
                    "addressline2"
                     */
                    // txtAddress.setText();

                    Log.d("Location",new StringBuilder().append("addressline2: ").append
                            (completeAddress.getString("addressline2")).append("\ncity: ").append
                            (completeAddress.getString("city")).append("\npostalcode: ").append
                            (completeAddress.getString("postalcode")).append("\nstate: ").append
                            (completeAddress.getString("state")).toString());

                    // txtLatLong.setText();
                    Log.d("LatLong",new StringBuilder().append("Lat:").append(currentLatitude).append
                            ("  Long:").append(currentLongitude).toString());

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
//            if (resultCode == Activity.RESULT_OK && data != null) {
//                AddressData addressData = data.getParcelableExtra(Constants.ADDRESS_INTENT);
//                location=new ModelLocation();
//                dropOff.setText(addressData.toString());
//                location.setLatitude(addressData.getLatitude());
//                location.setLongitude(addressData.getLongitude());
//                bookingModelClass.setDropOffLocation(location);
//            }
        }
    }

    public void selecLocation(View view) {

        Intent i = new Intent(this, LocationPickerActivity.class);
        startActivityForResult(i, PICK_UP_REQUEST);



//        Intent intent;
//        if (mLocation!=null){
//            intent = new PlacePicker.IntentBuilder()
//                    .setLatLong(mLocation.latitude, mLocation.longitude)  // Initial Latitude and Longitude the Map will load into
//                    .showLatLong(true)  // Show Coordinates in the Activity
//                    .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
//                    .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
//                    .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
//                    //.setMarkerDrawable(R.drawable.marker) // Change the default Marker Image
//                    .setMarkerImageImageColor(R.color.colorPrimary)
//                    .setFabColor(R.color.colorPrimary)
//                    .setPrimaryTextColor(android.R.color.white) // Change text color of Shortened Address
//                    .setSecondaryTextColor(android.R.color.darker_gray) // Change text color of full Address
//                    // .setBottomViewColor(R.color.bottomViewColor) // Change Address View Background Color (Default: White)
//                    .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
//                    .setMapType(MapType.NORMAL)
//                    //.setPlaceSearchBar(true, GOOGLE_API_KEY) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
//                    .onlyCoordinates(true)  //Get only Coordinates from Place Picker
//                    .build(this);
//
//        }
    }

    public void dropOffLocationSelect(View view) {

        Intent i = new Intent(this, LocationPickerActivity.class);
        startActivityForResult(i, DROP_OFF_REQUEST);


//        Intent intent = new PlacePicker.IntentBuilder()
//                .setLatLong(30.6682, 73.1114)  // Initial Latitude and Longitude the Map will load into
//                .showLatLong(true)  // Show Coordinates in the Activity
//                .setMapZoom(12.0f)  // Map Zoom Level. Default: 14.0
//                .setAddressRequired(true) // Set If return only Coordinates if cannot fetch Address for the coordinates. Default: True
//                .hideMarkerShadow(true) // Hides the shadow under the map marker. Default: False
//                //.setMarkerDrawable(R.drawable.marker) // Change the default Marker Image
//                .setMarkerImageImageColor(R.color.colorPrimary)
//                .setFabColor(R.color.colorPrimary)
//                .setPrimaryTextColor(android.R.color.white) // Change text color of Shortened Address
//                .setSecondaryTextColor(android.R.color.darker_gray) // Change text color of full Address
//                // .setBottomViewColor(R.color.bottomViewColor) // Change Address View Background Color (Default: White)
//                .setMapRawResourceStyle(R.raw.map_style)  //Set Map Style (https://mapstyle.withgoogle.com/)
//                .setMapType(MapType.NORMAL)
//                //.setPlaceSearchBar(true, GOOGLE_API_KEY) //Activate GooglePlace Search Bar. Default is false/not activated. SearchBar is a chargeable feature by Google
//                .onlyCoordinates(true)  //Get only Coordinates from Place Picker
//                .build(this);
//
//        startActivityForResult(intent, DROP_OFF_REQUEST);
    }
    public static <GenericClass> GenericClass getSavedObjectFromPreference(Context context, String preferenceFileName, String preferenceKey, Class<GenericClass> classType) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceFileName, 0);
        if (sharedPreferences.contains(preferenceKey)) {
            final Gson gson = new Gson();
            return gson.fromJson(sharedPreferences.getString(preferenceKey, ""), classType);
        }
        return null;
    }


}