package com.ashtech.sahayak_updated.HelperClass;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.PagerAdapter;

import com.ashtech.sahayak_updated.R;

public class SliderAdapterClass extends PagerAdapter {

    Context context;            // for specific activity

    LayoutInflater inflater;

    public SliderAdapterClass(Context context) {
        this.context = context;
    }

    int images[] = {
            R.drawable.add_missing_place,
            R.drawable.sit_back_and_relax,
            R.drawable.make_a_call

    };
    int headings[]={
            R.string.first_slide_title,
            R.string.second_slide_title,
            R.string.third_slide_title
    };
    int descriptions[]={
            R.string.slide_one_description,
            R.string.slide_two_description,
           R.string.slide_three_description
    };

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);  //requesting for the permission
        View view =inflater.inflate(R.layout.slides_layout,container,false);       // pointing toward the layout which hv to be used
        // HOOKS
        ImageView imageView=view.findViewById(R.id.slider_image);
        TextView heading=view.findViewById(R.id.slider_heading);
        TextView desc=view.findViewById(R.id.slider_desc);


        imageView.setImageResource(images[position]);
        heading.setText(headings[position]);
        desc.setText(descriptions[position]);

        container.addView(view);
        return view;




    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ConstraintLayout)object);
    }

    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(ConstraintLayout)object;
    }
}
