package intents.ca.edumedia.desj0251.opendoorsfinal.services;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.gson.Gson;

import java.io.IOException;

import intents.ca.edumedia.desj0251.opendoorsfinal.model.DataItem;
import intents.ca.edumedia.desj0251.opendoorsfinal.utils.HttpHelper;
import intents.ca.edumedia.desj0251.opendoorsfinal.utils.RequestPackage;

/**
 * Class MyService.
 *
 * Fetch the data at URI.
 * Return an array of Building[] as a broadcast message.
 *
 * @providedBy Gerald Hurdle
 * @author David Gasner
 * @edited John Desjardins
 */

public class MyService extends IntentService {

    public static final String MY_SERVICE_MESSAGE = "myServiceMessage";
    public static final String MY_SERVICE_PAYLOAD = "myServicePayload";
    public static final String MY_SERVICE_RESPONSE = "myServiceResponse";
    public static final String MY_SERVICE_EXCEPTION = "myServiceException";
    public static final String REQUEST_PACKAGE = "requestPackage";

    public MyService() {
        super("MyService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        RequestPackage requestPackage = (RequestPackage) intent.getParcelableExtra(REQUEST_PACKAGE);

        String response;
        try {
            response = HttpHelper.downloadUrl(requestPackage, "desj0251", "password");
        } catch (IOException e) {
            e.printStackTrace();
            Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
            messageIntent.putExtra(MY_SERVICE_EXCEPTION, e.getMessage());
            LocalBroadcastManager manager =
                    LocalBroadcastManager.getInstance(getApplicationContext());
            manager.sendBroadcast(messageIntent);
            return;
        }

        Intent messageIntent = new Intent(MY_SERVICE_MESSAGE);
        Gson gson = new Gson();
        switch (requestPackage.getMethod()) {
            case GET:
                DataItem[] buildingsArray = gson.fromJson(response, DataItem[].class);
                messageIntent.putExtra(MY_SERVICE_PAYLOAD, buildingsArray);
                break;

            case POST:
            case PUT:
            case DELETE:
                DataItem building = gson.fromJson(response, DataItem.class);
                messageIntent.putExtra(MY_SERVICE_RESPONSE, building);
                break;
        }
        LocalBroadcastManager manager =
                LocalBroadcastManager.getInstance(getApplicationContext());
        manager.sendBroadcast(messageIntent);
    }
}