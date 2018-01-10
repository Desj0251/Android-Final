package intents.ca.edumedia.desj0251.opendoorsfinal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import intents.ca.edumedia.desj0251.opendoorsfinal.model.DataItem;
import intents.ca.edumedia.desj0251.opendoorsfinal.services.MyService;
import intents.ca.edumedia.desj0251.opendoorsfinal.utils.HttpMethod;
import intents.ca.edumedia.desj0251.opendoorsfinal.utils.RequestPackage;

/**
 *  Building Adaptor: Display of list items
 *  @author John Desjardins (desj0251@algonquinlive.com)
 */
public class BuildingAdapter extends RecyclerView.Adapter<BuildingAdapter.ViewHolder> {

    private static final String PHOTOS_BASE_URL = "https://doors-open-ottawa.mybluemix.net/buildings/";
    public static final String BUILDING_KEY = "building_key";

    private Context         mContext;
    private List<DataItem>  mBuildings;

    private static final String  JSON_URL = "https://doors-open-ottawa.mybluemix.net/buildings";

    public BuildingAdapter(Context context, List<DataItem> buildings) {
        this.mContext = context;
        this.mBuildings = buildings;
    }

    @Override
    public BuildingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View buildingView = inflater.inflate(R.layout.list_building, parent, false);
        ViewHolder viewHolder = new ViewHolder(buildingView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BuildingAdapter.ViewHolder holder, int position) {
        final DataItem aBuilding = mBuildings.get(position);

        holder.tvName.setText(aBuilding.getNameEN());
        holder.tvAddress.setText(aBuilding.getAddressEN());

        String url = PHOTOS_BASE_URL + aBuilding.getBuildingId() + "/image";
        // force picasso to fetch the planet's image from the internet, and not use the cache
        // handles the case when the image for Pluto is updated
        Picasso.with(mContext)
                .load(url)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .error(R.drawable.noimage)
                .fit()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);
                intent.putExtra(BUILDING_KEY, aBuilding);
                mContext.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {

                // -- Code for Alert Dialog from: https://stackoverflow.com/questions/2478517/how-to-display-a-yes-no-dialog-box-on-android -- //
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Confirm Delete");
                builder.setMessage("Are you sure you want to delete " + aBuilding.getNameEN() + "?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing but close the dialog
                        //Toast.makeText(mContext, aBuilding.getNameEN() + " has been deleted!", Toast.LENGTH_SHORT).show();


                        RequestPackage requestPackage = new RequestPackage();
                        requestPackage.setMethod( HttpMethod.DELETE );
                        requestPackage.setEndPoint( JSON_URL + "/" + aBuilding.getBuildingId() );

                        Intent intent = new Intent(mContext, MyService.class);
                        intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
                        mContext.startService(intent);

                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View V) {
                Intent intent = new Intent(mContext, EditActivity.class);
                intent.putExtra(BUILDING_KEY, aBuilding);
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mBuildings.size();
    }

    public void setBuildings(DataItem[] buildingsArray) {
        mBuildings.clear();
        mBuildings.addAll(new ArrayList<>(Arrays.asList(buildingsArray)));
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView tvName;
        public TextView tvAddress;
        public ImageView imageView;
        public ImageButton delete;
        public ImageButton edit;
        public View mView;

        public ViewHolder(View BuildingView) {
            super(BuildingView);

            tvName = (TextView) BuildingView.findViewById(R.id.buildingNameText);
            tvAddress = (TextView) BuildingView.findViewById(R.id.buildingAddressText);
            imageView = (ImageView) BuildingView.findViewById(R.id.imageView);
            delete = (ImageButton) BuildingView.findViewById(R.id.deleteButton);
            edit = (ImageButton) BuildingView.findViewById(R.id.editButton);
            mView = BuildingView;
        }
    }

    public void sortByNameAscending() {
        Collections.sort( mBuildings, new Comparator<DataItem>() {
            @Override
            public int compare( DataItem lhs, DataItem rhs ) {

                return lhs.getNameEN().toLowerCase().compareTo( rhs.getNameEN().toLowerCase() );
            }
        });

        notifyDataSetChanged();
    }

    public void sortByNameDescending() {
        Collections.sort( mBuildings, Collections.reverseOrder(new Comparator<DataItem>() {
            @Override
            public int compare( DataItem lhs, DataItem rhs ) {
                return lhs.getNameEN().toLowerCase().compareTo( rhs.getNameEN().toLowerCase() );
            }
        }));

        notifyDataSetChanged();
    }

}