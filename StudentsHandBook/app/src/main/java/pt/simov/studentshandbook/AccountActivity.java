package pt.simov.studentshandbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import pt.simov.firebase.FirebaseConfig;
import pt.simov.helpers.Manager;

public class AccountActivity extends BaseActivity{

    private Button logoutButton;
    private Button confirmNewPasswordButton;
    private EditText passwordField;
    private EditText newPasswordField;
    private EditText newPasswordConfirmationField;
    private TextView nameField;
    private TextView emailField;
    private Spinner courseSpinner;
    private Spinner classSpinner;
    private Boolean changeCourse;
    private Boolean changeClass;
    private FirebaseAuth firebaseAuth;
    private Manager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        initNaviationView(2);
        changeCourse=false;
        changeClass=false;
        logoutButton = (Button) findViewById(R.id.logoutButton);
        confirmNewPasswordButton=(Button)findViewById(R.id.confirmNewPasswordButton);
        passwordField=(EditText) findViewById(R.id.passwordField);
        newPasswordField=(EditText) findViewById(R.id.newPasswordField);
        newPasswordConfirmationField=(EditText) findViewById(R.id.newPasswordConfirmationField);
        nameField=(TextView) findViewById(R.id.accountNameField);
        emailField=(TextView) findViewById(R.id.accountEmailField);
        courseSpinner = findViewById(R.id.courseSpinner);
        courseSpinner.setVisibility(View.VISIBLE);
        classSpinner = findViewById(R.id.classSpinner);
        classSpinner.setVisibility(View.VISIBLE);
        firebaseAuth = FirebaseConfig.getInstanceFirebaseAuth();
        nameField.setText(manager.getInstance().getUser().getFirstName()+ " "+manager.getInstance().getUser().getLastName());
        emailField.setText(manager.getInstance().getUser().getEmail());
        ArrayAdapter<CharSequence> spinnerCourseAdapter = ArrayAdapter.createFromResource(this,
                R.array.course_array, android.R.layout.simple_spinner_item);
        spinnerCourseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<CharSequence> spinnerClassAdapter = ArrayAdapter.createFromResource(this,
                R.array.class_array, android.R.layout.simple_spinner_item);
        spinnerClassAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(spinnerCourseAdapter);
        classSpinner.setAdapter(spinnerClassAdapter);
        if (Manager.getInstance().getUser().getCourse() != null) {
            int spinnerPosition = spinnerCourseAdapter.getPosition(Manager.getInstance().getUser().getCourse());
            courseSpinner.setSelection(spinnerPosition);
        }
        if (Manager.getInstance().getUser().getCourseclass() != null) {
            int spinnerPosition = spinnerClassAdapter.getPosition(Manager.getInstance().getUser().getCourseclass());
            classSpinner.setSelection(spinnerPosition);
        }
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(changeCourse){
                    Manager.getInstance().getServiceFacade().getServiceUserInterface().changeCourse(firebaseAuth.getInstance().getCurrentUser().getUid(),courseSpinner.getSelectedItem().toString());
                    Manager.getInstance().getUser().setCourse(courseSpinner.getSelectedItem().toString());
                    Manager.getInstance().getServiceFacade().getServiceUserInterface().getEvents();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(changeClass){
                    Manager.getInstance().getServiceFacade().getServiceUserInterface().changeClass(firebaseAuth.getInstance().getCurrentUser().getUid(),classSpinner.getSelectedItem().toString());
                    Manager.getInstance().getUser().setCourseclass(classSpinner.getSelectedItem().toString());
                    Manager.getInstance().getServiceFacade().getServiceUserInterface().getEvents();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        confirmNewPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordField.getText().toString().length() > 0
                        && newPasswordField.getText().toString().length() > 0
                        && newPasswordConfirmationField.getText().toString().length() > 0
                        && newPasswordField.getText().toString().compareTo(newPasswordConfirmationField.getText().toString()) == 0) {
                    firebaseAuth.signInWithEmailAndPassword(firebaseAuth.getCurrentUser().getEmail().toString(), passwordField.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                firebaseAuth.getCurrentUser().updatePassword(newPasswordField.getText().toString());
                                passwordField.setText("");
                                newPasswordField.setText("");
                                newPasswordConfirmationField.setText("");
                                Toast.makeText(AccountActivity.this, "Changed Password with success", Toast.LENGTH_SHORT).show();
                            } else {
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                    passwordField.setText("");
                                    newPasswordField.setText("");
                                    newPasswordConfirmationField.setText("");
                                } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                    Toast.makeText(AccountActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                    passwordField.setText("");
                                    newPasswordField.setText("");
                                    newPasswordConfirmationField.setText("");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                } else if (passwordField.getText().toString().length() > 0
                        && newPasswordField.getText().toString().length() > 0
                        && newPasswordConfirmationField.getText().toString().length() > 0
                        && newPasswordField.getText().toString().compareTo(newPasswordConfirmationField.getText().toString()) != 0) {
                    Toast.makeText(AccountActivity.this, "Wrong Passwords, please verify", Toast.LENGTH_SHORT).show();
                    passwordField.setText("");
                    newPasswordField.setText("");
                    newPasswordConfirmationField.setText("");
                } else {
                    Toast.makeText(AccountActivity.this, "Please fill the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        });
        changeCourse=true;
        changeClass=true;
    }

}
