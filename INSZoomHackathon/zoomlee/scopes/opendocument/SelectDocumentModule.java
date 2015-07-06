package com.zoomlee.Zoomlee.scopes.opendocument;

import com.zoomlee.Zoomlee.scopes.ActivityScope;
import com.zoomlee.Zoomlee.ui.view.selectdocument.SelectDocumentView;

import dagger.Module;
import dagger.Provides;

@Module
public class SelectDocumentModule {

    private final SelectDocumentView.Presenter presenter;

    public SelectDocumentModule(SelectDocumentView.Presenter presenter) {
        this.presenter = presenter;
    }

    @ActivityScope
    @Provides
    SelectDocumentView.Presenter providePresenter() {
        return presenter;
    }
}
