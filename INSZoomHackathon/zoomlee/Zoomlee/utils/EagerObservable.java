package com.zoomlee.Zoomlee.utils;

import java.util.Observable;

public class EagerObservable extends Observable {

    @Override
    public boolean hasChanged() {
        return true;
    }
}
