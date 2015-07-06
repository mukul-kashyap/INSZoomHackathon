package com.zoomlee.Zoomlee.syncservice.resttask_executors;

import android.content.Context;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.CommonResponse;
import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.net.api.ApiUrl;
import com.zoomlee.Zoomlee.net.api.TaxDataApi;
import com.zoomlee.Zoomlee.net.model.Tax;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

/**
 * Created by
 *
 * @author Evgen Marinin <ievgen.marinin@alterplay.com>
 * @date 05.05.15.
 */
class DeleteTaxTaskExecutor implements TaskExecutorFactory.RestTaskExecutor {

    @Override
    public boolean execute(Context context, RestTask restTask) {
        if (SharedPreferenceUtils.getUtils().getPrivateKey() == null) {
            return false;
        }

        boolean result = true;
        DaoHelper<Tax> taxDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Tax.class);
        Tax tax = taxDaoHelper.getItemByLocalId(context, restTask.getLocalItemId(), true);
        if (tax == null)
            return true;
        if (tax.getRemoteId() == -1) {
            taxDaoHelper.deleteByLocalId(context, tax.getId());
            return true;
        }

        TaxDataApi api = buildTaxDataApi();
        try {
            CommonResponse<Object> userCommonResponse = api.deleteTax(SharedPreferenceUtils.getUtils().getPrivateKey(),
                    tax.getRemoteId());
            if (userCommonResponse.getError().getCode() != 200) {
                result = false;
            }
        } catch (RetrofitError error) {
            error.printStackTrace();
            result = false;
        }

        if (result) taxDaoHelper.deleteByLocalId(context, tax.getId());

        return result;
    }

    private TaxDataApi buildTaxDataApi() {
        return new RestAdapter.Builder()
                .setEndpoint(ApiUrl.API_URL)
                .build()
                .create(TaxDataApi.class);
    }
}
