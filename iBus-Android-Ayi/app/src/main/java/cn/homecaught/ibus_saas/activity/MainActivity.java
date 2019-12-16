package cn.homecaught.ibus_saas.activity;

import android.Manifest;
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
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.Menu;
import android.view.MenuItem;

import android.graphics.drawable.Drawable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_saas.MyApplication;
import cn.homecaught.ibus_saas.R;
import cn.homecaught.ibus_saas.adapter.FragmentTabAdapter;
import cn.homecaught.ibus_saas.model.LineBean;
import cn.homecaught.ibus_saas.model.UgrentBean;
import cn.homecaught.ibus_saas.model.UserBean;
import cn.homecaught.ibus_saas.util.CameraDialog;
import cn.homecaught.ibus_saas.util.DialogTool;
import cn.homecaught.ibus_saas.util.HttpData;
import cn.homecaught.ibus_saas.util.ImageUntils;
import cn.homecaught.ibus_saas.util.StatusBarCompat;
import cn.homecaught.ibus_saas.fragment.MeFragment;
import cn.homecaught.ibus_saas.fragment.MessageFragment;
import cn.homecaught.ibus_saas.fragment.WorkFragment;
import io.rong.imkit.RongIM;


public class MainActivity extends AppCompatActivity implements MeFragment.OnMeHeadImageUploadListener {
    private List<UserBean> mFriends;

    private RadioGroup rgs;
    private ImageView ivHead;
    private int currentIndex = 0;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    private int  selectedReportIndex = 0;
    private int selectedLineIndex = -1;

    private int selectedSiteIndex = 0;

    public UserBean manager;

    private String mHeadPath;

    private List<UgrentBean> ugrents;

    private List<LineBean> lineBeans;



    Toolbar toolbar = null;

    private ProgressDialog progressDialog;

    private CameraDialog cameraDialog;

    private WorkFragment workFragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, PlayerMusicService.class);

        startService(intent);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        workFragment = new WorkFragment();
        fragments.add(workFragment);
        fragments.add(new MessageFragment());
        MeFragment fragment = new MeFragment();
        fragment.setOnMeHeadImageUploadListener(this);
        fragments.add(fragment);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("工作");
        setSupportActionBar(toolbar);

        rgs = (RadioGroup) findViewById(R.id.tabs_rg);
        StatusBarCompat.compat(this);
        StatusBarCompat.compat(this, 0x000);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("请求网络中，请稍等...");

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
                        preDrawable = MainActivity.this.getResources().getDrawable(R.mipmap.icon_message_normal);
                        break;
                    case 2:
                        preRb = (RadioButton) MainActivity.this.findViewById(R.id.tab_rb_c);
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
                        toolbar.setTitle("工作");
                        break;
                    case 1:
                        rb = (RadioButton) MainActivity.this.findViewById(R.id.tab_rb_b);
                        drawable = MainActivity.this.getResources().getDrawable(R.mipmap.icon_message_selected);
                        toolbar.setTitle("消息");
                        break;
                    case 2:
                        rb = (RadioButton) MainActivity.this.findViewById(R.id.tab_rb_c);
                        drawable = MainActivity.this.getResources().getDrawable(R.mipmap.icon_user_selected);
                        toolbar.setTitle("我的");
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

        //Android 6.0判断用户是否授予定位权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//如果 API level 是大于等于 23(Android 6.0) 时
            //判断是否具有权限
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //判断是否需要向用户解释为什么需要申请该权限
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    Toast.makeText(MainActivity.this,"自Android 6.0开始需要打开位置权限",Toast.LENGTH_SHORT).show();
                }
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        22);
            }
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_report:
                progressDialog.show();
                new HasTravelTask(0).execute();

                break;
            case R.id.action_add:
                new HasTravelTask(1).execute();

                break;
            case R.id.action_chat:
                /*
                if (manager != null){
                    String schoolId = MyApplication.getInstance().getSharedPreferenceManager().getSchoolId();
                    RongIM.getInstance().startPrivateChat(MainActivity.this, schoolId +"_"+ manager.getId(),
                            manager.getUserFirstName() + "" + manager.getUserLastName());
                }else {
                    Toast.makeText(this, "未设置校巴经理", Toast.LENGTH_SHORT).show();
                }
                */
                new GetFriendsTask().execute();

                break;
            default:
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
                setTitle("请选择需要群发的消息").
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
                                if (selectedLineIndex == -1){
                                    new SetUrgent(ugrentBean.getId(), null).execute();

                                }else {
                                    LineBean lineBean = lineBeans.get(selectedLineIndex);
                                    new SetUrgent(ugrentBean.getId(), lineBean.getId()).execute();

                                }
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


    private void showLineAlert(final int type) {

        final String[] reports = new String[lineBeans.size()];
        List<String> names = new ArrayList<>();
        for (int i=0; i <lineBeans.size(); i++){
            LineBean ugrentBean = lineBeans.get(i);
            names.add(ugrentBean.getLineName());
        }
        names.toArray(reports);
        selectedLineIndex = 0;
        String msg = "请选择线路发送消息";

        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle(msg).
                setIcon(R.mipmap.icon_report)
                .setSingleChoiceItems(reports, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedLineIndex = which;
                    }
                }).
                        setPositiveButton("确认", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (type == 0) {
                                    new GetUgrentTask().execute();
                                }else {
                                    LineBean ugrentBean = lineBeans.get(selectedLineIndex);
                                    R(ugrentBean);
                                }
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
            case 22:
                break;

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
                MyApplication.getInstance().getSharedPreferenceManager().setBusId(jsonObject.getJSONObject("info").getString("id"));
                manager = new UserBean(jsonObject.getJSONObject("info").getJSONObject("bus_manager_data"));
                JSONArray jsonArray = jsonObject.getJSONObject("info").getJSONArray("bus_lines");
                lineBeans = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i ++){
                    JSONObject obj = jsonArray.getJSONObject(i);
                    LineBean lineBean = new LineBean(obj);
                    lineBeans.add(lineBean);
                }

                workFragment.reloadLines(lineBeans);

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
        private String mLineId;

        public SetUrgent(String urgentId, String lineId) {
            super();
            mUrgentId = urgentId;
            mLineId = lineId;
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.setUrgent(mUrgentId, mLineId);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.hide();
            selectedLineIndex = -1;
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


    public class HasTravelTask extends AsyncTask<Void, Void, String>{
        private int mtype = 0;
        public HasTravelTask(int type) {
            super();
            mtype = type;
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.hasTravel();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            try{

                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");

                if (mtype == 0) {
                    if(status) {
                        new GetUgrentTask().execute();
                    } else {
                        showLineAlert(0);
                    }

                } else {

                    if (status) {
                        LineBean curLine = workFragment.getCurLine();
                        if (curLine == null) return;

                        R(curLine);

                    } else {
                        showLineAlert(1);
                    }

                }


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

    private void  R(final LineBean curLine) {
        final List<String> sites = new ArrayList();
        for (int i = 0; i < curLine.getSites().length();i++) {
            try {
                JSONObject xjsonObject = curLine.getSites().getJSONObject(i);
                sites.add(xjsonObject.getString("site_name"));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String[] strs1 = sites.toArray(new String[sites.size()]);
        DialogTool.createSingleChoiceDialog(MainActivity.this, "请选择即将到达的站点发送提醒", strs1, "确定", "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                JSONArray sites =curLine.getSites();
                try {
                    new ArrivalReminder(curLine.getId(), sites.getJSONObject(selectedSiteIndex).getInt("id") + "").execute();

                }catch (Exception e){

                }
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedSiteIndex = which;
            }
        }, 0).show();
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


    public class GetFriendsTask extends AsyncTask<Void, Void, String> {

        public GetFriendsTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {

            String userId = MyApplication.getInstance().getLoginUser().getId();
            return HttpData.getFriends(userId);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                if (mFriends == null)
                    mFriends =new ArrayList<>();
                mFriends.clear();
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("info");
                for (int i = 0; i< jsonArray.length(); i++){
                    UserBean userBean = new UserBean(jsonArray.getJSONObject(i));
                    mFriends.add(userBean);
                }

                showFriendsAlert();
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

    public class ArrivalReminder extends AsyncTask<Void, Void, String> {

        private String mlineId;
        private String msiteId;

        public ArrivalReminder(String lineId, String siteId) {
            super();
            mlineId =lineId;
            msiteId = siteId;
        }

        @Override
        protected String doInBackground(Void... params) {

            return HttpData.arrivalReminder(mlineId + "", msiteId + "");
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.hide();
            try{
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");
                if (status == false){
                    Toast.makeText(getBaseContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getBaseContext(), "提醒成功", Toast.LENGTH_SHORT).show();
                }
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


    private void showFriendsAlert() {

        final CharSequence[] reports = new CharSequence[mFriends.size()];
        final boolean checkedItems[] = new boolean[mFriends.size()];
        List<String> names = new ArrayList<>();
        for (int i=0; i <mFriends.size(); i++){
            UserBean userBean = mFriends.get(i);
            names.add(userBean.getUserFirstName() + " " + userBean.getUserLastName());
            checkedItems[i] = false;
        }
        names.toArray(reports);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Please select friends.");
        builder.setMultiChoiceItems(reports, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final List<UserBean> selectUsers = new ArrayList<UserBean>();
                for (int i = 0; i < mFriends.size(); i++) {
                    if (checkedItems[i] == true) {
                        selectUsers.add(mFriends.get(i));
                    }
                }
                if (selectUsers.size() != 0) {
                    final String schoolId = MyApplication.getInstance().getSharedPreferenceManager().getSchoolId();
                    if (selectUsers.size() == 1) {
                        UserBean user = selectUsers.get(0);
                        RongIM.getInstance().startPrivateChat(MainActivity.this, schoolId + "_" + user.getId(), user.getUserFirstName() + " " + user.getUserLastName());
                    } else {
                        final List<String> ids = new ArrayList<String>();

                        final EditText inputServer = new EditText(MainActivity.this);
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Please input title!").setIcon(android.R.drawable.ic_dialog_info).setView(inputServer)
                                .setNegativeButton("Cancel", null);
                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                String title = "";
                                for (int i = 0; i < selectUsers.size(); i++) {
                                    UserBean user = selectUsers.get(i);
                                    ids.add(schoolId + "_" + user.getId());

                                    title += user.getUserLastName() + " ";
                                }

                                if (inputServer.getText().length() !=0) {
                                    title = inputServer.getText().toString();
                                }
                                RongIM.getInstance().createDiscussionChat(MainActivity.this, ids, title);
                            }
                        });
                        builder.show();



                    }
                }
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

}
