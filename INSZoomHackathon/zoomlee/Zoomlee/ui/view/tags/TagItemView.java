package com.zoomlee.Zoomlee.ui.view.tags;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.dao.TagsDocDaoHelper;
import com.zoomlee.Zoomlee.utils.UiUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Author vbevans94.
 */
public class TagItemView extends RelativeLayout {

    private static int sPadding;

    @InjectView(R.id.categoryNameTv)
    TextView textName;

    @InjectView(R.id.docsTypesTv)
    TextView textTypes;

    @InjectView(R.id.layout_name)
    View layoutName;

    @InjectView(R.id.alertsTv)
    TextView textAlerts;

    public TagItemView(Context context, AttributeSet attrs) {
        super(context, attrs);

        inflate(context, R.layout.include_item_document_category, this);

        if (sPadding == 0) {
            sPadding = getResources().getDimensionPixelSize(R.dimen.horizontal_margin);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        layoutName.setPadding(sPadding, 0, 0, 0);
    }

    /**
     * Binds tag to view.
     * @param tag to be binded
     */
    public void bind(TagsDocDaoHelper.TagsDocAlerts tag) {
        textName.setText(tag.getName());
        textTypes.setText(TextUtils.join(", ", tag.getDocsTypesNames()));

        int alertCount = tag.getAlertsCount();
        if (alertCount > 0) {
            if (alertCount < 10) {
                textAlerts.setBackgroundResource(R.drawable.updates_background);
            } else {
                textAlerts.setBackgroundResource(R.drawable.updates_background_long);
            }

            textAlerts.setText(Integer.toString(alertCount));
            UiUtil.show(textAlerts);
        } else {
            UiUtil.hide(textAlerts);
        }
    }
}
