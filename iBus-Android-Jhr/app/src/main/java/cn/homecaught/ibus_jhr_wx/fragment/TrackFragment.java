package cn.homecaught.ibus_jhr_wx.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.homecaught.ibus_jhr_wx.MyApplication;
import cn.homecaught.ibus_jhr_wx.activity.LoginActivity;
import cn.homecaught.ibus_jhr_wx.adapter.TrackListViewAdapter;
import cn.homecaught.ibus_jhr_wx.model.BusBean;
import cn.homecaught.ibus_jhr_wx.model.ChildBean;
import cn.homecaught.ibus_jhr_wx.model.LineBean;
import cn.homecaught.ibus_jhr_wx.util.HttpData;
import cn.homecaught.ibus_jhr_wx.view.BusInfoPopWindow;
import cn.homecaught.ibus_jhr_wx.R;

public class TrackFragment extends Fragment{
    private List<ChildBean> childs;
    private List<LineBean> lines;
    private List<BusBean> buses;
    private BusBean currentBus;

    private TrackListViewAdapter listViewAdapter;

    public ChildBean getCurrentChild() {
        return currentChild;
    }

    public void setCurrentChild(ChildBean currentChild) {
        this.currentChild = currentChild;
        tvTitle.setText("Track(" + currentChild.getFirstName() + " " + currentChild.getLastName() +")");
    }

    public void setCurrentBus(BusBean currentBus) {
        this.currentBus = currentBus;
        tvTitle.setText("Track("+ currentBus.getBusNumber() + ")");
    }


    private ChildBean currentChild = null;

    private BusInfoPopWindow popWindow;
    private View container;
    private ListView listView;
    private TextView tvTitle;

    private ProgressDialog progressDialog;

    private int selectedReportIndex = 0;
    private Timer mTimer;
    private SyncTask syncTask = null;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("AAAAAAAAAA____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("AAAAAAAAAA____onCreate");
        mTimer = new Timer();
        setTimerTask();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("AAAAAAAAAA____onCreateView");

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Tips");
        progressDialog.setMessage("Please wait a moment...");
        this.container = inflater.inflate(R.layout.track_fragment, container, false);
        listView = (ListView) this.container.findViewById(R.id.listView);
        tvTitle = (TextView) this.container.findViewById(R.id.title);

        new GetChildsTask().execute();


        this.container.findViewById(R.id.child).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectAlert();

            }
        });
        this.container.findViewById(R.id.info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBusInfo();

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
        mTimer.cancel();
        mTimer = null;
//        syncTask.cancel(true);
        syncTask = null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("AAAAAAAAAA____onDetach");
    }


    private void  setTimerTask(){
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 100;
                doActionHandler.sendMessage(message);
            }
        }, 500, 5000);
    }

    private boolean isManager() {
        if (MyApplication.getInstance().getLoginUser().getUserRole().equals("manager")){
            return true;
        }
        return false;
    }
    private Handler doActionHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int msgId = msg.what;
            switch (msgId){
                case 100:
                {
                    if (!isManager()){
                        if (currentChild != null){
                            if (syncTask != null) {
                                if (syncTask.getStatus() != AsyncTask.Status.FINISHED){
                                    return;
                                }else{
                                    syncTask = null;
                                }
                            }
                            syncTask = new SyncTask();
                            syncTask.execute();
                            Log.i("EERRRR", "Timer execute!");
                        }
                    }else {
                        if (currentBus != null){
                            if (syncTask != null) {
                                if (syncTask.getStatus() != AsyncTask.Status.FINISHED){
                                    return;
                                }else{
                                    syncTask = null;
                                }
                            }
                            syncTask = new SyncTask();
                            syncTask.execute();
                            Log.i("EERRRR", "Timer execute!");
                        }
                    }

                }
                    break;
                default:
                    break;
            }
        }
    };



    private void showBusInfo() {
        if (currentBus != null){
            popWindow = new BusInfoPopWindow(getActivity(), currentBus);
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

            Dialog alertDialog = new AlertDialog.Builder(getActivity()).
                    setTitle("Please select").
                    setIcon(R.mipmap.icon_report)
                    .setSingleChoiceItems(childnames, selectedReportIndex, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedReportIndex = which;
                        }
                    }).
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setCurrentBus(buses.get(selectedReportIndex));

                                }
                            }).
                            setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

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

            Dialog alertDialog = new AlertDialog.Builder(getActivity()).
                    setTitle("Please select").
                    setIcon(R.mipmap.icon_report)
                    .setSingleChoiceItems(childnames, selectedReportIndex, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedReportIndex = which;
                        }
                    }).
                            setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    setCurrentChild(childs.get(selectedReportIndex));

                                }
                            }).
                            setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO Auto-generated method stub
                                }
                            }).create();
            alertDialog.show();

        }

    }

    public class  SyncTask extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... params) {
            if (!isManager()){
                return HttpData.getChildLines(currentChild.getId());
            }
            return HttpData.getBusLines(currentBus.getId());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try{
                JSONObject jsonObject = new JSONObject(s);
                boolean status = jsonObject.getBoolean("status");

                if (status){

                    JSONArray jsonArray = jsonObject.getJSONObject("info").getJSONObject("line").getJSONArray("line_sites");

                    if (lines == null){
                        lines = new ArrayList<>();
                    }
                    lines.clear();
                    if (isManager()){
                        for(int i = 0; i < jsonArray.length(); i++){
                            LineBean lineBean = new LineBean(jsonArray.getJSONObject(i));
                            lines.add(lineBean);
                        }
                    }else {
                        String upId = jsonObject.getJSONObject("info").getJSONObject("on").getString("site_id");
                        String offId = jsonObject.getJSONObject("info").getJSONObject("off").getString("site_id");
                        currentBus = new BusBean(jsonObject.getJSONObject("info").getJSONObject("bus"));

                        for(int i = 0; i < jsonArray.length(); i++){
                            LineBean lineBean = new LineBean(jsonArray.getJSONObject(i));
                            if (lineBean.getId().equals(upId)){
                                lineBean.setChildUpOff(LineBean.CHILD_LINE_UP);
                            }
                            if (lineBean.getId().equals(offId)){
                                lineBean.setChildUpOff(LineBean.CHILD_LINE_OFF);
                            }
                            lines.add(lineBean);
                        }
                    }


                    if (listViewAdapter == null){
                        listViewAdapter = new TrackListViewAdapter(getActivity(), lines);
                        listView.setAdapter(listViewAdapter);
                    }else {
                        listViewAdapter.setItems(lines);
                    }

                }else {
                    String msg = jsonObject.getString("msg");
                    if (msg.length() != 0){
                        Toast.makeText(getContext(), msg , Toast.LENGTH_SHORT).show();
                    }
                    if (msg.equals("没有权限")){
                        MyApplication.getInstance().getSharedPreferenceManager().setUserMobile("");
                        MyApplication.getInstance().getSharedPreferenceManager().setUserPass("");

                        Intent intent = new Intent(getActivity(), LoginActivity.class);

                        startActivity(intent);

                        getActivity().finish();
                    }
                }
            }catch (Exception e){

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
