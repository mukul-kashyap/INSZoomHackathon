package com.zoomlee.Zoomlee.syncservice.resttask_executors;

import android.content.Context;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.CommonResponse;
import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.net.api.ApiUrl;
import com.zoomlee.Zoomlee.net.api.StaticDataApi;
import com.zoomlee.Zoomlee.net.model.CategoriesDocumentsType;
import com.zoomlee.Zoomlee.net.model.Category;
import com.zoomlee.Zoomlee.net.model.Changes;
import com.zoomlee.Zoomlee.net.model.Color;
import com.zoomlee.Zoomlee.net.model.Country;
import com.zoomlee.Zoomlee.net.model.DocumentsType;
import com.zoomlee.Zoomlee.net.model.DocumentsType2Field;
import com.zoomlee.Zoomlee.net.model.FieldsType;
import com.zoomlee.Zoomlee.net.model.FilesType;
import com.zoomlee.Zoomlee.net.model.Group;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils.LastSyncTimeKeys;

import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 29.01.15.
 */
class GetStaticDataTaskExecutor implements TaskExecutorFactory.RestTaskExecutor {

    private static Class[] STATIC_DATA_CLASSES = new Class[]{
            CategoriesDocumentsType.class, Category.class, Color.class, Country.class,
            DocumentsType.class, DocumentsType2Field.class, FieldsType.class, Group.class,
            FilesType.class
    };

    @Override
    public boolean execute(Context context, RestTask restTask) {
        StaticDataApi api = buildStaticDataApi();
        boolean success = true;
        int lastDownloadTime = SharedPreferenceUtils.getUtils().getIntSetting(LastSyncTimeKeys.STATIC_DATA);
        try {
            CommonResponse<Changes> changes = api.getChanges(lastDownloadTime);
            if (changes.getError().getCode() == 200) {
                for (Class dataClass : STATIC_DATA_CLASSES) {
                    DaoHelper daoHelper = DaoHelpersContainer.getInstance().getDaoHelper(dataClass);
                    success = success && daoHelper.downloadItems(context, api, changes.getBody());
                }
                lastDownloadTime = changes.getBody().getLastUpdate() + 1;
            } else {
                success = false;
            }
        } catch (RetrofitError error) {
            try {
                CommonResponse<List<Object>> listCommonResponse = new CommonResponse<>();
                listCommonResponse = (CommonResponse<List<Object>>) error.getBodyAs(listCommonResponse.getClass());
                if (listCommonResponse != null && listCommonResponse.getError().getCode() != 200)
                    success = false;
                else
                    error.printStackTrace();
            } catch (RetrofitError error1) {
                error1.printStackTrace();
                success = false;
            }
        }

        if (success) {
            SharedPreferenceUtils.getUtils().setIntSetting(LastSyncTimeKeys.STATIC_DATA, lastDownloadTime);
        }
        return success;
    }

    private StaticDataApi buildStaticDataApi() {
        return new RestAdapter.Builder()
                .setEndpoint(ApiUrl.API_URL)
                .build()
                .create(StaticDataApi.class);
    }
}
