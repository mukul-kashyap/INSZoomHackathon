package com.zoomlee.Zoomlee.ui.view.selectcategory;

import android.content.Context;
import android.util.AttributeSet;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.net.model.Category;
import com.zoomlee.Zoomlee.ui.view.SelectItemView;

public class SelectCategoryItemView extends SelectItemView {

    private static int sPadding;

    public SelectCategoryItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (sPadding == 0) {
            sPadding = (int) getResources().getDimension(R.dimen.select_category_image_padding);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        imageItem.setPadding(sPadding, sPadding, sPadding, sPadding);
    }

    public void bind(Category category) {
        textItemName.setText(category.getName().toUpperCase());
        imageItem.setImageResource(Category.getIconRes(category.getRemoteId()));
    }
}