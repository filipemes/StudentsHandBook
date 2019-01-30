package pt.simov.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class Anomaly implements Parcelable {

    private String description;
    private String publisher;
    private String publisherUid;
    private String status;//solved or not
    private String room;
    private String uid;
    private String date;
    private String imageURL;

    public Anomaly(String description, String publisher, String publisherUid, String status, String room, String uid, String date, String imageURL) {
        this.description = description;
        this.publisherUid = publisherUid;
        this.publisher = publisher;
        this.status = status;
        this.room = room;
        this.uid = uid;
        this.date = date;
        this.imageURL = imageURL;
    }

    public Anomaly(String description, String publisher, String publisherUid, String status, String room, String uid, String date) {
        this.description = description;
        this.publisherUid = publisherUid;
        this.publisher = publisher;
        this.status = status;
        this.room = room;
        this.uid = uid;
        this.date = date;
    }

    protected Anomaly(Parcel in) {
        description = in.readString();
        publisher = in.readString();
        publisherUid = in.readString();
        status = in.readString();
        room = in.readString();
        uid = in.readString();
        date = in.readString();
    }

    public Anomaly() {
    }

    public static final Creator<Anomaly> CREATOR = new Creator<Anomaly>() {
        @Override
        public Anomaly createFromParcel(Parcel in) {
            return new Anomaly(in);
        }

        @Override
        public Anomaly[] newArray(int size) {
            return new Anomaly[size];
        }
    };

    public String getDescription() {
        return this.description;
    }

    public String getPublisher() {
        return this.publisher;
    }

    public String getPublisherUid() {
        return this.publisherUid;
    }

    public String getStatus() {
        return this.status;
    }

    public String getRoom() {
        return this.room;
    }

    public String getUid() {
        return this.uid;
    }

    public String getDate() {
        return this.date;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getImageURL() {
        return this.imageURL;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(description);
        dest.writeString(publisher);
        dest.writeString(publisherUid);
        dest.writeString(status);
        dest.writeString(room);
        dest.writeString(uid);
        dest.writeString(date);
        dest.writeString(imageURL);
    }

}
