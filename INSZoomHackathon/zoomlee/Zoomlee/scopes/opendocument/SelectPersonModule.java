package com.zoomlee.Zoomlee.scopes.opendocument;

import com.zoomlee.Zoomlee.scopes.ActivityScope;
import com.zoomlee.Zoomlee.ui.view.selectperson.SelectPersonView;

import dagger.Module;
import dagger.Provides;

@Module
public class SelectPersonModule {

    private final SelectPersonView.Presenter presenter;

    public SelectPersonModule(SelectPersonView.Presenter presenter) {
        this.presenter = presenter;
    }

    @ActivityScope
    @Provides
    SelectPersonView.Presenter providePresenter() {
        return presenter;
    }
}
