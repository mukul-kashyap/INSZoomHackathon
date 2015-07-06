package com.zoomlee.Zoomlee.ui.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zoomlee.Zoomlee.R;
import com.zoomlee.Zoomlee.ZoomleeApp;
import com.zoomlee.Zoomlee.utils.Events;
import com.zoomlee.Zoomlee.dao.TagsDocDaoHelper;
import com.zoomlee.Zoomlee.scopes.tags.DaggerTagsComponent;
import com.zoomlee.Zoomlee.scopes.tags.TagsComponent;
import com.zoomlee.Zoomlee.scopes.tags.TagsModule;
import com.zoomlee.Zoomlee.ui.activity.CreateEditDocActivity;
import com.zoomlee.Zoomlee.ui.activity.DocumentsActivity;
import com.zoomlee.Zoomlee.ui.view.tags.TagsView;
import com.zoomlee.Zoomlee.utils.GAEvents;
import com.zoomlee.Zoomlee.utils.GAUtil;

import java.util.List;

import de.greenrobot.event.EventBus;

import static com.zoomlee.Zoomlee.ui.fragments.NavigationDrawerFragment.PersonChangedMessage;

/**
 * Author vbevans94.
 */
public class TagsFragment extends Fragment implements TagsView.Presenter {

    private LoadDataAsyncTask loadTask;
    private TagsView tagsView;
    private TagsDocDaoHelper tagsDao;

    public static TagsFragment newInstance() {
        return new TagsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tagsDao = new TagsDocDaoHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tags, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tagsView = (TagsView) view;
        createComponent().injectView(tagsView);
    }

    private TagsComponent createComponent() {
        return DaggerTagsComponent.builder().tagsModule(new TagsModule(this)).build();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        loadData();
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
        cancelTask();
    }

    @Override
    public void onStart() {
        super.onStart();
        GAUtil.getUtil().timeSpent(GAEvents.ACTION_TAGS_LIST);
    }

    @Override
    public void createDocument(){
        CreateEditDocActivity.startActivity(getActivity());
    }

    @Override
    public void selectTag(TagsDocDaoHelper.TagsDocAlerts tag) {
        if(tag.getId() == -1)
            return;
        ZoomleeApp zoomleeApp = (ZoomleeApp) getActivity().getApplication();
        DocumentsActivity.startActivityWithTag(getActivity(), tag.getId(), zoomleeApp.getSelectedPersonId());
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(PersonChangedMessage personChangedMessage) {
        loadData();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Events.DocumentChanged event) {
        loadData();
    }

    @SuppressWarnings("unused")
    public void onEventMainThread(Events.TagChanged event) {
        loadData();
    }

    private void loadData() {
        cancelTask();
        loadTask = new LoadDataAsyncTask();
        loadTask.execute();
    }

    private void cancelTask() {
        if (loadTask != null) {
            loadTask.cancel(true);
            loadTask = null;
        }
    }

    private class LoadDataAsyncTask extends AsyncTask<Void, Void, List<TagsDocDaoHelper.TagsDocAlerts>> {

        @Override
        protected List<TagsDocDaoHelper.TagsDocAlerts> doInBackground(Void... params) {
            ZoomleeApp zoomleeApp = (ZoomleeApp) getActivity().getApplication();
            return tagsDao.getTagsDocAlerts(zoomleeApp.getSelectedPersonId());
        }

        @Override
        protected void onPostExecute(List<TagsDocDaoHelper.TagsDocAlerts> tags) {
            tagsView.setTags(tags);
        }
    }
}
