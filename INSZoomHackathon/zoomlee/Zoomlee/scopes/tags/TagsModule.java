package com.zoomlee.Zoomlee.scopes.tags;

import com.zoomlee.Zoomlee.scopes.ActivityScope;
import com.zoomlee.Zoomlee.ui.view.tags.TagsView;

import dagger.Module;
import dagger.Provides;

@Module
public class TagsModule {

    private final TagsView.Presenter presenter;

    public TagsModule(TagsView.Presenter presenter) {
        this.presenter = presenter;
    }

    @ActivityScope
    @Provides
    TagsView.Presenter providePresenter() {
        return presenter;
    }
}
