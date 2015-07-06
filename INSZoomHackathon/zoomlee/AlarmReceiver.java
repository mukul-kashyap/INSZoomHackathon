package com.zoomlee.Zoomlee;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.model.Document;
import com.zoomlee.Zoomlee.ui.activity.DocumentDetailsActivity;
import com.zoomlee.Zoomlee.utils.DeveloperUtil;
import com.zoomlee.Zoomlee.utils.NotificationsUtil;

/**
 * Created by
 *
 * @author Michael Oknov <misha.oknov@alterplay.com>
 * @since 3/12/15
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static final String ACTION = "com.zoomlee.Zoomlee.android.receivers.NOTIFICATION_ALARM";
    public static final String EXTRA_MESSAGE = "zoomlee_extra_notification_message";
    public static final String EXTRA_TITLE = "zoomlee_extra_notification_title";
    public static final String EXTRA_DOC_ID = "zoomlee_extra_document";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!ACTION.equals(intent.getAction()))
            return;

        DeveloperUtil.michaelLog();

        DeveloperUtil.michaelLog(intent.getExtras().containsKey(EXTRA_MESSAGE));
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        DeveloperUtil.michaelLog("message = " + message);
        int docId = intent.getIntExtra(EXTRA_DOC_ID, -1);
        DeveloperUtil.michaelLog(docId);
        DaoHelper<Document> daoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Document.class);
        Document document = daoHelper.getItemByLocalId(context, docId);

        String title = document.getName();

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, DocumentDetailsActivity.getIntentForStart(context, document.getId(), true, document.getLocalPersonId(), true), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationsUtil.showNotification(context, pendingIntent, title, message);
    }
}
