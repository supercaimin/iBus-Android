package cn.homecaught.ibus_android.fragment;

import android.app.Activity;
import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import cn.homecaught.ibus_android.R;
import cn.homecaught.ibus_android.MyApplication;
import cn.homecaught.ibus_android.activity.LoginActivity;
import cn.homecaught.ibus_android.activity.PwdActivity;
import cn.homecaught.ibus_android.activity.WebViewActivity;
import cn.homecaught.ibus_android.util.CameraDialog;
import cn.homecaught.ibus_android.util.HttpData;
import cn.homecaught.ibus_android.util.ImageUntils;

/**
 * Created with IntelliJ IDEA.
 * Author: wangjie  email:wangjie@cyyun.com
 * Date: 13-6-14
 * Time: 下午2:39
 */
public class MeFragment extends Fragment {

    private View container;
    private ImageView ivHead;

    public OnMeHeadImageUploadListener getOnMeHeadImageUploadListener() {
        return onMeHeadImageUploadListener;
    }

    public void setOnMeHeadImageUploadListener(OnMeHeadImageUploadListener onMeHeadImageUploadListener) {
        this.onMeHeadImageUploadListener = onMeHeadImageUploadListener;
    }

    private OnMeHeadImageUploadListener onMeHeadImageUploadListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        System.out.println("CCCCCCCCCC____onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("CCCCCCCCCC____onCreate");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        System.out.println("CCCCCCCCCC____onCreateView");
        this.container = inflater.inflate(R.layout.me_fragment, container, false);
        this.container.findViewById(R.id.llAbout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("webContent", WebViewActivity.WEB_CONTENT_ABOUT_US);
                startActivity(intent);
            }
        });
        this.container.findViewById(R.id.llWork).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("webContent", WebViewActivity.WEB_CONTENT_WROK);
                startActivity(intent);
            }
        });

        this.container.findViewById(R.id.llUrgent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WebViewActivity.class);
                intent.putExtra("webContent", WebViewActivity.WEB_CONTENT_URGENT);
                startActivity(intent);
            }
        });

        this.container.findViewById(R.id.llChgPwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PwdActivity.class);
                startActivity(intent);
            }
        });

        this.container.findViewById(R.id.llLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyApplication.getInstance().getSharedPreferenceManager().setUserMobile("");
                MyApplication.getInstance().getSharedPreferenceManager().setUserPass("");


                Intent intent = new Intent(getActivity(), LoginActivity.class);

                startActivity(intent);

                getActivity().finish();

            }
        });

        ivHead = (ImageView)this.container.findViewById(R.id.ivHead);

        ImageLoader.getInstance().displayImage(HttpData.BASE_URL
                + MyApplication.getInstance().getLoginUser().getUserHead(), ivHead);


        ivHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onMeHeadImageUploadListener != null) onMeHeadImageUploadListener.onHeadImageClick(ivHead);
            }
        });
        return this.container;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        System.out.println("CCCCCCCCCC____onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("CCCCCCCCCC____onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("CCCCCCCCCC____onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("CCCCCCCCCC____onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("CCCCCCCCCC____onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("CCCCCCCCCC____onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("CCCCCCCCCC____onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        System.out.println("CCCCCCCCCC____onDetach");
    }


    public interface OnMeHeadImageUploadListener{

        public void onHeadImageClick(ImageView ivHead);
    }

}
