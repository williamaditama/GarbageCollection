package com.example.william.garbagecollection;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.location.FusedLocationSource;
import com.tomtom.online.sdk.location.LocationSource;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.MarkerBuilder;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.RouteBuilder;
import com.tomtom.online.sdk.map.SimpleMarkerBalloon;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.data.Avoid;
import com.tomtom.online.sdk.routing.data.FullRoute;
import com.tomtom.online.sdk.routing.data.RouteQueryBuilder;
import com.tomtom.online.sdk.routing.data.RouteResult;
import com.tomtom.online.sdk.routing.data.TravelMode;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import static com.tomtom.online.sdk.map.MapConstants.DEFAULT_ZOOM_LEVEL;
import static com.tomtom.online.sdk.map.MapConstants.ORIENTATION_NORTH;

/**
 * Created by willi on 02/03/2018.
 */

public class MainDriver extends AppCompatActivity {

    private boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);

        mapFragment.getAsyncMap(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final TomtomMap map) {
                //Map is ready!
                map.setMyLocationEnabled(true);

                LocationRequest locationRequest = new LocationRequest();
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                final Location userLocation = null;
                final LatLng userLatLng = null;
                final LocationSource locationSource = new FusedLocationSource(
                        MainDriver.this, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.d("TEST", "Location: " + location);


                        firstTime = false;

                        LatLng userLocation = new LatLng(location);
                        LatLng userLatLng = new LatLng(userLocation.getLatitude(),userLocation.getLongitude());
                        map.centerOn(userLocation.getLatitude(), userLocation.getLongitude(),
                                DEFAULT_ZOOM_LEVEL, ORIENTATION_NORTH);


                    }
                }, locationRequest);
                locationSource.activate();


                //Get the garbage can locations
                //TODO Test Data:
                ArrayList<GarbageCan> garbageArr = new ArrayList<>();
                garbageArr.add(new GarbageCan(new LatLng(37.7849548, -122.4276254), 5));
                garbageArr.add(new GarbageCan(new LatLng(37.786999, -122.429382), 10));
                garbageArr.add(new GarbageCan(new LatLng(37.786, -122.43), 15));
                garbageArr.add(new GarbageCan(new LatLng(37.784, -122.44), 30));

                //TODO
//                GarbageCan nearest = GarbageCan.getNearest(userLocation, garbageArr);
                GarbageCan nearest = new GarbageCan(new LatLng(37.7797299,-122.4092301),5);

                //Create Marker on nearest
                MarkerBuilder marker = new MarkerBuilder(nearest.getLocation())
                        .markerBalloon(new SimpleMarkerBalloon(
                                nearest.getLocation().toSimplerString()));
                map.addMarker(marker);
                //Find closest path to nearest
                TravelMode travelMode = TravelMode.TRUCK;
                RouteQueryBuilder query = new RouteQueryBuilder(userLatLng, nearest.getLocation())
                        .withMaxAlternatives(0)
                        .withAvoidType(Avoid.FERRIES);
                RoutingApi api = OnlineRoutingApi.create(MainDriver.this);

                api.planRoute(query)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<RouteResult>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull RouteResult routeResult) throws Exception {
                                //Your route result is ready!

                                FullRoute fullRoute = routeResult.getRoutes().get(0);
                                RouteBuilder routeBuilder = new RouteBuilder(fullRoute.getCoordinates())
                                        .isActive(true);

                                map.addRoute(routeBuilder);
                                map.getRouteSettings().displayRoutesOverview();

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@io.reactivex.annotations.NonNull Throwable throwable) throws Exception {

                            }
                        });
            }
        });
    }
}
