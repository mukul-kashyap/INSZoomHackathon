package com.zoomlee.Zoomlee.syncservice.resttask_executors;

import android.content.Context;
import android.text.TextUtils;

import com.zoomlee.Zoomlee.net.CommonResponse;
import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.net.api.ApiUrl;
import com.zoomlee.Zoomlee.net.api.UserDataApi;
import com.zoomlee.Zoomlee.net.model.User;
import com.zoomlee.Zoomlee.syncservice.RestTaskPoster;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 29.01.15.
 */
class GetUserTaskExecutor implements TaskExecutorFactory.RestTaskExecutor {

    @Override
    public boolean execute(Context context, RestTask restTask) {
        if (SharedPreferenceUtils.getUtils().getPrivateKey() == null) {
            return false;
        }

        //TODO relogin
        boolean result = true;
        UserDataApi api = buildUserDataApi();
        try {
            User user = SharedPreferenceUtils.getUtils().getUserSettings();
            CommonResponse<User> userCommonResponse = api.getUser(user.getPrivateKey(), user.getRemoteId());
            if (userCommonResponse.getError().getCode() == 200 && userCommonResponse.getBody() != null) {
                User newUser = userCommonResponse.getBody();
                if (TextUtils.isEmpty(newUser.getImageRemotePath()) || !newUser.getImageRemotePath().equals(user.getImageRemotePath())) {
                    RestTaskPoster.postTask(context, new RestTask(RestTask.Types.USER_GET_ICON), true);
                }

                newUser.setImageLocalPath(user.getImageLocalPath());

                SharedPreferenceUtils.getUtils().saveUserSettings(newUser);
            } else {
                result = false;
            }
        } catch (RetrofitError error) {
            error.printStackTrace();
            result = false;
        }

        return result;
    }

    private UserDataApi buildUserDataApi() {
        return new RestAdapter.Builder()
                .setEndpoint(ApiUrl.API_URL)
                .build()
                .create(UserDataApi.class);
    }
}
