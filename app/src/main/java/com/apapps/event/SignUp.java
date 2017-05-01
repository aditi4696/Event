package com.apapps.event;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.R.attr.name;

public class SignUp extends AppCompatActivity {

    private EditText inputEmail, inputPassword, inputUname;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase Instance;
    private DatabaseReference DbInstance;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputUname = (EditText) findViewById(R.id.uname);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUp.this, Login.class));
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String uname = inputUname.getText().toString().trim();
                final String email = inputEmail.getText().toString().trim();
                final String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(uname)) {
                    Toast.makeText(getApplicationContext(), "Enter user name!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                final FirebaseUser user = auth.getCurrentUser();
                                                UserProfileChangeRequest change = new UserProfileChangeRequest.Builder().setDisplayName(uname).build();
                                                user.updateProfile(change).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            progressBar.setVisibility(View.GONE);
                                                            Toast.makeText(SignUp.this,"User Name stored in DB", Toast.LENGTH_SHORT).show();
                                                            Toast.makeText(SignUp.this, "Account created.", Toast.LENGTH_SHORT).show();

                                                            String uId = user.getUid();
                                                            String uName = user.getDisplayName();
                                                            Instance = FirebaseDatabase.getInstance();
                                                            DbInstance = Instance.getReference("users");
                                                            AppUsers userNew = new AppUsers(uId, uName);

                                                            DbInstance.child(uId).setValue(userNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        startActivity(new Intent(SignUp.this, HomeScreen.class));
                                                                    } else {
                                                                        Toast.makeText(SignUp.this, "Failed to create event. Please try again later.", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });

                                                            if (!task.isSuccessful()) {
                                                                Toast.makeText(SignUp.this, "Authentication failed." + task.getException(),
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    }
                                                });
                                            }
                                        });
                            }
                        });

            }
        });
    }
}