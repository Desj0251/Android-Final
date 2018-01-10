package intents.ca.edumedia.desj0251.opendoorsfinal.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * POJO class for a Planet JSON object.
 *
 * Before You Begin - install the Parceable plugin for Android Studio (1)
 *
 * Copy 'n paste the JSON structure for a Planet: https://planets.mybluemix.net/planets/0
 *
 * Next, use this online tool to generate this POJO class: http://www.jsonschema2pojo.org/
 * Apply these settings:
 *   Package: mad9132.planets.model
 *   Class name: PlanetPOJO
 *   Target language: Java
 *   Source type: JSON
 *   Annotation style: None
 *   Check:
 *      Use primitive types
 *      Use double numbers
 *      Include getters and setters
 *
 * Finally, implement the Parceable interface (click 'PlanetPOJO' > Code > Generate... > Parceable
 *
 * @providedBy Gerald Hurdle
 * @author Gerald.Hurdle@AlgonquinCollege.com
 * @edited John Desjardins
 *
 * Reference
 * 1) "Model response data with POJO classes", Chapter 3. Requesting Data over the Web, Android
 *     App Development: RESTful Web Services by David Gassner
 */

public class DataItem implements Parcelable {

    private String id;
    private int buildingId;
    private String nameEN;
    private String categoryFR;
    private int categoryId;
    private double longitude;
    private double latitude;
    private String postalCode;
    private String province;
    private String city;
    private String imageDescriptionFR;
    private String imageDescriptionEN;
    private String image;
    private String sundayClose;
    private String sundayStart;
    private String saturdayClose;
    private String saturdayStart;
    private String categoryEN;
    private String descriptionFR;
    private String descriptionEN;
    private String addressFR;
    private String addressEN;
    private boolean isNewBuilding;
    private String nameFR;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public String getNameEN() {
        return nameEN;
    }

    public void setNameEN(String nameEN) {
        this.nameEN = nameEN;
    }

    public String getCategoryFR() {
        return categoryFR;
    }

    public void setCategoryFR(String categoryFR) {
        this.categoryFR = categoryFR;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getImageDescriptionFR() {
        return imageDescriptionFR;
    }

    public void setImageDescriptionFR(String imageDescriptionFR) {
        this.imageDescriptionFR = imageDescriptionFR;
    }

    public String getImageDescriptionEN() {
        return imageDescriptionEN;
    }

    public void setImageDescriptionEN(String imageDescriptionEN) {
        this.imageDescriptionEN = imageDescriptionEN;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSundayClose() {
        return sundayClose;
    }

    public void setSundayClose(String sundayClose) {
        this.sundayClose = sundayClose;
    }

    public String getSundayStart() {
        return sundayStart;
    }

    public void setSundayStart(String sundayStart) {
        this.sundayStart = sundayStart;
    }

    public String getSaturdayClose() {
        return saturdayClose;
    }

    public void setSaturdayClose(String saturdayClose) {
        this.saturdayClose = saturdayClose;
    }

    public String getSaturdayStart() {
        return saturdayStart;
    }

    public void setSaturdayStart(String saturdayStart) {
        this.saturdayStart = saturdayStart;
    }

    public String getCategoryEN() {
        return categoryEN;
    }

    public void setCategoryEN(String categoryEN) {
        this.categoryEN = categoryEN;
    }

    public String getDescriptionFR() {
        return descriptionFR;
    }

    public void setDescriptionFR(String descriptionFR) {
        this.descriptionFR = descriptionFR;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public void setDescriptionEN(String descriptionEN) {
        this.descriptionEN = descriptionEN;
    }

    public String getAddressFR() {
        return addressFR;
    }

    public void setAddressFR(String addressFR) {
        this.addressFR = addressFR;
    }

    public String getAddressEN() {
        return addressEN;
    }

    public void setAddressEN(String addressEN) {
        this.addressEN = addressEN;
    }

    public boolean isIsNewBuilding() {
        return isNewBuilding;
    }

    public void setIsNewBuilding(boolean isNewBuilding) {
        this.isNewBuilding = isNewBuilding;
    }

    public String getNameFR() {
        return nameFR;
    }

    public void setNameFR(String nameFR) {
        this.nameFR = nameFR;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeInt(this.buildingId);
        dest.writeString(this.nameEN);
        dest.writeString(this.categoryFR);
        dest.writeInt(this.categoryId);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeString(this.postalCode);
        dest.writeString(this.province);
        dest.writeString(this.city);
        dest.writeString(this.imageDescriptionFR);
        dest.writeString(this.imageDescriptionEN);
        dest.writeString(this.image);
        dest.writeString(this.sundayClose);
        dest.writeString(this.sundayStart);
        dest.writeString(this.saturdayClose);
        dest.writeString(this.saturdayStart);
        dest.writeString(this.categoryEN);
        dest.writeString(this.descriptionFR);
        dest.writeString(this.descriptionEN);
        dest.writeString(this.addressFR);
        dest.writeString(this.addressEN);
        dest.writeByte(this.isNewBuilding ? (byte) 1 : (byte) 0);
        dest.writeString(this.nameFR);
    }

    public DataItem() {
    }

    protected DataItem(Parcel in) {
        this.id = in.readString();
        this.buildingId = in.readInt();
        this.nameEN = in.readString();
        this.categoryFR = in.readString();
        this.categoryId = in.readInt();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.postalCode = in.readString();
        this.province = in.readString();
        this.city = in.readString();
        this.imageDescriptionFR = in.readString();
        this.imageDescriptionEN = in.readString();
        this.image = in.readString();
        this.sundayClose = in.readString();
        this.sundayStart = in.readString();
        this.saturdayClose = in.readString();
        this.saturdayStart = in.readString();
        this.categoryEN = in.readString();
        this.descriptionFR = in.readString();
        this.descriptionEN = in.readString();
        this.addressFR = in.readString();
        this.addressEN = in.readString();
        this.isNewBuilding = in.readByte() != 0;
        this.nameFR = in.readString();
    }

    public static final Creator<DataItem> CREATOR = new Creator<DataItem>() {
        @Override
        public DataItem createFromParcel(Parcel source) {
            return new DataItem(source);
        }

        @Override
        public DataItem[] newArray(int size) {
            return new DataItem[size];
        }
    };
}
