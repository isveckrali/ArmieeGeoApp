package com.example.armieegeoapp.Launch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.example.armieegeoapp.ListFragment.ListingFragment;
import com.example.armieegeoapp.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.fragment_listing_container,new ListingFragment(),ListingFragment.LISTING_FRAGMENT_TAG)
                    .addToBackStack(null)
                    .commit();
        }
    }
}
