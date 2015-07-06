package com.zoomlee.Zoomlee.syncservice.resttask_executors;

import android.content.Context;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.CommonResponse;
import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.net.api.ApiUrl;
import com.zoomlee.Zoomlee.net.api.DocumentDataApi;
import com.zoomlee.Zoomlee.net.model.Document;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 29.01.15.
 */
class DeleteDocumentTaskExecutor implements TaskExecutorFactory.RestTaskExecutor {

    @Override
    public boolean execute(Context context, RestTask restTask) {
        if (SharedPreferenceUtils.getUtils().getPrivateKey() == null) {
            return false;
        }

        boolean result = true;
        DaoHelper<Document> documentDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Document.class);
        Document document = documentDaoHelper.getItemByLocalId(context, restTask.getLocalItemId(), true);
        if (document == null)
            return true;
        if (document.getRemoteId() == -1) {
            documentDaoHelper.deleteByLocalId(context, document.getId());
            return true;
        }

        DocumentDataApi api = buildDocumentDataApi();
        try {
            CommonResponse<Object> commonResponse = api.deleteDocument(SharedPreferenceUtils.getUtils().getPrivateKey(),
                    document.getRemoteId());
            if (commonResponse.getError().getCode() != 200) {
                result = false;
            }
        } catch (RetrofitError error) {
            error.printStackTrace();
            result = false;
        }

        if (result) documentDaoHelper.deleteByLocalId(context, document.getId());

        return result;
    }

    private DocumentDataApi buildDocumentDataApi() {
        return new RestAdapter.Builder()
                .setEndpoint(ApiUrl.API_URL)
                .build()
                .create(DocumentDataApi.class);
    }
}
