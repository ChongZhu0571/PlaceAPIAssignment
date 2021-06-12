package com.chong.placesdkassessment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mancj.materialsearchbar.MaterialSearchBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GoogleMap.OnCameraIdleListener,
        OnMapReadyCallback{
    PlacesClient placesClient;
    ImageButton btn_search;
    ImageButton btn_myLocation;
    AutocompleteSupportFragment autocompleteFragment;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient mapClient;
    GoogleMap googleMap;
    CardView cardView;
    RecyclerView recyclerView;
    Switch btn_switch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_search = findViewById(R.id.btn_search);
        btn_myLocation = findViewById(R.id.btn_myLocation);
        cardView = findViewById(R.id.view_cardView);
        recyclerView = findViewById(R.id.view_recyclerView);
        btn_switch = findViewById(R.id.swh_outcome);
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(this);
        mapClient = LocationServices.getFusedLocationProviderClient(this);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                
            }
        });
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_autocomplete);
        autocompleteFragment.getView().setVisibility(View.GONE);
        if(!Places.isInitialized()){
            Places.initialize(getApplicationContext(), getString(R.string.place_api));
        }
        placesClient = Places.createClient(this);
        myLocation_clicked(btn_search);
       // searchBar = findViewById(R.id.searchBar);

    }

    private void getCurrentLocation() {
        @SuppressLint("MissingPermission")
        Task<Location> task = mapClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!= null){
                    //Sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(@NonNull GoogleMap googleMap) {
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            MarkerOptions options = new MarkerOptions().position(latLng).title("You're here");
                            //Zoom map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,12));
                            googleMap.clear();
                            googleMap.addMarker(options);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }
        }
    }

    public void search_clicked(View view) {
        autocompleteFragment.getView().setVisibility(View.VISIBLE);
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME));
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                final LatLng latLng = place.getLatLng();
                //MarkerOptions options = new MarkerOptions().position(latLng).title(String.valueOf(Place.Field.NAME));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,13));
                googleMap.clear();
                getSearchInfoByMap(String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));
                getSearchInfoByTable(String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude));
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
    }
    public  void getSearchInfoByMap(String location){
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location+"&radius=1500&type=restaurant&key="+getString(R.string.place_api);
        Log.e("url",url);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray results = response.getJSONArray("results");
                    for(int i = 0;i<results.length();i++){
                        double lat = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                        double lng = results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                        Log.e("lat: ",String.valueOf(lat));
                        LatLng latLng = new LatLng(lat,lng);
                        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
                        googleMap.addMarker(markerOptions);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("response: ",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    public  void getSearchInfoByTable(String location){
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location+"&radius=1500&type=restaurant&key="+getString(R.string.place_api);
        Log.e("url",url);
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    List<String> names = new ArrayList<>();
                    List<String> statuses = new ArrayList<>();
                    List<String> addresses = new ArrayList<>();
                    List<String> images_refs = new ArrayList<>();
                    JSONArray results = response.getJSONArray("results");
                    for(int i = 0;i<results.length();i++){
                        String name = results.getJSONObject(i).getString("name");
                        boolean status = results.getJSONObject(i).getJSONObject("opening_hours").getBoolean("open_now");
                        String sts = "Open Now: No";
                        if(status){
                            sts = "Open Now: Yes";
                        }
                        String address = results.getJSONObject(i).getString("vicinity");
                        JSONArray photos = results.getJSONObject(i).getJSONArray("photos");
                        String image_ref = photos.getJSONObject(0).getString("photo_reference");
                        images_refs.add(image_ref);
                        names.add(name);
                        statuses.add(sts);
                        addresses.add(address);
                 //       String imageUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference="+image_ref+"&key="+getString(R.string.place_api);
                        MyAdaptor myAdaptor = new MyAdaptor(names,statuses,addresses);
                        recyclerView.setAdapter(myAdaptor);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("response: ",e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    @Override
    public void onCameraIdle() {

    }

    @Override
    public void onMapClick(@NonNull LatLng latLng) {
        googleMap.clear();
        MarkerOptions options = new MarkerOptions().position(latLng).title("you tap here");
        googleMap.addMarker(options);
        Toast.makeText(getApplicationContext(),"tapped, ponit= "+String.valueOf(latLng.longitude),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng) {
        Toast.makeText(getApplicationContext(),"Long pressed, ponit= "+String.valueOf(latLng.longitude),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(this);
        this.googleMap.setOnMapLongClickListener(this);
        this.googleMap.setOnCameraIdleListener(this);
    }

    public void myLocation_clicked(View view) {
        if(ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{ACCESS_FINE_LOCATION},44);
        }
    }

    public void swicth_clicked(View view) {
        if(btn_switch.isChecked()){
            cardView.setVisibility(View.VISIBLE);
        }
        else {
            cardView.setVisibility(View.INVISIBLE);
        }
    }

    public void list_clicked(View view) {
        cardView.setVisibility(View.INVISIBLE);
        btn_switch.setChecked(false);
    }

/*    public void getCurrentPlace() {
        List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);

// Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);

// Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Task<FindCurrentPlaceResponse> placeResponse = placesClient.findCurrentPlace(request);
            placeResponse.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    FindCurrentPlaceResponse response = task.getResult();
                    for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                        Log.e("TAG", String.format("Place '%s' has likelihood: %f",
                                placeLikelihood.getPlace().getName(),
                                placeLikelihood.getLikelihood()));
                    }
                } else {
                    Exception exception = task.getException();
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e("TAG", "Place not found: " + apiException.getStatusCode());
                    }
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"No location permission",Toast.LENGTH_LONG).show();
        }else{
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
            if((mapView != null)){
            }
        }
    }
    private void getLocationPermission() {

    }

    public void getAutoComplete(){
        searchBar.addTextChangeListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(@NonNull Place place) {
                        Log.e("select place info:", "Place: " + place.getName() + ", " + place.getId());
                    }

                    @Override
                    public void onError(@NonNull Status status) {
                        Log.e("select place error:", "An error occurred: " + status);
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        searchBar.setOnClickListener(new );
    }*/

}