package com.zoomlee.Zoomlee.scopes.tags;

import com.zoomlee.Zoomlee.scopes.ActivityScope;
import com.zoomlee.Zoomlee.ui.view.tags.TagsView;

import dagger.Component;

@ActivityScope
@Component(modules = TagsModule.class)
public interface TagsComponent {

    void injectView(TagsView view);
}
