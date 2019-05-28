package cn.homecaught.ibus_saas_wx.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.homecaught.ibus_saas_wx.MyApplication;
import cn.homecaught.ibus_saas_wx.R;
import cn.homecaught.ibus_saas_wx.util.HttpData;

public class SplashActivity extends AppCompatActivity {

    private  final int SPLASH_DISPLAY_LENGTH = 3000;
    private TextView tvSchoolName;
    private ImageView ivLogo;
    private ImageView ivSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        tvSchoolName = (TextView) findViewById(R.id.tvSchoolName);
        ivLogo = (ImageView) findViewById(R.id.ivLogo);
        ivSplash = (ImageView) findViewById(R.id.iv_splash);

        if (MyApplication.getInstance().getSharedPreferenceManager().getSchoolName() != null){
            tvSchoolName.setText(MyApplication.getInstance().getSharedPreferenceManager().getSchoolName());
            ImageLoader.getInstance().displayImage(HttpData.getBaseUrl()
                    + MyApplication.getInstance().getSharedPreferenceManager().getSchoolLogo(), ivLogo);
            ImageLoader.getInstance().displayImage(HttpData.getBaseUrl()
                    + MyApplication.getInstance().getSharedPreferenceManager().getSchoolImages(), ivSplash);

        }

        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();

                    }
                }
        , SPLASH_DISPLAY_LENGTH);
    }
}
