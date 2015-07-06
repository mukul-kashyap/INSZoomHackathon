package com.zoomlee.Zoomlee.syncservice.resttask_executors;

import android.content.Context;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.CommonResponse;
import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.net.api.ApiUrl;
import com.zoomlee.Zoomlee.net.api.PersonDataApi;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.utils.DeveloperUtil;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import java.io.File;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.mime.TypedFile;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 29.01.15.
 */
class PostPersonTaskExecutor implements TaskExecutorFactory.RestTaskExecutor {

    @Override
    public boolean execute(Context context, RestTask restTask) {
        if (SharedPreferenceUtils.getUtils().getPrivateKey() == null) {
            return false;
        }

        DeveloperUtil.michaelLog("PostPersonTaskExecutor");

        boolean result = true;
        DaoHelper<Person> personDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Person.class);
        Person person = personDaoHelper.getItemByLocalId(context, restTask.getLocalItemId(), true);

        if (person == null) return true;
        PersonDataApi api = buildPersonDataApi();
        try {
            TypedFile fileToSend = null;
            if (person.getImageLocalPath() != null)
                fileToSend = new TypedFile("image/jpg", new File(person.getImageLocalPath()));

            CommonResponse<Person> userCommonResponse = api.postPerson(SharedPreferenceUtils.getUtils().getPrivateKey(),
                    person.getName(), fileToSend);
            if (userCommonResponse.getError().getCode() == 200 && userCommonResponse.getBody() != null) {
                Person newPerson = userCommonResponse.getBody();
                newPerson.setImageLocalPath(person.getImageLocalPath());
                newPerson.setId(person.getId());
                personDaoHelper.saveRemoteChanges(context, newPerson);
            } else {
                DeveloperUtil.michaelLog("PostPersonTaskExecutor error code: " + userCommonResponse.getError().getCode());
                result = false;
            }
        } catch (RetrofitError error) {
            error.printStackTrace();
            result = false;
        }

        return result;
    }

    private PersonDataApi buildPersonDataApi() {
        return new RestAdapter.Builder()
                .setEndpoint(ApiUrl.API_URL)
                .build()
                .create(PersonDataApi.class);
    }
}
