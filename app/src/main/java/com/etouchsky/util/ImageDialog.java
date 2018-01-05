package com.etouchsky.util;



import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.Window;
import android.widget.ImageView;

import com.etouchsky.adapter.AsyncImageLoader;
import com.etouchsky.adapter.AsyncImageLoader.ImageCallback;
import com.etouchsky.wisdom.Commodity_Details;
import com.etouchsky.wisdom.R;

public class ImageDialog extends Dialog{
	private String result;
	private Context mcContext;
	ImageView imageView;
	private AsyncImageLoader asyncImageLoader;
	public ImageDialog(Context context,String Result) {
		super(context);
		// TODO Auto-generated constructor stub
		this.result=Result;
		this.mcContext = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.imagedialog);
		Window w = getWindow();
		w.setBackgroundDrawableResource(R.color.no_back);
		w.setType(R.style.NobackDialog);
		asyncImageLoader = new AsyncImageLoader();
		Commodity_Details.mDownStartTime = System.currentTimeMillis();
		init();


		
	}

	String name;
	Bitmap bitmap;
	private void init() {
		// TODO Auto-generated method stub
		 imageView=(ImageView)findViewById(R.id.da_image);
		
		 //imageView.setImageBitmap(Tool.returnBitMap(result, mcContext));
			Drawable cachedImage = asyncImageLoader.loadDrawable(result,
					new ImageCallback() {

						public void imageLoaded(Drawable imageDrawable,
								String imageUrl) {

							imageView.setImageDrawable(zoomDrawable(imageDrawable,700,700));
						
						}

					});
			if (cachedImage == null) {

				imageView.setImageResource(R.mipmap.photo_loading);
				
			} else {

				imageView.setImageDrawable(cachedImage);
				
			}
	}
	
	private Drawable zoomDrawable(Drawable drawable, int w, int h) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap oldbmp = drawableToBitmap(drawable);
		Matrix matrix = new Matrix();
		float scaleWidth = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
				matrix, true);
		return new BitmapDrawable(newbmp);
	}

	private Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565;
		Bitmap bitmap = Bitmap.createBitmap(width, height, config);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
		if(isShowing()){
			dismiss();

		}
		}

		return super.onTouchEvent(event);
	}

}
