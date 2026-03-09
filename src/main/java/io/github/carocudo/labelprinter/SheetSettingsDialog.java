package io.github.carocudo.labelprinter;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

import java.util.ResourceBundle;

public class SheetSettingsDialog extends Dialog<PrintSettings> {

    public SheetSettingsDialog(PrintSettings settings, ResourceBundle bundle) {
        setTitle(bundle.getString("sheetsettings.title"));

        ButtonType saveButton = new ButtonType(bundle.getString("sheetsettings.button.save"), ButtonBar.ButtonData.OK_DONE);
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
        themeCombo.getItems().addAll("Corporate", "Dark", "Minimal", "Ocean");
        themeCombo.setValue(settings.getTheme());
        themeCombo.setMaxWidth(Double.MAX_VALUE);

        ComboBox<String> languageCombo = new ComboBox<>();
        languageCombo.getItems().addAll("English", "Svenska");
        languageCombo.setValue(settings.getLanguage().equals("sv") ? "Svenska" : "English");
        languageCombo.setMaxWidth(Double.MAX_VALUE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(12));

        ColumnConstraints col0 = new ColumnConstraints();
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHgrow(javafx.scene.layout.Priority.ALWAYS);
        grid.getColumnConstraints().addAll(col0, col1);

        int row = 0;
        grid.add(new Label(bundle.getString("sheetsettings.pagewidth")), 0, row);
        grid.add(pageWidthField, 1, row++);
        grid.add(new Label(bundle.getString("sheetsettings.pageheight")), 0, row);
        grid.add(pageHeightField, 1, row++);
        grid.add(new Label(bundle.getString("sheetsettings.labelwidth")), 0, row);
        grid.add(labelWidthField, 1, row++);
        grid.add(new Label(bundle.getString("sheetsettings.labelheight")), 0, row);
        grid.add(labelHeightField, 1, row++);
        grid.add(new Label(bundle.getString("sheetsettings.gapx")), 0, row);
        grid.add(gapXField, 1, row++);
        grid.add(new Label(bundle.getString("sheetsettings.gapy")), 0, row);
        grid.add(gapYField, 1, row++);
        grid.add(new Label(bundle.getString("sheetsettings.margintop")), 0, row);
        grid.add(marginTopField, 1, row++);
        grid.add(new Label(bundle.getString("sheetsettings.marginleft")), 0, row);
        grid.add(marginLeftField, 1, row++);
        grid.add(new Label(bundle.getString("sheetsettings.paddingtop")), 0, row);
        grid.add(paddingTopField, 1, row++);
        grid.add(new Label(bundle.getString("sheetsettings.paddingleft")), 0, row);
        grid.add(paddingLeftField, 1, row++);
        grid.add(new Label(bundle.getString("sheetsettings.language")), 0, row);
        grid.add(languageCombo, 1, row++);
        grid.add(new Label(bundle.getString("sheetsettings.theme")), 0, row);
        grid.add(themeCombo, 1, row);


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
            updated.setLanguage(languageCombo.getValue().equals("Svenska") ? "sv" : "en");
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
