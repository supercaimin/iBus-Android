package cn.homecaught.ibus_android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import org.json.JSONObject;

import cn.homecaught.ibus_android.R;
import cn.homecaught.ibus_android.util.HttpData;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    public static final String WEB_CONTENT_HANDBOOK = "handbook";
    public static final String WEB_CONTENT_TIMETABLE = "timetable";
    public static final String WEB_CONTENT_ABOUT_US = "about_us";

    private String webContent = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        webView = (WebView) findViewById(R.id.webview);

        Intent intent = getIntent();
        webContent = intent.getStringExtra("webContent");
        new GetWebContentTask().execute();

        if (webContent.equals(WEB_CONTENT_ABOUT_US)) {
            setTitle("关于我们");
        } else if (webContent.equals(WEB_CONTENT_HANDBOOK)) {
        } else if (webContent.equals(WEB_CONTENT_TIMETABLE)) {
        } else {

        }

    }

    public class GetWebContentTask extends AsyncTask<Void, Void, String> {
        public GetWebContentTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            if (webContent.equals(WEB_CONTENT_ABOUT_US)) {
                return HttpData.about();
            } else if (webContent.equals(WEB_CONTENT_HANDBOOK)) {
                return HttpData.handbook();
            } else if (webContent.equals(WEB_CONTENT_TIMETABLE)) {
                return HttpData.timetable();
            } else {

            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                webView.getSettings().setDefaultTextEncodingName("UTF-8");
                webView.loadData(jsonObject.getString("info"), "text/html; charset=UTF-8", null);
            } catch (Exception e) {

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
