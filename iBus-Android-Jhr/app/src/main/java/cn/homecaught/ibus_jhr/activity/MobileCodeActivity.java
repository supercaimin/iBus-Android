package cn.homecaught.ibus_jhr.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.homecaught.ibus_jhr.MyApplication;
import cn.homecaught.ibus_jhr.R;
import cn.homecaught.ibus_jhr.model.SchoolBean;
import cn.homecaught.ibus_jhr.model.UserBean;
import cn.homecaught.ibus_jhr.util.HttpData;

public class MobileCodeActivity extends AppCompatActivity {

    private EditText mEditTextMobile = null;

    private Button mOKBtn = null;
    private ProgressBar mProgressView = null;

    public final static int CODE_TYPE_FIND_PWD = 0;

    public final static int CODE_TYPE_REGISTER = 1;
    public final static String CODE_TYPE = "code_type";

    private int codeType = CODE_TYPE_FIND_PWD;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_code);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditTextMobile = (EditText) findViewById(R.id.mobile);
        mProgressView = (ProgressBar) findViewById(R.id.loading);
        Intent intent = getIntent();
        codeType = intent.getIntExtra(CODE_TYPE, CODE_TYPE_FIND_PWD);

        mOKBtn = (Button) findViewById(R.id.ok);
        mOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MobileCodeActivity.isMobile(mEditTextMobile.getText().toString())) {
                    showProgress(true);
                    new GetCodeTask(mEditTextMobile.getText().toString()).execute();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.title_get_code_tip, Toast.LENGTH_LONG).show();
                }
            }
        });

        setTitle(R.string.title_get_code);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 校验手机号
     */
    public static boolean isMobile(String mobile) {
        String regExp = "^((13[0-9])|(15[^4])|(166)|(17[0-8])|(18[0-9])|(19[8-9])|(147,145))\\d{8}$";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(mobile);
        return matcher.matches();
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class GetCodeTask extends AsyncTask<Void, Void, String> {

        private final String mMobile;

        GetCodeTask(String mobile) {
            mMobile = mobile;
        }

        @Override
        protected String doInBackground(Void... params) {
            if (codeType == CODE_TYPE_FIND_PWD)
                return HttpData.getForgetCode(mMobile);
            else
                return HttpData.getRegCode(mMobile);
        }

        @Override
        protected void onPostExecute(final String result) {
            showProgress(false);
            boolean success = false;
            try {
                JSONObject jsonObject = new JSONObject(result);
                success = jsonObject.getBoolean("status");
                if (success) {
                    Intent intent = null;
                    if (codeType == CODE_TYPE_FIND_PWD) {
                        intent = new Intent(MobileCodeActivity.this, PwdActivity.class);
                        intent.putExtra(PwdActivity.PWD_ACTION_TYPE, PwdActivity.PWD_ACTION_FIND);

                    } else {
                        intent = new Intent(MobileCodeActivity.this, RegisterActivity.class);

                    }
                    intent.putExtra("mobile", mMobile);

                    startActivity(intent);
                    MobileCodeActivity.this.finish();

                }
                Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mEditTextMobile.setVisibility(show ? View.GONE : View.VISIBLE);
            mEditTextMobile.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mEditTextMobile.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mOKBtn.setVisibility(show ? View.GONE : View.VISIBLE);
            mOKBtn.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mOKBtn.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mEditTextMobile.setVisibility(show ? View.GONE : View.VISIBLE);
            mOKBtn.setVisibility(show ? View.GONE : View.VISIBLE);

        }
    }
}
