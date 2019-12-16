package cn.homecaught.ibus_saas.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.homecaught.ibus_saas.R;
import cn.homecaught.ibus_saas.model.ChildBean;
import cn.homecaught.ibus_saas.util.HttpData;

/**
 * Created by a1 on 16/6/16.
 */
public class StudentInfoPopWindow extends PopupWindow {
    private Context context;
    private LayoutInflater mInflater;
    private View contentView;
    private CircleImageView imageView;
    private TextView tvName;
    private TextView tvSN;
    private TextView tvGrade;
    private TextView tvMobile;
    private TextView tvKinder;
    private TextView tvPickUpStop;
    private TextView tvPickOffStop;

    public StudentInfoPopWindowMobileClickInterface getOnMobileClickInterface() {
        return onMobileClickInterface;
    }

    public void setOnMobileClickInterface(StudentInfoPopWindowMobileClickInterface onMobileClickInterface) {
        this.onMobileClickInterface = onMobileClickInterface;
    }

    public StudentInfoPopWindowMobileClickInterface onMobileClickInterface;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public interface StudentInfoPopWindowMobileClickInterface {
        public void OnMobileClickInterface(String mobile);
    }

    public StudentInfoPopWindow(Context context, ChildBean user) {
        this.context = context;
        initWindow(user);
    }

    private void initWindow(final ChildBean userBean) {
        // TODO Auto-generated method stub
        mInflater = LayoutInflater.from(context);
        contentView = mInflater.inflate(R.layout.student_info_popwindow, null);
        imageView = (CircleImageView)contentView.findViewById(R.id.ivHead);

        ImageLoader.getInstance().displayImage(HttpData.getBaseUrl() + userBean.getHead(),
                imageView);
        tvName = (TextView) contentView.findViewById(R.id.tvName);
        tvName.setText(userBean.getLastName() + userBean.getFirstName());
        tvSN = (TextView) contentView.findViewById(R.id.tvSN);
        tvSN.setText(userBean.getSN());
        tvGrade = (TextView) contentView.findViewById(R.id.tvGrade);
        tvGrade.setText(userBean.getGrade());
        tvPickOffStop =(TextView) contentView.findViewById(R.id.tvPickOffStop);
        tvPickUpStop = (TextView) contentView.findViewById(R.id.tvPickUpStop);
        if (userBean.getGuardian() != null){
            tvMobile = (TextView) contentView.findViewById(R.id.tvMobile);
            tvMobile.setText(userBean.getGuardian().getUserMobile());
            tvMobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onMobileClickInterface != null) {
                        onMobileClickInterface.OnMobileClickInterface(userBean.getGuardian().getUserMobile());
                    }

                }
            });
            tvKinder = (TextView) contentView.findViewById(R.id.tvKinder);
            tvKinder.setText(userBean.getGuardian().getUserRealName());
        }

        if (userBean.getPickOffStop() != null)
            tvPickOffStop.setText(userBean.getPickOffStop().getLineName());
        if (userBean.getPickUpStop() != null)
            tvPickUpStop.setText(userBean.getPickUpStop().getLineName());
        setContentView(contentView);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(0xFFFFFFFF);
        setBackgroundDrawable(dw);
        setFocusable(true);
    }

}
