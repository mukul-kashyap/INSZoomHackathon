package com.zoomlee.Zoomlee.syncservice.resttask_executors;

import android.content.Context;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.net.model.Document;
import com.zoomlee.Zoomlee.net.model.Form;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.net.model.Tag;
import com.zoomlee.Zoomlee.net.model.Tax;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import retrofit.RetrofitError;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 29.01.15.
 */
class GetUserDataTaskExecutor implements TaskExecutorFactory.RestTaskExecutor {

    private static Class[] USER_DATA_CLASSES = new Class[]{
            Person.class, Document.class, Tag.class, Tax.class, Form.class
    };

    @Override
    public boolean execute(Context context, RestTask restTask) {
        if (SharedPreferenceUtils.getUtils().getPrivateKey() == null) {
            return false;
        }

        boolean result = true;
        DaoHelper daoHelper;
        try {
            for (Class itemClass: USER_DATA_CLASSES) {
                daoHelper = DaoHelpersContainer.getInstance().getDaoHelper(itemClass);
                result = daoHelper.downloadItems(context, null, null);
                if (!result) break;
            }
        } catch (RetrofitError error) {
            error.printStackTrace();
            result = false;
        }

        return result;
    }
}
