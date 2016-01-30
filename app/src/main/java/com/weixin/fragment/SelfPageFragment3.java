package com.weixin.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.weixin.R;

public class SelfPageFragment3 extends Fragment implements View.OnClickListener
{
	private String mTitle = "Default";

	public static final String TITLE = "title";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.self_page,container,false);
		LinearLayout selfItem = (LinearLayout) view.findViewById(R.id.self_detail_item);
		selfItem.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.self_detail_dialog,null);
				RelativeLayout changeIcon = (RelativeLayout) dialogView.findViewById(R.id.change_icon_item);
				changeIcon.setOnClickListener(this);
				AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(dialogView).create();

			}
		});
		return view;

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()){
			case R.id.change_icon_item:

				break;
		}
	}
}
