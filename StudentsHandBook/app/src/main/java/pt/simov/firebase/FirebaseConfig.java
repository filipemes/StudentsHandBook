package pt.simov.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig {

    private static FirebaseAuth firebaseAuth;
    private static DatabaseReference firebaseReference;

    /**
     * Get instance of FirebaseAuth
     * @return
     */
    public static FirebaseAuth getInstanceFirebaseAuth(){
         if(firebaseAuth==null){
             firebaseAuth= FirebaseAuth.getInstance();
             return firebaseAuth;
         }
         return firebaseAuth;
    }

    /**
     * Get instance of DatabaseReference
     * @return
     */
    public static DatabaseReference getInstanceDatabaseReference(){
        if(firebaseReference==null){
            firebaseReference= FirebaseDatabase.getInstance().getReference();
            return firebaseReference;
        }
        return firebaseReference;
    }

}
