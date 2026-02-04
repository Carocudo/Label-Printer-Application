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
        TextField paddingField = new TextField(Double.toString(settings.getPaddingMm()));

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
        grid.add(new Label("Text padding (mm)"), 0, row);
        grid.add(paddingField, 1, row);

        getDialogPane().setContent(grid);

        setResultConverter(buttonType -> {
            if (buttonType != saveButton) {
                return null;
            }
            PrintSettings updated = new PrintSettings();
            updated.setFontSize(settings.getFontSize());
            updated.setPageWidthMm(parseValue(pageWidthField.getText(), settings.getPageWidthMm()));
            updated.setPageHeightMm(parseValue(pageHeightField.getText(), settings.getPageHeightMm()));
            updated.setLabelWidthMm(parseValue(labelWidthField.getText(), settings.getLabelWidthMm()));
            updated.setLabelHeightMm(parseValue(labelHeightField.getText(), settings.getLabelHeightMm()));
            updated.setGapXmm(parseValue(gapXField.getText(), settings.getGapXmm()));
            updated.setGapYmm(parseValue(gapYField.getText(), settings.getGapYmm()));
            updated.setPaddingMm(parseValue(paddingField.getText(), settings.getPaddingMm()));
            return updated;
        });
    }

    private double parseValue(String value, double fallback) {
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
}
