package com.example.labelprinter;

import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class SheetSettingsDialog extends Dialog<PrintSettings> {
    public SheetSettingsDialog(PrintSettings settings) {
        setTitle("Sheet & Label Settings");

        ButtonType saveButton = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        TextField pageWidthField = new TextField(Double.toString(settings.getPageWidthMm()));
        TextField pageHeightField = new TextField(Double.toString(settings.getPageHeightMm()));
        TextField labelWidthField = new TextField(Double.toString(settings.getLabelWidthMm()));
        TextField labelHeightField = new TextField(Double.toString(settings.getLabelHeightMm()));
        TextField gapXField = new TextField(Double.toString(settings.getGapXmm()));
        TextField gapYField = new TextField(Double.toString(settings.getGapYmm()));
        TextField marginTopField = new TextField(Double.toString(settings.getMarginTopMm()));
        TextField marginLeftField = new TextField(Double.toString(settings.getMarginLeftMm()));
        TextField paddingTopField = new TextField(Double.toString(settings.getPaddingTopMm()));
        TextField paddingLeftField = new TextField(Double.toString(settings.getPaddingLeftMm()));

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(12));

        int row = 0;
        grid.add(new Label("Page width (mm)"), 0, row);
        grid.add(pageWidthField, 1, row++);
        grid.add(new Label("Page height (mm)"), 0, row);
        grid.add(pageHeightField, 1, row++);
        grid.add(new Label("Label width (mm)"), 0, row);
        grid.add(labelWidthField, 1, row++);
        grid.add(new Label("Label height (mm)"), 0, row);
        grid.add(labelHeightField, 1, row++);
        grid.add(new Label("Horizontal gap (mm)"), 0, row);
        grid.add(gapXField, 1, row++);
        grid.add(new Label("Vertical gap (mm)"), 0, row);
        grid.add(gapYField, 1, row++);
        grid.add(new Label("Top margin (mm)"), 0, row);
        grid.add(marginTopField, 1, row++);
        grid.add(new Label("Left margin (mm)"), 0, row);
        grid.add(marginLeftField, 1, row++);
        grid.add(new Label("Text top padding (mm)"), 0, row);
        grid.add(paddingTopField, 1, row++);
        grid.add(new Label("Text left padding (mm)"), 0, row);
        grid.add(paddingLeftField, 1, row);

        getDialogPane().setContent(grid);

        setResultConverter(buttonType -> {
            if (buttonType != saveButton) {
                return null;
            }
            PrintSettings updated = new PrintSettings();
            updated.setFontSize(settings.getFontSize());
            updated.setPageWidthMm(parsePositiveValue(pageWidthField.getText(), settings.getPageWidthMm()));
            updated.setPageHeightMm(parsePositiveValue(pageHeightField.getText(), settings.getPageHeightMm()));
            updated.setLabelWidthMm(parsePositiveValue(labelWidthField.getText(), settings.getLabelWidthMm()));
            updated.setLabelHeightMm(parsePositiveValue(labelHeightField.getText(), settings.getLabelHeightMm()));
            updated.setGapXmm(parseNonNegativeValue(gapXField.getText(), settings.getGapXmm()));
            updated.setGapYmm(parseNonNegativeValue(gapYField.getText(), settings.getGapYmm()));
            updated.setMarginTopMm(parseNonNegativeValue(marginTopField.getText(), settings.getMarginTopMm()));
            updated.setMarginLeftMm(parseNonNegativeValue(marginLeftField.getText(), settings.getMarginLeftMm()));
            updated.setPaddingTopMm(parseNonNegativeValue(paddingTopField.getText(), settings.getPaddingTopMm()));
            updated.setPaddingLeftMm(parseNonNegativeValue(paddingLeftField.getText(), settings.getPaddingLeftMm()));
            return updated;
        });
    }

    private double parsePositiveValue(String value, double fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            double parsed = Double.parseDouble(value.trim());
            return parsed > 0 ? parsed : fallback;
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private double parseNonNegativeValue(String value, double fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            double parsed = Double.parseDouble(value.trim());
            return parsed >= 0 ? parsed : fallback;
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
