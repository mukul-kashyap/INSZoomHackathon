package com.zoomlee.Zoomlee.scopes.data.person;

import android.content.Context;
import android.os.AsyncTask;

import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class AllPersonsTask extends AsyncTask<Void, Void, List<Person>> {

    private final DaoHelper<Person> daoPersons = DaoHelpersContainer.getInstance().getDaoHelper(Person.class);
    private final Context context;

    public AllPersonsTask(Context context) {
        this.context = context;
    }

    @Override
    protected List<Person> doInBackground(Void... params) {
        return listPersons();
    }

    public List<Person> listPersons() {
        List<Person> allPersons = new ArrayList<>();
        allPersons.add(SharedPreferenceUtils.getUtils().getUserSettings());
        allPersons.addAll(daoPersons.getAllItems(context));
        return allPersons;
    }
}
