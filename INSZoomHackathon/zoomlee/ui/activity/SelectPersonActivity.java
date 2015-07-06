package com.zoomlee.Zoomlee.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.net.model.File;
import com.zoomlee.Zoomlee.net.model.Person;
import com.zoomlee.Zoomlee.scopes.data.person.AllPersonsTask;
import com.zoomlee.Zoomlee.scopes.opendocument.DaggerSelectPersonComponent;
import com.zoomlee.Zoomlee.scopes.opendocument.SelectPersonComponent;
import com.zoomlee.Zoomlee.scopes.opendocument.SelectPersonModule;
import com.zoomlee.Zoomlee.ui.view.selectperson.SelectPersonView;
import com.zoomlee.Zoomlee.utils.ActivityUtils;
import com.zoomlee.Zoomlee.utils.BillingUtils;
import com.zoomlee.Zoomlee.utils.Events;
import com.zoomlee.Zoomlee.utils.RequestCodes;
import com.zoomlee.Zoomlee.utils.SharedPreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class SelectPersonActivity extends UnsecuredActivity implements SelectPersonView.Presenter {

    @InjectView(R.id.select_person_view)
    SelectPersonView selectPersonView;

    private GetTask task;

    /**
     * Starts adding attachment to document from person selection activity.
     *
     * @param context  to start from
     * @param path     of the attachment
     * @param fileType of the attachment
     */
    public static void startActivity(Context context, String path, File.Type fileType) {
        Intent intent = new Intent(context, SelectPersonActivity.class);
        intent.putExtra(CreateEditDocActivity.EXTRA_ATTACHMENT_PATH, path);
        intent.putExtra(CreateEditDocActivity.EXTRA_ATTACHMENT_TYPE, fileType);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_person_view);

        ButterKnife.inject(this);

        // create object pool and satisfy our dependencies from it
        createComponent().injectView(selectPersonView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onBackPressed() {
        shutdown();
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.SELECT_CATEGORY) {
            switch (resultCode) {
                case RESULT_OK:
                case SecuredActionBarActivity.RESULT_PIN_FAILED:
                    // we mustn't allow user see anything without pin or in case he finished adding steps
                    shutdown();
                    break;

                default:
                    unpin();
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void shutdown() {
        ActivityUtils.finishAndRemoveTask(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadPersons();
        EventBus.getDefault().register(this);
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Events.PersonChanged event) {
        loadPersons();
    }

    private void loadPersons() {
        if (task == null) {
            if (BillingUtils.isFamilyContent(SharedPreferenceUtils.getUtils().getUserSettings())){
                task = new GetTask(this);
                task.execute();
            } else {
                List<Person> persons = new ArrayList<>();
                persons.add(SharedPreferenceUtils.getUtils().getUserSettings());
                selectPersonView.setPersons(persons);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (task != null) {
            // stop thread, indicate not running
            task.cancel(true);
            task = null;
        }
    }

    private SelectPersonComponent createComponent() {
        return DaggerSelectPersonComponent.builder()
                .selectPersonModule(new SelectPersonModule(this))
                .build();
    }

    @Override
    public void selectPerson(Person person) {
        SelectCategoryActivity.startForResult(this, person.getId(), getIntent(), RequestCodes.SELECT_CATEGORY);
    }

    private class GetTask extends AllPersonsTask {

        public GetTask(Context context) {
            super(context);
        }

        @Override
        protected void onPostExecute(@NonNull List<Person> persons) {
            selectPersonView.setPersons(persons);

            task = null;
        }
    }
}
