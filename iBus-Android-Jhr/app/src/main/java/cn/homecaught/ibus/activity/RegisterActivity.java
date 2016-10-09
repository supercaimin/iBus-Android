package cn.homecaught.ibus.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus.MyApplication;
import cn.homecaught.ibus.R;
import cn.homecaught.ibus.model.UserBean;
import cn.homecaught.ibus.util.HttpData;

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


    // UI references.
    private EditText mUserMobileView;
    private EditText mUserEmailView;
    private EditText mUserPassView;
    private EditText mUserRePassView;
    private EditText mUserFirstNameView;
    private EditText mUserLastNameView;
    private EditText mChildFirstNameView;
    private EditText mChildLastNameView;
    private EditText mChildSNView;

    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        mUserFirstNameView = (EditText) findViewById(R.id.first_name);
        mUserLastNameView = (EditText) findViewById(R.id.last_name);
        mChildFirstNameView = (EditText) findViewById(R.id.child_first_name);
        mChildLastNameView = (EditText) findViewById(R.id.child_last_name);
        mChildSNView = (EditText) findViewById(R.id.sn);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
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

        if (TextUtils.isEmpty(mUserLastNameView.getText().toString())){
            mUserLastNameView.setError("Required.");
            focusView = mUserLastNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mChildFirstNameView.getText().toString())){
            mChildFirstNameView.setError("Required.");
            focusView = mChildFirstNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mChildLastNameView.getText().toString())){
            mChildLastNameView.setError("Required.");
            focusView = mChildLastNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mChildSNView.getText().toString())){
            mChildSNView.setError("Required.");
            focusView = mChildSNView;
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
            mAuthTask = new UserRegisterTask(mUserMobileView.getText().toString(),
                    mUserEmailView.getText().toString(),
                    mUserPassView.getText().toString(),
                    mUserFirstNameView.getText().toString(),
                    mUserLastNameView.getText().toString(),
                    mChildFirstNameView.getText().toString(),
                    mChildLastNameView.getText().toString(),
                    mChildSNView.getText().toString());
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
        private String mUserEmail;
        private String mUserPass;
        private String mUserFirstName;
        private String mUserLastName;
        private String mChildFirstName;
        private String mChildLastName;
        private String mChildSN;


        UserRegisterTask(String userMobile,
                         String userEmail,
                         String userPass,
                         String userFirstName,
                         String userLastName,
                         String childFirstName,
                         String childLastName,
                         String childSN) {
            mUserMobile = userMobile;
            mUserEmail = userEmail;
            mUserLastName = userLastName;
            mUserFirstName = userFirstName;
            mUserPass = userPass;
            mChildFirstName = childFirstName;
            mChildLastName = childLastName;
            mChildSN = childSN;
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.register(mUserMobile,
                    mUserEmail,
                    mUserPass,
                    mUserFirstName,
                    mUserLastName,
                    mChildFirstName,
                    mChildLastName,
                    mChildSN
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

