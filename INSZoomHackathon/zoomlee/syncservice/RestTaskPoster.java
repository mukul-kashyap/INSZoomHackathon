package com.zoomlee.Zoomlee.syncservice;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.provider.helpers.RestTasksHelper.RestTasksContract;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 29.01.15.
 */
public class RestTaskPoster {

    public static void postTask(Context context, RestTask restTask, boolean executeImmediately) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(RestTasksContract.LOCAL_ID, restTask.getLocalItemId());
        contentValues.put(RestTasksContract.TYPE, restTask.getType());
        contentResolver.insert(RestTasksContract.CONTENT_URI, contentValues);

        if (executeImmediately) SyncUtils.triggerSync();
    }

    public static void requestSync() {
        SyncUtils.triggerSync();
    }
}
