package com.zoomlee.Zoomlee.scopes.data.category;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.zoomlee.Zoomlee.dao.CategoriesDocAlertsDaoHelper;
import com.zoomlee.Zoomlee.net.model.Category;

import java.util.ArrayList;
import java.util.List;

import static com.zoomlee.Zoomlee.dao.CategoriesDocAlertsDaoHelper.CategoriesDocAlerts;

public class CategoriesByPersonTask extends AsyncTask<Integer, Void, List<Category>> {

    private final CategoriesDocAlertsDaoHelper daoCategories;
    // TODO: create only category query

    public CategoriesByPersonTask(Context context) {
        this.daoCategories = new CategoriesDocAlertsDaoHelper(context);
    }

    @Override
    public List<Category> doInBackground(@NonNull Integer... param) {
        return listCategoriesByPerson(param[0]);
    }

    public List<Category> listCategoriesByPerson(int personId) {
        List<Category> categories = new ArrayList<>();
        List<CategoriesDocAlerts> alertses = daoCategories.getCategoriesDocAlerts(personId);
        // transform category doc alerts to categories
        for (CategoriesDocAlerts alerts : alertses) {
            categories.add(alerts.getCategory());
        }
        return categories;
    }
}
