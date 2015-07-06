package com.zoomlee.Zoomlee.syncservice.resttask_executors;

import android.content.Context;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.CommonResponse;
import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.net.api.ApiUrl;
import com.zoomlee.Zoomlee.net.api.FileDataApi;
import com.zoomlee.Zoomlee.net.model.File;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 29.01.15.
 */
class DeleteFileTaskExecutor implements TaskExecutorFactory.RestTaskExecutor {

    @Override
    public boolean execute(Context context, RestTask restTask) {
        if (SharedPreferenceUtils.getUtils().getPrivateKey() == null) {
            return false;
        }

        DaoHelper<File> fileDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(File.class);
        File file = fileDaoHelper.getItemByLocalId(context, restTask.getLocalItemId(), true);
        if (file == null) return true;
        if (file.getRemoteId() == -1) {
            fileDaoHelper.deleteByLocalId(context, file.getId());
            return true;
        }

        boolean result = true;
        FileDataApi api = buildFileDataApi();
        try {
            CommonResponse<Object> fileCommonResponse = api.deleteFile(SharedPreferenceUtils.getUtils().getPrivateKey(),
                    file.getRemoteId());
            //TODO check for bad file id error
            if (fileCommonResponse.getError().getCode() != 200)
                result = false;
        } catch (RetrofitError error) {
            error.printStackTrace();
            result = false;
        }

        if (result) fileDaoHelper.deleteByLocalId(context, file.getId());

        return result;
    }

    private FileDataApi buildFileDataApi() {
        return new RestAdapter.Builder()
                .setEndpoint(ApiUrl.API_URL)
                .build()
                .create(FileDataApi.class);
    }
}
