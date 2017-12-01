package org.pltw.examples.triptracker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class LoginActivity extends AppCompatActivity {
    EditText enter_name;
    EditText enter_email;
    EditText enter_password;
    Button sign_up_button;
    Button login_button;
    TextView sign_up_text;
    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Backendless.initApp(this, getString(R.string.APP_ID), getString(R.string.API_KEY));
        setContentView(R.layout.activity_login);
        enter_email = (EditText) findViewById(R.id.enter_email);
        enter_password = (EditText) findViewById(R.id.enter_password);
        enter_name = (EditText) findViewById(R.id.enter_name);
        sign_up_button = (Button) findViewById(R.id.sign_up_button);
        login_button = (Button) findViewById(R.id.login_button);
        sign_up_text = (TextView) findViewById(R.id.sign_up_text);
        Backendless.initApp(this, getString(R.string.APP_ID), getString(R.string.API_KEY));
        login_button.setOnClickListener(new View.OnClickListener() {

            public void warnUser(String message){
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(message);
                builder.setTitle(R.string.authentication_error_title);
                builder.setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            public boolean validateData(String email, String password){
                if (email.contains("@")){
                    if (password.length() >= 6){
                        if (!password.contains(email.split("@")[0])){
                            return true;
                        }
                        else {
                            warnUser("Password cannot match or contain any portion of the email address.");
                        }
                    }
                    else {
                        warnUser("Password does not meet complexity requirements.");
                    }
                }
                else{
                    warnUser("Email address " + email + " doesn't follow standard address format. Please check and retype your email address.");
                }
                return false;
            }
            public void onClick(View view) {
                    String userEmail = enter_email.getText().toString();
                    String password = enter_password.getText().toString();
                    String name = enter_name.getText().toString();

                    userEmail = userEmail.trim();
                    password = password.trim();
                    name = name.trim();

                    if (!userEmail.isEmpty() && !password.isEmpty()) {
                        if (validateData(userEmail, password)) {
                            BackendlessUser newUser = new BackendlessUser();
                            newUser.setEmail(userEmail);
                            newUser.setPassword(password);
                            final ProgressDialog pDialog = ProgressDialog.show(LoginActivity.this,
                                    "Please Wait!",
                                    "Logging in...",
                                    true);
                            Backendless.UserService.login(userEmail, password, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser backendlessUser) {
                                    Log.i(TAG, "Successfully logged in: " + backendlessUser.getProperty("name"));
                                    Intent intent = new Intent(LoginActivity.this, TripListActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    pDialog.dismiss();
                                    warnUser(backendlessFault.getMessage());
                                }
                            });
                        }
                    }
                    else {
                        warnUser(getString(R.string.empty_field_signup_error));
                    }
            }
        });
        sign_up_button.setOnClickListener(new View.OnClickListener() {

            public void warnUser(String message){
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage(message);
                builder.setTitle(R.string.authentication_error_title);
                builder.setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }

            public boolean validateData(String email, String password){
                if (email.contains("@")){
                    if (password.length() >= 6){
                        if (!password.contains(email.split("@")[0])){
                            return true;
                        }
                        else {
                            warnUser("Password cannot match or contain any portion of the email address.");
                        }
                    }
                    else {
                        warnUser("Password does not meet complexity requirements.");
                    }
                }
                else{
                    warnUser("Email address " + email + " doesn't follow standard address format. Please check and retype your email address.");
                }
                return false;
            }
            public void onClick(View view) {
                    String userEmail = enter_email.getText().toString();
                    String password = enter_password.getText().toString();
                    String name = enter_name.getText().toString();

                    userEmail = userEmail.trim();
                    password = password.trim();
                    name = name.trim();

                    if (!userEmail.isEmpty() && !password.isEmpty() && !name.isEmpty()) {
                        if (validateData(userEmail, password)) {
                            BackendlessUser newUser = new BackendlessUser();
                            newUser.setEmail(userEmail);
                            newUser.setPassword(password);
                            newUser.setProperty("name", name);
                            final ProgressDialog pDialog = ProgressDialog.show(LoginActivity.this,
                                    "Please Wait!",
                                    "Creating new account...",
                                    true);
                            Backendless.UserService.register(newUser, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser backendlessUser) {
                                    Log.i(TAG, "Successfully registered user: " + backendlessUser.getProperty("name"));
                                    Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    pDialog.dismiss();
                                    warnUser(backendlessFault.getMessage());
                                }
                            });
                        }
                    }
                    else {
                        warnUser(getString(R.string.empty_field_signup_error));
            }

        }
    });
        sign_up_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sign_up_button.getVisibility() == View.VISIBLE) {
                    sign_up_button.setVisibility(View.GONE);
                } else
                    sign_up_button.setVisibility(View.VISIBLE);

                if (enter_name.getVisibility() == View.VISIBLE) {
                    enter_name.setVisibility(View.GONE);
                } else
                    enter_name.setVisibility(View.VISIBLE);

                if (login_button.getVisibility() == View.GONE) {
                    login_button.setVisibility(View.VISIBLE);
                } else
                    login_button.setVisibility(View.GONE);

                if (sign_up_button.getVisibility() == View.VISIBLE) {
                    sign_up_text.setText("CANCEL SIGN UP");
                } else sign_up_text.setText("SIGN UP");
            }
                    });
                }
            }

