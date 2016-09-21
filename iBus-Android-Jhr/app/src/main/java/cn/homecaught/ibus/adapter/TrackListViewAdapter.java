package cn.homecaught.ibus.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.homecaught.ibus.R;
import cn.homecaught.ibus.model.LineBean;
import cn.homecaught.ibus.model.UserBean;
import cn.homecaught.ibus.view.LineView;

/**
 * Created by a1 on 16/9/21.
 */
public class TrackListViewAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<LineBean> mItems;

    private int MIN_HEIGHT = 120;

    public TrackListViewAdapter(Context context, List<LineBean> items) {

        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder  viewHolder = null;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.list_stop_item, null);
            viewHolder = new ViewHolder();
            viewHolder.stopShapeView = (View) convertView.findViewById(R.id.stop_shape);
            viewHolder.stopNameTextView = (TextView) convertView.findViewById(R.id.stop_name);
            viewHolder.timeArrivedTextView = (TextView) convertView.findViewById(R.id.time_arrived);
            viewHolder.lineView = (LineView) convertView.findViewById(R.id.line);
            viewHolder.distanceTextView = (TextView) convertView.findViewById(R.id.distance);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        LineBean lineBean = mItems.get(position);
        viewHolder.stopNameTextView.setText(lineBean.getLineSite());
        viewHolder.distanceTextView.setText(lineBean.getLineDistance() + "km");
        if (position == 0){
            viewHolder.lineView.setVisibility(View.GONE);
            viewHolder.distanceTextView.setVisibility(View.GONE);
        }else {
            viewHolder.lineView.setVisibility(View.VISIBLE);
            viewHolder.distanceTextView.setVisibility(View.VISIBLE);
        }
        int distance = Integer.parseInt(lineBean.getLineDistance());
        int height = 0;
        if (distance < 1){
            height = MIN_HEIGHT;
        }else {
            height = MIN_HEIGHT + distance * 60;
        }
        viewHolder.lineView.setLineHeight(height);
        ViewGroup.LayoutParams layoutParams  = viewHolder.lineView.getLayoutParams();
        layoutParams.height = height;
        viewHolder.lineView.setLayoutParams(layoutParams);
        String shortDate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
            Date date = sdf.parse(lineBean.getArrivedTime());
            shortDate = date.getHours() + ":" + date.getMinutes();
            if(lineBean.getChildUpOff() == LineBean.CHILD_LINE_NORMAL){
                viewHolder.timeArrivedTextView.setText("Arrived " + shortDate);
                viewHolder.stopShapeView.setBackgroundResource(R.drawable.shape_arrived_stop);
            }else {
                if (lineBean.getChildUpOff() == LineBean.CHILD_LINE_UP){
                    viewHolder.timeArrivedTextView.setText("Picked up " + shortDate);
                }else {
                    viewHolder.timeArrivedTextView.setText("Get off " + shortDate);
                }
                viewHolder.stopShapeView.setBackgroundResource(R.drawable.shape_dest_stop);
            }
            viewHolder.lineView.setStyle(LineView.LINE_NORMAL_STYLE);
        }catch (Exception e){
            viewHolder.timeArrivedTextView.setText("");
            viewHolder.stopShapeView.setBackgroundResource(R.drawable.shape_unarrived_stop);
            viewHolder.lineView.setStyle(LineView.LINE_DASHED_STYLE);
        }

        return convertView;
    }

    public void setItems(List<LineBean> items){
        mItems = items;
        notifyDataSetChanged();
    }

    class ViewHolder {
        View stopShapeView;
        TextView stopNameTextView;
        TextView timeArrivedTextView;
        LineView lineView;
        TextView distanceTextView;
    }
}
