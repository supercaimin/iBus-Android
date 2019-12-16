package cn.homecaught.ibus_saas.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.homecaught.ibus_saas.R;
import cn.homecaught.ibus_saas.model.ChildBean;
import cn.homecaught.ibus_saas.util.HttpData;

import java.util.List;

/**
 * Created by a1 on 16/6/16.
 */
public class GridViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ChildBean> mItems;
    private String mRouteType = null;

    public void setOnInfoButtonOnClickListener(OnInfoButtonOnClickListener onInfoButtonOnClickListener) {
        this.onInfoButtonOnClickListener = onInfoButtonOnClickListener;
    }

    public OnInfoButtonOnClickListener onInfoButtonOnClickListener;

    public GridViewAdapter(Context context, List<ChildBean> items, String routeType) {

        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mItems = items;
        mRouteType = routeType;
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
                        onInfoButtonOnClickListener.onClick((ChildBean) v.getTag());
                    }
                }
            });

            viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView)convertView.findViewById(R.id.tvName);
            viewHolder.headImageView = (ImageView)convertView.findViewById(R.id.imageView);
            viewHolder.userNameTextView = (TextView) convertView.findViewById(R.id.tvUserName);
            viewHolder.nickNameTextView = (TextView) convertView.findViewById(R.id.tvNickName);
            viewHolder.viewMask = (View) convertView.findViewById(R.id.viewMask);
            viewHolder.infoBtn = (Button) convertView.findViewById(R.id.btn_info);

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.headImageView.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.head));
        ChildBean userBean = mItems.get(position);
//        Log.v("dddd", mRouteType + ":" + userBean.getPickUpStop().getLineName() + "::" + userBean.getPickOffStop().getLineName());
        if (mRouteType.equals("go")){
            if (userBean.getPickUpStop() != null)
                viewHolder.nameTextView.setText(userBean.getPickUpStop().getLineName());
            else
                viewHolder.nameTextView.setText("");
        } else {
            if (userBean.getPickOffStop() != null)
                viewHolder.nameTextView.setText(userBean.getPickOffStop().getLineName());
            else
                viewHolder.nameTextView.setText("");
        }

        viewHolder.userNameTextView.setText(userBean.getFirstName() + " " + userBean.getLastName());
        if (userBean.getNickName().equals("")){
            viewHolder.nickNameTextView.setVisibility(View.GONE);
        }else {
            viewHolder.nickNameTextView.setVisibility(View.VISIBLE);
            viewHolder.nickNameTextView.setText(userBean.getNickName());
        }
        viewHolder.infoBtn.setTag(userBean);


        if (userBean.getLeaveType().equals("leave")){
            if (userBean.isLeave()) {
                viewHolder.infoBtn.setBackgroundResource(R.mipmap.hong);
                convertView.setEnabled(false);
            }else {
                convertView.setEnabled(true);
                if (userBean.getUserOnBus().equals("off")){
                    viewHolder.viewMask.setVisibility(View.VISIBLE);
                    viewHolder.infoBtn.setBackgroundResource(R.mipmap.icon_info);
                } else {
                    viewHolder.viewMask.setVisibility(View.GONE);
                    viewHolder.infoBtn.setBackgroundResource(R.mipmap.l);
                }
            }
        } else if (userBean.getLeaveType().equals("temp_line")){
            viewHolder.infoBtn.setBackgroundResource(R.mipmap.lv);
            convertView.setEnabled(false);
        } else {
            convertView.setEnabled(true);
            if (userBean.getUserOnBus().equals("off")){
                viewHolder.viewMask.setVisibility(View.VISIBLE);
                viewHolder.infoBtn.setBackgroundResource(R.mipmap.icon_info);
            } else {
                viewHolder.viewMask.setVisibility(View.GONE);
                viewHolder.infoBtn.setBackgroundResource(R.mipmap.l);
            }
        }




        ImageLoader.getInstance().displayImage(HttpData.getBaseUrl() + userBean.getHead(),
                viewHolder.headImageView);


        return convertView;
    }

    class ViewHolder {
        ImageView headImageView;
        TextView nameTextView;
        TextView userNameTextView;
        TextView nickNameTextView;

        View viewMask;
        Button infoBtn;

    }

    public interface OnInfoButtonOnClickListener{
        public void onClick(ChildBean userBean);
    }

    public void setmItems(List<ChildBean> items, String lineType){
        mItems = items;
        mRouteType = lineType;
        notifyDataSetChanged();
    }
}
