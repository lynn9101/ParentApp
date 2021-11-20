package com.example.parentapp.models;

import java.util.ArrayList;

/**
 * The ChildrenManager class is a singleton used throughout the app
 * The class manages the retrieval, addition, modification & deletion of child object
 */
public class ChildrenManager {
    private static ChildrenManager childrenManager;
    private ArrayList<Child> children;
    private ArrayList<Child> spinnerChildren;

    public static ChildrenManager getInstance() {
        if (childrenManager == null) {
            childrenManager = new ChildrenManager();
        }

        return childrenManager;
    }

    private ChildrenManager() {
        this.children = new ArrayList<>();
        this.spinnerChildren = new ArrayList<>();
    }

    public ArrayList<Child> getChildren() {
        return this.children;
    }
    public ArrayList<Child> getSpinnerChildren() {
        return this.spinnerChildren;
    }

    public void setChildren(ArrayList<Child> children) {
        this.children = children;
    }

    public void setSpinnerChildren(ArrayList<Child> spinnerChildren) {
        this.spinnerChildren = spinnerChildren;
    }

    public int getSpinnerChildByID(ArrayList<Child> spinnerChildren1, int childID) {
        for (Child child : spinnerChildren1) {
            if (child.getUniqueID() == childID) {
                return spinnerChildren1.indexOf(child);
            }
        }
        return -1;
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

    public void addChild(Child child) {
        this.children.add(child);
    }

    public void addSpinnerChild(Child child) { this.spinnerChildren.add(child);}

    public Child updateChild(int index, Child child) {
        this.children.set(index, child);
        return child;
    }

    public Child updateSpinnerChild(int index, Child child) {
        this.spinnerChildren.set(index, child);
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
