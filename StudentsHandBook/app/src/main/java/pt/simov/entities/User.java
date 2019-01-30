package pt.simov.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class User implements Parcelable{

    private String firstName;
    private String lastName;
    private String email;
    private String number;
    private String uid;
    private String course;
    private String courseclass;
    private ArrayList<ScheduleEvent> eventList;

    public User(String firstName, String lastName, String email, String number,String uid,String course, String courseclass) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.number = number;
        this.uid=uid;
        this.course=course;
        this.courseclass=courseclass;
    }

    protected User(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        number = in.readString();
        uid = in.readString();
    }

	public User() {
    }

    public User(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getUid() { return uid; }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getNumber() {
        return number;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getCourseclass() {
        return courseclass;
    }

    public void setCourseclass(String courseclass) {
        this.courseclass = courseclass;
    }

    public ArrayList<ScheduleEvent> getEventList() {
        return eventList;
    }

    public void clearEventList(){
        this.eventList = new ArrayList<>();
    }

    public void setEventList(ArrayList<ScheduleEvent> eventList) {
        this.eventList = eventList;
    }

    public void addEvent(ScheduleEvent ev){
        this.eventList.add(ev);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(number);
        dest.writeString(uid);
        dest.writeString(course);
        dest.writeString(courseclass);
    }
}
