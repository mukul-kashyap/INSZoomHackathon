package com.zoomlee.Zoomlee.syncservice.resttask_executors;

import android.content.Context;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.utils.DeveloperUtil;

import static com.zoomlee.Zoomlee.syncservice.resttask_executors.TaskExecutorFactory.RestTaskExecutor;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 23.02.15.
 */
class UploadPersonTaskExecutor implements RestTaskExecutor {

    @Override
    public boolean execute(Context context, RestTask restTask) {
        RestTaskExecutor taskExecutor;
        DaoHelper<Person> personDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Person.class);
        Person person = personDaoHelper.getItemByLocalId(context, restTask.getLocalItemId(), true);
        if (person == null) return true;
        if (person.getRemoteId() != -1)
            taskExecutor = new PutPersonTaskExecutor();
        else
            taskExecutor = new PostPersonTaskExecutor();

        return taskExecutor.execute(context, restTask);
    }
}
