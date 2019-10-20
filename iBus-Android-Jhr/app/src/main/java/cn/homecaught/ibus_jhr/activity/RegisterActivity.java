package cn.homecaught.ibus_jhr.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_jhr.MyApplication;
import cn.homecaught.ibus_jhr.R;
import cn.homecaught.ibus_jhr.model.SchoolBean;
import cn.homecaught.ibus_jhr.util.HttpData;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */
    private static final int REQUEST_READ_CONTACTS = 0;

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserRegisterTask mAuthTask = null;

    private List<SchoolBean> mSchools;
    private int mCurSelectedSchoolIndex;

    // UI references.
    private EditText mUserMobileView;
    private EditText mUserCodeView;

    private EditText mUserEmailView;
    private EditText mUserPassView;
    private EditText mUserRePassView;
    private EditText mUserFirstNameView;
    private EditText mUserLastNameView;
    private EditText mChild1FirstNameView;
    private EditText mChild1LastNameView;
    private EditText mChild1SNView;

    private EditText mChild2FirstNameView;
    private EditText mChild2LastNameView;
    private EditText mChild2SNView;

    private EditText mChild3FirstNameView;
    private EditText mChild3LastNameView;
    private EditText mChild3SNView;

    private View mProgressView;
    private View mLoginFormView;
    private Button btnSchool = null;
    private RadioGroup rgNum;

    private View child1_tip;
    private View child1_last_name_v;
    private View child1_first_name_v;
    private View child1_school_id_v;
    private View child2_tip;
    private View child2_last_name_v;
    private View child2_first_name_v;
    private View child2_school_id_v;
    private View child3_tip;
    private View child3_last_name_v;
    private View child3_first_name_v;
    private View child3_school_id_v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.action_sign_up_short);

        // Set up the login form.
        mUserEmailView = (EditText) findViewById(R.id.email);
        //populateAutoComplete();

        mUserPassView = (EditText) findViewById(R.id.password);
        mUserRePassView = (EditText) findViewById(R.id.repassword);
        mUserPassView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mUserMobileView = (EditText) findViewById(R.id.mobile);
        Intent intent = getIntent();
        mUserMobileView.setText(intent.getStringExtra("mobile"));
        mUserCodeView = (EditText) findViewById(R.id.mobileCode);
        mUserFirstNameView = (EditText) findViewById(R.id.first_name);
        mUserLastNameView = (EditText) findViewById(R.id.last_name);

        mChild1FirstNameView = (EditText) findViewById(R.id.child_first_name1);
        mChild1LastNameView = (EditText) findViewById(R.id.child_last_name1);
        mChild1SNView = (EditText) findViewById(R.id.sn1);

        mChild2FirstNameView = (EditText) findViewById(R.id.child_first_name2);
        mChild2LastNameView = (EditText) findViewById(R.id.child_last_name2);
        mChild2SNView = (EditText) findViewById(R.id.sn2);

        mChild3FirstNameView = (EditText) findViewById(R.id.child_first_name3);
        mChild3LastNameView = (EditText) findViewById(R.id.child_last_name3);
        mChild3SNView = (EditText) findViewById(R.id.sn3);

        child1_tip = findViewById(R.id.child1_tip);
        child1_last_name_v = findViewById(R.id.child1_last_name_v);
        child1_first_name_v = findViewById(R.id.child1_first_name_v);
        child1_school_id_v = findViewById(R.id.child1_school_id_v);

        child2_tip = findViewById(R.id.child2_tip);
        child2_last_name_v = findViewById(R.id.child2_last_name_v);
        child2_first_name_v = findViewById(R.id.child2_first_name_v);
        child2_school_id_v = findViewById(R.id.child2_school_id_v);

        child3_tip = findViewById(R.id.child3_tip);
        child3_last_name_v = findViewById(R.id.child3_last_name_v);
        child3_first_name_v = findViewById(R.id.child3_first_name_v);
        child3_school_id_v = findViewById(R.id.child3_school_id_v);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        rgNum = (RadioGroup) findViewById(R.id.rgSex);
        rgNum.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rbOne:
                        child1_tip.setVisibility(View.VISIBLE);
                        child1_last_name_v.setVisibility(View.VISIBLE);
                        child1_first_name_v.setVisibility(View.VISIBLE);
                        child1_school_id_v.setVisibility(View.VISIBLE);

                        child2_tip.setVisibility(View.GONE);
                        child2_last_name_v.setVisibility(View.GONE);
                        child2_first_name_v.setVisibility(View.GONE);
                        child2_school_id_v.setVisibility(View.GONE);

                        child3_tip.setVisibility(View.GONE);
                        child3_last_name_v.setVisibility(View.GONE);
                        child3_first_name_v.setVisibility(View.GONE);
                        child3_school_id_v.setVisibility(View.GONE);
                        break;
                    case R.id.rbTow:
                        child1_tip.setVisibility(View.VISIBLE);
                        child1_last_name_v.setVisibility(View.VISIBLE);
                        child1_first_name_v.setVisibility(View.VISIBLE);
                        child1_school_id_v.setVisibility(View.VISIBLE);

                        child2_tip.setVisibility(View.VISIBLE);
                        child2_last_name_v.setVisibility(View.VISIBLE);
                        child2_first_name_v.setVisibility(View.VISIBLE);
                        child2_school_id_v.setVisibility(View.VISIBLE);

                        child3_tip.setVisibility(View.GONE);
                        child3_last_name_v.setVisibility(View.GONE);
                        child3_first_name_v.setVisibility(View.GONE);
                        child3_school_id_v.setVisibility(View.GONE);
                        break;

                    case R.id.rbThree:
                        child1_tip.setVisibility(View.VISIBLE);
                        child1_last_name_v.setVisibility(View.VISIBLE);
                        child1_first_name_v.setVisibility(View.VISIBLE);
                        child1_school_id_v.setVisibility(View.VISIBLE);

                        child2_tip.setVisibility(View.VISIBLE);
                        child2_last_name_v.setVisibility(View.VISIBLE);
                        child2_first_name_v.setVisibility(View.VISIBLE);
                        child2_school_id_v.setVisibility(View.VISIBLE);

                        child3_tip.setVisibility(View.VISIBLE);
                        child3_last_name_v.setVisibility(View.VISIBLE);
                        child3_first_name_v.setVisibility(View.VISIBLE);
                        child3_school_id_v.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });



        btnSchool = (Button) findViewById(R.id.btn_school);
        btnSchool.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportAlert();
            }
        });
        showProgress(true);
        new GetSchoolTask().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mUserEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUserEmailView.setError(null);
        mUserPassView.setError(null);
        mUserRePassView.setError(null);

        // Store values at the time of the login attempt.
        String email = mUserEmailView.getText().toString();
        String password = mUserPassView.getText().toString();
        String repassword = mUserRePassView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mUserPassView.setError(getString(R.string.error_invalid_password));
            focusView = mUserPassView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(repassword) && !isPasswordValid(repassword)) {
            mUserRePassView.setError(getString(R.string.error_invalid_password));
            focusView = mUserRePassView;
            cancel = true;
        }

        if (!mUserRePassView.getText().toString().equals(mUserPassView.getText().toString())){
            mUserRePassView.setError("Two input passwords are not consistent.");
            focusView = mUserRePassView;
            cancel = true;
        }
        if (!TextUtils.isEmpty(email) && !isEmailValid(email)) {
            mUserEmailView.setError("The Email is inValid.");
            focusView = mUserEmailView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mUserFirstNameView.getText().toString())){
            mUserFirstNameView.setError("Required.");
            focusView = mUserFirstNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mUserCodeView.getText().toString())){
            mUserCodeView.setError("Required.");
            focusView = mUserCodeView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mUserLastNameView.getText().toString())){
            mUserLastNameView.setError("Required.");
            focusView = mUserLastNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mChild1FirstNameView.getText().toString())){
            mChild1FirstNameView.setError("Required.");
            focusView = mChild1FirstNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mChild1LastNameView.getText().toString())){
            mChild1LastNameView.setError("Required.");
            focusView = mChild1LastNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mChild1SNView.getText().toString())){
            mChild1SNView.setError("Required.");
            focusView = mChild1SNView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mUserMobileView.getText().toString())){
            mUserMobileView.setError("Required.");
            focusView = mUserMobileView;
            cancel = true;
        }


        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserRegisterTask(mUserMobileView.getText().toString(),mUserCodeView.getText().toString(),
                    mUserEmailView.getText().toString(),
                    mUserPassView.getText().toString(),
                    mUserFirstNameView.getText().toString(),
                    mUserLastNameView.getText().toString(),
                    mChild1FirstNameView.getText().toString(),
                    mChild1LastNameView.getText().toString(),
                    mChild1SNView.getText().toString(),
                    mChild2FirstNameView.getText().toString(),
                    mChild2LastNameView.getText().toString(),
                    mChild2SNView.getText().toString(),
                    mChild3FirstNameView.getText().toString(),
                    mChild3LastNameView.getText().toString(),
                    mChild3SNView.getText().toString()
                    );
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }


    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

       // mEmailView.setAdapter(adapter);
    }

    private void showReportAlert() {

        final String[] reports = new String[mSchools.size()];
        List<String> names = new ArrayList<>();
        for (int i=0; i <mSchools.size(); i++){
            SchoolBean ugrentBean = mSchools.get(i);
            names.add(ugrentBean.getSchoolName());
        }
        names.toArray(reports);

        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("Please choose your school").
                setIcon(R.mipmap.icon_report)
                .setSingleChoiceItems(reports, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurSelectedSchoolIndex = which;
                    }
                }).
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SchoolBean ugrentBean = mSchools.get(mCurSelectedSchoolIndex);
                                btnSchool.setText(ugrentBean.getSchoolName());
                            }
                        }).
                        setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).
                        create();
        alertDialog.show();
    }

    public class GetSchoolTask extends AsyncTask<Void, Void, String>{

        public GetSchoolTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.getSchool();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            showProgress(false);

            try{
                if (mSchools == null)
                    mSchools =new ArrayList<>();
                mSchools.clear();
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("info");

                for (int i = 0; i< jsonArray.length(); i++){
                    SchoolBean schoolBean = new SchoolBean(jsonArray.getJSONObject(i));
                    mSchools.add(schoolBean);
                }
                SchoolBean schoolBean = mSchools.get(0);
                btnSchool.setText(schoolBean.getSchoolName());
            }catch (Exception e){
                e.printStackTrace();
            }
            super.onPostExecute(s);

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(String s) {
            super.onCancelled(s);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserRegisterTask extends AsyncTask<Void, Void, String> {
        private String mUserMobile;
        private String mUserCode;

        private String mUserEmail;
        private String mUserPass;
        private String mUserFirstName;
        private String mUserLastName;
        private String mChild1FirstName;
        private String mChild1LastName;
        private String mChild1SN;

        private String mChild2FirstName;
        private String mChild2LastName;
        private String mChild2SN;

        private String mChild3FirstName;
        private String mChild3LastName;
        private String mChild3SN;


        UserRegisterTask(String userMobile,
                         String userCode,
                         String userEmail,
                         String userPass,
                         String userFirstName,
                         String userLastName,
                         String child1FirstName,
                         String child1LastName,
                         String child1SN,
                         String child2FirstName,
                         String child2LastName,
                         String child2SN,
                         String child3FirstName,
                         String child3LastName,
                         String child3SN) {
            mUserMobile = userMobile;
            mUserCode = userCode;
            mUserEmail = userEmail;
            mUserLastName = userLastName;
            mUserFirstName = userFirstName;
            mUserPass = userPass;
            mChild1FirstName = child1FirstName;
            mChild1LastName = child1LastName;
            mChild1SN = child1SN;

            mChild2FirstName = child2FirstName;
            mChild2LastName = child2LastName;
            mChild2SN = child2SN;

            mChild3FirstName = child3FirstName;
            mChild3LastName = child3LastName;
            mChild3SN = child3SN;
        }

        @Override
        protected String doInBackground(Void... params) {

            SchoolBean curSchool = mSchools.get(mCurSelectedSchoolIndex);
            MyApplication.getInstance().getSharedPreferenceManager().setSchoolDomain(curSchool.getSchoolDomain());

            return HttpData.register(mUserMobile,
                    mUserCode,
                    mUserEmail,
                    mUserPass,
                    mUserFirstName,
                    mUserLastName,
                    mChild1FirstName,
                    mChild1LastName,
                    mChild1SN,
                    mChild2FirstName,
                    mChild2LastName,
                    mChild2SN,
                    mChild3FirstName,
                    mChild3LastName,
                    mChild3SN
                    );
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            showProgress(false);
            boolean success = false;
            try {
                JSONObject jsonObject = new JSONObject(result);
                success = jsonObject.getBoolean("status");
                if (success){
                    MyApplication.getInstance().getSharedPreferenceManager().clear();
                    finish();
                    Toast.makeText(getApplicationContext(), "Thanks for signing up with iBus.", Toast.LENGTH_LONG).show();

                }else {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

