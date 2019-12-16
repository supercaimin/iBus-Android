package cn.homecaught.ibus_jhr.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.homecaught.ibus_jhr.R;
import cn.homecaught.ibus_jhr.model.BusBean;
import cn.homecaught.ibus_jhr.util.HttpData;

/**
 * Created by a1 on 16/6/16.
 */
public class BusInfoPopWindow extends PopupWindow {
    private Context context;
    private LayoutInflater mInflater;
    private View contentView;
    private TextView tvBusNumber;
    private CircleImageView driverImageView;
    private TextView tvDriverName;
    private TextView tvDriverMobile;
    private CircleImageView ayiImageView;
    private TextView tvAyiName;
    private TextView tvAyiMoblie;
    private CircleImageView managerImageView;
    private TextView tvManagerName;
    private TextView tvManagerMoblie;

    public BusInfoPopWindow(Context context, BusBean bus) {
        this.context = context;
        initWindow(bus);
    }

    private void initWindow(BusBean busBean) {
        // TODO Auto-generated method stub
        mInflater = LayoutInflater.from(context);
        contentView = mInflater.inflate(R.layout.bus_info_popwindow, null);
        tvBusNumber = (TextView) contentView.findViewById(R.id.plate_number);
        tvBusNumber.setText("   车牌号:" + busBean.getBusNumber() + "   ");
        driverImageView = (CircleImageView)contentView.findViewById(R.id.ivDriverHead);
        ImageLoader.getInstance().displayImage(HttpData.getBaseUrl() + busBean.getDriver().getUserHead(), driverImageView);
        tvDriverName = (TextView) contentView.findViewById(R.id.tvDriverName);
        tvDriverName.setText("司机:" + busBean.getDriver().getUserRealName());
        tvDriverMobile = (TextView) contentView.findViewById(R.id.tvDriverMobile);
        tvDriverMobile.setText(busBean.getDriver().getUserMobile());
        tvDriverMobile.setVisibility(View.GONE);

        ayiImageView = (CircleImageView)contentView.findViewById(R.id.ivAyiHead);
        ImageLoader.getInstance().displayImage(HttpData.getBaseUrl() + busBean.getAyi().getUserHead(), ayiImageView);
        tvAyiName = (TextView) contentView.findViewById(R.id.tvAyiName);
        tvAyiName.setText("校车老师:"+busBean.getAyi().getUserRealName());
        tvAyiMoblie = (TextView) contentView.findViewById(R.id.tvAyiMobile);
        tvAyiMoblie.setText(busBean.getAyi().getUserMobile());

        managerImageView = (CircleImageView)contentView.findViewById(R.id.ivManagerHead);
        ImageLoader.getInstance().displayImage(HttpData.getBaseUrl() + busBean.getManager().getUserHead(), managerImageView);
        tvManagerName = (TextView) contentView.findViewById(R.id.tvManagerName);
        tvManagerName.setText("校车经理:" + busBean.getManager().getUserRealName());
        tvManagerMoblie = (TextView) contentView.findViewById(R.id.tvManagerMobile);
        tvManagerMoblie.setText(busBean.getManager().getUserMobile());

        setContentView(contentView);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(0xFFFFFFFF);
        setBackgroundDrawable(dw);
        setFocusable(true);
    }

}
