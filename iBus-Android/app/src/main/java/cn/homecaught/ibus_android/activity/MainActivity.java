package cn.homecaught.ibus_android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.widget.Toast;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.Menu;
import android.view.MenuItem;

import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_android.R;
import cn.homecaught.ibus_android.fragment.*;
import cn.homecaught.ibus_android.adapter.FragmentTabAdapter;
import cn.homecaught.ibus_android.model.UgrentBean;
import cn.homecaught.ibus_android.model.UserBean;
import cn.homecaught.ibus_android.util.HttpData;
import cn.homecaught.ibus_android.util.StatusBarCompat;
import io.rong.imkit.RongIM;


public class MainActivity extends AppCompatActivity {

    private RadioGroup rgs;
    private int currentIndex = 0;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    private int selectedReportIndex = 0;

    public UserBean manager;

    private List<UgrentBean> ugrents;

    Toolbar toolbar = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragments.add(new WorkFragment());
        fragments.add(new MessageFragment());
        fragments.add(new MeFragment());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setLogo(R.mipmap.ic_launcher);
        toolbar.setTitle("工作");
        setSupportActionBar(toolbar);

        rgs = (RadioGroup) findViewById(R.id.tabs_rg);
        StatusBarCompat.compat(this);
        StatusBarCompat.compat(this, 0x000);


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
        new GetBusTaskTask().execute();
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
                new GetUgrentTask().execute();
                break;
            case R.id.action_add:
                startActivity(new Intent(this, AddStudentActivity.class));
                break;
            case R.id.action_chat:
                RongIM.getInstance().startPrivateChat(MainActivity.this, manager.getId(),
                        manager.getUserFirstName() + "" + manager.getUserLastName());
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
                                Toast.makeText(MainActivity.this, reports[selectedReportIndex], Toast.LENGTH_SHORT).show();

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
}
