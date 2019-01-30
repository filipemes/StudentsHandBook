package pt.simov.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

public class POI implements Parcelable {
    private String uid;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private String imageLink;
    private String cellPhone;
    private boolean confirmed;

    public POI(String name, String address, Double latitude, Double longitude){
        this.name=name;
        this.address=address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public POI(String name, String address){
        this.name=name;
        this.address=address;
    }

    public POI(String name, Double latitude, Double longitude){
        this.name=name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public POI(){

    }

    public POI(Parcel in) {
        this.uid=in.readString();
        this.name = in.readString();
        this.address = in.readString();
        this.latitude =in.readDouble();
        this.longitude = in.readDouble();
        this.imageLink = in.readString();
        this.cellPhone = in.readString();
    }

    public String getUid(){return this.uid;}

    public void setUid(String uid){
        this.uid=uid;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name=name;
    }

    public HashMap<String,Double> getCoords(){
        if(this.latitude == null || this.longitude == null){
            return new HashMap<String, Double>();
        }
        HashMap<String,Double> list = new HashMap<String, Double>();
        list.put("Latitude",this.latitude);
        list.put("Longitude",this.longitude);
        return list;
    }

    public void setCoords(HashMap<String,Double> coords){
        this.latitude =coords.get("Latitude");
        this.longitude =coords.get("Longitude");
    }

    public String getAddress(){
        return this.address;
    }

    public void setAddress(String address){
        this.address=address;
    }

    public String getImageLink(){
        return this.imageLink;
    }

    public void setImageLink(String imageLink){
        this.imageLink =imageLink;
    }

    public String getCellPhone(){ return this.cellPhone; }

    public void setCellPhone(String cellPhone){
        this.cellPhone = cellPhone;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.uid);
        parcel.writeString(this.name);
        parcel.writeString(this.address);
        parcel.writeDouble(this.latitude);
        parcel.writeDouble(this.longitude);
        parcel.writeString(this.imageLink);
        parcel.writeString(this.cellPhone);
    }

    public static final Parcelable.Creator<POI> CREATOR
            = new Parcelable.Creator<POI>() {
        public POI createFromParcel(Parcel in) {
            return new POI(in);
        }

        public POI[] newArray(int size) {
            return new POI[size];
        }
    };

}
