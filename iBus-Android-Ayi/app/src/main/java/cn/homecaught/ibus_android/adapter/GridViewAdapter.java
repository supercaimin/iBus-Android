package cn.homecaught.ibus_android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.homecaught.ibus_android.R;
import cn.homecaught.ibus_android.model.UserBean;
import cn.homecaught.ibus_android.util.HttpData;

import java.util.List;

/**
 * Created by a1 on 16/6/16.
 */
public class GridViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<UserBean> mItems;
    private String mTravelType = "";

    public void setOnInfoButtonOnClickListener(OnInfoButtonOnClickListener onInfoButtonOnClickListener) {
        this.onInfoButtonOnClickListener = onInfoButtonOnClickListener;
    }

    public OnInfoButtonOnClickListener onInfoButtonOnClickListener;

    public GridViewAdapter(Context context, List<UserBean> items) {

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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder  viewHolder = null;
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.grid_item, null);
            convertView.findViewById(R.id.btn_info).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onInfoButtonOnClickListener != null){
                        onInfoButtonOnClickListener.onClick((UserBean)v.getTag());
                    }
                }
            });

            viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView)convertView.findViewById(R.id.tvName);
            viewHolder.headImageView = (ImageView)convertView.findViewById(R.id.imageView);
            viewHolder.snTextView = (TextView) convertView.findViewById(R.id.tvSN);
            viewHolder.viewMask = (View) convertView.findViewById(R.id.viewMask);
            viewHolder.infoBtn = (Button) convertView.findViewById(R.id.btn_info);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        UserBean userBean = mItems.get(position);
        if (userBean.getPickOffStop() != null)
            viewHolder.nameTextView.setText(userBean.getPickOffStop().getLineName());
        else
            viewHolder.nameTextView.setText("");
        viewHolder.snTextView.setText(userBean.getUserSN());
        viewHolder.infoBtn.setTag(userBean);

        if (userBean.getUserOnBus().equals("off"))
            viewHolder.viewMask.setVisibility(View.VISIBLE);
        else
            viewHolder.viewMask.setVisibility(View.GONE);

        ImageLoader.getInstance().displayImage(HttpData.BASE_URL + userBean.getUserHead(),
                viewHolder.headImageView);


        return convertView;
    }

    class ViewHolder {
        ImageView headImageView;
        TextView nameTextView;
        TextView snTextView;
        View viewMask;
        Button infoBtn;

    }

    public interface OnInfoButtonOnClickListener{
        public void onClick(UserBean userBean);
    }

    public void setmItems(List<UserBean> items, String travelType){
        mItems = items;
        mTravelType = travelType;
        notifyDataSetChanged();
    }
}
