package com.example.william.garbagecollection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.tomtom.online.sdk.common.location.LatLng;
import com.tomtom.online.sdk.map.MapFragment;
import com.tomtom.online.sdk.map.OnMapReadyCallback;
import com.tomtom.online.sdk.map.Route;
import com.tomtom.online.sdk.map.RouteBuilder;
import com.tomtom.online.sdk.map.TomtomMap;
import com.tomtom.online.sdk.routing.OnlineRoutingApi;
import com.tomtom.online.sdk.routing.RoutingApi;
import com.tomtom.online.sdk.routing.data.Avoid;
import com.tomtom.online.sdk.routing.data.FullRoute;
import com.tomtom.online.sdk.routing.data.RouteQuery;
import com.tomtom.online.sdk.routing.data.RouteQueryBuilder;
import com.tomtom.online.sdk.routing.data.RouteResult;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.BiConsumer;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getAsyncMap(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@android.support.annotation.NonNull final TomtomMap tomtomMap) {
                //Your map is ready!

                tomtomMap.setPadding(150, 150, 150, 150);

                RoutingApi api = OnlineRoutingApi.create(MainActivity.this);

                LatLng origin = new LatLng(37.7841393,-122.395754);
                LatLng destination = new LatLng(37.7829028,-122.427789);

                RouteQuery query = new RouteQueryBuilder(origin, destination)
                        .withMaxAlternatives(0)
                        .withAvoidType(Avoid.FERRIES);



                api.planRoute(query)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<RouteResult>() {
                            @Override
                            public void accept(@NonNull RouteResult routeResult) throws Exception {
                                //Your route result is ready!

                                FullRoute fullRoute = routeResult.getRoutes().get(0);
                                RouteBuilder routeBuilder = new RouteBuilder(fullRoute.getCoordinates())
                                        .isActive(true);

                                tomtomMap.addRoute(routeBuilder);
                                tomtomMap.getRouteSettings().displayRoutesOverview();

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {

                            }
                        });


            }
        });


    }
}
