package cn.homecaught.ibus_android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import cn.homecaught.ibus_android.R;

import java.util.List;

/**
 * Created by a1 on 16/6/16.
 */
public class GridViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;

    public void setOnInfoButtonOnClickListener(OnInfoButtonOnClickListener onInfoButtonOnClickListener) {
        this.onInfoButtonOnClickListener = onInfoButtonOnClickListener;
    }

    public OnInfoButtonOnClickListener onInfoButtonOnClickListener;

    public GridViewAdapter(Context context) {

        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return 20;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(R.layout.grid_item, null);
        convertView.findViewById(R.id.btn_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onInfoButtonOnClickListener != null){
                    onInfoButtonOnClickListener.onClick("");
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tvDate;
        TextView tvName;
        TextView tvSchool;
        TextView tvValue;
        TextView tvStatus;

    }

    public interface OnInfoButtonOnClickListener{
        public void onClick(String studentId);
    }
}
