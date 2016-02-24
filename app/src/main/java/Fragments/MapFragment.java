package Fragments;

import android.os.Bundle;
import android.content.Intent;

import bgu_apps.checklease.ShowApartment;
import data.Apartment;
import data.Data;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import bgu_apps.checklease.R;

/**
 * Created by user on 09/01/2016.
 */
public class MapFragment extends SupportMapFragment  {

    private GoogleMap _map;

    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        this.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                _map = googleMap;

                putMarkers();
                _map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.hideInfoWindow();
                        int appIndex = Integer.parseInt(marker.getTitle());
                        Intent showApp = new Intent(MapFragment.this.getActivity(), ShowApartment.class);
                        showApp.putExtra("AppIndex", appIndex);
                        MapFragment.this.getActivity().startActivity(showApp);
                        return true;
                    }
                });

                CameraPosition cityPos = new CameraPosition.Builder().target(Data.getCityLatLan()).zoom(Data.getCityZoom()).build();
                _map.moveCamera(CameraUpdateFactory.newCameraPosition(cityPos));
            }
        });
    }

    public void onResume(){
        super.onResume();

        if(_map != null)
            putMarkers();
    }

    private void putMarkers(){
        _map.clear();
        for(int i=0; i<ApartmentListFragment._apartments.size(); i++){
            Apartment curr = ApartmentListFragment._apartments.get(i);
            MarkerOptions marker = new MarkerOptions();

            double lat = Double.parseDouble(curr.getValue(Data.ADDRESS_LAT).getStrValue());
            double lan = Double.parseDouble(curr.getValue(Data.ADDRESS_LAN).getStrValue());
            LatLng appLocation = new LatLng(lat, lan);
            marker.position(appLocation);

            BitmapDescriptor icon;
            int rate = Data.getRate(curr.getCalcPrice(), curr.getGivenPrice());
            switch(rate){
                case 1:
                    if(curr.isFavorite())
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.home_green_s_fav);
                    else
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.home_green_s);
                    break;
                case 2:
                    if(curr.isFavorite())
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.home_orange_s_fav);
                    else
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.home_orange_s);
                    break;
                case 3:
                    if(curr.isFavorite())
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.home_red_s_fav);
                    else
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.home_red_s);
                    break;
                default:
                    if(curr.isFavorite())
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.home_grey_s_fav);
                    else
                        icon = BitmapDescriptorFactory.fromResource(R.drawable.home_grey_s);
                    break;
            }

            marker.icon(icon);
            marker.title("" + i);

            _map.addMarker(marker);
        }
    }
}