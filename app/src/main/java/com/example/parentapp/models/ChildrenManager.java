package com.example.parentapp.models;

public class ChildrenManager {
    private static ChildrenManager childrenManager;

    public static ChildrenManager getInstance() {
        if (childrenManager == null) {
            childrenManager = new ChildrenManager();
        }

        return childrenManager;
    }

    private ChildrenManager() {

    }
}
