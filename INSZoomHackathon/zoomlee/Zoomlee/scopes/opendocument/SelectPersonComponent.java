package com.zoomlee.Zoomlee.scopes.opendocument;

import com.zoomlee.Zoomlee.scopes.ActivityScope;
import com.zoomlee.Zoomlee.ui.view.selectperson.SelectPersonView;

import dagger.Component;

@ActivityScope
@Component(modules = SelectPersonModule.class)
public interface SelectPersonComponent {

    void injectView(SelectPersonView view);
}
