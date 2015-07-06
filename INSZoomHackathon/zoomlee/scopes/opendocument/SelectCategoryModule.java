package com.zoomlee.Zoomlee.scopes.opendocument;

import com.zoomlee.Zoomlee.scopes.ActivityScope;
import com.zoomlee.Zoomlee.ui.view.selectcategory.SelectCategoryView;

import dagger.Module;
import dagger.Provides;

@Module
public class SelectCategoryModule {

    private final SelectCategoryView.Presenter presenter;

    public SelectCategoryModule(SelectCategoryView.Presenter presenter) {
        this.presenter = presenter;
    }

    @ActivityScope
    @Provides
    SelectCategoryView.Presenter providePresenter() {
        return presenter;
    }
}
