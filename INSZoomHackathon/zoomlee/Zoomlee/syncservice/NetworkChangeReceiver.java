package com.zoomlee.Zoomlee.syncservice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zoomlee.Zoomlee.utils.DeveloperUtil;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DeveloperUtil.michaelLog("NetworkChangeReceiver.onReceive");
        if (SharedPreferenceUtils.getUtils().getBooleanSettings(SharedPreferenceUtils.REQUIRE_SYNC)
                && SyncUtils.isNetworkActive(context))
            DeveloperUtil.michaelLog("NetworkChangeReceiver triggerSync");
            SyncUtils.triggerSync();
    }
}

