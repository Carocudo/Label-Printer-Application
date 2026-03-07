package com.example.labelprinter;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class FontSettingsDialog extends Dialog<Double> {
    public FontSettingsDialog(double currentSize) {
        setTitle("Redigera teckenstil ");
        getDialogPane().getStylesheets().add(
                getClass().getResource("/com/example/labelprinter/style.css").toExternalForm()
        );

        ButtonType saveButton = new ButtonType("Spara", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);

        Slider sizeSlider = new Slider(8, 24, currentSize);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setMajorTickUnit(4);
        sizeSlider.setMinorTickCount(3);

        Label sizeLabel = new Label();
        sizeLabel.textProperty().bind(sizeSlider.valueProperty().asString("Teckenstorlek: %.0f"));

        VBox layout = new VBox(10, sizeLabel, sizeSlider);
        layout.setPadding(new Insets(12));

        getDialogPane().setContent(layout);

        setResultConverter(buttonType -> buttonType == saveButton ? sizeSlider.getValue() : null);
    }
}
