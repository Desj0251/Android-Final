package intents.ca.edumedia.desj0251.opendoorsfinal;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import intents.ca.edumedia.desj0251.opendoorsfinal.model.DataItem;

/**
 *  Details Activity: Displays details of building in own view
 *  @author John Desjardins (desj0251@algonquinlive.com)
 */
public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView  tvName;
    private TextView  tvCategory;
    private TextView  tvDescription;
    private ImageView buildingImage;
    private TextView  tvSatHours;
    private TextView  tvSunHours;
    private TextView  tvAddress;
    private TextView  tvCity;

    // Variables for Google Map
    private GoogleMap mMap;
    LatLng location;
    private double lat;
    private double lon;
    private String address;

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(location).title(address));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

       DataItem selectedBuilding = getIntent().getExtras().getParcelable(BuildingAdapter.BUILDING_KEY);
        if (selectedBuilding == null) {
            throw new AssertionError("Null data item received!");
        }

        location = new LatLng(selectedBuilding.getLatitude(),selectedBuilding.getLongitude());
        address = selectedBuilding.getAddressEN();

        tvName = (TextView) findViewById(R.id.tvName);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        buildingImage = (ImageView) findViewById(R.id.buildingImage);
        tvSatHours = (TextView) findViewById(R.id.saturdayHours);
        tvSunHours = (TextView) findViewById(R.id.sundayHours);
        tvAddress = (TextView) findViewById(R.id.tvAddress);
        tvCity = (TextView) findViewById(R.id.tvCity);

        tvName.setText( selectedBuilding.getNameEN());
        tvCategory.setText(selectedBuilding.getCategoryEN());
        tvDescription.setText(selectedBuilding.getDescriptionEN());
        tvAddress.setText(selectedBuilding.getAddressEN());
        tvCity.setText(selectedBuilding.getCity() + " " + selectedBuilding.getProvince() + ", Canada");

        String url = "https://doors-open-ottawa.mybluemix.net/buildings/" + selectedBuilding.getBuildingId() + "/image";
        Picasso.with(this)
                .load(Uri.parse(url))
                .error(R.drawable.noimage)
                .fit()
                .into(buildingImage);

        String sat;
        String sun;

        try {
            sat = selectedBuilding.getSaturdayStart().substring(11) + " - " + selectedBuilding.getSaturdayClose().substring(11);
        }catch(Exception e){
            sat = "CLOSED";
        }
        try {
            sun = selectedBuilding.getSundayStart().substring(11) + " - " + selectedBuilding.getSundayClose().substring(11);
        }catch(Exception e){
            sun = "CLOSED";
        }

        tvSatHours.setText(sat);
        tvSunHours.setText(sun);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }
}