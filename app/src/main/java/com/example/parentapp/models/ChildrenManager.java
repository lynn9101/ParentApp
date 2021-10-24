package com.example.parentapp.models;

import java.util.ArrayList;

public class ChildrenManager {
    private static ChildrenManager childrenManager;
    private ArrayList<Child> children;

    public static ChildrenManager getInstance() {
        if (childrenManager == null) {
            childrenManager = new ChildrenManager();
        }

        return childrenManager;
    }

    private ChildrenManager() {
        this.children = new ArrayList<>();
    }

    public ArrayList<Child> getChildren() {
        return this.children;
    }

    public Child getChild(int childId) {
        if (childId >= this.children.size()) {
            throw new IllegalArgumentException("Child ID: " + childId + " exceeds children count.");
        }

        if (this.children.get(childId) == null) {
            throw new IllegalArgumentException("Child ID: " + childId + " not found.");
        }

        return this.children.get(childId);
    }

    public Child addChild(Child child) {
        this.children.add(child);
        return child;
    }

    public Child updateChild(int index, Child child) {
        this.children.set(index, child);
        return child;
    }

    public void removeChild(int childId) {
        if (childId >= this.children.size()) {
            throw new IllegalArgumentException("Child ID: " + childId + " exceeds children count.");
        }

        if (this.children.get(childId) == null) {
            throw new IllegalArgumentException("Child ID: " + childId + " not found.");
        }

        this.children.remove(childId);
    }
}
