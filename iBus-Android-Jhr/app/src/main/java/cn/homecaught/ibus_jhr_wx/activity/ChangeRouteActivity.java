package cn.homecaught.ibus_jhr_wx.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import cn.homecaught.ibus_jhr_wx.R;
import cn.homecaught.ibus_jhr_wx.model.ChildBean;
import cn.homecaught.ibus_jhr_wx.model.LineBean;
import cn.homecaught.ibus_jhr_wx.util.HttpData;

public class ChangeRouteActivity extends AppCompatActivity {

    private Button btnChild;
    private Button btnRoute;
    private Button btnStop;
    private Button submitButton;
    private DatePicker datePicker;

    private String currentChildId;
    private String currentRouteId;
    private String currentStopId;
    private String date;

    private int currentSelectedIndex = 0;

    private static final int SELECT_CHILD = 100;
    private static final int SELECT_ROUTE = 101;
    private static final int SELECT_STOP = 102;
    private static final int SUBMIT_ACTION = 103;

    private int currentSelectTarget = SELECT_CHILD;

    private ArrayList selectItems;
    private ArrayList childs;
    private ArrayList routes;
    private JSONArray stops;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_route);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_route_change);

        btnChild = (Button) findViewById(R.id.btn_child);
        btnChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelectTarget = SELECT_CHILD;
                new NetWorkTask().execute();
            }
        });

        btnRoute = (Button) findViewById(R.id.btn_route);
        btnRoute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelectTarget = SELECT_ROUTE;
                new NetWorkTask().execute();
            }
        });

        btnStop = (Button) findViewById(R.id.btn_stop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelectTarget = SELECT_STOP;
                if (selectItems == null)
                    selectItems = new ArrayList();
                selectItems.clear();
                if (stops != null){
                    for (int i=0;i<stops.length();i++) {
                        try {
                            JSONObject object = stops.getJSONObject(i);
                            selectItems.add(((JSONObject) object).getString("site_name"));

                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    showReportAlert();

                }

            }
        });

        submitButton = (Button) findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSelectTarget = SUBMIT_ACTION;
                new NetWorkTask().execute();
            }
        });

        datePicker = (DatePicker) findViewById(R.id.dpPicker);
        Calendar c1 = Calendar.getInstance();
// 获得年份
        int year = c1.get(Calendar.YEAR);
// 获得月份
        int month = c1.get(Calendar.MONTH);

// 获得星期几（注意（这个与Date类是不同的）：1代表星期日、2代表星期1、3代表星期二，以此类推）
        int day = c1.get(Calendar.DAY_OF_WEEK);

        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year,
                                      int monthOfYear, int dayOfMonth) {
                // 获取一个日历对象，并初始化为当前选中的时间
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, monthOfYear, dayOfMonth);
                SimpleDateFormat format = new SimpleDateFormat(
                        "yyyy-MM-dd");
                date = format.format(calendar.getTime());
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }



    private void showReportAlert() {

        final String[] reports = new String[selectItems.size()];
        selectItems.toArray(reports);

        Dialog alertDialog = new AlertDialog.Builder(this).
                setTitle("请选择").
                setIcon(R.mipmap.icon_report)
                .setSingleChoiceItems(reports, 0, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        currentSelectedIndex = which;
                    }
                }).
                        setPositiveButton("完成", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (currentSelectTarget == SELECT_CHILD) {
                                    ChildBean bean = (ChildBean)childs.get(currentSelectedIndex);
                                    currentChildId = bean.getId();
                                    btnChild.setText(bean.getLastName() + " " + bean.getFirstName());
                                }
                                if (currentSelectTarget == SELECT_ROUTE) {
                                    LineBean bean = (LineBean) routes.get(currentSelectedIndex);
                                    currentRouteId = bean.getId();
                                    btnRoute.setText(bean.getLineName());
                                    stops = bean.getStops();

                                }
                                if (currentSelectTarget == SELECT_STOP) {
                                    try {

                                        JSONObject jsonObject = stops.getJSONObject(currentSelectedIndex);
                                        currentStopId = jsonObject.getString("id");
                                        btnStop.setText(jsonObject.getString("site_name"));
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                    }

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

    public class NetWorkTask extends AsyncTask<Void, Void, String> {

        public NetWorkTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            if (currentSelectTarget == SELECT_CHILD)
                return HttpData.getChilds();

            if (currentSelectTarget == SELECT_ROUTE)
                return HttpData.getBackLines();

            if (currentSelectTarget == SUBMIT_ACTION) {
                return  HttpData.tempLine(currentChildId,date, currentRouteId, currentStopId);
            }

            return null;
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

                if (status){
                    if (currentSelectTarget == SUBMIT_ACTION) {
                        Toast.makeText(ChangeRouteActivity.this, R.string.tip_submitted_successfully, Toast.LENGTH_SHORT).show();
                        ChangeRouteActivity.this.finish();
                    }

                    JSONArray jsonArray = jsonObject.getJSONArray("info");
                    if (selectItems == null)
                        selectItems = new ArrayList();
                    selectItems.clear();

                    if (currentSelectTarget == SELECT_CHILD) {
                        if (childs == null){
                            childs = new ArrayList<>();
                        }
                        childs.clear();
                        for(int i = 0; i < jsonArray.length(); i++){
                            ChildBean userBean = new ChildBean(jsonArray.getJSONObject(i));
                            childs.add(userBean);
                            selectItems.add(userBean.getLastName() + " " + userBean.getFirstName());
                        }
                        showReportAlert();
                    }

                    if (currentSelectTarget==SELECT_ROUTE) {
                        if (routes == null){
                            routes = new ArrayList<>();
                        }
                        routes.clear();
                        for(int i = 0; i < jsonArray.length(); i++){
                            LineBean lineBean = new LineBean(jsonArray.getJSONObject(i));
                            routes.add(lineBean);
                            selectItems.add(lineBean.getLineName());
                        }
                        showReportAlert();
                    }



                }else {
                    Toast.makeText(ChangeRouteActivity.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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


}
