package com.example.armieegeoapp.ListFragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.armieegeoapp.Adapter.FragmentListingAdapter;
import com.example.armieegeoapp.Compare.DistanceFromMeComparator;
import com.example.armieegeoapp.InternetController.InternetController;
import com.example.armieegeoapp.Launch.MainActivity;
import com.example.armieegeoapp.Models.GeoListingModel;
import com.example.armieegeoapp.R;
import com.example.armieegeoapp.RestApi.ManagerAll;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListingFragment extends Fragment implements SearchView.OnQueryTextListener, CompoundButton.OnCheckedChangeListener {

    //Constants
    public static final String LISTING_FRAGMENT_TAG = "listingFragmentTag";

    private static final int REQUEST_LOCATION = 1;
    private LocationManager locationManager;

    //Variables
    private List<GeoListingModel> geoListingModels;
    private RecyclerView recyclerView;
    private View view;
    private LinearLayoutManager layoutManager;
    private FragmentListingAdapter fragmentListingAdapter;
    private ProgressBar progressBar;
    private SearchView searchView;
    private ToggleButton toggleButton;

    private LocationManager mLocationManager;

    public ListingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_list, container, false);
        defineComponents();
        checkInternetAndGetData();
        addPermission();
        defineLocation();
        return view;
    }

    //Define location manage
    private void defineLocation() {
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
    }

    //request location permission with Popop
    private void addPermission() {
       requestPermissions(new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_LOCATION);
    }

    //check Gps is enabled or not
    private void checkGPS() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //write func to enabled gps
        onGPS();
        } else {
            // GPS is already on Then
            getLocation();
        }
    }

    //Get user current location by types
    private void getLocation() {
        //Check permission again
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        } else {
            Location locationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location locationNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location locationPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (locationNetwork != null) {
                double lat = locationNetwork.getLatitude();
                double longi = locationNetwork.getLongitude();
                Collections.sort(geoListingModels,new DistanceFromMeComparator(new GeoListingModel(lat,longi)));
                fragmentListingAdapter.filteredGeoListingModels = geoListingModels;
                fragmentListingAdapter.geoListingModels = geoListingModels;
                fragmentListingAdapter.notifyDataSetChanged();
                //Log.i(LISTING_FRAGMENT_TAG,"lat " + lat + "longi " + longi);
            } else {
                Toast.makeText(getActivity(), "Can't get your location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //İf CPS is enabled, request to open it.
    private void onGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            dialogInterface.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Set data to Recyler view
    private void setData() {
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter
        fragmentListingAdapter = new FragmentListingAdapter(getActivity(), geoListingModels);
        recyclerView.setAdapter(fragmentListingAdapter);
        fragmentListingAdapter.notifyDataSetChanged();

        checkGPS();

    }

    //Define all components by it's id
    private void defineComponents() {
        recyclerView = view.findViewById(R.id.recylerView);
        progressBar = view.findViewById(R.id.progressBar);
        searchView = view.findViewById(R.id.search_bar);
        toggleButton = view.findViewById(R.id.toggle_button);

        searchView.setOnQueryTextListener(this);
        toggleButton.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) this);
    }

    //Check internet connection whether which is connect
    private void checkInternetAndGetData() {
        if (InternetController.isNetworkConnected(getActivity())) {
            getData();
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    //Get data from server
    private void getData() {
        geoListingModels = new ArrayList<>();
        Call<List<GeoListingModel>> geoListingModelCall = ManagerAll.getInstance().getWeatherListingData();
        geoListingModelCall.enqueue(new Callback<List<GeoListingModel>>() {
            @Override
            public void onResponse(Call<List<GeoListingModel>> call, Response<List<GeoListingModel>> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        geoListingModels = response.body();
                        setData();
                    }else {
                        Toast.makeText(getActivity(), "Data weren't got", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Request isn't successful", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.INVISIBLE);
            }
            @Override
            public void onFailure(Call<List<GeoListingModel>> call, Throwable t) {
                Toast.makeText(getActivity(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        fragmentListingAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (fragmentListingAdapter != null) {
            fragmentListingAdapter.getFilter().filter(s);
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b == true) {
            searchView.setQueryHint("Search by Dates");
            FragmentListingAdapter.searchType = FragmentListingAdapter.DATE;

        } else {
            FragmentListingAdapter.searchType = FragmentListingAdapter.NUMBER_OF_BEDS;
            searchView.setQueryHint("Search by Number Of Bads");
        }
    }
}
