package com.aleksus.handtohand.presentation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.aleksus.handtohand.R;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class ViewPagerActivity extends AppCompatActivity {

	public static String firstImage;
	public static String secondImage;
	public static String thirdImage;


	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
		ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
		firstImage = getIntent().getStringExtra("image1");
		secondImage = getIntent().getStringExtra("image2");
		thirdImage = getIntent().getStringExtra("image3");
		viewPager.setAdapter(new SamplePagerAdapter());
	}

	static class SamplePagerAdapter extends PagerAdapter {

		final String[] sImages = new String[]{ firstImage, secondImage, thirdImage  };

		@Override
		public int getCount() {
			return sImages.length;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			PhotoView photoView = new PhotoView(container.getContext());
//			photoView.setImageResource(sImages[position]);
			Glide
					.with(container.getContext())
					.load(sImages[position])
					.placeholder(R.mipmap.ic_record_voice_over_black)
					.error(R.drawable.ic_error)
					.crossFade(100)
					.into(photoView);

			// Now just add PhotoView to ViewPager and return it
			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
}
