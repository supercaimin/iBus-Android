package cn.homecaught.ibus_jhr.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_jhr.MyApplication;
import cn.homecaught.ibus_jhr.R;
import cn.homecaught.ibus_jhr.activity.LoginActivity;
import cn.homecaught.ibus_jhr.adapter.TrackListViewAdapter;
import cn.homecaught.ibus_jhr.model.BusBean;
import cn.homecaught.ibus_jhr.model.ChildBean;
import cn.homecaught.ibus_jhr.model.LineBean;
import cn.homecaught.ibus_jhr.util.HttpData;
import cn.homecaught.ibus_jhr.view.BusInfoPopWindow;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AirPlusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AirPlusFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AirPlusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Toolbar toolbar = null;

    private WebView webView;

    private List<ChildBean> childs;
    private List<LineBean> lines;
    private List<BusBean> buses;
    private BusBean currentBus;

    private ProgressDialog progressDialog;

    private int selectedReportIndex = 0;
    private ChildBean currentChild = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AirPlusFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AirPlusFragment newInstance(String param1, String param2) {
        AirPlusFragment fragment = new AirPlusFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public AirPlusFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_air_plus, container, false);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(R.string.tip_tip);
        progressDialog.setMessage(getString(R.string.tip_wait));

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.tip_wait);

        toolbar.inflateMenu(R.menu.map);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSelectAlert();
                return false;
            }
        });


        webView = (WebView) view.findViewById(R.id.webview);

        this.webView.getSettings().setSupportZoom(false);
//      this.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.webView.getSettings().setJavaScriptEnabled(true);
        this.webView.getSettings().setDomStorageEnabled(true);
        //webView.loadUrl("http://www.ibuschina.com/map/?busid=13&lineid=50&domain=xyd.ibokun.com");
        new GetChildsTask().execute();
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("CCCCCCCCCC____onAttach");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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

            Dialog alertDialog = new AlertDialog.Builder(getActivity()).
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
        toolbar.setTitle(getString(R.string.line_lablel_track) + "(" + currentChild.getFirstName() + " " + currentChild.getLastName() +")");
        new SyncTask().execute();
    }
    public void setCurrentBus(BusBean currentBus) {
        this.currentBus = currentBus;
        toolbar.setTitle(getString(R.string.line_lablel_track) + "("+ currentBus.getBusNumber() + ")");
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
                    String lineid = jsonObject.getJSONObject("info").getJSONObject("line").getString("id");

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
                        //setCurrentBus();
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
                    String url = "http://www.ibuschina.com/map/?busid=" +  currentBus.getId() + "&lineid="+
                            lineid+"&domain=" + MyApplication.getInstance().getSharedPreferenceManager().getSchoolDomain();
                    webView.loadUrl(url);
                    Log.v("Map URL:", url);



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

}
