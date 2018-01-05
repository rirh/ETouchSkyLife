package com.etouchsky.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.etouchsky.wisdom.R;

import java.util.List;


public class WelgallaryAdapter extends BaseAdapter{
	private List<Integer> mAppList;
	private AsyncImageLoader asyncImageLoader;
	Context mContext;


	private LayoutInflater mInflater;

	public List<Integer> getmAppList() {
		return mAppList;
	}

	public void setmAppList(List<Integer> mAppList) {
		this.mAppList = mAppList;
	}

	private class RecentViewHolder {

		private ImageView imageView;

	}

	public WelgallaryAdapter(Context c) {
		this.mContext = c;

		mInflater = (LayoutInflater) c
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		asyncImageLoader = new AsyncImageLoader();

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.widget.Adapter#getCount()
	 */

	public void clear() {
		if (mAppList != null) {
			mAppList.clear();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {

		return mAppList.get(position);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.widget.Adapter#getItemId(int)
	 */

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub

		return position;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */


	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final RecentViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.gallery_item, null);
			holder = new RecentViewHolder();

			holder.imageView = (ImageView) convertView
					.findViewById(R.id.gallery_image);



			convertView.setTag(holder);
		} else {
			holder = (RecentViewHolder) convertView.getTag();
		}

		System.out.println(position);
		int imageUrl = mAppList.get(position);
		holder.imageView.setTag(imageUrl);

		/*Drawable cachedImage = asyncImageLoader.loadDrawable(imageUrl,
				new ImageCallback() {

					public void imageLoaded(Drawable imageDrawable,
											String imageUrl) {

						holder.imageView.setImageDrawable(imageDrawable);

					}

				});*/
		if (imageUrl == 0) {
			holder.imageView.setImageResource(R.mipmap.photo_loading);
		} else {
			//holder.imageView.setImageDrawable(cachedImage);
			holder.imageView.setBackgroundResource(imageUrl);
		}
		return convertView;
	}

	public void remove(int position) {
		mAppList.remove(position);
		this.notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAppList.size();
	}
}
