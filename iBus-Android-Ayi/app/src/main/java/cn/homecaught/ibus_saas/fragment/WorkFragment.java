package cn.homecaught.ibus_saas.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.homecaught.ibus_saas.R;
import cn.homecaught.ibus_saas.adapter.LineDataAdapter;
import cn.homecaught.ibus_saas.model.ChildBean;
import cn.homecaught.ibus_saas.model.LineBean;
import cn.homecaught.ibus_saas.util.HttpData;
import cn.homecaught.ibus_saas.view.StudentInfoPopWindow;

public class WorkFragment extends Fragment implements View.OnClickListener{


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
    private Timer mTimer;

    private boolean isSendingData = false;

    private boolean mConnected = false;

    public OnStartConnectBluetoothLe getOnStartConnectBluetoothLe() {
        return onStartConnectBluetoothLe;
    }

    public void setOnStartConnectBluetoothLe(OnStartConnectBluetoothLe onStartConnectBluetoothLe) {
        this.onStartConnectBluetoothLe = onStartConnectBluetoothLe;
    }

    private OnStartConnectBluetoothLe onStartConnectBluetoothLe;



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
        progressDialog.setMessage("请稍等...");

        this.container = inflater.inflate(R.layout.work_fragment, container, false);

        btnAction = (Button) this.container.findViewById(R.id.btnAction);
        btnAction.setOnClickListener(this);

        llSelect = this.container.findViewById(R.id.llSelect);
        llContent = this.container.findViewById(R.id.llContent);

        listView = (ListView) this.container.findViewById(R.id.listview);


        llSelect.setVisibility(View.INVISIBLE);
        llContent.setVisibility(View.VISIBLE);
        mTimer = new Timer();
        setTimerTask();

        if (!mConnected){
            btnAction.setText("连接扫描器");
        }

        return this.container;
    }


    private void  setTimerTask(){
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 100;
                doActionHandler.sendMessage(message);
            }
        }, 100, 100);
    }

    private Handler doActionHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int msgId = msg.what;
            switch (msgId){
                case 100:
                {
                    if (mTokens != null
                            && mTokens.size() != 0
                            && isSendingData == false){
                        new QrcodeSendTask(mTokens.get(0)).execute();
                        isSendingData = true;
                    }
                }
                break;
                default:
                    break;
            }
        }
    };

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
                new TravelStartTask().execute();
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





    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btnAction:
                progressDialog.show();
                if (mConnected){
                    new TravelEndTask().execute();
                }else {
                    if (onStartConnectBluetoothLe != null){
                        onStartConnectBluetoothLe.onStartConnect();
                    }
                }

                break;


            default:
                break;
        }
    }



    public class TravelStartTask extends AsyncTask<Void, Void, String>{

        public TravelStartTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {
            return  HttpData.qrcodeTravelStart(curLine.getId());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                progressDialog.hide();
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
            return  HttpData.qrcodeTravelEnd(curLine.getId());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                progressDialog.hide();
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");
                if (status == false){
                    Toast.makeText(getContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }else {
                    isTravelStart = false;
                    btnAction.setText("开始行程");
                    Toast.makeText(WorkFragment.this.getContext(), "行程结束", Toast.LENGTH_LONG).show();

                    llSelect.setVisibility(View.VISIBLE);
                    llContent.setVisibility(View.GONE);
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
            isSendingData = false;
            mTokens.remove(mToken);

            try{
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");
                if (status == false){
                    Toast.makeText(getContext(), jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();
                }else {
                    ChildBean bean = new ChildBean(jsonObject.getJSONObject("info").getJSONObject("child"));
                    Toast.makeText(getContext(), bean.getFirstName() + " " + bean.getLastName(), Toast.LENGTH_SHORT).show();

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

    public void onDidConnectedBluetoothle(boolean connected){
        mConnected = connected;
        if (mConnected){
            btnAction.setText("开始行程");

            llSelect.setVisibility(View.VISIBLE);
            llContent.setVisibility(View.INVISIBLE);
        }else {
            btnAction.setText("连接扫描器");

            llSelect.setVisibility(View.INVISIBLE);
            llContent.setVisibility(View.VISIBLE);
        }
        progressDialog.hide();
    }


    public interface OnStartConnectBluetoothLe{

        public void onStartConnect();
    }
}
