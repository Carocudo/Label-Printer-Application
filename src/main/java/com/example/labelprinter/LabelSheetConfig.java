package com.example.labelprinter;

public final class LabelSheetConfig {
    public static final int COLUMNS = 2;
    public static final int ROWS = 7;

    public static final double LABEL_WIDTH_MM = 90;
    public static final double LABEL_HEIGHT_MM = 38;
    public static final double GAP_X_MM = 2;
    public static final double GAP_Y_MM = 2;

    private LabelSheetConfig() {
    }

    public static double mmToPoints(double mm) {
        return mm * 72.0 / 25.4;
    }
}
