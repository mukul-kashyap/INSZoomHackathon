package com.zoomlee.Zoomlee.scopes.opendocument;

import com.zoomlee.Zoomlee.scopes.ActivityScope;
import com.zoomlee.Zoomlee.ui.view.OpenDocView;
import com.zoomlee.Zoomlee.ui.view.selectcategory.SelectCategoryView;

import dagger.Module;
import dagger.Provides;

@Module
public class OpenDocumentModule {

    private final OpenDocView.Presenter presenter;

    public OpenDocumentModule(OpenDocView.Presenter presenter) {
        this.presenter = presenter;
    }

    @ActivityScope
    @Provides
    OpenDocView.Presenter providePresenter() {
        return presenter;
    }
}
