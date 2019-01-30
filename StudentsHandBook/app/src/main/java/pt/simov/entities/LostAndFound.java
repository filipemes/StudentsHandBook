package pt.simov.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.time.LocalDateTime;

public class LostAndFound implements Parcelable {

    private String location;
    private String uid;
    private String subject;
    private String dateNow;
    private String lostOrFound;
    private String imageLink;
    private String description;
    private String observation;
    private String status;
    private User user;

    public LostAndFound(
                        String location,
                        String subject,
                        LocalDateTime dateNow, String lostOrFound, String imageLink,
                        String description,
                        String observation,
                        String status,
                        User uid) {
        this.status = status;
        this.location = location;
        this.subject = subject;
        this.description = description;
        this.observation = observation;
        this.lostOrFound = lostOrFound;
        this.imageLink = imageLink;
        this.user = uid;
    }

    public LostAndFound() {
    }

    protected LostAndFound(Parcel in) {
        location = in.readString();
        subject = in.readString();
        lostOrFound = in.readString();
        imageLink = in.readString();
        //user = in.readString();
        description = in.readString();
        observation = in.readString();
        status = in.readString();
    }



    public static final Creator<LostAndFound> CREATOR = new Creator<LostAndFound>() {
        @Override
        public LostAndFound createFromParcel(Parcel in) {
            return new LostAndFound(in);
        }

        @Override
        public LostAndFound[] newArray(int size) {
            return new LostAndFound[size];
        }
    };

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return this.uid;
    }

    public User getUser() {
        return user;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getDateNow() {
        return dateNow;
    }

    public void setDateNow(String dateNow) {
        this.dateNow = dateNow;
    }

    public void setUser(User uid) {
        this.user = uid;
    }

    public String getSubject() {
        return subject;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLostOrFound() {
        return lostOrFound;
    }

    public void setLostOrFound(String lostOrFound) {
        this.lostOrFound = lostOrFound;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subject);
        dest.writeString(location);
        dest.writeString(imageLink);
        //dest.writeString(uid);
        dest.writeString(description);
        dest.writeString(observation);
        dest.writeString(status);
    }

    @Override
    public String toString() {
        return "LostAndFound{" +
                "location='" + location + '\'' +
                ", subject='" + subject + '\'' +
                ", dateNow='" + dateNow + '\'' +
                ", lostOrFound='" + lostOrFound + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", description='" + description + '\'' +
                ", observation='" + observation + '\'' +
                ", status='" + status + '\'' +
                ", user=" + user +
                '}';
    }
}
