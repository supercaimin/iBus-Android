package cn.homecaught.ibus_jhr.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.homecaught.ibus_jhr.MyApplication;
import cn.homecaught.ibus_jhr.R;
import cn.homecaught.ibus_jhr.model.UserBean;
import cn.homecaught.ibus_jhr.util.HttpData;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imlib.model.Conversation;
import io.rong.imkit.RongIM;

/**
 * Created with IntelliJ IDEA.
 * Author: wangjie  email:wangjie@cyyun.com
 * Date: 13-6-14
 * Time: 下午2:39
 */
public class MessageFragment extends Fragment{
    private Toolbar toolbar = null;
    private List<UserBean> mFriends;
    private int mSelectedTargetUserIndex ;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("BBBBBBBBBBB____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("BBBBBBBBBBB____onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("BBBBBBBBBBB____onCreateView");

        View v = inflater.inflate(R.layout.message_fragment, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        toolbar.setTitle("Message");
        toolbar.inflateMenu(R.menu.menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                new GetFriendsTask().execute();
                return false;
            }
        });

        ConversationListFragment fragment = new ConversationListFragment();
        Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
                .appendPath("conversationlist")
                .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//设置群组会话聚合显示
                .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "false")//设置讨论组会话非聚合显示
                .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "false")//设置系统会话非聚合显示
                .build();
        fragment.setUri(uri);


        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.rong_content, fragment);
        transaction.commit();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("BBBBBBBBBBB____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("BBBBBBBBBBB____onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("BBBBBBBBBBB____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("BBBBBBBBBBB____onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("BBBBBBBBBBB____onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("BBBBBBBBBBB____onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("BBBBBBBBBBB____onDetach");
    }

    public class GetFriendsTask extends AsyncTask<Void, Void, String> {

        public GetFriendsTask() {
            super();
        }

        @Override
        protected String doInBackground(Void... params) {

            String userId = MyApplication.getInstance().getLoginUser().getId();
            return HttpData.getFriends(userId);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            try{
                if (mFriends == null)
                    mFriends =new ArrayList<>();
                mFriends.clear();
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray = jsonObject.getJSONArray("info");
                for (int i = 0; i< jsonArray.length(); i++){
                    UserBean userBean = new UserBean(jsonArray.getJSONObject(i));
                    mFriends.add(userBean);
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

    private void showReportAlert() {

        final CharSequence[] reports = new CharSequence[mFriends.size()];
        final boolean checkedItems[] = new boolean[mFriends.size()];
        List<String> names = new ArrayList<>();
        for (int i=0; i <mFriends.size(); i++){
            UserBean userBean = mFriends.get(i);
            names.add(userBean.getUserFirstName() + " " + userBean.getUserLastName());
            checkedItems[i] = false;
        }
        names.toArray(reports);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please select friends.");
        builder.setMultiChoiceItems(reports, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedItems[which] = isChecked;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                List<UserBean> selectUsers = new ArrayList<UserBean>();
                for (int i = 0; i < mFriends.size(); i++) {
                    if (checkedItems[i] == true) {
                        selectUsers.add(mFriends.get(i));
                    }
                }
                if (selectUsers.size() != 0) {
                    String schoolId = MyApplication.getInstance().getSharedPreferenceManager().getSchoolId();
                    if (selectUsers.size() == 1) {
                        UserBean user = selectUsers.get(0);
                        RongIM.getInstance().startPrivateChat(getActivity(), schoolId + "_" + user.getId(), user.getUserFirstName() + " " + user.getUserLastName());
                    } else {
                        List<String> ids = new ArrayList<String>();
                        String title = "";
                        for (int i = 0; i < selectUsers.size(); i++) {
                            UserBean user = selectUsers.get(i);
                            ids.add(schoolId + "_" + user.getId());

                            title += user.getUserLastName() + " ";
                        }
                        RongIM.getInstance().createDiscussionChat(getActivity(), ids, title);

                    }
                }
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

}
