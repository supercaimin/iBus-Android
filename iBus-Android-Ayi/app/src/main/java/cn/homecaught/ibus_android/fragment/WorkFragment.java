package cn.homecaught.ibus_android.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_android.R;
import cn.homecaught.ibus_android.adapter.GridViewAdapter;
import cn.homecaught.ibus_android.model.UserBean;
import cn.homecaught.ibus_android.util.HttpData;
import cn.homecaught.ibus_android.view.StudentInfoPopWindow;
import cn.homecaught.ibus_android.view.PullToRefreshLayout;

public class WorkFragment extends Fragment implements View.OnClickListener{
    private GridView gridView = null;
    private  PullToRefreshLayout pullToRefreshLayout;
    private GridViewAdapter adapter;
    private List<UserBean> students;
    private List<UserBean> orgStudents;


    private StudentInfoPopWindow popWindow;
    private View container;
    private RelativeLayout llArrive;
    private RelativeLayout llStart;

    private ProgressDialog progressDialog;

    private String travelType = "";

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("AAAAAAAAAA____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("AAAAAAAAAA____onCreate");


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
                UserBean userBean = students.get(position);
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
        llArrive = (RelativeLayout) this.container.findViewById(R.id.llArrive);
        llStart = (RelativeLayout) this.container.findViewById(R.id.llStart);

        llArrive.setVisibility(View.GONE);
        llStart.setVisibility(View.VISIBLE);

        this.container.findViewById(R.id.btnArrive).setOnClickListener(this);
        this.container.findViewById(R.id.btnGo).setOnClickListener(this);
        this.container.findViewById(R.id.btnBack).setOnClickListener(this);
        pullToRefreshLayout = (PullToRefreshLayout)this.container.findViewById(R.id.refresh_view);
        progressDialog.show();
        new SyncTask().execute();

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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("AAAAAAAAAA____onDetach");
    }


    private void show(UserBean user) {
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
                progressDialog.show();
                new SetTravelArriveStationTask().execute();
                break;
            case R.id.btnGo:
                travelType = HttpData.TRACK_TYPE_GO;
                progressDialog.show();
                new SetTravelStartTask().execute();
                break;
            case R.id.btnBack:
                travelType = HttpData.TRACK_TYPE_BACK;
                progressDialog.show();
                new SetTravelStartTask().execute();
                break;
            default:
                break;
        }
    }

    private List<UserBean> getChangedStudents() {
        List<UserBean> changedStudents = new ArrayList<>();
        for (int i = 0; i < students.size(); i ++){
            UserBean userBean = students.get(i);
            UserBean orgUserBean = orgStudents.get(i);
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
            return HttpData.getBusChildren();
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
                students.clear();

                if (orgStudents == null)
                    orgStudents = new ArrayList<>();
                orgStudents.clear();

                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("info");
                for(int i = 0; i < jsonArray.length(); i++){
                    UserBean userBean = new UserBean(jsonArray.getJSONObject(i));
                    students.add(userBean);
                    orgStudents.add(userBean.clone());

                }
                if (adapter == null){
                    adapter = new GridViewAdapter(getContext(), students);
                    adapter.setOnInfoButtonOnClickListener(new GridViewAdapter.OnInfoButtonOnClickListener() {
                        @Override
                        public void onClick(UserBean userBean) {
                            Log.d("TTTTTTT", "ccccccccccccccc");

                            show(userBean);
                        }
                    });
                    gridView.setAdapter(adapter);
                }else {
                    adapter.setmItems(students, travelType);
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
                    Toast.makeText(getContext(), jsonObject.getJSONObject("info").getJSONObject("line").getString("line_name"), Toast.LENGTH_SHORT).show();
                    if (hasNextStation){
                    }else {
                        llArrive.setVisibility(View.GONE);
                        llStart.setVisibility(View.VISIBLE);
                        Toast.makeText(getContext(), "行程结束", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(getContext(), jsonObject.getString("info"), Toast.LENGTH_SHORT).show();
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
            return HttpData.setTravelStart(travelType, getChangedStudents());
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

                llArrive.setVisibility(View.VISIBLE);
                llStart.setVisibility(View.GONE);

                if (status){
                    progressDialog.show();
                    new SyncTask().execute();
                    Toast.makeText(getContext(), "行程开始", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(getContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
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
