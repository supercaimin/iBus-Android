package cn.homecaught.ibus_jhr.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.homecaught.ibus_jhr.R;
import cn.homecaught.ibus_jhr.util.CameraDialog;
import cn.homecaught.ibus_jhr.util.HttpData;
import cn.homecaught.ibus_jhr.util.ImageUntils;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class ApplicationActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

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

    private CameraDialog cameraDialog;
    private String mHeadPath;


    // UI references.
    private ImageView headImageView;
    private EditText mUserFirstNameView;
    private EditText mUserLastNameView;
    private EditText mChildSNView;
    private EditText mCompoundView;
    private EditText mGradeView;

    private CheckBox checkBox;

    private View mProgressView;
    private View mLoginFormView;
    private TextView mPickUpView;
    private TextView mDropOffView;
    private List<String> onlines;
    private List<String> offlines;
    private List<String> onlineIds;
    private List<String> offlineIds;
    private int onlineSelectedIndex = -1;
    private int offlineSelectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        headImageView = (ImageView) findViewById(R.id.ivHead);
        headImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showCameraDialog();
            }
        });
        mUserFirstNameView = (EditText) findViewById(R.id.first_name);
        mUserLastNameView = (EditText) findViewById(R.id.last_name);
        mChildSNView = (EditText) findViewById(R.id.sn);
        mPickUpView = (TextView) findViewById(R.id.tv_pick_up);
        mDropOffView = (TextView) findViewById(R.id.tv_drop_off);
        mGradeView = (EditText) findViewById(R.id.grade);
        mCompoundView = (EditText) findViewById(R.id.compound);
        checkBox = (CheckBox) findViewById(R.id.checkbox);
        mPickUpView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectLinesDialog(mPickUpView, onlines);
            }
        });

        mDropOffView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectLinesDialog(mDropOffView, offlines);
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.submit);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                  attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        onlineIds = new ArrayList<>();
        offlineIds = new ArrayList<>();
        onlines = new ArrayList<>();
        offlines = new ArrayList<>();
      //  new GetSelectLinesTask().execute();
    }

    private void showSelectLinesDialog(final TextView targetView, final List<String> lines) {
        int selectedIndex = 0;
        if (targetView == mPickUpView) {
            selectedIndex = onlineSelectedIndex;
        } else {
            selectedIndex = offlineSelectedIndex;
        }
        final String names[] = new String[lines.size()];
        lines.toArray(names);
        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("Please select").
                setIcon(R.mipmap.icon_report)
                .setSingleChoiceItems(names, selectedIndex, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (targetView == mPickUpView) {
                            onlineSelectedIndex = which;
                        } else {
                            offlineSelectedIndex = which;
                        }                    }
                }).
                        setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (targetView == mPickUpView) {
                                    mPickUpView.setText(lines.get(onlineSelectedIndex));
                                } else {
                                    mDropOffView.setText(lines.get(offlineSelectedIndex));
                                }
                            }
                        }).
                        setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).create();
        alertDialog.show();
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


        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(mHeadPath)){
            Toast.makeText(this, "Photo is Required.", Toast.LENGTH_LONG).show();
            return;
        }
        if (TextUtils.isEmpty(mUserFirstNameView.getText().toString())) {
            mUserFirstNameView.setError("Required.");
            focusView = mUserFirstNameView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mUserLastNameView.getText().toString())) {
            mUserLastNameView.setError("Required.");
            focusView = mUserLastNameView;
            cancel = true;
        }


        if (TextUtils.isEmpty(mGradeView.getText().toString())) {
            mGradeView.setError("Required.");
            focusView = mGradeView;
            cancel = true;
        }

        if (TextUtils.isEmpty(mChildSNView.getText().toString())) {
            mChildSNView.setError("Required.");
            focusView = mChildSNView;
            cancel = true;
        }

/*
        if (offlineSelectedIndex == -1 ||
                onlineSelectedIndex == -1){
            Toast.makeText(this, "Please select pick up/off compounds.", Toast.LENGTH_LONG).show();
            return;
        }
        */

        if(!checkBox.isChecked()){
            Toast.makeText(this, "Please check the agreement.", Toast.LENGTH_LONG).show();
            return;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);

            new SubmitTask(mUserFirstNameView.getText().toString(),
                    mUserLastNameView.getText().toString(),
                    mChildSNView.getText().toString(),
                    mGradeView.getText().toString(),
                    mCompoundView.getText().toString(),
                    mHeadPath
                    ).execute();
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
                new ArrayAdapter<>(ApplicationActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        // mEmailView.setAdapter(adapter);
    }

    public void showCameraDialog() {
        if (cameraDialog == null) {
            cameraDialog = new CameraDialog(this);
        }
        cameraDialog.show();
    }


    @Override
    protected void onActivityResult(int request, int result, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(request, result, data);
        if (result == RESULT_OK) {
            switch (request) {
                case CameraDialog.CAMERA_CODE: {
                    File outFile = new File(CameraDialog.UPLOAD_FILE_PARENT_PATH
                            + "img" + System.currentTimeMillis() + ".png");
                    boolean comSuc = ImageUntils.compressBmpByOptions(
                            cameraDialog.getCameraFile(), 400, 400, outFile);
                    if (comSuc) {
                        new UploadImageTask(outFile.getAbsolutePath()).execute();
                    }
                }
                break;
                case CameraDialog.IMAGE_CODE: {
                    final boolean isKitKat = Build.VERSION.SDK_INT >= 19;
                    if (!isKitKat) {
                        Bitmap bm = null;
                        ContentResolver resolver = getContentResolver();
                        try {
                            Uri originalUri = data.getData();
                            bm = MediaStore.Images.Media.getBitmap(resolver,
                                    originalUri);
                            String[] proj = {MediaStore.Images.Media.DATA};
                            Cursor cursor = getContentResolver().query(originalUri,
                                    proj, null, null, null);
                            int column_index = cursor
                                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                            cursor.moveToFirst();
                            String path = cursor.getString(column_index);

                            File outFile = new File(
                                    CameraDialog.UPLOAD_FILE_PARENT_PATH + "img"
                                            + System.currentTimeMillis() + ".png");
                            boolean comSuc = ImageUntils.compressBmpByOptions(
                                    new File(path), 200, 200, outFile);
                            if (comSuc) {
                                new UploadImageTask(outFile.getAbsolutePath()).execute();
                            }

                        } catch (IOException e) {
                        }
                    } else {
                        Uri selectedImage = data.getData();
                        String path = ImageUntils.getPath(this, selectedImage);
                        File outFile = new File(
                                CameraDialog.UPLOAD_FILE_PARENT_PATH + "img"
                                        + System.currentTimeMillis() + ".png");
                        boolean comSuc = ImageUntils.compressBmpByOptions(new File(
                                path), 200, 200, outFile);
                        if (comSuc) {
                            new UploadImageTask(outFile.getAbsolutePath()).execute();
                        }

                    }
                }
                break;
                case CameraDialog.CLIP_IMAGE:

                    break;

                default:
                    break;
            }
        }
    }

    public class UploadImageTask extends AsyncTask<Void, Void, String> {

        private String mFilePath;

        public UploadImageTask(String filePath) {
            super();
            mFilePath = filePath;
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.uploadImage(mFilePath);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");
                if (status) {
                    mHeadPath = jsonObject.getJSONObject("info").getString("url");
                    ImageLoader.getInstance().displayImage(HttpData.getBaseUrl() + mHeadPath, headImageView);
                    Toast.makeText(ApplicationActivity.this, "Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ApplicationActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
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

    public class SubmitTask extends AsyncTask<Void, Void, String> {

        private String mUserFirstName;
        private String mUserLastName;
        private String mChildSN;
        private String mGrade;
        private String mCompound;
        private String mUserHead;


        SubmitTask(String userFirstName,
                         String userLastName,
                         String childSN,
                         String grade,
                         String compound,
                         String userHead
                         ) {
            mUserFirstName = userFirstName;
            mUserLastName = userLastName;
            mChildSN = childSN;
            mGrade = grade;
            mCompound = compound;
            mUserHead = userHead;
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.addChild(mUserFirstName,
                    mUserLastName,
                    mChildSN,
                    mGrade,
                    mCompound,
                    mUserHead
            );
        }

        @Override
        protected void onPostExecute(final String result) {
            showProgress(false);
            boolean success = false;
            try {
                JSONObject jsonObject = new JSONObject(result);
                success = jsonObject.getBoolean("status");
                if (success) {
                    finish();
                    Toast.makeText(getApplicationContext(), "Thanks for your online application.", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    public class GetSelectLinesTask extends AsyncTask<Void, Void, String> {
        public GetSelectLinesTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.getSelectLines();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");

                if (status) {

                    JSONObject jsonObject1 = jsonObject.getJSONObject("info").getJSONObject("on");
                    Iterator it = jsonObject1.keys();
                    while (it.hasNext()){
                        String key = it.next().toString();
                        onlineIds.add(key);
                        onlines.add(jsonObject1.getString(key));
                    }

                    JSONObject jsonObject2 = jsonObject.getJSONObject("info").getJSONObject("off");
                    Iterator it1 = jsonObject2.keys();
                    while (it1.hasNext()){
                        String key = it1.next().toString();
                        offlineIds.add(key);
                        offlines.add(jsonObject2.getString(key));
                    }

                } else {
                    Toast.makeText(getBaseContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
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

}

