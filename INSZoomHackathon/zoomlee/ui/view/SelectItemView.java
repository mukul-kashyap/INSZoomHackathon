package com.zoomlee.Zoomlee.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zoomlee.Zoomlee.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SelectItemView extends FrameLayout {

    @InjectView(R.id.image_item)
    protected ImageView imageItem;

    @InjectView(R.id.text_item_name)
    protected TextView textItemName;

    public SelectItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.include_item_select, this);

        setClipToPadding(false);
        setClipChildren(false);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (isInEditMode()) {
            return;
        }

        ButterKnife.inject(this);
    }
}
