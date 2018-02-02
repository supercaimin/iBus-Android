package cn.homecaught.ibus_saas.activity;

import android.Manifest;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
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
import cn.homecaught.ibus_saas.util.HttpData;
import cn.homecaught.ibus_saas.util.ImageUntils;
import cn.homecaught.ibus_saas.util.StatusBarCompat;
import cn.homecaught.ibus_saas.fragment.MeFragment;
import cn.homecaught.ibus_saas.fragment.MessageFragment;
import cn.homecaught.ibus_saas.fragment.WorkFragment;
import io.rong.imkit.RongIM;


public class MainActivity extends AppCompatActivity implements MeFragment.OnMeHeadImageUploadListener {

    private RadioGroup rgs;
    private ImageView ivHead;
    private int currentIndex = 0;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    private int selectedReportIndex = 0;

    public UserBean manager;

    private String mHeadPath;

    private List<UgrentBean> ugrents;

    private List<LineBean> lineBeans;



    Toolbar toolbar = null;

    private ProgressDialog progressDialog;

    private CameraDialog cameraDialog;

    private WorkFragment workFragment;


    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private String mDeviceAddress;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);


        workFragment = new WorkFragment();
        fragments.add(workFragment);

        workFragment.setOnStartConnectBluetoothLe(new WorkFragment.OnStartConnectBluetoothLe() {
            @Override
            public void onStartConnect() {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Android M Permission check
                    if (MainActivity.this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                        Toast.makeText(MainActivity.this, "请打开手机蓝牙再试", Toast.LENGTH_LONG).show();
                    }else {

                        initLeDevice();

                    }
                }

            }
        });
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
                new GetUgrentTask().execute();
                break;
            case R.id.action_add:
                startActivity(new Intent(this, AddStudentActivity.class));
                break;
            case R.id.action_chat:
                if (manager != null){
                    String schoolId = MyApplication.getInstance().getSharedPreferenceManager().getSchoolId();
                    RongIM.getInstance().startPrivateChat(MainActivity.this, schoolId +"_"+ manager.getId(),
                            manager.getUserFirstName() + "" + manager.getUserLastName());
                }else {
                    Toast.makeText(this, "未设置校巴经理", Toast.LENGTH_SHORT).show();
                }
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
            case PERMISSION_REQUEST_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // TODO request success
                    initLeDevice();
                }
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
                manager = new UserBean(jsonObject.getJSONObject("info").getJSONObject("bus_manager_data"));
                JSONArray jsonArray = jsonObject.getJSONObject("info").getJSONArray("bus_lines");
                lineBeans = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i ++){
                    JSONObject obj = jsonArray.getJSONObject(i);
                    LineBean lineBean = new LineBean(obj);
                    lineBeans.add(lineBean);
                }

                workFragment.reloadLines(lineBeans);
                String busid = jsonObject.getJSONObject("info").getString("id");
                Log.e("BUS ID:", busid);
                MyApplication.getInstance().getSharedPreferenceManager().setBusId(busid);

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


    private void initLeDevice()
    {
        if (mServiceConnection != null){
            unbindService(mServiceConnection);
            mServiceConnection = null;
        }
        if (mBluetoothLeService != null){
            mBluetoothLeService.disconnect();
            mBluetoothLeService.close();
            mBluetoothLeService = null;

        }
        mServiceConnection =  new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName componentName, IBinder service) {
                mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
                if (!mBluetoothLeService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                    finish();
                }
                // Automatically connects to the device upon successful start-up initialization.
                mBluetoothLeService.connect(mDeviceAddress);


            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mBluetoothLeService = null;
            }
        };
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {

        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //mLeDeviceListAdapter.addDevice(device);
                            //mLeDeviceListAdapter.notifyDataSetChanged();
                            if (device.getName() != null)
                            Log.d("NNNNNNNN:", device.getName());
                            else
                                Log.d("NNNNNNNN:", "null");

                            mDeviceAddress = device.getAddress();
                            Intent gattServiceIntent = new Intent(MainActivity.this, BluetoothLeService.class);
                            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                            scanLeDevice(false);

                        }
                    });
                }

            };

    private final static String TAG = MainActivity.class.getSimpleName();



    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;


    private ServiceConnection mServiceConnection = null;

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                workFragment.onDidConnectedBluetoothle(mConnected);
                updateConnectionState(R.string.connected);
                Toast.makeText(MainActivity.this,"扫描器连接成功！", Toast.LENGTH_LONG).show();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                workFragment.onDidConnectedBluetoothle(mConnected);
                Toast.makeText(MainActivity.this,"扫描器连接失败！", Toast.LENGTH_LONG).show();
                updateConnectionState(R.string.disconnected);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                startNotifyGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                workFragment.onDidReceiveBluetoothData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    private void startNotifyGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {

            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                uuid = gattCharacteristic.getUuid().toString();
                if (uuid.equals(SampleGattAttributes.TARGET_CHARACTERISTIC_CONFIG)){
                    mNotifyCharacteristic = gattCharacteristic;
                }
            }
        }
        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, true);
    }
    private void displayData(String data) {
        if (data != null) {
           // 3200KsCab+wU+ae7A7V3eOFi5AlmNEk8yF8DJRHKq8G8YElF3luT||8

            Log.v("DATA DATA:", data);
            Toast.makeText(this, data, Toast.LENGTH_LONG).show();
        }
    }

    public static String bin2hex(String bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }
    public static byte[] hex2byte(byte[] b) {
        if ((b.length % 2) != 0) {
            throw new IllegalArgumentException("长度不是偶数");
        }
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个进制字节
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        b = null;
        return b2;
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mConnectionState.setText(resourceId);
            }
        });
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mServiceConnection != null)
            unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onKeyDown( int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == event.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
