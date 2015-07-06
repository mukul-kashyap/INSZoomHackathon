package com.zoomlee.Zoomlee.incitations;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.ui.activity.CountriesActivity;
import com.zoomlee.Zoomlee.ui.activity.InviteActivity;
import com.zoomlee.Zoomlee.ui.activity.SubscriptionActivity;

/**
 * Author vbevans94.
 */
public enum Incitation {

    SELECT_HOME_COUNTRY(R.string.incitation_select_home_country, CountriesActivity.class),
    INVITE_YOUR_FRIENDS(R.string.incitation_invite_your_friends, InviteActivity.class),
    SUBSCRIBE_TO_ZOOMLEE(R.string.incitation_subscribe_to_zoomlee, SubscriptionActivity.class);

    public final int textResId;
    public Class activityClass;

    Incitation(int textResId, Class activityClass) {
        this.textResId = textResId;
        this.activityClass = activityClass;
    }
}
