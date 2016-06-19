package cn.homecaught.ibus_android.view;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import cn.homecaught.ibus_android.R;
/**
 * Created by a1 on 16/6/16.
 */
public class StudentInfoPopWindow extends PopupWindow {
    private Context context;
    private LayoutInflater mInflater;
    private View contentView;

    public StudentInfoPopWindow(Context context) {
        this.context = context;
        initWindow();
    }

    private void initWindow() {
        // TODO Auto-generated method stub
        mInflater = LayoutInflater.from(context);
        contentView = mInflater.inflate(R.layout.student_info_popwindow, null);
        setContentView(contentView);
        setWidth(LinearLayout.LayoutParams.WRAP_CONTENT);
        setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        ColorDrawable dw = new ColorDrawable(0xFFFFFFFF);
        setBackgroundDrawable(dw);
        setFocusable(true);
    }

}
