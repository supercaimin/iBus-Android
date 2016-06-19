package cn.homecaught.ibus_android.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.view.Menu;
import android.view.MenuItem;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_android.R;
import cn.homecaught.ibus_android.fragment.*;
import cn.homecaught.ibus_android.adapter.FragmentTabAdapter;
import cn.homecaught.ibus_android.util.StatusBarCompat;
import io.rong.imkit.RongIM;


public class MainActivity extends AppCompatActivity {

    private RadioGroup rgs;
    private int currentIndex = 0;
    public List<Fragment> fragments = new ArrayList<Fragment>();

    private int selectedReportIndex = 0;

    public String hello = "hello ";

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
                showReportAlert();
                break;
            case R.id.action_add:
                startActivity(new Intent(this, AddStudentActivity.class));
                break;
            case R.id.action_chat:
                if (RongIM.getInstance() != null) {
                    /**
                     * 启动单聊界面。
                     *
                     * @param context      应用上下文。
                     * @param targetUserId 要与之聊天的用户 Id。
                     * @param title        聊天的标题，如果传入空值，则默认显示与之聊天的用户名称。
                     */
                    RongIM.getInstance().startPrivateChat(this,"targetId","title");
                }                break;
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

        final String[] reports = new String[]{"车辆故障", "道路拥堵", "路遇事故", "雨雪天气", "其它"};

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

}
