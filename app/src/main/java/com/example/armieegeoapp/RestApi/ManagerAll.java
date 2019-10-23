package com.example.armieegeoapp.RestApi;

import com.example.armieegeoapp.Models.GeoListingModel;

import java.util.List;

import retrofit2.Call;

public class ManagerAll extends BaseManager{

    private static ManagerAll ourInstance = new ManagerAll();

    public static synchronized ManagerAll getInstance() {

        return ourInstance;

    }

    public Call<List<GeoListingModel>> getWeatherListingData() {

        Call<List<GeoListingModel>> call = getRestApiClient().getUsetGeoInfo();
        return call;
    }
}
