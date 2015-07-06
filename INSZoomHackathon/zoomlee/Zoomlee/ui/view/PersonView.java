package com.zoomlee.Zoomlee.ui.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.utils.PicassoUtil;
import com.zoomlee.Zoomlee.utils.UiUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by
 *
 * @author Michael Oknov <misha.oknov@alterplay.com>
 * @date 1/27/15
 */
public class PersonView extends RelativeLayout {

    private boolean isSelected;
    private Person person;

    @InjectView(R.id.personAvatarIv)
    ImageView avatar;

    @InjectView(R.id.personNameTv)
    ZMTextView name;

    private static final float UNSELECTED_ALPHA = 0.4f;
    private static final float SELECTED_ALPHA = 1.0f;

    public PersonView(Context context) {
        this(context, null);
    }

    public PersonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PersonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        inflate(context, R.layout.view_person, this);

        ButterKnife.inject(this);

        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PersonView, 0, 0);
        String personName = array.getString(R.styleable.PersonView_personName);
        Drawable personAvatar = array.getDrawable(R.styleable.PersonView_personAvatar);
        array.recycle();

        name.setText(personName);
        avatar.setImageDrawable(personAvatar);

        setSelected(false);
    }

    public void setAvatar(Drawable drawable) {
        avatar.setImageDrawable(drawable);
    }

    public void setAvatar(int drawableRes) {
        avatar.setImageResource(drawableRes);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setName(int nameRes) {
        this.name.setText(nameRes);
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;

        setAlpha(isSelected ? SELECTED_ALPHA : UNSELECTED_ALPHA);
        name.setChecked(isSelected);
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        setTag(person.getId());

        int personId = person.getId();
        if (personId == Person.ALL_ID)
            PicassoUtil.getInstance().load(R.drawable.person_all).into(avatar);
        else {
            UiUtil.loadPersonIcon(person, avatar, R.drawable.person_me);
        }

        name.setText(person.getName());
    }

    public int getPersonId() {
        return person.getId();
    }
}
