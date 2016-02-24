package data;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Juvy on 24/02/2016.
 */
public class City {
    private String _name;
    private String _id;
    private Double _lat;
    private double _lan;
    private float _zoom;

    public City(String name, String id, Double lat, Double lan, float zoom){
        _name = name;
        _id = id;
        _lat = lat;
        _lan = lan;
        _zoom =  zoom;
    }

    public String get_name(){
        return _name;
    }

    public String get_id(){
        return _id;
    }

    public LatLng getLatLan(){
        return new LatLng(_lat, _lan);
    }

    public float getZoom(){
        return _zoom;
    }
}
