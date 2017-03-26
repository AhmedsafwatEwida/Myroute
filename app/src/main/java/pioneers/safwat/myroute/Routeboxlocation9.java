package pioneers.safwat.myroute;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationListener;
import com.mapbox.mapboxsdk.location.LocationServices;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.services.Constants;
import com.mapbox.services.commons.ServicesException;
import com.mapbox.services.commons.geojson.LineString;
import com.mapbox.services.commons.models.Position;
import com.mapbox.services.commons.utils.PolylineUtils;
import com.mapbox.services.directions.v5.DirectionsCriteria;
import com.mapbox.services.directions.v5.MapboxDirections;
import com.mapbox.services.directions.v5.models.DirectionsResponse;
import com.mapbox.services.directions.v5.models.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static pioneers.safwat.myroute.MyprojectHandler.KEY_DIST;
import static pioneers.safwat.myroute.MyprojectHandler.KEY_ID;
import static pioneers.safwat.myroute.MyprojectHandler.KEY_NAME;
import static pioneers.safwat.myroute.MyprojectHandler.KEY_ROUTE;
import static pioneers.safwat.myroute.MyprojectHandler.KEY_SIZE;
import static pioneers.safwat.myroute.MyprojectHandler.TABLE_ROUTES;

public class Routeboxlocation9 extends AppCompatActivity implements  MapboxMap.OnMyLocationChangeListener ,AdapterView.OnItemSelectedListener {

    private static final String TAG = "DirectionsActivity";
    private MapView mapView;
    private MapboxMap map;
    private DirectionsRoute currentRoute,markerroute;
    private LocationServices locationServices;
    private FloatingActionButton floatingActionButton;
    private Marker marker;
    private Position origin;
    private Spinner savedroutes;
    private EditText routenametext;
    String line,distance;
   // private Polyline line;
  // private Position destination;
    List<LatLng> latlngpoints = new ArrayList<>();
    List<LatLng> dlatlngpoints = new ArrayList<>();
    List<Position> points = new ArrayList<>();
    List<Position> dpoints = new ArrayList<>();
Integer linesize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final MyprojectHandler db=new MyprojectHandler(Routeboxlocation9.this);
        setContentView(R.layout.map_box5);
        savedroutes = (Spinner) findViewById(R.id.saved_route_spinner);
        savedroutes.setOnItemSelectedListener(Routeboxlocation9.this);
        List<String> routelist = db.getAllroutes();
        ArrayAdapter<String> dataadapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item,routelist);
        dataadapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
       savedroutes.setAdapter(dataadapter);
        MapboxAccountManager.start(this, getString(R.string.access_token));
        routenametext = (EditText) findViewById(R.id.routetext);
        routenametext.setVisibility(View.VISIBLE);
        locationServices = LocationServices.getLocationServices(Routeboxlocation9.this);
      //  final Location lastLocation = locationServices.getLastLocation();

        floatingActionButton = (FloatingActionButton) findViewById(R.id.route_toggle_fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
    line = PolylineUtils.encode(points, Constants.GOOGLE_PRECISION);
           //     line = PolylineUtils.encode(points, Constants.GOOGLE_PRECISION);
                linesize=points.size();
                try {
                    distance = String.valueOf(getdistance(points.get(0), points.get(points.size()-1)));
                } catch (ServicesException e) {
                    e.printStackTrace();
                }

                //Toast.makeText(getBaseContext(), (int) getdistance(points.get(0), points.get(points.size()-1)), Toast.LENGTH_LONG).show();
            //    List<Position> poss= PolylineUtils.decode(line,Constants.GOOGLE_PRECISION);
                  //  Toast.makeText(Routeboxlocation9.this, getdistance(points.get(0), points.get(points.size()-1)), Toast.LENGTH_LONG).show();
                 db.addroute(new RouteDB(routenametext.getText().toString(), line, distance,String.valueOf(linesize)));
            //    Toast.makeText(getBaseContext(), "New Project  " + routenametext.getText().toString()+" successfuly added ", Toast.LENGTH_LONG).show();
                Toast.makeText(Routeboxlocation9.this,
                        "Route name  " +routenametext.getText().toString() + "  added Successfully",
                        Toast.LENGTH_LONG).show();
            }
        });


   //  final  Position origin = Position.fromCoordinates(lastLocation.getLongitude(),lastLocation.getLatitude());
//        mydestination(true);
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {
                map = mapboxMap;
               map.setMyLocationEnabled(true);
           //     enableLocation(true);
                map.setOnMyLocationChangeListener(Routeboxlocation9.this);
                mapboxMap.setOnMapClickListener(new MapboxMap.OnMapClickListener(){
                    @Override
                    public void onMapClick(@NonNull LatLng mpoint) {
                        Position origin = points.get(0);
                        Position markerdestination = Position.fromCoordinates(mpoint.getLongitude(), mpoint.getLatitude());
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(markerdestination.getLatitude(), markerdestination.getLongitude()))
                                .title("Destination")
                                .snippet(markerdestination.getCoordinates().toString()));
                        try {
                            getmarkerRoute(origin, markerdestination);
                        } catch (ServicesException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        });
    }
    @Override
    public void onMyLocationChange(@Nullable Location location) {
        if (location != null) {
          //  map.easeCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLongitude(), location.getLatitude())));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 15));
            Position position = Position.fromCoordinates(location.getLongitude(),location.getLatitude());
            points.add(position);
            Position origin = points.get(0);
            latlngpoints.add(new LatLng(position.getLatitude(), position.getLongitude()));
             drawPolyline(points);
            try {
                getRoute(origin,points.get(points.size()-1));
            } catch (ServicesException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_streets:
                map.setStyleUrl(Style.MAPBOX_STREETS);
                break;
            case R.id.action_sat:
                map.setStyleUrl(Style.SATELLITE_STREETS);
                break;
            case R.id.action_dark:
                map.setStyleUrl(Style.DARK);
                break;
            case R.id.action_light:
                map.setStyleUrl(Style.LIGHT);
                break;
        }
        return true;
    }
    private void getRoute(Position origin, Position destination) throws ServicesException {
     //   mydestination(true);
      //  Position destination = Position.fromCoordinates(mydestination(true).getLongitude(),mydestination(true).getLatitude());
        MapboxDirections client = new MapboxDirections.Builder()
                .setOrigin(origin)
                .setDestination(destination)
                .setProfile(DirectionsCriteria.PROFILE_DRIVING)
                .setAccessToken(MapboxAccountManager.getInstance().getAccessToken())
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.d(TAG, "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().getRoutes().size() < 1) {
                    Log.e(TAG, "No routes found");
                    return;
                }

                // Print some info about the route
                currentRoute = response.body().getRoutes().get(0);
                Log.d(TAG, "Distance: " + currentRoute.getDistance());
                Toast.makeText(Routeboxlocation9.this,
                        "Route is " + currentRoute.getDistance() + " meters long.",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(TAG, "Error: " + throwable.getMessage());
                Toast.makeText(Routeboxlocation9.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String getdistance(Position origin, Position destination) throws ServicesException {
        //   mydestination(true);
        //  Position destination = Position.fromCoordinates(mydestination(true).getLongitude(),mydestination(true).getLatitude());
        MapboxDirections client = new MapboxDirections.Builder()
                .setOrigin(origin)
                .setDestination(destination)
                .setProfile(DirectionsCriteria.PROFILE_DRIVING)
                .setAccessToken(MapboxAccountManager.getInstance().getAccessToken())
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.d(TAG, "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().getRoutes().size() < 1) {
                    Log.e(TAG, "No routes found");
                    return;
                }

                // Print some info about the route
                currentRoute = response.body().getRoutes().get(0);
                Log.d(TAG, "Distance: " + currentRoute.getDistance());
                Toast.makeText(Routeboxlocation9.this,
                        "Route is " + currentRoute.getDistance() + " meters long.",
                        Toast.LENGTH_LONG).show();
                distance= String.valueOf(currentRoute.getDistance());

            }

            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(TAG, "Error: " + throwable.getMessage());
                Toast.makeText(Routeboxlocation9.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return  distance;
    }
    private void getmarkerRoute(Position origin, Position destination) throws ServicesException {
        //   mydestination(true);
        //  Position destination = Position.fromCoordinates(mydestination(true).getLongitude(),mydestination(true).getLatitude());
        MapboxDirections client = new MapboxDirections.Builder()
                .setOrigin(origin)
                .setDestination(destination)
                .setProfile(DirectionsCriteria.PROFILE_DRIVING)
                .setAccessToken(MapboxAccountManager.getInstance().getAccessToken())
                .build();

        client.enqueueCall(new Callback<DirectionsResponse>() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                // You can get the generic HTTP info about the response
                Log.d(TAG, "Response code: " + response.code());
                if (response.body() == null) {
                    Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                    return;
                } else if (response.body().getRoutes().size() < 1) {
                    Log.e(TAG, "No routes found");
                    return;
                }

                // Print some info about the route
                markerroute = response.body().getRoutes().get(0);
                Log.d(TAG, "Distance: " + markerroute.getDistance());
                Toast.makeText(Routeboxlocation9.this,
                        "Route is " + markerroute.getDistance() + " meters long.",
                        Toast.LENGTH_LONG).show();

                // Draw the route on the map
                  drawRoute(markerroute);
            }
            private void drawRoute(DirectionsRoute route) {
                // Convert LineString coordinates into LatLng[]
                LineString lineString = LineString.fromPolyline(route.getGeometry(), Constants.OSRM_PRECISION_V5);
                List<Position> coordinates = lineString.getCoordinates();
                LatLng[] markerpoints = new LatLng[coordinates.size()];
                for (int i = 0; i < coordinates.size(); i++) {
                    markerpoints[i] = new LatLng(
                            coordinates.get(i).getLatitude(),
                            coordinates.get(i).getLongitude());
                }

                // Draw Points on MapView
                map.addPolyline(new PolylineOptions()
                        .add(markerpoints)
                        .color(Color.parseColor("#009688"))
                        .width(5));
            }
            @Override
            public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                Log.e(TAG, "Error: " + throwable.getMessage());
                Toast.makeText(Routeboxlocation9.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
   private void drawPolyline(List<Position> points) {
       LatLng[] pointsArray = new LatLng[points.size()];
       for (int i = 0; i < points.size(); i++) {
           pointsArray[i] = new LatLng(points.get(i).getLatitude(), points.get(i).getLongitude());
       }
       /*Toast.makeText(this,
               "Route is " + points.size()+ " meters long.",
               Toast.LENGTH_LONG).show();*/
       map.addPolyline(new PolylineOptions()
               .add(pointsArray)
               .color(Color.parseColor("#009688"))
               .width(5));
   }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    private void enableLocation(boolean enabled) {
        if (enabled) {
            // If we have the last location of the user, we can move the camera to that position.
            final Location lastLocation = locationServices.getLastLocation();
            if (lastLocation != null) {
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation), 15));
            }

            locationServices.addLocationListener(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    if (location != null) {
                        // Move the map camera to where the user location is and then remove the
                        // listener so the camera isn't constantly updating when the user location
                        // changes. When the user disables and then enables the location again, this
                        // listener is registered again and will adjust the camera once again.
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location), 15));
                        locationServices.removeLocationListener(this);
                     //   Position position = Position.fromCoordinates(location.getLongitude(),location.getLatitude());
                      //  points.add(position);
                   //     points.add(new LatLng(location.getLongitude(),location.getLatitude()));
                        Toast.makeText(Routeboxlocation9.this,
                                "Route is " + location.getLatitude()+"  "+location.getLongitude()+ " meters long.",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        // Enable or disable the location layer on the map
        map.setMyLocationEnabled(enabled);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String label = parent.getItemAtPosition(position).toString();
        SQLiteDatabase database = new MyprojectHandler(this).getReadableDatabase();
        final String SELECT_EMPLOYEE_WITH_EMPLOYER = "SELECT * " +
                "FROM " + TABLE_ROUTES + " WHERE " + KEY_NAME + " like ?";
        String[] selectionArgs = {"%" + label + "%"};
        Cursor cursor = database.rawQuery(SELECT_EMPLOYEE_WITH_EMPLOYER, selectionArgs);
        if (cursor != null)
            cursor.moveToFirst();
        RouteDB dBroutes =new RouteDB(Integer.parseInt(cursor.getString(cursor.getColumnIndex(KEY_ID))),
                cursor.getString(cursor.getColumnIndex(KEY_NAME)),
                cursor.getString(cursor.getColumnIndex(KEY_ROUTE)),
                cursor.getString(cursor.getColumnIndex(KEY_DIST)),
                cursor.getString(cursor.getColumnIndex(KEY_SIZE)));
        List<Position> pos= PolylineUtils.decode(dBroutes.getroutepath(), Constants.GOOGLE_PRECISION);
        map.clear();
        drawPolyline(pos);
      //  map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((LatLng) pos), 15));
     //   map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pos.size(),15));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(pos.get(0).getLatitude(),pos.get(0).getLongitude()),15));
        // Showing selected spinner item
        Toast.makeText(parent.getContext(), "You selected: " +dBroutes.getroutename(),Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}