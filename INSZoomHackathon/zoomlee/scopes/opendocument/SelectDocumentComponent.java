package com.zoomlee.Zoomlee.scopes.opendocument;

import com.zoomlee.Zoomlee.scopes.ActivityScope;
import com.zoomlee.Zoomlee.ui.view.selectdocument.SelectDocumentView;

import dagger.Component;

@ActivityScope
@Component(modules = SelectDocumentModule.class)
public interface SelectDocumentComponent {

    void injectView(SelectDocumentView view);
}
