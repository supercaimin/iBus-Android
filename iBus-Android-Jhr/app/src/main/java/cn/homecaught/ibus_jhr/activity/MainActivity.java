package cn.homecaught.ibus_jhr.activity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.Menu;
import android.view.MenuItem;

import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_jhr.MyApplication;
import cn.homecaught.ibus_jhr.fragment.AirPlusFragment;
import cn.homecaught.ibus_jhr.fragment.MeFragment;
import cn.homecaught.ibus_jhr.fragment.MessageFragment;
import cn.homecaught.ibus_jhr.fragment.TrackFragment;
import cn.homecaught.ibus_jhr.R;
import cn.homecaught.ibus_jhr.adapter.FragmentTabAdapter;
import cn.homecaught.ibus_jhr.model.UgrentBean;
import cn.homecaught.ibus_jhr.model.UserBean;
import cn.homecaught.ibus_jhr.util.CameraDialog;
import cn.homecaught.ibus_jhr.util.HttpData;
import cn.homecaught.ibus_jhr.util.ImageUntils;
import cn.homecaught.ibus_jhr.util.StatusBarCompat;


public class MainActivity extends AppCompatActivity implements MeFragment.OnMeHeadImageUploadListener {

    private RadioGroup rgs;
    private ImageView ivHead;
    private int currentIndex = 0;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    private int selectedReportIndex = 0;

    public UserBean manager;

    private String mHeadPath;

    private List<UgrentBean> ugrents;


    private ProgressDialog progressDialog;

    private CameraDialog cameraDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragments.add(new TrackFragment());
        fragments.add(new AirPlusFragment());
        fragments.add(new MessageFragment());
        MeFragment fragment = new MeFragment();
        fragment.setOnMeHeadImageUploadListener(this);
        fragments.add(fragment);



        rgs = (RadioGroup) findViewById(R.id.tabs_rg);
        StatusBarCompat.compat(this);
        StatusBarCompat.compat(this, 0x000);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Tips");
        progressDialog.setMessage("Please wait a moment...");

        FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments, R.id.tab_content, rgs);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
                System.out.println("Extra---- " + index + " checked!!! ");
                RadioButton preRb = null;
                Drawable preDrawable = null;
                switch (currentIndex) {
                    case 0:
                        preRb = (RadioButton) MainActivity.this.findViewById(R.id.tab_rb_a);
                        preDrawable = MainActivity.this.getResources().getDrawable(R.mipmap.icon_work_normal);
                        break;
                    case 1:
                        preRb = (RadioButton) MainActivity.this.findViewById(R.id.tab_rb_b);
                        preDrawable = MainActivity.this.getResources().getDrawable(R.mipmap.airplus_normal);
                        break;
                    case 2:
                        preRb = (RadioButton) MainActivity.this.findViewById(R.id.tab_rb_c);
                        preDrawable = MainActivity.this.getResources().getDrawable(R.mipmap.icon_message_normal);
                        break;
                    case 3:
                        preRb = (RadioButton) MainActivity.this.findViewById(R.id.tab_rb_d);
                        preDrawable = MainActivity.this.getResources().getDrawable(R.mipmap.icon_user_normal);
                        break;
                    default:
                        break;
                }

                preRb.setCompoundDrawablesWithIntrinsicBounds(null, preDrawable, null, null);
                preRb.setTextColor(Color.BLACK);


                RadioButton rb = (RadioButton) radioGroup.getChildAt(index);
                Drawable drawable = null;

                switch (index) {
                    case 0:
                        rb = (RadioButton) MainActivity.this.findViewById(R.id.tab_rb_a);
                        drawable = MainActivity.this.getResources().getDrawable(R.mipmap.icon_work_selected);
                        break;
                    case 1:
                        rb = (RadioButton) MainActivity.this.findViewById(R.id.tab_rb_b);
                        drawable = MainActivity.this.getResources().getDrawable(R.mipmap.airplus_selected);
                        break;
                    case 2:
                        rb = (RadioButton) MainActivity.this.findViewById(R.id.tab_rb_c);
                        drawable = MainActivity.this.getResources().getDrawable(R.mipmap.icon_message_selected);
                        break;
                    case 3:
                        rb = (RadioButton) MainActivity.this.findViewById(R.id.tab_rb_d);
                        drawable = MainActivity.this.getResources().getDrawable(R.mipmap.icon_user_selected);
                        break;
                    default:
                        break;
                }
                rb.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
                rb.setTextColor(MainActivity.this.getResources().getColor(R.color.colorPrimary));

                currentIndex = index;

            }
        });
        progressDialog.show();
        new GetBusTaskTask().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_chat:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }


    private void showReportAlert() {

        final String[] reports = new String[ugrents.size()];
        List<String> names = new ArrayList<>();
        for (int i=0; i <ugrents.size(); i++){
            UgrentBean ugrentBean = ugrents.get(i);
            names.add(ugrentBean.getName());
        }
        names.toArray(reports);

        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("请故障选择").
                setIcon(R.mipmap.icon_report)
                .setSingleChoiceItems(reports, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedReportIndex = which;
                    }
                }).
                        setPositiveButton("确认", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                UgrentBean ugrentBean = ugrents.get(selectedReportIndex);
                                new SetUrgent(ugrentBean.getId()).execute();

                            }
                        }).
                        setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).
                        create();
        alertDialog.show();
    }



    @Override
    public void onHeadImageClick(ImageView ivHead)
    {
        this.ivHead = ivHead;
        if (cameraDialog == null) {
            cameraDialog = new CameraDialog(this);
        }
        cameraDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 123: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
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
                            //       UpImgeForEnd(outFile.getAbsolutePath());
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

    //{"status":true,"msg":"","info":{"state":"SUCCESS","url":"\/data\/upload\/images\/20160629\/150030_760113.png","title":"150030_760113.png","original":"img1467183628581.png","type":".png","size":25917}}
    public class UploadImageTask extends AsyncTask<Void, Void, String>{

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
                    new UpHeadImageTask().execute();
                    ImageLoader.getInstance().displayImage(HttpData.getBaseUrl() + mHeadPath, ivHead);
                    Toast.makeText(MainActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
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

    public class GetBusTaskTask extends AsyncTask<Void, Void, String> {
        public GetBusTaskTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.getBus();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.hide();
            try {
                JSONObject jsonObject = new JSONObject(s);
                manager = new UserBean(jsonObject.getJSONObject("info").getJSONObject("bus_manager_data"));
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


    public class SetUrgent extends AsyncTask<Void, Void, String> {

        private String mUrgentId;

        public SetUrgent(String urgentId) {
            super();
            mUrgentId = urgentId;
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.setUrgent(mUrgentId);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.hide();
            try{
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");
                if (status == false){
                    Toast.makeText(getBaseContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getBaseContext(), "报告成功", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
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

    public class GetUgrentTask extends AsyncTask<Void, Void, String>{

        public GetUgrentTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.getUrgent();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.hide();
            try{
                if (ugrents == null)
                    ugrents =new ArrayList<>();
                ugrents.clear();
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("info");
                for (int i = 0; i< jsonArray.length(); i++){
                    UgrentBean ugrentBean = new UgrentBean(jsonArray.getJSONObject(i));
                    ugrents.add(ugrentBean);
                }

                showReportAlert();
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



    public class UpHeadImageTask extends AsyncTask<Void, Void, String>{

        public UpHeadImageTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return  HttpData.chgInfo(mHeadPath,
                    MyApplication.getInstance().getLoginUser().getUserFirstName(),
                    MyApplication.getInstance().getLoginUser().getUserLastName());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {

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
