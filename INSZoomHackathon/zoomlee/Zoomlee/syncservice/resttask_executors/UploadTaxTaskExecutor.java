package com.zoomlee.Zoomlee.syncservice.resttask_executors;

import android.content.Context;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.net.model.Tax;

import static com.zoomlee.Zoomlee.syncservice.resttask_executors.TaskExecutorFactory.RestTaskExecutor;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 05.05.15.
 */
class UploadTaxTaskExecutor implements RestTaskExecutor {

    @Override
    public boolean execute(Context context, RestTask restTask) {
        RestTaskExecutor taskExecutor;
        DaoHelper<Tax> taxDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Tax.class);
        Tax tax = taxDaoHelper.getItemByLocalId(context, restTask.getLocalItemId(), true);
        if (tax == null) return true;
        if (tax.getRemoteId() != -1)
            taskExecutor = new PutTaxTaskExecutor();
        else
            taskExecutor = new PostTaxTaskExecutor();

        return taskExecutor.execute(context, restTask);
    }
}
