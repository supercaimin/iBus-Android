package cn.homecaught.ibus_saas.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import cn.homecaught.ibus_saas.R;
import cn.homecaught.ibus_saas.model.LineBean;

/**
 * Created by a1 on 2017/3/30.
 */

public class LineDataAdapter extends ArrayAdapter<LineBean> {

    private int resourceId;
    public LineDataAdapter(Context context, int textViewResourceId, List<LineBean> objects){
        super(context, textViewResourceId,objects);
        resourceId=textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
// TODO Auto-generated method stub
        LineBean data=getItem(position);
        View view;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceId, null);
        }
        else {
            view=convertView;
        }
        TextView dataStringTextView=(TextView)view.findViewById(R.id.tv_line);
        dataStringTextView.setText(data.getLineName());
        return view;
    }

}