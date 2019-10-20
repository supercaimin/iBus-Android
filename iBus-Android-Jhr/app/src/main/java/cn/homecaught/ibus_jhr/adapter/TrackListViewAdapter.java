package cn.homecaught.ibus_jhr.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.homecaught.ibus_jhr.R;
import cn.homecaught.ibus_jhr.model.LineBean;
import cn.homecaught.ibus_jhr.view.LineView;

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
            height = MIN_HEIGHT + distance * 20;
        }
        viewHolder.lineView.setLineHeight(height);
        ViewGroup.LayoutParams layoutParams  = viewHolder.lineView.getLayoutParams();
        layoutParams.height = height;
        viewHolder.lineView.setLayoutParams(layoutParams);
        String shortDate = "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

            Date date = sdf.parse(lineBean.getArrivedTime());
            int minute = date.getMinutes();
            shortDate = date.getHours() + ":";
            if (minute > 9){
                shortDate += minute + "";
            }else {
                shortDate += "0" + minute;
            }
            if(lineBean.getChildUpOff() == LineBean.CHILD_LINE_NORMAL){
                if (position == 0){
                    viewHolder.timeArrivedTextView.setText(mContext.getText(R.string.line_label_departure) + shortDate);
                }else {
                    viewHolder.timeArrivedTextView.setText(mContext.getText(R.string.line_label_arrived) + shortDate);
                }
            }else {
                if (lineBean.getChildUpOff() == LineBean.CHILD_LINE_UP){
                    viewHolder.timeArrivedTextView.setText(mContext.getText(R.string.line_label_picked_up) + shortDate);
                } else {
                    viewHolder.timeArrivedTextView.setText(mContext.getText(R.string.line_label_get_off) + shortDate);
                }
            }

            viewHolder.lineView.setStyle(LineView.LINE_NORMAL_STYLE);
        }catch (Exception e){
            viewHolder.timeArrivedTextView.setText("");
            viewHolder.lineView.setStyle(LineView.LINE_DASHED_STYLE);
        }

        if (lineBean.getArrivedTime().equals("null")){
            viewHolder.stopShapeView.setBackgroundResource(R.drawable.shape_unarrived_stop);
        }else {
            viewHolder.stopShapeView.setBackgroundResource(R.drawable.shape_arrived_stop);
            Log.i("xxxxxxxxx position", "  " + position);
            Log.i("xxxxxxxxx mszie", "  " + mItems.size());
            Log.i("xxxxxxxxx", lineBean.getArrivedTime());

            if (position != (mItems.size() - 1)){
                LineBean nextLine = mItems.get(position + 1);
                if (nextLine.getArrivedTime().equals("null")){
                    viewHolder.stopShapeView.setBackgroundResource(R.drawable.shape_dest_stop);
                }
            }else {
                if (!lineBean.getArrivedTime().equals("null")){
                    viewHolder.stopShapeView.setBackgroundResource(R.drawable.shape_dest_stop);
                }
            }
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
