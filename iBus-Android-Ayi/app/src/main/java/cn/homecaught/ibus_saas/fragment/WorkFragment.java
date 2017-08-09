package cn.homecaught.ibus_saas.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.homecaught.ibus_saas.MyApplication;
import cn.homecaught.ibus_saas.R;
import cn.homecaught.ibus_saas.adapter.GridViewAdapter;
import cn.homecaught.ibus_saas.adapter.LineDataAdapter;
import cn.homecaught.ibus_saas.model.ChildBean;
import cn.homecaught.ibus_saas.model.LineBean;
import cn.homecaught.ibus_saas.util.HttpData;
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



    private boolean isTravelStart = false;

    private List<LineBean> lineBeans;
    private LineBean curLine;

    private Button btnAction = null;


    private List<String> mTokens;


    private View llSelect;

    private View llContent;
    private ListView listView;

    private ProgressDialog progressDialog;



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

        btnAction = (Button) this.container.findViewById(R.id.btnAction);
        btnAction.setOnClickListener(this);

        llSelect = this.container.findViewById(R.id.llSelect);
        listView = (ListView) this.container.findViewById(R.id.listview);

        return this.container;
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
                progressDialog.show();
                llSelect.setVisibility(View.GONE);
                llContent.setVisibility(View.VISIBLE);
            }
        });
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
            case R.id.btnAction:
                if (isTravelStart == false){
                    new TravelStartTask().execute();
                }else {
                    new TravelEndTask().execute();
                }

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



    public class TravelStartTask extends AsyncTask<Void, Void, String>{

        public TravelStartTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return  HttpData.qrcodeTravelStart();
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
                if (status == false){
                    Toast.makeText(getContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }else {
                    isTravelStart = true;
                    btnAction.setText("结束行程");
                    Toast.makeText(WorkFragment.this.getContext(), "行程开始", Toast.LENGTH_LONG).show();
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


    public class TravelEndTask extends AsyncTask<Void, Void, String>{

        public TravelEndTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return  HttpData.qrcodeTravelEnd();
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
                if (status == false){
                    Toast.makeText(getContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }else {
                    isTravelStart = false;
                    btnAction.setText("开始行程");
                    Toast.makeText(WorkFragment.this.getContext(), "行程结束", Toast.LENGTH_LONG).show();
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


    public class QrcodeSendTask extends AsyncTask<Void, Void, String>{

        private String mToken;
        public QrcodeSendTask(String token) {
            super();
            mToken = token;
        }

        @Override
        protected String doInBackground(Void... params) {
            return  HttpData.qrcodeSend(mToken);
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
                if (status == false){
                    Toast.makeText(getContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }else {

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


    public void onDidReceiveBluetoothData(String token){

        if (isTravelStart == false)
            Toast.makeText(getActivity(), "行程尚未开始，扫码无效", Toast.LENGTH_LONG).show();
        if (mTokens == null) mTokens = new ArrayList<>();
        if (!mTokens.contains(token)){
            mTokens.add(token);
        }

    }


}
