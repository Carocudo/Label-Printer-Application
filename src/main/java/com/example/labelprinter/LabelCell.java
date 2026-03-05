package com.example.labelprinter;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LabelCell extends StackPane {
    private final int row;
    private final int col;
    private final Rectangle background;
    private final VBox textBox;
    private final Label productLabel = new Label();
    private final Label versionLabel = new Label();
    private final Label warehouseLabel = new Label();
    private final Label dateLabel = new Label();
    private LabelData data;
    private boolean active;
    private boolean selected;
    private double fontSize = 12;

    public LabelCell(int row, int col, double width, double height) {
        this.row = row;
        this.col = col;
        this.background = new Rectangle(width, height);
        background.setFill(Color.WHITE);
        background.setStroke(Color.LIGHTGRAY);
        background.setStrokeWidth(1);

        textBox = new VBox(2, productLabel, versionLabel, warehouseLabel, dateLabel);
        textBox.setPadding(new Insets(4));
        textBox.setAlignment(Pos.TOP_LEFT);

        getChildren().addAll(background, textBox);
        getStyleClass().add("label-cell");

        setMinSize(width, height);
        setPrefSize(width, height);
        setMaxSize(width, height);
        updateFontSize();
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public LabelData getData() {
        return data;
    }

    public void setData(LabelData data) {
        this.data = data;
        updateSelectedFromData();
        updateText();
        updateStyles();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
        updateStyles();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        updateStyles();
    }

    public void toggleSelected() {
        setSelected(!selected);
    }

    public void setFontSize(double fontSize) {
        this.fontSize = fontSize;
        updateFontSize();
    }

    public void setDimensions(double width, double height) {
        background.setWidth(width);
        background.setHeight(height);
        setMinSize(width, height);
        setPrefSize(width, height);
        setMaxSize(width, height);
    }

    public void setTextPadding(double topPaddingPx, double leftPaddingPx) {
        textBox.setPadding(new Insets(topPaddingPx, 0, 0, leftPaddingPx));
    }

    public double getFontSize() {
        return fontSize;
    }

    public void refresh() {
        updateText();
        updateSelectedFromData();
        updateStyles();
    }

    private void updateText() {
        if (data == null || data.isEmpty()) {
            productLabel.setText("");
            versionLabel.setText("");
            warehouseLabel.setText("");
            dateLabel.setText("");
            return;
        }
        String productText = "";
        if (data.getProduct() != null) {
            productText = data.getProduct().getName().isBlank()
                    ? data.getProduct().getCode()
                    : data.getProduct().getName();
        }
        String versionText = data.getVersion() == null ? "" : data.getVersion();
        String productLine = (productText + " " + versionText).trim();
        productLabel.setText(productLine);
        versionLabel.setText(data.getWarehouse() == null ? "" : data.getWarehouse());
        warehouseLabel.setText(data.getDate() == null ? "" : data.getDate().toString());
        dateLabel.setText("");
    }

    private void updateStyles() {
        getStyleClass().removeAll("label-cell-active", "label-cell-selected");

        if (selected) {
            background.setFill(Color.web("#cfe4ff"));
            getStyleClass().add("label-cell-selected");
        } else {
            background.setFill(Color.WHITE);
        }
        if (active) {
            background.setStroke(Color.web("#2c5f8a"));
            background.setStrokeWidth(2);
            getStyleClass().add("label-cell-active");
        } else {
            background.setStroke(Color.LIGHTGRAY);
            background.setStrokeWidth(1);
        }
    }

    private void updateFontSize() {
        productLabel.setStyle("-fx-font-size: " + fontSize + "px; -fx-font-weight: bold;");
        versionLabel.setStyle("-fx-font-size: " + fontSize + "px;");
        warehouseLabel.setStyle("-fx-font-size: " + fontSize + "px;");
        dateLabel.setStyle("-fx-font-size: " + fontSize + "px;");
    }

    private void updateSelectedFromData() {
        boolean hasData = data != null && !data.isEmpty();
        if (hasData && !selected) {
            selected = true;
        } else if (!hasData && selected) {
            selected = false;
        }
    }
}
