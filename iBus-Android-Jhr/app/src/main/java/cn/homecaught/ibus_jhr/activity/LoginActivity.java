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
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_jhr.MyApplication;
import cn.homecaught.ibus_jhr.model.SchoolBean;
import cn.homecaught.ibus_jhr.model.UserBean;
import cn.homecaught.ibus_jhr.util.HttpData;
import cn.homecaught.ibus_jhr.R;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    /**
     * Id to identity READ_CONTACTS permission request.
     */

    private static final int REQUEST_READ_CONTACTS = 0;
    private List<SchoolBean> mSchools;
    private int mCurSelectedSchoolIndex;
    private Boolean mFirstLogin = false;
    private Button btnSchool = null;
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
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mMobileView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        setTitle(R.string.action_sign_in_short);
        mMobileView = (AutoCompleteTextView) findViewById(R.id.etMobile);
        populateAutoComplete();

        mPasswordView = (EditText) findViewById(R.id.etPassword);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        Button mSignUpButton = (Button) findViewById(R.id.email_sign_up_button);
        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MobileCodeActivity.class);
                intent.putExtra(MobileCodeActivity.CODE_TYPE, MobileCodeActivity.CODE_TYPE_REGISTER);
                startActivity(intent);

                    }
        });

        Button findPwdBtn = (Button) findViewById(R.id.find_password_button);
        findPwdBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MobileCodeActivity.class));
            }
        });

        btnSchool = (Button) findViewById(R.id.btn_school);
        btnSchool.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showReportAlert();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if (!MyApplication.getInstance().getSharedPreferenceManager().getUserMobile().equals("")
             && !MyApplication.getInstance().getSharedPreferenceManager().getUserPass().equals("")){
            mMobileView.setText(MyApplication.getInstance().getSharedPreferenceManager().getUserMobile());
            mPasswordView.setText(MyApplication.getInstance().getSharedPreferenceManager().getUserPass());

            attemptLogin();
        }else {
            mFirstLogin = true;
            showProgress(true);
            new GetSchoolTask().execute();
        }

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
            Snackbar.make(mMobileView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
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
        mMobileView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String mobile = mMobileView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(mobile)) {
            mMobileView.setError(getString(R.string.error_field_required));
            focusView = mMobileView;
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
            mAuthTask = new UserLoginTask(mobile, password);
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
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mMobileView.setAdapter(adapter);
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
                setTitle(R.string.tip_choose_school).
                setIcon(R.mipmap.icon_report)
                .setSingleChoiceItems(reports, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mCurSelectedSchoolIndex = which;
                    }
                }).
                        setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SchoolBean ugrentBean = mSchools.get(mCurSelectedSchoolIndex);
                                btnSchool.setText(ugrentBean.getSchoolName());
                            }
                        }).
                        setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {

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
    public class UserLoginTask extends AsyncTask<Void, Void, String> {

        private final String mMobile;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mMobile = email;
            mPassword = password;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (mFirstLogin){
                SchoolBean curSchool = mSchools.get(mCurSelectedSchoolIndex);
                MyApplication.getInstance().getSharedPreferenceManager().setSchoolDomain(curSchool.getSchoolDomain());

            }
            return HttpData.login(mMobile, mPassword);
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
                    UserBean userBean = new UserBean(jsonObject.getJSONObject("info"));
                    MyApplication.getInstance().setLoginUser(userBean);
                    MyApplication.getInstance().connect(userBean.getUserToken());

                    if (mFirstLogin){
                        SchoolBean curSchool = mSchools.get(mCurSelectedSchoolIndex);
                        MyApplication.getInstance().getSharedPreferenceManager().setSchoolDomain(curSchool.getSchoolDomain());
                        MyApplication.getInstance().getSharedPreferenceManager().setSchoolId(curSchool.getId());
                        MyApplication.getInstance().getSharedPreferenceManager().setSchoolName(curSchool.getSchoolName());
                        MyApplication.getInstance().getSharedPreferenceManager().setSchoolLogo(curSchool.getSchoolLogo());
                        MyApplication.getInstance().getSharedPreferenceManager().setSchoolImages(curSchool.getSchoolImages());
                        MyApplication.getInstance().getSharedPreferenceManager().setSchoolRemark(curSchool.getSchoolRemark());
                    }
                }else {
                    MyApplication.getInstance().getSharedPreferenceManager().clear();
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            if (success) {
                MyApplication.getInstance().getSharedPreferenceManager().setUserMobile(mMobile);
                MyApplication.getInstance().getSharedPreferenceManager().setUserPass(mPassword);
                finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));

            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}

