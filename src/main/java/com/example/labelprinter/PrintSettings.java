package com.example.labelprinter;

public class PrintSettings {
    private double pageWidthMm = LabelSheetConfig.DEFAULT_PAGE_WIDTH_MM;
    private double pageHeightMm = LabelSheetConfig.DEFAULT_PAGE_HEIGHT_MM;
    private double labelWidthMm = LabelSheetConfig.DEFAULT_LABEL_WIDTH_MM;
    private double labelHeightMm = LabelSheetConfig.DEFAULT_LABEL_HEIGHT_MM;
    private double gapXmm = LabelSheetConfig.DEFAULT_GAP_X_MM;
    private double gapYmm = LabelSheetConfig.DEFAULT_GAP_Y_MM;
    private double paddingTopMm = LabelSheetConfig.DEFAULT_PADDING_TOP_MM;
    private double paddingLeftMm = LabelSheetConfig.DEFAULT_PADDING_LEFT_MM;
    private double marginTopMm = LabelSheetConfig.DEFAULT_MARGIN_TOP_MM;
    private double marginLeftMm = LabelSheetConfig.DEFAULT_MARGIN_LEFT_MM;
    private double fontSize = 12;

    private String theme = "corporate";

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public double getPageWidthMm() {
        return pageWidthMm;
    }

    public void setPageWidthMm(double pageWidthMm) {
        this.pageWidthMm = pageWidthMm;
    }

    public double getPageHeightMm() {
        return pageHeightMm;
    }

    public void setPageHeightMm(double pageHeightMm) {
        this.pageHeightMm = pageHeightMm;
    }

    public double getLabelWidthMm() {
        return labelWidthMm;
    }

    public void setLabelWidthMm(double labelWidthMm) {
        this.labelWidthMm = labelWidthMm;
    }

    public double getLabelHeightMm() {
        return labelHeightMm;
    }

    public void setLabelHeightMm(double labelHeightMm) {
        this.labelHeightMm = labelHeightMm;
    }

    public double getGapXmm() {
        return gapXmm;
    }

    public void setGapXmm(double gapXmm) {
        this.gapXmm = gapXmm;
    }

    public double getGapYmm() {
        return gapYmm;
    }

    public void setGapYmm(double gapYmm) {
        this.gapYmm = gapYmm;
    }

    public double getPaddingTopMm() {
        return paddingTopMm;
    }

    public void setPaddingTopMm(double paddingTopMm) {
        this.paddingTopMm = paddingTopMm;
    }

    public double getPaddingLeftMm() {
        return paddingLeftMm;
    }

    public void setPaddingLeftMm(double paddingLeftMm) {
        this.paddingLeftMm = paddingLeftMm;
    }

    public double getMarginTopMm() {
        return marginTopMm;
    }

    public void setMarginTopMm(double marginTopMm) {
        this.marginTopMm = marginTopMm;
    }

    public double getMarginLeftMm() {
        return marginLeftMm;
    }

    public void setMarginLeftMm(double marginLeftMm) {
        this.marginLeftMm = marginLeftMm;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }
}
