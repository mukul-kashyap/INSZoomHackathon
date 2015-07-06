package com.zoomlee.Zoomlee;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.android.gms.maps.MapView;
import com.zoomlee.Zoomlee.dao.DaoHelper;
import com.zoomlee.Zoomlee.dao.DaoHelpersContainer;
import com.zoomlee.Zoomlee.net.RestTask;
import com.zoomlee.Zoomlee.net.model.Country;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.scopes.app.AppComponent;
import com.zoomlee.Zoomlee.scopes.app.AppModule;
import com.zoomlee.Zoomlee.scopes.app.DaggerAppComponent;
import com.zoomlee.Zoomlee.syncservice.RestTaskPoster;
import com.zoomlee.Zoomlee.syncservice.SyncUtils;
import com.zoomlee.Zoomlee.utils.ApplicationUpgradeUtil;
import com.zoomlee.Zoomlee.utils.GAUtil;
import com.zoomlee.Zoomlee.utils.PicassoUtil;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

/**
 * Created by  Evgen Marinin <ievgen.marinin@alterplay.com> on 23.01.15.
 */
public class ZoomleeApp extends Application {
    private DaoHelper<Country> countryDaoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Country.class);
    private Person person = null;
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferenceUtils.init(getApplicationContext());
        ApplicationUpgradeUtil.getInstance(getApplicationContext()).upgradeApp();
        PicassoUtil.init(this);
        GAUtil.init(this);
        SyncUtils.init(getApplicationContext());
        RestTaskPoster.postTask(this, new RestTask(RestTask.Types.STATIC_DATA_GET), false);
        RestTaskPoster.postTask(this, new RestTask(RestTask.Types.USER_DATA_GET), false);
        RestTaskPoster.postTask(this, new RestTask(RestTask.Types.USER_GET), true);

        appComponent = createAppComponent();
        initMap();

        if (SharedPreferenceUtils.getUtils().isPinSetuped()) {
            LocationService.startLocationTracking(this);
        }
    }

    private void initMap() {
        // Fixing Later Map loading Delay
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    private AppComponent createAppComponent() {
        return DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public static ZoomleeApp get(Context context) {
        return (ZoomleeApp) context.getApplicationContext();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public int getSelectedPersonId() {
        //just to avoid NPE
        if (person == null) person = SharedPreferenceUtils.getUtils().getUserSettings();
        return person.getId();
    }

    public void setSelectedPersonId(int selectedPersonId) {
        if (selectedPersonId == Person.ME_ID) {
            person = SharedPreferenceUtils.getUtils().getUserSettings();
        } else {
            DaoHelper<Person> daoHelper = DaoHelpersContainer.getInstance().getDaoHelper(Person.class);
            person = daoHelper.getItemByLocalId(this, selectedPersonId);
        }

        if (person == null) person = SharedPreferenceUtils.getUtils().getUserSettings();
    }

    public void setSelectedPerson(Person person) {
        this.person = person;
    }

    public Person getSelectedPerson() {
        return person;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
