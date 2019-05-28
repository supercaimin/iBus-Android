package cn.homecaught.ibus_saas_wx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import cn.homecaught.ibus_saas_wx.R;
import cn.homecaught.ibus_saas_wx.model.ChildBean;
import cn.homecaught.ibus_saas_wx.util.HttpData;

import java.util.List;

/**
 * Created by a1 on 16/6/16.
 */
public class GridViewAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private Context mContext;
    private List<ChildBean> mItems;

    public void setOnInfoButtonOnClickListener(OnInfoButtonOnClickListener onInfoButtonOnClickListener) {
        this.onInfoButtonOnClickListener = onInfoButtonOnClickListener;
    }

    public OnInfoButtonOnClickListener onInfoButtonOnClickListener;

    public GridViewAdapter(Context context, List<ChildBean> items) {

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
        if (userBean.getPickOffStop() != null)
            viewHolder.nameTextView.setText(userBean.getPickOffStop().getLineName());
        else
            viewHolder.nameTextView.setText("");
        viewHolder.userNameTextView.setText(userBean.getFirstName() + " " + userBean.getLastName());
        if (userBean.getNickName().equals("")){
            viewHolder.nickNameTextView.setVisibility(View.GONE);
        }else {
            viewHolder.nickNameTextView.setVisibility(View.VISIBLE);
            viewHolder.nickNameTextView.setText(userBean.getNickName());
        }
        viewHolder.infoBtn.setTag(userBean);

        if (userBean.getUserOnBus().equals("off"))
            viewHolder.viewMask.setVisibility(View.VISIBLE);
        else
            viewHolder.viewMask.setVisibility(View.GONE);

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

    public void setmItems(List<ChildBean> items){
        mItems = items;
        notifyDataSetChanged();
    }
}
