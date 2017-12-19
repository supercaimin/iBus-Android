package cn.homecaught.ibus_saas.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_saas.R;
import cn.homecaught.ibus_saas.adapter.GridViewAdapter;
import cn.homecaught.ibus_saas.adapter.LineDataAdapter;
import cn.homecaught.ibus_saas.model.ChildBean;
import cn.homecaught.ibus_saas.model.LineBean;
import cn.homecaught.ibus_saas.util.HttpData;
import cn.homecaught.ibus_saas.util.LocationService;
import cn.homecaught.ibus_saas.view.StudentInfoPopWindow;
import cn.homecaught.ibus_saas.view.PullToRefreshLayout;

public class WorkFragment extends Fragment implements View.OnClickListener{
    private GridView gridView = null;
    private  PullToRefreshLayout pullToRefreshLayout;
    private GridViewAdapter adapter;
    private List<ChildBean> students;
    private List<ChildBean> orgStudents;


    private StudentInfoPopWindow popWindow;
    private View container;


    private View llSelect;

    private View llContent;
    private ListView listView;

    private ProgressDialog progressDialog;
    private Button btnArrive;
    private Button btnStart;


    private boolean isTravelStart = false;

    private List<LineBean> lineBeans;
    private LineBean curLine;

    private Intent serviceIntent;



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("AAAAAAAAAA____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("AAAAAAAAAA____onCreate");

        serviceIntent = new Intent(this.getContext(), LocationService.class);
        this.getContext().startService(serviceIntent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("AAAAAAAAAA____onCreateView");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("提示");
        progressDialog.setMessage("请求网络中，请稍等...");
        this.container = inflater.inflate(R.layout.work_fragment, container, false);
        gridView = (GridView) this.container.findViewById(R.id.gview);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ChildBean userBean = students.get(position);
                View maskView = view.findViewById(R.id.viewMask);
                if (userBean.getUserOnBus().equals(HttpData.CHILD_STATUS_ON)){
                    userBean.setUserOnBus(HttpData.CHILD_STATUS_OFF);
                    maskView.setVisibility(View.VISIBLE);
                }else {
                    userBean.setUserOnBus(HttpData.CHILD_STATUS_ON);
                    maskView.setVisibility(View.GONE);
                }
            }
        });
        llSelect = this.container.findViewById(R.id.llSelect);
        listView = (ListView) this.container.findViewById(R.id.listview);
        llContent = this.container.findViewById(R.id.llContent);
        btnArrive = (Button) this.container.findViewById(R.id.btnArrive);
        btnStart = (Button) this.container.findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);
        this.container.findViewById(R.id.btnArrive).setOnClickListener(this);
        pullToRefreshLayout = (PullToRefreshLayout)this.container.findViewById(R.id.refresh_view);

        //new SyncTask().execute();

        pullToRefreshLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                progressDialog.show();
                new SyncTask().execute();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });


        return this.container;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("AAAAAAAAAA____onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("AAAAAAAAAA____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("AAAAAAAAAA____onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("AAAAAAAAAA____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("AAAAAAAAAA____onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("AAAAAAAAAA____onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("AAAAAAAAAA____onDestroy");
        getContext().stopService(serviceIntent);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("AAAAAAAAAA____onDetach");
    }

    public void reloadLines(List<LineBean> beanList){
        LineDataAdapter lineDataAdapter = new LineDataAdapter(this.getContext(), R.layout.line_item, beanList);
        lineBeans = beanList;

        listView.setAdapter(lineDataAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                curLine = lineBeans.get(position);
                Toast.makeText(WorkFragment.this.getContext(), curLine.getLineName(), Toast.LENGTH_LONG).show();
                new SyncTask().execute();
                progressDialog.show();
                llSelect.setVisibility(View.GONE);
                llContent.setVisibility(View.VISIBLE);
            }
        });
    }


    private void show(ChildBean user) {
        popWindow = new StudentInfoPopWindow(getActivity(), user);
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        getActivity().getWindow().setAttributes(lp);
        popWindow.showAtLocation(container, Gravity.CENTER, 0, 0);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnArrive:
                if (isTravelStart){
                    progressDialog.show();
                    new SetTravelArriveStationTask().execute();
                }else {
                    progressDialog.show();
                    new SetTravelStartTask().execute();
                    isTravelStart = true;

                    btnArrive.setText("到站");
                }

                break;
            case R.id.btnStart:
                isTravelStart = false;
                btnArrive.setText("开始行程");
                llSelect.setVisibility(View.VISIBLE);
                llContent.setVisibility(View.GONE);
                break;

            default:
                break;
        }
    }

    private List<ChildBean> getChangedStudents() {
        List<ChildBean> changedStudents = new ArrayList<>();
        for (int i = 0; i < students.size(); i ++){
            ChildBean userBean = students.get(i);
            ChildBean orgUserBean = orgStudents.get(i);
            if (!userBean.getUserOnBus().equals(orgUserBean.getUserOnBus())){
                changedStudents.add(userBean);
            }
        }
        return changedStudents;
    }
    public class SyncTask extends AsyncTask<Void, Void, String> {
        public SyncTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.getBusChildren(curLine.getId());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            progressDialog.hide();
            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);

            try{
                if (students == null)
                    students = new ArrayList<>();

                if (orgStudents == null)
                    orgStudents = new ArrayList<>();

                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");
                if (status){
                    students.clear();
                    orgStudents.clear();
                    JSONArray jsonArray = jsonObject.getJSONObject("info").getJSONArray("children");
                    for(int i = 0; i < jsonArray.length(); i++){
                        ChildBean userBean = new ChildBean(jsonArray.getJSONObject(i));
                        students.add(userBean);
                        orgStudents.add(userBean.clone());

                    }
                    if (adapter == null){
                        adapter = new GridViewAdapter(getContext(), students);
                        adapter.setOnInfoButtonOnClickListener(new GridViewAdapter.OnInfoButtonOnClickListener() {
                            @Override
                            public void onClick(ChildBean userBean) {
                                Log.d("TTTTTTT", "ccccccccccccccc");

                                show(userBean);
                            }
                        });
                        gridView.setAdapter(adapter);
                    }else {
                        adapter.setmItems(students);
                    }
                }else {
                    if (jsonObject.getString("msg").equals("该巴士没有行程")){

                    }else {
                        Toast.makeText(getContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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

    public class SetTravelArriveStationTask extends AsyncTask<Void, Void, String> {
        public SetTravelArriveStationTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.setTravelArriveStation(getChangedStudents());
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
                if(status){
                    boolean hasNextStation = jsonObject.getJSONObject("info").getBoolean("has_next_station");
                    Toast.makeText(getContext(), jsonObject.getJSONObject("info").getJSONObject("last_site").getString("site_name"), Toast.LENGTH_SHORT).show();
                    if (hasNextStation){
                    }else {
                        Toast.makeText(getContext(), "行程结束", Toast.LENGTH_SHORT).show();
                        isTravelStart = false;
                        btnArrive.setText("开始行程");

                        llSelect.setVisibility(View.VISIBLE);
                        llContent.setVisibility(View.GONE);
                    }
                }else {

                    if (jsonObject.getString("msg").equals("该巴士没有行程")){
                        Toast.makeText(getContext(), "行程结束", Toast.LENGTH_SHORT).show();
                        isTravelStart = false;
                        btnArrive.setText("开始行程");

                        llSelect.setVisibility(View.VISIBLE);
                        llContent.setVisibility(View.GONE);
                    }else {
                        Toast.makeText(getContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();

                    }
                    /* Toast.makeText(getContext(), "行程结束", Toast.LENGTH_SHORT).show();
                    isTravelStart = false;
                    btnArrive.setText("开始行程");
                    llSelect.setVisibility(View.VISIBLE);
                    llContent.setVisibility(View.GONE);
                    */
                }

            }catch (Exception e){
                e.printStackTrace();
            }
            progressDialog.show();
            new SyncTask().execute();
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

    public class SetTravelStartTask extends AsyncTask<Void, Void, String> {
        public SetTravelStartTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return HttpData.setTravelStart(curLine.getId(), getChangedStudents());
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

                //llArrive.setVisibility(View.VISIBLE);
                //llStart.setVisibility(View.GONE);

                if (status){
                    progressDialog.show();
                    new SyncTask().execute();
                    Toast.makeText(getContext(), "行程开始", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();


                    //llSelect.setVisibility(View.VISIBLE);
                    //llContent.setVisibility(View.GONE);
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
