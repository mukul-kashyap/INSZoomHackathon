package com.zoomlee.Zoomlee.ui.view.selectperson;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.ui.view.SelectItemView;
import com.zoomlee.Zoomlee.utils.UiUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SelectPersonItemView extends SelectItemView {

    public SelectPersonItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void bind(Person person) {
        textItemName.setText(person.getName().toUpperCase());
        UiUtil.loadPersonIcon(person, imageItem, false);
    }
}
