package intents.ca.edumedia.desj0251.opendoorsfinal.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * RequestPackage - POJO composed of the endPoint (i.e. URL address), method (default: "GET"), and
 * a map of query params.
 *
 * Implements the Parceable interface, allowing instances of this class to be passed around in Intents.
 *
 * Source - https://gist.github.com/1bc5ed77d08c80d75117b62e987887bb.git
 *
 * @providedBy Gerald Hurdle
 * @author David Gasner
 * @edited John Desjardins
 */
public class RequestPackage implements Parcelable {

    private String endPoint;
    private HttpMethod method = HttpMethod.GET;
    private Map<String, String> params = new HashMap<>();

    public String getEndpoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setParam(String key, String value) {
        params.put(key, value);
    }

    public String getEncodedParams() {
        StringBuilder sb = new StringBuilder();
        for (String key : params.keySet()) {
            String value = null;
            try {
                value = URLEncoder.encode(params.get(key), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(key).append("=").append(value);
        }
        return sb.toString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.endPoint);
        dest.writeString(this.method.toString());
        dest.writeInt(this.params.size());
        for (Map.Entry<String, String> entry : this.params.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeString(entry.getValue());
        }
    }

    public RequestPackage() {
    }

    protected RequestPackage(Parcel in) {
        this.endPoint = in.readString();
        this.method = HttpMethod.valueOf(in.readString());
        int paramsSize = in.readInt();
        this.params = new HashMap<String, String>(paramsSize);
        for (int i = 0; i < paramsSize; i++) {
            String key = in.readString();
            String value = in.readString();
            this.params.put(key, value);
        }
    }

    public static final Creator<RequestPackage> CREATOR = new Creator<RequestPackage>() {
        @Override
        public RequestPackage createFromParcel(Parcel source) {
            return new RequestPackage(source);
        }

        @Override
        public RequestPackage[] newArray(int size) {
            return new RequestPackage[size];
        }
    };
}
