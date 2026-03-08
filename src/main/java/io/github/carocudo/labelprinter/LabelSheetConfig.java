package io.github.carocudo.labelprinter;

public final class LabelSheetConfig {
    public static final int COLUMNS = 2;
    public static final int ROWS = 7;

    public static final double DEFAULT_PAGE_WIDTH_MM = 210;
    public static final double DEFAULT_PAGE_HEIGHT_MM = 297;
    public static final double DEFAULT_LABEL_WIDTH_MM = 90;
    public static final double DEFAULT_LABEL_HEIGHT_MM = 38;
    public static final double DEFAULT_GAP_X_MM = 2;
    public static final double DEFAULT_GAP_Y_MM = 2;
    public static final double DEFAULT_PADDING_TOP_MM = 2;
    public static final double DEFAULT_PADDING_LEFT_MM = 2;
    public static final double DEFAULT_MARGIN_TOP_MM = 0;
    public static final double DEFAULT_MARGIN_LEFT_MM = 0;

    private LabelSheetConfig() {
    }

    public static double mmToPoints(double mm) {
        return mm * 72.0 / 25.4;
    }

    public static double mmToPixels(double mm) {
        return mm * 96.0 / 25.4;
    }
}
