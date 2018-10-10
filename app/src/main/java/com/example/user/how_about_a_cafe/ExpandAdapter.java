package com.example.user.how_about_a_cafe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ExpandAdapter extends BaseExpandableListAdapter {
	//    FirebaseStorage storage = FirebaseStorage.getInstance("gs://how-about-a-cafe.appspot.com");
//    StorageReference storageRef = storage.getReference();
	private Context context;
	private int groupLayout = 0;
	private int chlidLayout = 0;
	private ArrayList<myGroup> DataList;
	private LayoutInflater myinf = null;
	private ViewHolder viewHolder;

	public ExpandAdapter(Context context, int groupLay, int chlidLay, ArrayList<myGroup> DataList) {
		this.DataList = DataList;
		this.groupLayout = groupLay;
		this.chlidLayout = chlidLay;
		this.context = context;
		this.myinf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = myinf.inflate(this.groupLayout, parent, false);
		}

		TextView groupName = (TextView) convertView.findViewById(R.id.groupName);
		groupName.setText(DataList.get(groupPosition).groupName);
		viewHolder.iv_image = (ImageView) convertView.findViewById(R.id.hwa_down);
		if (isExpanded) {
			viewHolder.iv_image.setImageResource(R.drawable.ic_arrow_up);
		} else {
			viewHolder.iv_image.setImageResource(R.drawable.ic_arrow_down);
		}
		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			convertView = myinf.inflate(this.chlidLayout, parent, false);
		}
		TextView childPrice = (TextView) convertView.findViewById(R.id.childPrice);
		childPrice.setText(DataList.get(groupPosition).childPrice.get(childPosition));
		TextView childName = (TextView) convertView.findViewById(R.id.childName);
		childName.setText(DataList.get(groupPosition).child.get(childPosition));
//        ImageView childImage = (ImageView) convertView.findViewById(R.id.childImage);
//        Glide.with(context).load(DataList.get(groupPosition).childImage.get(childPosition)).into(childImage);

		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public String getChild(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return DataList.get(groupPosition).child.get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return DataList.get(groupPosition).child.size();
	}

	@Override
	public myGroup getGroup(int groupPosition) {
		// TODO Auto-generated method stub
		return DataList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return DataList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	class ViewHolder {
		public ImageView iv_image;
	}
}
