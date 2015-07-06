package com.zoomlee.Zoomlee.scopes.opendocument;

import com.zoomlee.Zoomlee.scopes.ActivityScope;
import com.zoomlee.Zoomlee.ui.view.OpenDocView;

import dagger.Component;

@ActivityScope
@Component(modules = OpenDocumentModule.class)
public interface OpenDocumentComponent {

    void injectView(OpenDocView view);
}
