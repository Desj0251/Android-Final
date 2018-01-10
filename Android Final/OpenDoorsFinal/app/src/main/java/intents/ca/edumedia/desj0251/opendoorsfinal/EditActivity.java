package intents.ca.edumedia.desj0251.opendoorsfinal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import intents.ca.edumedia.desj0251.opendoorsfinal.model.DataItem;
import intents.ca.edumedia.desj0251.opendoorsfinal.services.MyService;
import intents.ca.edumedia.desj0251.opendoorsfinal.utils.HttpMethod;
import intents.ca.edumedia.desj0251.opendoorsfinal.utils.RequestPackage;

/**
 *  Edit Activity: Allows for the edit of buildings
 *  @author John Desjardins (desj0251@algonquinlive.com)
 */
public class EditActivity extends AppCompatActivity {

    private static final String  JSON_URL = "https://doors-open-ottawa.mybluemix.net/buildings";
    private final static int CAMERA_REQUEST_CODE = 100;

    private static regexValid time24hFormatValidator;

    private EditText    etName;
    private EditText    etAddress;
    private EditText    etDescription;
    private ImageView   buildingImage;
    private CheckBox    cbIsNew;
    private TextView    tvCity;
    private EditText    etSatHoursOpen;
    private EditText    etSatHoursClose;
    private EditText    etSunHoursOpen;
    private EditText    etSunHoursClose;
    private TextView    tvCategory;
    private Bitmap      bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_);

        final DataItem selectedBuilding = getIntent().getExtras().getParcelable(BuildingAdapter.BUILDING_KEY);
        if (selectedBuilding == null) {
            throw new AssertionError("Null data item received!");
        }

        time24hFormatValidator = new regexValid();

        etName = (EditText) findViewById(R.id.etName);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etDescription = (EditText) findViewById(R.id.etDescription);
        buildingImage = (ImageView) findViewById(R.id.buildingImage);
        cbIsNew = (CheckBox) findViewById(R.id.checkBox);
        tvCategory = (TextView) findViewById(R.id.tvCategory);
        tvCity = (TextView) findViewById(R.id.tvAddress);
        etSatHoursOpen = (EditText) findViewById(R.id.saturdayHoursOpen);
        etSatHoursClose = (EditText) findViewById(R.id.saturdayHoursClose);
        etSunHoursOpen = (EditText) findViewById(R.id.sundayHoursOpen);
        etSunHoursClose = (EditText) findViewById(R.id.sundayHoursClose);
        bitmap = null;

        etName.setText(selectedBuilding.getNameEN());
        etAddress.setText(selectedBuilding.getAddressEN());
        etDescription.setText(selectedBuilding.getDescriptionEN());
        cbIsNew.setChecked(selectedBuilding.isIsNewBuilding());
        tvCity.setText(selectedBuilding.getCity() + " " + selectedBuilding.getProvince() + ", Canada");
        tvCategory.setText(selectedBuilding.getCategoryEN());

        String url = "https://doors-open-ottawa.mybluemix.net/buildings/" + selectedBuilding.getBuildingId() + "/image";
        Picasso.with(this)
                .load(Uri.parse(url))
                .error(R.drawable.noimage)
                .fit()
                .into(buildingImage);

        if ( selectedBuilding.getSaturdayStart() != null  && selectedBuilding.getSaturdayClose() != null ) {
            etSatHoursOpen.setText(selectedBuilding.getSaturdayStart().substring(11));
            etSatHoursClose.setText(selectedBuilding.getSaturdayClose().substring(11));
        } else {
            etSatHoursOpen.setText("");
            etSatHoursClose.setText("");
        }

        if ( selectedBuilding.getSundayStart() != null  && selectedBuilding.getSundayClose() != null ) {
            etSunHoursOpen.setText(selectedBuilding.getSundayStart().substring(11));
            etSunHoursClose.setText(selectedBuilding.getSundayClose().substring(11));
        } else {
            etSunHoursOpen.setText("");
            etSunHoursClose.setText("");
        }

        //todo: camera takes picture and places it in the imageview

        // Camera takes photo and places it in image view but upload of image on edit is not implemented
        FloatingActionButton photoButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            }
        });

        Button saveButton = (Button) findViewById(R.id.addSave);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                String name = etName.getText().toString();
                String address = etAddress.getText().toString();
                String description = etDescription.getText().toString();
                boolean IsNew = cbIsNew.isChecked();
                String satStart = etSatHoursOpen.getText().toString();
                String satClose = etSatHoursClose.getText().toString();
                String sunStart = etSunHoursOpen.getText().toString();
                String sunClose = etSunHoursClose.getText().toString();

                if(name == null || name.isEmpty()) {
                    etName.setError("Name field cannot be BLANK");
                    etName.requestFocus();
                }
                else if(address == null || address.isEmpty()) {
                    etAddress.setError("Address field cannot be BLANK");
                    etAddress.requestFocus();
                }
                else {
                    RequestPackage requestPackage = new RequestPackage();
                    requestPackage.setMethod(HttpMethod.PUT);
                    requestPackage.setEndPoint(JSON_URL + "/" + selectedBuilding.getBuildingId());
                    requestPackage.setParam("nameEN", name);
                    requestPackage.setParam("addressEN", address);
                    requestPackage.setParam("isNewBuilding", IsNew + "");
                    if (description != null && !description.isEmpty()) {
                        requestPackage.setParam("descriptionEN", description);
                    }

                    //todo: hours validation
                    // setParam crashes when sending a null value therefore setting hours to CLOSED
                    // is not implemented
                    Boolean resOpen = time24hFormatValidator.validate( satStart );
                    Boolean resClose = time24hFormatValidator.validate( satClose );
                    if ( resOpen && resClose ) {
                        requestPackage.setParam("saturdayStart", "2018/07/07 " + satStart);
                        requestPackage.setParam("saturdayClose", "2018/07/07 " + satClose);
                    }
//                    else if ( satStart.isEmpty() || satClose.isEmpty() ) {
//                        // if empty set as closed
//                        requestPackage.setParam("saturdayStart", null);
//                        requestPackage.setParam("saturdayClose", null);
//                    }

                    Boolean resSunOpen = time24hFormatValidator.validate( sunStart );
                    Boolean resSunClose = time24hFormatValidator.validate( sunClose );
                    if ( resSunOpen && resSunClose ) {
                        requestPackage.setParam("sundayStart", "2018/07/08 " + sunStart);
                        requestPackage.setParam("sundayClose", "2018/07/08 " + sunClose);
                    }
//                    else if ( sunStart.isEmpty() || sunClose.isEmpty() ) {
//                        // if empty set as closed
//                        requestPackage.setParam("sundayStart", null);
//                        requestPackage.setParam("sundayClose", null);
//                    }

                    Intent intent = new Intent(EditActivity.this, MyService.class);
                    intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
                    startService(intent);

                    finish();
                }
            }
        });

        Button cancelButton = (Button) findViewById(R.id.addCancel);
        cancelButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                bitmap = (Bitmap) extras.get("data");
                if(bitmap != null){
                    //there is a picture
                    buildingImage.setImageBitmap(bitmap);
                }
            }

            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled: Take a Photo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
