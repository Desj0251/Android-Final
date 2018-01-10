package intents.ca.edumedia.desj0251.opendoorsfinal;

import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import intents.ca.edumedia.desj0251.opendoorsfinal.model.DataItem;
import intents.ca.edumedia.desj0251.opendoorsfinal.services.MyService;
import intents.ca.edumedia.desj0251.opendoorsfinal.services.UploadImageFileService;
import intents.ca.edumedia.desj0251.opendoorsfinal.utils.HttpMethod;
import intents.ca.edumedia.desj0251.opendoorsfinal.utils.NetworkHelper;
import intents.ca.edumedia.desj0251.opendoorsfinal.utils.RequestPackage;

/**
 *  Main Activity
 *  @author John Desjardins (desj0251@algonquinlive.com)
 */
public class MainActivity extends AppCompatActivity {

    private static final String JSON_URL = "https://doors-open-ottawa.mybluemix.net/buildings";
    private static final String TAG = "CRUD";
    private static final int    NO_SELECTED_CATEGORY_ID = -1;
    private static final String ABOUT_DIALOG_TAG = "About Dialog";

    public static final int     REQUEST_NEW_BUILDING = 1;
    public static final String  NEW_BUILDING_DATA = "NEW_BUILDING_DATA";
    public static final String  NEW_BUILDING_IMAGE = "NEW_BUILDING_IMAGE";

    private static final String REMEMBER_SELECTED_CATEGORY_ID = "lastSelectedCategoryId";

    private ProgressBar     mProgressBar;
    private BuildingAdapter mBuildingAdapter;
    private List<DataItem>  mBuildingsList;
    private RecyclerView    mRecyclerView;
    private DrawerLayout    mDrawerLayout;
    private ListView        mDrawerList;
    private String[]        mCategories;
    private Bitmap          mBitmap;

    private int mRememberSelectedCategoryId;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mProgressBar.setVisibility(View.INVISIBLE);
            if (intent.hasExtra(MyService.MY_SERVICE_PAYLOAD)) {
                DataItem[] buildingsArray = (DataItem[]) intent
                        .getParcelableArrayExtra(MyService.MY_SERVICE_PAYLOAD);
                Toast.makeText(MainActivity.this,
                        "Received " + buildingsArray.length + " buildings from service",
                        Toast.LENGTH_SHORT).show();

                mBuildingsList = Arrays.asList(buildingsArray);
                displayBuildings();
            } else if (intent.hasExtra(MyService.MY_SERVICE_RESPONSE)) {
                DataItem myBuilding = intent.getParcelableExtra(MyService.MY_SERVICE_RESPONSE);
                uploadBitmap(myBuilding.getBuildingId());
                fetchBuildings(mRememberSelectedCategoryId);
            } else if (intent.hasExtra(MyService.MY_SERVICE_EXCEPTION)) {
                String message = intent.getStringExtra(MyService.MY_SERVICE_EXCEPTION);
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pbNetwork);
        mProgressBar.setVisibility(View.INVISIBLE);

        // Code to manage sliding navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mCategories = getResources().getStringArray(R.array.categories);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerList.setAdapter(new ArrayAdapter<>(this,
                R.layout.drawer_category_row, mCategories));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Category: " + mCategories[position], Toast.LENGTH_SHORT).show();
                mDrawerLayout.closeDrawer(mDrawerList);
                fetchBuildings(position);
            }
        });
//      end of navigation drawer

        mRecyclerView = (RecyclerView) findViewById(R.id.rvBuildings);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize( true );

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(MyService.MY_SERVICE_MESSAGE));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                startActivityForResult(intent, REQUEST_NEW_BUILDING);
            }
        });

        SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );
        mRememberSelectedCategoryId = settings.getInt(REMEMBER_SELECTED_CATEGORY_ID, NO_SELECTED_CATEGORY_ID);
        fetchBuildings(mRememberSelectedCategoryId);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if ( item.isCheckable() ) {
            // remember which sort option the user picked
            item.setChecked( true );

            // which sort menu item did the user pick?
            switch( item.getItemId() ) {
                case R.id.action_sort_name_asc:
                    mBuildingAdapter.sortByNameAscending();
                    Log.i( TAG, "Sorting buildings by name (a-z)" );
                    break;

                case R.id.action_sort_name_dsc:
                    mBuildingAdapter.sortByNameDescending();
                    Log.i( TAG, "Sorting buildings by name (z-a)" );
                    break;
            }
            return true;
        } else if (id == R.id.action_about) {
            DialogFragment newFragment = new AboutDialogFragment();
            newFragment.show(getFragmentManager(), ABOUT_DIALOG_TAG);
            return true;
        } else {
            switch (item.getItemId()) {
                case R.id.action_all_items:
                    // fetch and display all buildings
                    //fetchBuildings(NO_SELECTED_CATEGORY_ID);
                    fetchBuildings(NO_SELECTED_CATEGORY_ID);
                    return true;
                case R.id.action_choose_category:
                    //open the drawer
                    mDrawerLayout.openDrawer(mDrawerList);
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(getApplicationContext())
                .unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences settings = getSharedPreferences( getResources().getString(R.string.app_name), Context.MODE_PRIVATE );
        SharedPreferences.Editor editor = settings.edit();

        editor.putInt( REMEMBER_SELECTED_CATEGORY_ID, mRememberSelectedCategoryId );
        editor.commit();
    }

    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void uploadBitmap(int buildingId) {
        if (mBitmap != null ) {
            RequestPackage requestPackage = new RequestPackage();
            requestPackage.setMethod(HttpMethod.POST);
            requestPackage.setEndPoint(JSON_URL + "/" + buildingId + "/image");

            Toast.makeText(this, "Uploaded image for Building Id: " + buildingId + "", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, UploadImageFileService.class);
            intent.putExtra(UploadImageFileService.REQUEST_PACKAGE, requestPackage);
            intent.putExtra(NEW_BUILDING_IMAGE, mBitmap);
            startService(intent);
        }
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_BUILDING) {
            if (resultCode == RESULT_OK) {
                DataItem newBuilding = data.getExtras().getParcelable(NEW_BUILDING_DATA);
                Bitmap image  = (Bitmap) data.getParcelableExtra(NEW_BUILDING_IMAGE);
                if (image != null) {
                    mBitmap = image;
                }

                Toast.makeText(this, "Added Building: " + newBuilding.getNameEN(), Toast.LENGTH_SHORT).show();
            }

            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled: Add New Building", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayBuildings() {
        if (mBuildingsList != null) {
            mBuildingAdapter = new BuildingAdapter(this, mBuildingsList);
            mRecyclerView.setAdapter(mBuildingAdapter);
        }
    }

    private void fetchBuildings(int selectedCategoryId) {
        mRememberSelectedCategoryId = selectedCategoryId;
        if (NetworkHelper.hasNetworkAccess(this)) {
            mProgressBar.setVisibility(View.VISIBLE);
            RequestPackage requestPackage = new RequestPackage();
            requestPackage.setEndPoint(JSON_URL);
            if (selectedCategoryId != NO_SELECTED_CATEGORY_ID) {
                requestPackage.setParam("categoryId", selectedCategoryId + "");
            }

            Intent intent = new Intent(this, MyService.class);
            intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
            startService(intent);
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
    }

}
