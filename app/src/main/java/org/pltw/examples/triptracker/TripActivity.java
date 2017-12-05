package org.pltw.examples.triptracker;


        import android.Manifest;
        import android.content.pm.PackageManager;
        import android.os.Build;
        import android.os.Bundle;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.Fragment;
        import android.support.v4.app.FragmentManager;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.Window;
        import android.widget.Toast;

/*
 * Created by klaidley on 4/13/2015.
 */
public class TripActivity extends AppCompatActivity {
    private final String TAG = "TripActivity";
    private static final int PERMISSION_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set the view for the activity to be the xml layout screen that has the FrameLayout that will contain the trip fragment (which in turn uses fragment_trip.xml)
        setContentView(R.layout.activity_trip);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "Getting Location Permissions");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                // request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_LOCATION);
            }
        }
        //check if the trips list fragment already exists - otherwise, create a new one and add it to the fragment container frame found in activity_trip.xml
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.tripFragmentContainer);

        if (fragment==null) {
            fragment = new TripFragment();
            manager.beginTransaction()
                    .add(R.id.tripFragmentContainer, fragment)
                    .commit();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Location Permission Granted");
                } else {
                    // permission denied, disable the functionality that depends on this permission.
                    Log.i(TAG, "Location Permission Denied");
                    Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_LONG).show();
                }
                return;
            }

            //  other 'case' lines to check for other permissions this app will request in later lessons
        }
    }
}
