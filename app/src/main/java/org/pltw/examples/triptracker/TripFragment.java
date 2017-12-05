package org.pltw.examples.triptracker;



        import android.app.Activity;
        import android.app.AlertDialog;
        import android.content.Intent;
        import android.location.Location;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;

        import android.support.v4.app.FragmentManager;
        import android.support.v4.app.NavUtils;
        import android.util.Log;
        import android.view.LayoutInflater;
        import android.view.Menu;
        import android.view.MenuInflater;
        import android.view.MenuItem;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.CheckBox;
        import android.widget.EditText;
        import android.text.format.DateFormat;
        import android.widget.ImageButton;
        import android.widget.ProgressBar;
        import android.widget.Toast;

        import com.backendless.Backendless;
        import com.backendless.BackendlessUser;
        import com.backendless.async.callback.AsyncCallback;
        import com.backendless.exceptions.BackendlessFault;

        import org.pltw.examples.triptracker.Trip;

        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.Locale;

/*
 * Created by klaidley on 4/14/2015.
 */
public class TripFragment extends Fragment {
    private ImageButton mMapButton;
    private Location mLastLocation;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private EditText mLocTitle;
    private Button mCheckInButton;
    private Trip mTrip;
    private EditText mNameField;
    private EditText mDescriptionField;
    private Button mStartDateButton;
    private Button mEndDateButton;
    private CheckBox mPublicCheckBox;
    private boolean mEnabled = true;
    private boolean mPublicView = false;

    private static final String TAG = "TripFragment";
    private static final String DIALOG_DATE = "date";
    private static final int REQUEST_START_DATE = 1;
    private static final int REQUEST_END_DATE = 2;
    private static final String DATE_FORMAT = "E MM-dd-yyyy";
    public void finish(){
        Intent intent = getActivity().getIntent();
        intent.putExtra(Trip.EXTRA_TRIP_PUBLIC_VIEW, mPublicView);
        getActivity().setResult(Activity.RESULT_OK, intent);
        getActivity().finish();
    }

    @Override
    public void onCreate(Bundle savedInstanceSate) {
        super.onCreate(savedInstanceSate);
        setHasOptionsMenu(true);
        getActivity().setTitle(R.string.trip_details_text);

        //get the trip id from the Intent to see if it is a new trip (id="0") or existing trip
        String tripId = (String)getActivity().getIntent().getSerializableExtra(Trip.EXTRA_TRIP_ID);
        //get Public View flag
        mPublicView = getActivity().getIntent().getBooleanExtra(Trip.EXTRA_TRIP_PUBLIC_VIEW, false);

        if (tripId.equals("0")) {
            //new mTrip
            mTrip = new Trip();
        } else {
            //existing trip
            String id, name, desc, ownerId;
            Date sDate, eDate;
            boolean shared;
            Intent intent;

            //get the trip data from the intent
            intent = getActivity().getIntent();
            id = intent.getStringExtra(Trip.EXTRA_TRIP_ID);
            ownerId = intent.getStringExtra(Trip.EXTRA_TRIP_OWNER_ID);
            name = intent.getStringExtra(Trip.EXTRA_TRIP_NAME);
            desc = intent.getStringExtra(Trip.EXTRA_TRIP_DESC);
            sDate = (Date)intent.getSerializableExtra(Trip.EXTRA_TRIP_START_DATE);
            eDate = (Date)intent.getSerializableExtra(Trip.EXTRA_TRIP_END_DATE);
            shared = intent.getBooleanExtra(Trip.EXTRA_TRIP_PUBLIC, false);

            // todo: Activity 3.1.6
            BackendlessUser currentUser = Backendless.UserService.CurrentUser();
            if (mPublicView && !(currentUser.getObjectId().equals(ownerId))) {
                mEnabled = false;
            }

            //set the trip data on the mTrip object
            mTrip = new Trip();
            mTrip.setObjectId(id);
            mTrip.setName(name);
            mTrip.setDescription(desc);
            mTrip.setStartDate(sDate);
            mTrip.setEndDate(eDate);
            mTrip.setShared(shared);

            //Determine if the screen widgets will be enabled for editing
            // todo: Activity 3.1.6

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_trip, parent, false);

        mNameField = (EditText)v.findViewById(R.id.enter_trip_name);
        mNameField.setText(mTrip.getName());
        mNameField.setEnabled(mEnabled);

        mDescriptionField = (EditText)v.findViewById(R.id.enter_trip_description);
        mDescriptionField.setText(mTrip.getDescription());
        mDescriptionField.setEnabled(mEnabled);

        mStartDateButton = (Button)v.findViewById(R.id.start_date_button);
        if (mTrip.getStartDate()!=null) {
            updateDateView(mStartDateButton, mTrip.getStartDate());
        }
        mStartDateButton.setEnabled(mEnabled);

        mEndDateButton = (Button)v.findViewById(R.id.end_date_button);
        if (mTrip.getEndDate()!=null) {
            updateDateView(mEndDateButton, mTrip.getEndDate());
        }

        mEndDateButton.setEnabled(mEnabled);

        mPublicCheckBox = (CheckBox)v.findViewById(R.id.trip_public);
        mPublicCheckBox.setChecked(mTrip.isShared());
        mPublicCheckBox.setEnabled(mEnabled);

        //define the onClickListeners only if the buttons are enabled (i.e. not in View Only mode)
        if (mEnabled) {
            MyDateOnClickListener startDateListener = new MyDateOnClickListener();
            mStartDateButton.setOnClickListener(startDateListener);

            MyDateOnClickListener endDateListener = new MyDateOnClickListener();
            mEndDateButton.setOnClickListener(endDateListener);
        }

        return v;
    }

    private class MyDateOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            //get ready to call the Date Picker Fragment
            FragmentManager fm = getActivity().getSupportFragmentManager();
            DatePickerFragment dialog;

            //check if the button being clicked on is the Start Date, then send the DatePickerFragment the label for Start Date Hint, and the REQUEST_START_DATE request code
            if (v.getId()==R.id.start_date_button) {
                dialog = DatePickerFragment.newInstance(getDateFromView(mStartDateButton), R.string.start_date_hint);
                dialog.setTargetFragment(TripFragment.this, REQUEST_START_DATE);
            } else {
                //send the DatePickerFragment the label for End Date Hint, and the REQUEST_END_DATE request code
                dialog = DatePickerFragment.newInstance(getDateFromView(mEndDateButton), R.string.end_date_hint);
                dialog.setTargetFragment(TripFragment.this, REQUEST_END_DATE);
            }
            dialog.show(fm, DIALOG_DATE);

        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_trip_details, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                if (NavUtils.getParentActivityName(getActivity()) != null) {
                    finish();
                }
                return true;
            case R.id.action_post:
                //check if the trip is enabled for editing
                if (!mEnabled) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.post_error_message);
                    builder.setTitle(R.string.post_error_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    
                    //save the data to Backendless

                    updateTrip(item);
                }
                return true;
            case R.id.action_delete:
                //check if the trip is enabled for editing
                if (!mEnabled) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.delete_error_message);
                    builder.setTitle(R.string.delete_error_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    //delete the record from Backendless if it is an existing record
                    deleteTrip(item);
                    // todo: Activity 3.1.5

                }
                return true;
            case R.id.action_logout:
                Backendless.UserService.logout(new AsyncCallback<Void>() {
                    @Override
                    public void handleResponse(Void v) {
                        Log.i(TAG, "Successful logout");
                    }
                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Log.i(TAG, "Server reported an error on logout:" + backendlessFault.getMessage());
                    }
                });
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_settings:
                //place holder for settings screen
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_START_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            updateDateView(mStartDateButton, date);
        } else if (requestCode == REQUEST_END_DATE) {
            Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            updateDateView(mEndDateButton, date);
        }

    }

    private void updateDateView(Button dateButton, Date date) {
        dateButton.setText(DateFormat.format(DATE_FORMAT, date));
    }

    private Date getDateFromView(Button dateButton) {

        String dateString;
        Date date;

        dateString = dateButton.getText().toString();
        SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT, Locale.US);

        try {
            date = df.parse(dateString);
        }
        catch (Exception e) {
            date = new Date();
            Log.d(TAG, "Exception: " + e);
        }
        return(date);
    }

    private void updateTrip(MenuItem menuItem) {

        String name, desc;
        Date sDate, eDate;
        boolean shared;

        name = mNameField.getText().toString();
        desc = mDescriptionField.getText().toString();
        sDate = getDateFromView(mStartDateButton);
        eDate = getDateFromView(mEndDateButton);
        shared = mPublicCheckBox.isChecked();

        name = name.trim();
        desc = desc.trim();

        // If user doesn't enter a trip name, then give an error message.
        if (name.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.trip_error_message);
            builder.setTitle(R.string.trip_error_title);
            builder.setPositiveButton(android.R.string.ok, null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            menuItem.setActionView(new ProgressBar(getActivity()));

            //  save the trip in backendless
            //  todo: Activity 3.1.3
            mTrip.setName(name);
            mTrip.setDescription(desc);
            mTrip.setStartDate(sDate);
            mTrip.setEndDate(eDate);
            mTrip.setShared(shared);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Backendless.Data.of(Trip.class).save(mTrip);
                    finish();
                }
            });
            thread.start();
            try {
                thread.join();
            }catch (InterruptedException e){
                Log.e(TAG, "saving trip failed: "+ e.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(e.getMessage());
                builder.setTitle(getString(R.string.trip_error_title));
                builder.setPositiveButton(android.R.string.ok, null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

    }

    // 3.2.1 additions -----------------------
    private void manageLocationViews (View v) {

        // todo: call this method from onCreateView

        // todo: enable the check-ins and map views only if user owns the existing trip

    }






    // end 3.2.1 additions ---------------------------


    private void deleteTrip(MenuItem menuItem) {
        // todo: Activity 3.1.5
        if (mTrip.getObjectId()!=null){
            final Trip deleteTrip = mTrip;
            Thread deleteTread = new Thread(new Runnable() {
                @Override
                public void run() {
                    Backendless.Data.of(Trip.class).remove(deleteTrip);
                    Log.i(TAG, deleteTrip.getName()+ " remove");
                    finish();
                }
            });
            deleteTread.start();
        }
    }

}
