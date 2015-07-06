package com.zoomlee.Zoomlee.ui.view.invite;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.utils.Events;
import com.zoomlee.Zoomlee.invites.Contact;
import com.zoomlee.Zoomlee.utils.CircleTransform;
import com.zoomlee.Zoomlee.utils.PicassoUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Author vbevans94.
 */
public class InviteItemView extends RelativeLayout {

    @InjectView(R.id.text_name)
    TextView textName;

    @InjectView(R.id.text_details)
    TextView textDetails;

    @InjectView(R.id.text_status)
    TextView textStatus;

    @InjectView(R.id.image_contact)
    ImageView imageContact;

    private Contact contact;

    public InviteItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);
    }

    public void bind(Contact contact) {
        this.contact = contact;

        textName.setText(contact.getName());
        textDetails.setText(contact.getChannelsString());
        textStatus.setText(contact.getStatus().titleResId);
        textStatus.setEnabled(contact.getStatus().inviteEnabled);

        PicassoUtil.getInstance().load(contact.getDisplayPhotoUri())
                .transform(new CircleTransform())
                .placeholder(R.drawable.stub_person_green)
                .into(imageContact);
    }

    @OnClick(R.id.text_status)
    @SuppressWarnings("unused")
    void onInviteClicked() {
        EventBus.getDefault().post(new Events.InviteClicked(contact));
    }
}
