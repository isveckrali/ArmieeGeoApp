package com.example.armieegeoapp.ViewModels;

import com.example.armieegeoapp.Models.GeoListingModel;

public class LocationListViewModel {

    private String name,NumberOfBed;

    public LocationListViewModel(GeoListingModel geoListingModel) {
        this.name = "name: " + geoListingModel.getName();
        this.NumberOfBed = "Number Of bed: " + geoListingModel.getBedrooms();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumberOfBed() {
        return NumberOfBed;
    }

    public void setNumberOfBed(String numberOfBed) {
        NumberOfBed = numberOfBed;
    }
}
