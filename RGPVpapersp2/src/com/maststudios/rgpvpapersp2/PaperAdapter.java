package com.maststudios.rgpvpapersp2;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PaperAdapter extends BaseAdapter {

	private List<Paper> list;
	private Context context;

	public PaperAdapter(Context context, List<Paper> l) {
		list=l;
		this.context=context;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		Paper p=list.get(position);
		return p.getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.list_item_layout, parent, false);
		TextView heading = (TextView) rowView.findViewById(R.id.heading);
		TextView detail = (TextView) rowView.findViewById(R.id.details);
		TextView year = (TextView) rowView.findViewById(R.id.downloadYear);
		TextView isDownloaded = (TextView) rowView.findViewById(R.id.isDownloaded);
		heading.setText(list.get(position).getHeading());
		detail.setText(list.get(position).getDetails());
		year.setText(list.get(position).getYear());
		if(list.get(position).isDownloaded()){
			isDownloaded.setVisibility(android.view.View.VISIBLE);
		}else{
			isDownloaded.setVisibility(android.view.View.GONE);
		}
		return rowView;
	}

}
