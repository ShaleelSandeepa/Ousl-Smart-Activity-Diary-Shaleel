package com.ouslsmartactivitydiary.item;

public class AppearanceItem {

    public static final int APPEARANCE = 1;
    public static final int COLOR = 2;

    int appearanceIcon, color, state, colorID;
    String appearanceName;

    private final int viewType;

    public AppearanceItem(int viewType, int appearanceIcon, String appearanceName) {
        this.viewType = viewType;
        this.appearanceIcon = appearanceIcon;
        this.appearanceName = appearanceName;
    }

    public AppearanceItem(int viewType, int state, int colorID, int color) {
        this.viewType = viewType;
        this.state = state;
        this.colorID = colorID;
        this.color = color;
    }

    public int getAppearanceIcon() {
        return appearanceIcon;
    }

    public void setAppearanceIcon(int appearanceIcon) {
        this.appearanceIcon = appearanceIcon;
    }

    public String getAppearanceName() {
        return appearanceName;
    }

    public void setAppearanceName(String appearanceName) {
        this.appearanceName = appearanceName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getViewType() {
        return viewType;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getColorID() {
        return colorID;
    }

    public void setColorID(int colorID) {
        this.colorID = colorID;
    }
}
