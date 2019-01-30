package pt.simov.studentshandbook;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import pt.simov.firebase.FirebaseConfig;
import pt.simov.helpers.Manager;

public class LoginActivity extends AppCompatActivity {

    private EditText passwordField;
    private EditText emailField;
    private Button loginButton;
    private TextView signUpButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseConfig.getInstanceFirebaseAuth();
        firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null) {
            Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } else {
            setContentView(R.layout.activity_login);
            passwordField = (EditText) findViewById(R.id.passwordField);
            emailField = (EditText) findViewById(R.id.emailField);
            loginButton = (Button) findViewById(R.id.loginButton);
            signUpButton = (TextView) findViewById(R.id.signUpButton);

            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    startActivity(intent);
                }
            });
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (emailField.getText().toString().length() > 0 && passwordField.getText().toString().length() > 0) {
                        firebaseAuth.signInWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent i = new Intent(getApplicationContext(), MainMenuActivity.class);
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                        Toast.makeText(LoginActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                                        emailField.setText("");
                                        passwordField.setText("");
                                    } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                        Toast.makeText(LoginActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                                        passwordField.setText("");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } else {
                        Toast.makeText(LoginActivity.this, "Please fill the fields", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
