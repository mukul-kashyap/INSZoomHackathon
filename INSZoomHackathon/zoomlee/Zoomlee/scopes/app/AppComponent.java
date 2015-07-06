package com.zoomlee.Zoomlee.scopes.app;

import android.app.Application;

import com.zoomlee.Zoomlee.net.api.AuthApi;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    Application application();
}
