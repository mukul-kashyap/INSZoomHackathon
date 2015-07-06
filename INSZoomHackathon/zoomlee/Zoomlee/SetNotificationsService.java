package com.zoomlee.Zoomlee;

import android.app.IntentService;
import android.content.Intent;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.model.Field;
import com.zoomlee.Zoomlee.provider.helpers.FieldsHelper;
import com.zoomlee.Zoomlee.utils.NotificationsUtil;
import com.zoomlee.Zoomlee.utils.TimeUtil;

import java.util.List;

/**
 * Created by
 *
 * @author Michael Oknov <misha.oknov@alterplay.com>
 * @since 3/19/15
 */
public class SetNotificationsService extends IntentService {
    private static final String NAME = "SetupNotificationsServices";

    public SetNotificationsService() {
        super(NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        setupNoticiations();
    }

    private void setupNoticiations() {
        DaoHelper<Field> daoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Field.class);
        String selection = FieldsHelper.FieldsContract.TABLE_NAME + "." + FieldsHelper.FieldsContract.NOTIFY_ON + " > " + TimeUtil.getServerCurrentTimestamp();
        List<Field> fields = daoHelper.getAllItems(this, selection, null, null);

        for (Field field : fields)
            NotificationsUtil.addReminder(this, field);
    }
}
