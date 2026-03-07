package com.example.labelprinter;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class SheetSettingsDialog extends Dialog<PrintSettings> {
    public SheetSettingsDialog(PrintSettings settings) {
        setTitle("Ark- och etikettinställningar");
        getDialogPane().getStylesheets().add(
                getClass().getResource("/com/example/labelprinter/style.css").toExternalForm()
        );

        ButtonType saveButton = new ButtonType("Spara", ButtonBar.ButtonData.OK_DONE);
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

        ComboBox<String> themeCombo = new ComboBox<>();
        themeCombo.getItems().addAll("corporate", "dark", "minimal", "contrast");
        themeCombo.setValue(settings.getTheme());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(12));

        int row = 0;
        grid.add(new Label("Sidbredd (mm)"), 0, row);
        grid.add(pageWidthField, 1, row++);
        grid.add(new Label("Sidhöjd (mm)"), 0, row);
        grid.add(pageHeightField, 1, row++);
        grid.add(new Label("Etikettsbredd (mm)"), 0, row);
        grid.add(labelWidthField, 1, row++);
        grid.add(new Label("Etikettshöjd (mm)"), 0, row);
        grid.add(labelHeightField, 1, row++);
        grid.add(new Label("Horisontellt gap (mm)"), 0, row);
        grid.add(gapXField, 1, row++);
        grid.add(new Label("Vertikalt gap (mm)"), 0, row);
        grid.add(gapYField, 1, row++);
        grid.add(new Label("Övre marginal (mm)"), 0, row);
        grid.add(marginTopField, 1, row++);
        grid.add(new Label("Vänster marginal (mm)"), 0, row);
        grid.add(marginLeftField, 1, row++);
        grid.add(new Label("Textavstånd uppifrån (mm)"), 0, row);
        grid.add(paddingTopField, 1, row++);
        grid.add(new Label("Textavstånd från vänster (mm)"), 0, row);
        grid.add(paddingLeftField, 1, row++);

        grid.add(new Label("Tema"), 0, row);
        grid.add(themeCombo, 1, row++);

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
            updated.setTheme(themeCombo.getValue());
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
