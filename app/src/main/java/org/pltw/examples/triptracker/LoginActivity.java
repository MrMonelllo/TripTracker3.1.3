package org.pltw.examples.triptracker;

import android.app.Activity;
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
    Button sign_up_button;
    Button login_button;
    TextView sign_up_text;
    EditText enter_email;
    EditText enter_password;
    private final String  TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Backendless.initApp( this,
                getString(R.string.APP_ID),
                getString(R.string.API_KEY));
        enter_email = (EditText) findViewById(R.id.enter_email);
        enter_password = (EditText) findViewById(R.id.enter_password);
        enter_name = (EditText) findViewById(R.id.enter_name);
        sign_up_button = (Button) findViewById(R.id.sign_up_button);
        login_button = (Button) findViewById(R.id.login_button);
        sign_up_text = (TextView) findViewById(R.id.sign_up_text);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = enter_email.getText().toString();
                String password = enter_password.getText().toString();


                userEmail = userEmail.trim();
                password = password.trim();
                final ProgressDialog pDialog = ProgressDialog.show(LoginActivity.this,
                        "Please Wait!",
                        "Loging in",
                        true);
                Backendless.UserService.login(userEmail, password, new AsyncCallback<BackendlessUser>() {
                    @Override
                    public void handleResponse(BackendlessUser response) {
                        Log.i(TAG, "Login successful for "+ response.getEmail());
                        Intent intent = new Intent(LoginActivity.this, TripListActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void handleFault(BackendlessFault fault) {
                        Log.i(TAG, "login: " + fault.getMessage());
                        warnUser(fault.getMessage(), 1);
                        pDialog.dismiss();

                    }
                });
            }
        });
        sign_up_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = enter_email.getText().toString();
                String password = enter_password.getText().toString();
                String name = enter_name.getText().toString();

                userEmail = userEmail.trim();
                password = password.trim();
                name = name.trim();

                if (!userEmail.isEmpty() &&!password.isEmpty() && !name.isEmpty()&&password.length()>6) {
                    BackendlessUser user = new BackendlessUser();
                    user.setEmail(userEmail);
                    user.setPassword(password);
                    user.setProperty("name", name);
                    final ProgressDialog pDialog = ProgressDialog.show(LoginActivity.this,
                            "Please Wait!",
                            "Creating a new account...",
                            true);
                    Backendless.UserService.register(user, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser response) {
                            Log.i(TAG,"Registration successful for "+ response.getEmail() );
                            pDialog.dismiss();
                        }

                        @Override
                        public void handleFault(BackendlessFault fault) {
                            Log.i(TAG, "Registration failed: " + fault.getMessage());
                            warnUser(fault.getMessage(), 2);
                            pDialog.dismiss();
                        }
                    });

              /* register the user in Backendless */

                }
                else {
             /* warn the user of the problem */
                    warnUser(getString(R.string.empty_field_signup_error), 3);


                }
            }


        });
        sign_up_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sign_up_text.getText()=="Sign Up!"){
                    enter_name.setVisibility(View.VISIBLE);
                    sign_up_button.setVisibility(View.VISIBLE);
                    login_button.setVisibility(View.GONE);
                    sign_up_text.setText("Cancel Sign Up");
            }
            else{
                    sign_up_text.setText("Sign Up!");
                    enter_name.setVisibility(View.GONE);
                    sign_up_button.setVisibility(View.GONE);
                    login_button.setVisibility(View.VISIBLE);

                }
            }
        });

    }
    public void warnUser(String error, int id){
        String title;
        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage(error);
        if(id==2){
            builder.setTitle(R.string.authentication_error_title);
        }
        else if (id == 3){
            builder.setTitle("Invalid Sign Up Credentials");
        }
        else if(id ==1){
            builder.setTitle("Invalid Login Credentials");
        }

        builder.setPositiveButton(android.R.string.ok, null);
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
