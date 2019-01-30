package pt.simov.studentshandbook;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;

import pt.simov.entities.User;
import pt.simov.firebase.FirebaseConfig;
import pt.simov.services.ServiceFacade;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailField;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText passwordField;
    private EditText passwordConfirmField;
    private Spinner courseSpinner;
    private Spinner classSpinner;
    private Button buttonSignUp;
    private FirebaseAuth firebaseAuth;
    private ServiceFacade serviceFacade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        serviceFacade= new ServiceFacade();
        firebaseAuth=FirebaseConfig.getInstanceFirebaseAuth();
        emailField = (EditText) findViewById(R.id.emailField);
        firstNameField = (EditText) findViewById(R.id.firstNameField);
        lastNameField = (EditText) findViewById(R.id.lastNameField);
        passwordField = (EditText) findViewById(R.id.passwordField);
        passwordConfirmField = (EditText) findViewById(R.id.passwordConfirmField);
        courseSpinner = findViewById(R.id.courseSpinner);
        courseSpinner.setVisibility(View.VISIBLE);
        classSpinner = findViewById(R.id.classSpinner);
        classSpinner.setVisibility(View.VISIBLE);
        buttonSignUp = (Button) findViewById(R.id.signUpButton);
        ArrayAdapter<CharSequence> spinnerCourseAdapter = ArrayAdapter.createFromResource(this,
                R.array.course_array, android.R.layout.simple_spinner_item);
        spinnerCourseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> spinnerClassAdapter = ArrayAdapter.createFromResource(this,
                R.array.class_array, android.R.layout.simple_spinner_item);
        spinnerClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(spinnerCourseAdapter);
        classSpinner.setAdapter(spinnerClassAdapter);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailField.getText().toString().length() > 0 && firstNameField.getText().toString().length() > 0
                        && lastNameField.getText().toString().length() > 0 && passwordField.getText().toString().length() > 0 && passwordConfirmField.getText().toString().length() > 0 && passwordField.getText().toString().compareTo(passwordConfirmField.getText().toString())==0) {
                    firebaseAuth.createUserWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString())
                            .addOnCompleteListener(
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                String number = emailField.getText().toString().split("@")[0];
                                                serviceFacade.getServiceUserInterface().postUser(firstNameField.getText().toString(), lastNameField.getText().toString(), emailField.getText().toString(), number, task.getResult().getUser().getUid().toString(),courseSpinner.getSelectedItem().toString(),classSpinner.getSelectedItem().toString());
                                                finish();
                                            } else {
                                                try {
                                                    throw task.getException();
                                                } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                                    Toast.makeText(SignUpActivity.this, "Weak Password", Toast.LENGTH_LONG).show();
                                                    passwordField.setText("");
                                                    passwordConfirmField.setText("");
                                                } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                                    Toast.makeText(SignUpActivity.this, "Malformed Email", Toast.LENGTH_LONG).show();
                                                    passwordField.setText("");
                                                    passwordConfirmField.setText("");
                                                    emailField.setText("");
                                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                                    //Log.d(TAG, "onComplete: exist_email");
                                                    Toast.makeText(SignUpActivity.this, "This email already exists", Toast.LENGTH_LONG).show();
                                                    passwordField.setText("");
                                                    passwordConfirmField.setText("");
                                                    emailField.setText("");
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                    }
                            );
                }
                else if(emailField.getText().toString().length() > 0 && firstNameField.getText().toString().length() > 0
                        && lastNameField.getText().toString().length() > 0 && passwordField.getText().toString().length() > 0
                        && passwordConfirmField.getText().toString().length() > 0 && passwordField.getText().toString().compareTo(passwordConfirmField.getText().toString())!=0) {
                    Toast.makeText(SignUpActivity.this, "Passwords are not the same", Toast.LENGTH_SHORT).show();
                    passwordField.setText("");
                    passwordConfirmField.setText("");
                }
                else if(emailField.getText().toString().length() == 0 || firstNameField.getText().toString().length() == 0
                        || lastNameField.getText().toString().length() == 0 || passwordField.getText().toString().length() == 0 || passwordConfirmField.getText().toString().length()==0) {
                    Toast.makeText(SignUpActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    passwordField.setText("");
                    passwordConfirmField.setText("");
                }
            }
        });
    }
}
