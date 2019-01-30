package pt.simov.interfaces;

import pt.simov.entities.User;

public interface ServiceUserInterface {

    public void getEvents();

    public void postUser(String firstName, String lastName,String email,String number,String uuid, String couse, String courseclass);

    public void changeCourse(String uid, String course);

    public void changeClass(String uid, String courseClass);

    public void registerEventUser(String uid);

    public User getUser();
}
