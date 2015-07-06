package com.zoomlee.Zoomlee.ui.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zoomlee.Zoomlee.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Author vbevans94.
 */
public class AppItemView extends FrameLayout {

    @InjectView(R.id.image_icon)
    ImageView imageIcon;

    @InjectView(R.id.text_label)
    TextView textLabel;

    public AppItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    public void bind(Drawable icon, CharSequence label) {
        imageIcon.setImageDrawable(icon);
        textLabel.setText(label);
    }
}
