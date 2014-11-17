package cn.com.scitc.swl.rssnews.fragment;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import cn.com.scitc.swl.rssnews.R;
import cn.com.scitc.swl.rssnews.activity.RssListActivity;
import cn.com.scitc.swl.rssnews.adapter.ImageAdapter;
import cn.com.scitc.swl.rssnews.constants.Constants;
import cn.com.scitc.swl.rssnews.model.RssSource;

/**
 * 订阅
 * 
 * @author ASHENG
 * 
 */
@SuppressLint("InflateParams")
public class SubscibeFragment extends Fragment {
	private GridView gridView;
	private ImageAdapter adapter;
	private List<RssSource> list = Constants.getRssSource();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_scibe, null);
		init(view);
		return view;
	}

	/**
	 * 初始化
	 * 
	 * @param view
	 */
	public void init(View view) {
		gridView = (GridView) view.findViewById(R.id.home_gridview);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new ImageAdapter(getActivity());
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				// TODO Auto-generated method stub
				if (list.size() == position) {
					Toast.makeText(getActivity(), "更多内容，敬请期待…",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent(getActivity(), RssListActivity.class);
				intent.putExtra("id", position);
				startActivity(intent);
			}
		});
	}
}
