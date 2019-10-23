package com.example.armieegeoapp.RestApi;

import com.example.armieegeoapp.Models.GeoListingModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestApi {
    @GET("apartments.json")
    Call<List<GeoListingModel>> getUsetGeoInfo();
}
