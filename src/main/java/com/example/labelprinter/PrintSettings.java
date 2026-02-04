package com.example.labelprinter;

public class PrintSettings {
    private double pageWidthMm = LabelSheetConfig.DEFAULT_PAGE_WIDTH_MM;
    private double pageHeightMm = LabelSheetConfig.DEFAULT_PAGE_HEIGHT_MM;
    private double labelWidthMm = LabelSheetConfig.DEFAULT_LABEL_WIDTH_MM;
    private double labelHeightMm = LabelSheetConfig.DEFAULT_LABEL_HEIGHT_MM;
    private double gapXmm = LabelSheetConfig.DEFAULT_GAP_X_MM;
    private double gapYmm = LabelSheetConfig.DEFAULT_GAP_Y_MM;
    private double paddingMm = LabelSheetConfig.DEFAULT_PADDING_MM;
    private double fontSize = 12;

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

    public double getPaddingMm() {
        return paddingMm;
    }

    public void setPaddingMm(double paddingMm) {
        this.paddingMm = paddingMm;
    }

    public double getFontSize() {
        return fontSize;
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
    }
}
