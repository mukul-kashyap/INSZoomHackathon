package com.zoomlee.Zoomlee.syncservice.resttask_executors;

import android.content.Context;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.CommonResponse;
import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.net.api.ApiUrl;
import com.zoomlee.Zoomlee.net.api.FileDataApi;
import com.zoomlee.Zoomlee.net.model.Document;
import com.zoomlee.Zoomlee.net.model.File;
import com.zoomlee.Zoomlee.net.model.FilesType;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 29.01.15.
 */
class PostFileTaskExecutor implements TaskExecutorFactory.RestTaskExecutor {

    @Override
    public boolean execute(Context context, RestTask restTask) {
        if (SharedPreferenceUtils.getUtils().getPrivateKey() == null) {
            return false;
        }

        boolean result = true;
        DaoHelper<File> fileDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(File.class);
        File file = fileDaoHelper.getItemByLocalId(context, restTask.getLocalItemId(), true);

        if (file == null || file.getStatus() == File.STATUS_DELETED) return true;
        DaoHelper<Document> documentDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Document.class);
        Document document = documentDaoHelper.getItemByLocalId(context, file.getLocalDocumentId(), true);
        if (document == null || document.getStatus() == Document.STATUS_DELETED) return true;
        if (document.getRemoteId() == -1) return  false;

        FileDataApi api = buildFileDataApi();
        try {
            File.Type fileType = file.getType();
            String mimeType = fileType == null
                    ? (file.getTypeId() == FilesType.IMAGE_TYPE ? "image/png" : "application/pdf")
                    : fileType.mimeType;
            TypedFile fileToSend = new TypedFile(mimeType, new java.io.File(file.getLocalPath()));

            CommonResponse<File> fileCommonResponse = api.postFile(SharedPreferenceUtils.getUtils().getPrivateKey(),
                    document.getRemoteId(), file.getTypeId(), fileToSend);
            if (fileCommonResponse.getError().getCode() == 200 && fileCommonResponse.getBody() != null) {
                File newFile = fileCommonResponse.getBody();
                newFile.setLocalPath(file.getLocalPath());
                newFile.setId(file.getId());
                newFile.setLocalDocumentId(file.getLocalDocumentId());
                fileDaoHelper.saveRemoteChanges(context, newFile);
            } else {
                result = false;
            }
        } catch (RetrofitError error) {
            error.printStackTrace();
            result = false;
        }

        return result;
    }

    private FileDataApi buildFileDataApi() {
        return new RestAdapter.Builder()
                .setEndpoint(ApiUrl.API_URL)
                .build()
                .create(FileDataApi.class);
    }
}
