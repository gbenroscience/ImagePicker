package com.itis.libs.imagepick.utils;

import android.graphics.Color;

public class Props {


    private int cropRectThickness;
    private int headerColor;
    private int bgColor;
    private int cropperBorderColor;
    private boolean showPreview;
    private boolean needsCrop;
    private boolean showGrid;

    public int getHeaderColor() {
        return headerColor;
    }

    public void setHeaderColor(int headerColor) {
        this.headerColor = headerColor;
    }
    public void setHeaderColor(String headerColor) {
        this.headerColor = Color.parseColor(headerColor);
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public void setBgColor(String bgColor) {
        this.bgColor = Color.parseColor(bgColor);
    }

    public int getCropperBorderColor() {
        return cropperBorderColor;
    }

    public void setCropperBorderColor(int cropperBorderColor) {
        this.cropperBorderColor = cropperBorderColor;
    }

    public void setCropperBorderColor(String cropperBorderColor) {
        this.cropperBorderColor = Color.parseColor(cropperBorderColor);
    }

    public void setShowPreview(boolean showPreview) {
        this.showPreview = showPreview;
    }

    public boolean isShowPreview() {
        return showPreview;
    }

    public void setNeedsCrop(boolean needsCrop) {
        this.needsCrop = needsCrop;
    }

    public boolean isNeedsCrop() {
        return needsCrop;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setCropRectThickness(int cropRectThickness) {
        this.cropRectThickness = cropRectThickness;
    }

    public int getCropRectThickness() {
        return cropRectThickness;
    }
}