package cn.homecaught.ibus_jhr.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Picture;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bin.david.form.core.SmartTable;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_jhr.MyApplication;
import cn.homecaught.ibus_jhr.R;
import cn.homecaught.ibus_jhr.fragment.AirPlusFragment;
import cn.homecaught.ibus_jhr.model.BusBean;
import cn.homecaught.ibus_jhr.model.ChildBean;
import cn.homecaught.ibus_jhr.model.LineBean;
import cn.homecaught.ibus_jhr.model.Route;
import cn.homecaught.ibus_jhr.model.WeekDay;
import cn.homecaught.ibus_jhr.util.HttpData;

public class ScheduleActivity extends AppCompatActivity {

    SmartTable<WeekDay> table = null;
    View contentView ;
    AlertDialog mLDialog;

    private List<ChildBean> childs;
    private List<LineBean> lines;
    private List<BusBean> buses;
    private BusBean currentBus;

    private ProgressDialog progressDialog;

    private int selectedReportIndex = 0;
    private ChildBean currentChild = null;
    private ImageView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_schedule);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(R.string.tip_tip);
        progressDialog.setMessage(getString(R.string.tip_wait));
        List days = new ArrayList();
        WeekDay day = new WeekDay();
        day.setName("Mon");

        List rotues = new ArrayList();
        Route route = new Route();
        route.setName("去程");
        route.setBusNum("苏AG7A91");
        route.setBusLines("ASDD----BBB");
        route.setCompound("xxxx");
        route.setTime("23:00");

        Route route1 = new Route();
        route1.setName("返程");
        route1.setBusNum("苏AG7A91");
        route1.setBusLines("ASDD----BBB");
        route1.setCompound("xxxx");
        route1.setTime("23:00");

        rotues.add(route);
        rotues.add(route1);

        day.setRoutes(rotues);

        days.add(day);
        days.add(day);
        days.add(day);
        days.add(day);
        days.add(day);


        table = (SmartTable<WeekDay>) findViewById(R.id.table);
        table.setData(days);

        webView = (ImageView) findViewById(R.id.webview);


        contentView =findViewById(R.id.content);
        Button saveBtn = (Button) findViewById(R.id.save_button);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//相关权限的申请 存储权限

                try {
                    if (ActivityCompat.checkSelfPermission(ScheduleActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            || ActivityCompat.checkSelfPermission(ScheduleActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
                        ActivityCompat.requestPermissions(ScheduleActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    } else {
                        mLDialog.setMessage("正在保存图片...");
                        mLDialog.show();
                        saveMyBitmap("AuthCode", createViewBitmap(webView));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        mLDialog = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("提示")
                .setMessage("我是最简单的dialog").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        Toast.makeText(ScheduleActivity.this, "确定", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        Toast.makeText(ScheduleActivity.this, "关闭", Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                    }
                }).create();

        setTitle(R.string.title_schedule);
        new GetChildsTask().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /***
         * 第一个参数R.menu.menu:表示通过哪一个资源文件来创建选项菜单
         * 第二个参数menu:表示我们的菜单项将添加到哪个Menu对象中去；
         * ***/
        getMenuInflater().inflate(R.menu.map,menu);
        Log.e("onCreateOptionsMenu","is called");
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        if (item.getItemId() == R.id.action_chat) {
            showSelectAlert();
        }
        return super.onOptionsItemSelected(item);
    }

    //权限申请的回调
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLDialog.setMessage("正在保存图片...");
                    mLDialog.show();
                    try {
                        saveMyBitmap("AuthCode", createViewBitmap(webView));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(ScheduleActivity.this, "请先开启读写权限", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    //使用IO流将bitmap对象存到本地指定文件夹
    public void saveMyBitmap(final String bitName, final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String filePath = Environment.getExternalStorageDirectory().getPath();
                File file = new File(filePath + "/DCIM/Camera/" + bitName + ".png");
                try {
                    file.createNewFile();


                    FileOutputStream fOut = null;
                    fOut = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);


                    Message msg = Message.obtain();
                    msg.obj = file.getPath();
                    handler.sendMessage(msg);
                    //Toast.makeText(PayCodeActivity.this, "保存成功", Toast.LENGTH_LONG).show();
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String picFile = (String) msg.obj;
            String[] split = picFile.split("/");
            String fileName = split[split.length - 1];
            try {
                MediaStore.Images.Media.insertImage(getApplicationContext()
                        .getContentResolver(), picFile, fileName, null);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            // 最后通知图库更新
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + picFile)));
            Toast.makeText(ScheduleActivity.this, "图片保存图库成功", Toast.LENGTH_LONG).show();
            if (mLDialog != null && mLDialog.isShowing()) {
                mLDialog.dismiss();
            }
        }
    };



    public static Bitmap createViewBitmap(final View view){
        if (view == null)
            return null;
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        int measureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(measureSpec, measureSpec);

        if (view.getMeasuredWidth()<=0 || view.getMeasuredHeight()<=0) {
          //  L.e("ImageUtils.viewShot size error");
            return null;
        }
        Bitmap bm;
        try {
            bm = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        }catch (OutOfMemoryError e){
            System.gc();
            try {
                bm = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            }catch (OutOfMemoryError ee){
              //  L.e("ImageUtils.viewShot error", ee);
                return null;
            }
        }
        Canvas bigCanvas = new Canvas(bm);
        Paint paint = new Paint();
        int iHeight = bm.getHeight();
        bigCanvas.drawBitmap(bm, bm.getWidth(), iHeight, paint);
        view.draw(bigCanvas);
        return bm;
    }

    private void showSelectAlert() {
        if (isManager()){
            final String[] childnames = new String[buses.size()];
            List<String> names = new ArrayList<>();
            for (int i=0; i <buses.size(); i++){
                BusBean busBean = buses.get(i);
                names.add(busBean.getBusNumber());
            }
            names.toArray(childnames);

            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle(R.string.tip_select).
                    setIcon(R.mipmap.icon_report)
                    .setSingleChoiceItems(childnames, selectedReportIndex, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedReportIndex = which;
                        }
                    }).
                            setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setCurrentBus(buses.get(selectedReportIndex));

                                }
                            }).
                            setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).create();
            alertDialog.show();

        }else {
            final String[] childnames = new String[childs.size()];
            List<String> names = new ArrayList<>();
            for (int i=0; i <childs.size(); i++){
                ChildBean userBean = childs.get(i);
                names.add(userBean.getFirstName() + " " + userBean.getLastName());
            }
            names.toArray(childnames);

            Dialog alertDialog = new AlertDialog.Builder(this).
                    setTitle(R.string.tip_select).
                    setIcon(R.mipmap.icon_report)
                    .setSingleChoiceItems(childnames, selectedReportIndex, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedReportIndex = which;
                        }
                    }).
                            setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setCurrentChild(childs.get(selectedReportIndex));

                                }
                            }).
                            setNegativeButton(R.string.action_cancel, new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).create();
            alertDialog.show();

        }

    }

    private boolean isManager() {
        if (MyApplication.getInstance().getLoginUser().getUserRole().equals("manager")){
            return true;
        }
        return false;
    }

    public void setCurrentChild(ChildBean currentChild) {
        this.currentChild = currentChild;
        //setTitle(currentChild.getFirstName() + " " + currentChild.getLastName());
        progressDialog.show();
        new SyncTask().execute();
    }
    public void setCurrentBus(BusBean currentBus) {
        this.currentBus = currentBus;
        //setTitle(getString(R.string.line_lablel_track) + "("+ currentBus.getBusNumber() + ")");
        new SyncTask().execute();
    }

    public class GetChildsTask extends AsyncTask<Void, Void, String> {
        public GetChildsTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            if (isManager()){
                return HttpData.getBuses();
            }
            return HttpData.getChilds();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if (progressDialog.isShowing())
                progressDialog.hide();
            try{
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");

                if (status){

                    JSONArray jsonArray = jsonObject.getJSONArray("info");


                    if (isManager()){
                        if (buses == null){
                            buses = new ArrayList<>();
                        }
                        buses.clear();
                        for(int i = 0; i < jsonArray.length(); i++){
                            BusBean busBean = new BusBean(jsonArray.getJSONObject(i));
                            buses.add(busBean);
                        }
                        if(!buses.isEmpty()){
                            if (currentBus == null)
                                setCurrentBus(buses.get(0));
                        }
                    }else {
                        if (childs == null){
                            childs = new ArrayList<>();
                        }
                        childs.clear();
                        for(int i = 0; i < jsonArray.length(); i++){
                            ChildBean userBean = new ChildBean(jsonArray.getJSONObject(i));
                            childs.add(userBean);
                        }
                        if(!childs.isEmpty()){
                            if (currentChild == null)
                                setCurrentChild(childs.get(0));
                        }
                    }
                }else {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){

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


    public class  SyncTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {

            return HttpData.getCalendarData(currentChild.getId());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (progressDialog.isShowing())
                progressDialog.hide();
            try{
                JSONObject jsonObject = new JSONObject(s);
                ImageLoader.getInstance().displayImage(jsonObject.getString("info"), webView);
            }catch (Exception e) {

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

}

