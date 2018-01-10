package intents.ca.edumedia.desj0251.opendoorsfinal.utils;

import android.support.annotation.NonNull;
import android.util.Base64;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Helper class for working with a remote server
 *
 * @povidedBy Gerald Hurdle
 * @author David Gassner
 * @edited John Desjardins
 */
public class HttpHelper {

    // TODO #1 - declare 2 class variables
    private static final String DELIMITER = "--";
    private static final String BOUNDARY  = "UploadImage" + Long.toString(System.currentTimeMillis()) + "UploadImage";

    /**
     * Returns text from a URL on a web server (no authentication)
     *
     * @param requestPackage
     * @return
     * @throws IOException
     */
    public static String downloadUrl(RequestPackage requestPackage) throws IOException {
        return downloadUrl(requestPackage, "", "");
    }

    /**
     * Returns text from a URL on a web server with basic authentication
     *
     * @param requestPackage
     * @param user
     * @param password
     * @return
     * @throws IOException
     */
    public static String downloadUrl(RequestPackage requestPackage, @NonNull String user, @NonNull String password) throws IOException {

        String address = requestPackage.getEndpoint();
        String encodedParams = requestPackage.getEncodedParams();

        if (requestPackage.getMethod() == HttpMethod.GET &&
                encodedParams.length() > 0) {
            address = String.format("%s?%s", address, encodedParams);
        }

        StringBuilder loginBuilder = null;
        if ( (user.isEmpty() == false) && (password.isEmpty() == false) ) {
            byte[] loginBytes = (user + ":" + password).getBytes();
            loginBuilder = new StringBuilder()
                    .append("Basic ")
                    .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));
        }

        InputStream is = null;
        try {
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (loginBuilder != null) {
                conn.addRequestProperty("Authorization", loginBuilder.toString());
            }
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(requestPackage.getMethod().toString());
            conn.setDoInput(true);
            JSONObject json = new JSONObject(requestPackage.getParams());
            String params = json.toString();
            if ( (requestPackage.getMethod() == HttpMethod.POST ||
                    requestPackage.getMethod() == HttpMethod.PUT) &&
                    params.length() > 0) {
                // The web service expects the request body to be in JSON format.
                conn.addRequestProperty("Accept", "application/json");
                conn.addRequestProperty("Content-Type", "application/json");
                conn.setDoInput(true);
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(params);
                writer.flush();
                writer.close();
            }
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Got response code " + responseCode);
            }
            is = conn.getInputStream();
            return readStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    /**
     * Reads an InputStream and converts it to a String.
     *
     * @param stream
     * @return
     * @throws IOException
     */
    private static String readStream(InputStream stream) throws IOException {

        byte[] buffer = new byte[1024];
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        BufferedOutputStream out = null;
        try {
            int length = 0;
            out = new BufferedOutputStream(byteArray);
            while ((length = stream.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
            return byteArray.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    // TODO #2 - new class method: uploadFile( )
    /**
     * Upload the data of filename to remote server (no authentication).
     *
     * @param requestPackage
     * @param filename
     * @param data
     * @return
     * @throws Exception
     */
    public static String uploadFile(RequestPackage requestPackage, String filename, byte[] data) throws Exception {
        return uploadFile(requestPackage, "", "", filename, data);
    }

    /**
     * Upload the data of filename to remove server. Authenticate as user with password.
     *
     * @param requestPackage
     * @param user
     * @param password
     * @param filename
     * @param data
     * @return
     * @throws Exception
     */
    public static String uploadFile(RequestPackage requestPackage, @NonNull String user, @NonNull String password, String filename, byte[] data) throws Exception {
        String address = requestPackage.getEndpoint();

        StringBuilder loginBuilder = null;
        if ( (user.isEmpty() == false) && (password.isEmpty() == false) ) {
            byte[] loginBytes = (user + ":" + password).getBytes();
            loginBuilder = new StringBuilder()
                    .append("Basic ")
                    .append(Base64.encodeToString(loginBytes, Base64.DEFAULT));
        }

        HttpURLConnection conn = null;
        try {
            // Make a connect to the server
            URL url = new URL(address);
            conn = (HttpURLConnection) url.openConnection();
            if (loginBuilder != null) {
                conn.addRequestProperty("Authorization", loginBuilder.toString());
            }
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestPackage.getMethod().toString());
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

            // Send the body
            DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
            dataOS.write((DELIMITER + BOUNDARY + "\r\n").getBytes());
            dataOS.write(("Content-Disposition: form-data; name=\"" + "image" + "\"; filename=\"" + filename + "\"\r\n").getBytes());
            dataOS.write(("Content-Type: image/jpeg\r\n").getBytes());
            dataOS.write(("Content-Transfer-Encoding: binary\r\n").getBytes());
            dataOS.write("\r\n".getBytes());

            dataOS.write(data);

            dataOS.write("\r\n".getBytes());
            dataOS.write((DELIMITER + BOUNDARY + DELIMITER + "\r\n").getBytes());

            dataOS.flush();
            dataOS.close();

            // Ensure we got the HTTP 200 response code
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                throw new Exception(String.format("Received the response code %d from the URL %s", responseCode, url));
            }

            // Read the response
            InputStream is = conn.getInputStream();
            byte[] b1 = new byte[1024];
            StringBuffer buffer = new StringBuffer();

            while (is.read(b1) != -1)
                buffer.append(new String(b1));

            return buffer.toString().trim();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }
}
