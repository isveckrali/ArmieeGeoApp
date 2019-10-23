package com.example.armieegeoapp.Adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.armieegeoapp.Models.GeoListingModel;
import com.example.armieegeoapp.R;
import com.example.armieegeoapp.ViewModels.LocationListViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FragmentListingAdapter extends RecyclerView.Adapter<FragmentListingAdapter.ViewHolder> implements Filterable {


    private Context context;
    public List<GeoListingModel> geoListingModels,filteredGeoListingModels;
    public static final String DATE = "date";
    public static final String NUMBER_OF_BEDS = "numberOfBeds";
    public static String searchType = NUMBER_OF_BEDS;


    public FragmentListingAdapter(Context context, List<GeoListingModel> geoListingModels) {
        this.context = context;
        this.geoListingModels = geoListingModels;
        this.filteredGeoListingModels = geoListingModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.geo_listing_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocationListViewModel locationListViewModel =  new LocationListViewModel(filteredGeoListingModels.get(position));
        holder.txtViewNumOfRoom.setText(locationListViewModel.getNumberOfBed());
        holder.txtViewName.setText(locationListViewModel.getName());

    }

    @Override
    public int getItemCount() {
        return filteredGeoListingModels.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charSequenceString = constraint.toString();
                if (charSequenceString.isEmpty()) {
                    filteredGeoListingModels = geoListingModels;
                } else {
                    List<GeoListingModel> filteredList = new ArrayList<>();
                    for (GeoListingModel name : geoListingModels) {
                        if (String.valueOf(name.getBedrooms()).contains(charSequenceString)) {
                            filteredList.add(name);
                        }
                        filteredGeoListingModels = filteredList;
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredGeoListingModels;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredGeoListingModels = (List<GeoListingModel>) results.values;
                notifyDataSetChanged();
            }
        };

    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtViewName, txtViewNumOfRoom;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtViewName = itemView.findViewById(R.id.text_view_name);
            txtViewNumOfRoom = itemView.findViewById(R.id.text_view_num_of_bad);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Toast.makeText(context, "Reserved: "+filteredGeoListingModels.get(getLayoutPosition()).getName(), Toast.LENGTH_SHORT).show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                geoListingModels.removeIf(obj -> obj.getId() == filteredGeoListingModels.get(getLayoutPosition()).getId());
            }
            filteredGeoListingModels.remove(getLayoutPosition());
            notifyDataSetChanged();
        }
    }
}
