package cn.homecaught.ibus_android.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.homecaught.ibus_android.R;
import cn.homecaught.ibus_android.model.ChildBean;
import cn.homecaught.ibus_android.model.UserBean;
import cn.homecaught.ibus_android.util.HttpData;

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

    public StudentInfoPopWindow(Context context, ChildBean user) {
        this.context = context;
        initWindow(user);
    }

    private void initWindow(ChildBean userBean) {
        // TODO Auto-generated method stub
        mInflater = LayoutInflater.from(context);
        contentView = mInflater.inflate(R.layout.student_info_popwindow, null);
        imageView = (CircleImageView)contentView.findViewById(R.id.ivHead);

        ImageLoader.getInstance().displayImage(HttpData.getBaseUrl() + userBean.getHead(),
                imageView);
        tvName = (TextView) contentView.findViewById(R.id.tvName);
        tvName.setText(userBean.getFastName() + userBean.getFirstName());
        tvSN = (TextView) contentView.findViewById(R.id.tvSN);
        tvSN.setText(userBean.getSN());
        tvGrade = (TextView) contentView.findViewById(R.id.tvGrade);
        tvGrade.setText(userBean.getGrade());
        tvPickOffStop =(TextView) contentView.findViewById(R.id.tvPickOffStop);
        tvPickUpStop = (TextView) contentView.findViewById(R.id.tvPickUpStop);
        if (userBean.getGuardian() != null){
            tvMobile = (TextView) contentView.findViewById(R.id.tvMobile);
            tvMobile.setText(userBean.getGuardian().getUserMobile());
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
