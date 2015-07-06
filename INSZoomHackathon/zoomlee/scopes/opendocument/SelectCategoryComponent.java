package com.zoomlee.Zoomlee.scopes.opendocument;

import com.zoomlee.Zoomlee.scopes.ActivityScope;
import com.zoomlee.Zoomlee.ui.view.selectcategory.SelectCategoryView;

import dagger.Component;

@ActivityScope
@Component(modules = SelectCategoryModule.class)
public interface SelectCategoryComponent {

    void injectView(SelectCategoryView view);
}
