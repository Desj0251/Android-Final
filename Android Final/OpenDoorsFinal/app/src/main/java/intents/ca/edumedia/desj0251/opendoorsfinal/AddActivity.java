package intents.ca.edumedia.desj0251.opendoorsfinal;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import intents.ca.edumedia.desj0251.opendoorsfinal.model.DataItem;
import intents.ca.edumedia.desj0251.opendoorsfinal.services.MyService;
import intents.ca.edumedia.desj0251.opendoorsfinal.utils.HttpMethod;
import intents.ca.edumedia.desj0251.opendoorsfinal.utils.RequestPackage;

import static intents.ca.edumedia.desj0251.opendoorsfinal.MainActivity.NEW_BUILDING_DATA;
import static intents.ca.edumedia.desj0251.opendoorsfinal.MainActivity.NEW_BUILDING_IMAGE;

/**
 *  Add Activity: Allows for the creation of new buildings
 *  @author John Desjardins (desj0251@algonquinlive.com)
 */
public class AddActivity extends AppCompatActivity {
    private final static int CAMERA_REQUEST_CODE = 100;
    private static final String  JSON_URL = "https://doors-open-ottawa.mybluemix.net/buildings";

    private EditText etName;
    private EditText etAddress;
    private EditText etDescription;
    private Bitmap bitmap;
    private ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etName = (EditText) findViewById(R.id.etName);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etDescription = (EditText) findViewById(R.id.etDescription);
        photoView = (ImageView) findViewById(R.id.buildingImage);
        bitmap = null;

        //todo: camera takes picture and places it in the imageview
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

                DataItem newBuilding = new DataItem();
                newBuilding.setNameEN( name );
                newBuilding.setAddressEN( address );

                //todo: Error Checking

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
                    requestPackage.setMethod(HttpMethod.POST);
                    requestPackage.setEndPoint(JSON_URL);
                    requestPackage.setParam("nameEN", name);
                    requestPackage.setParam("addressEN", address);
                    requestPackage.setParam("isNewBuilding", true + "");

                    if (description != null && !description.isEmpty()) {
                        requestPackage.setParam("descriptionEN", description);
                    }

                    Intent intent = new Intent(getApplicationContext(), MyService.class);
                    intent.putExtra(MyService.REQUEST_PACKAGE, requestPackage);
                    startService(intent);

                    intent = new Intent();
                    intent.putExtra(NEW_BUILDING_DATA, newBuilding);
                    if (bitmap != null) {
                        intent.putExtra(NEW_BUILDING_IMAGE, bitmap);
                    }
                    setResult(RESULT_OK, intent);
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
                    photoView.setImageBitmap(bitmap);
                }
            }

            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled: Take a Photo", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
