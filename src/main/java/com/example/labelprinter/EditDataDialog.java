package com.example.labelprinter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EditDataDialog extends Dialog<Void> {
    private final ObservableList<Product> products;
    private final ObservableList<String> versions;
    private final ObservableList<String> warehouses;

    public EditDataDialog(List<Product> products,
                          List<String> versions,
                          List<String> warehouses,
                          Consumer<EditDataPayload> onSave) {
        this.products = FXCollections.observableArrayList(products);
        this.versions = FXCollections.observableArrayList(versions);
        this.warehouses = FXCollections.observableArrayList(warehouses);

        setTitle("Edit Products & Versions");
        initModality(Modality.APPLICATION_MODAL);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(createProductsTab(), createSimpleListTab("Versions", this.versions),
                createSimpleListTab("Warehouses", this.warehouses));

        getDialogPane().setContent(tabPane);
        getDialogPane().setPrefSize(700, 420);

        setResultConverter(buttonType -> {
            if (buttonType == saveButtonType) {
                onSave.accept(new EditDataPayload(new ArrayList<>(this.products),
                        new ArrayList<>(this.versions),
                        new ArrayList<>(this.warehouses)));
            }
            return null;
        });
    }

    private Tab createProductsTab() {
        TableView<Product> tableView = new TableView<>(products);
        tableView.setEditable(true);

        TableColumn<Product, String> codeColumn = new TableColumn<>("Code");
        codeColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getCode()));
        codeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        codeColumn.setOnEditCommit(event -> event.getRowValue().setCode(event.getNewValue()));
        codeColumn.setPrefWidth(200);

        TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getName()));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> event.getRowValue().setName(event.getNewValue()));
        nameColumn.setPrefWidth(380);

        tableView.getColumns().addAll(codeColumn, nameColumn);

        Button addButton = new Button("Add");
        addButton.setOnAction(event -> products.add(new Product("", "")));

        Button removeButton = new Button("Remove");
        removeButton.setOnAction(event -> {
            Product selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                products.remove(selected);
            }
        });

        HBox actions = new HBox(10, addButton, removeButton);
        VBox layout = new VBox(10, tableView, actions);
        layout.setPadding(new Insets(10));
        VBox.setVgrow(tableView, Priority.ALWAYS);

        Tab tab = new Tab("Products", layout);
        tab.setClosable(false);
        return tab;
    }

    private Tab createSimpleListTab(String title, ObservableList<String> list) {
        ListView<String> listView = new ListView<>(list);
        listView.setEditable(true);
        listView.setCellFactory(TextFieldListCell.forListView());

        TextField inputField = new TextField();
        inputField.setPromptText("Add new " + title.toLowerCase());
        Button addButton = new Button("Add");
        addButton.setOnAction(event -> {
            if (!inputField.getText().isBlank()) {
                list.add(inputField.getText().trim());
                inputField.clear();
            }
        });
        Button removeButton = new Button("Remove");
        removeButton.setOnAction(event -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                list.remove(selected);
            }
        });

        HBox inputRow = new HBox(8, inputField, addButton, removeButton);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        VBox layout = new VBox(10, listView, inputRow);
        layout.setPadding(new Insets(10));
        VBox.setVgrow(listView, Priority.ALWAYS);

        Tab tab = new Tab(title, layout);
        tab.setClosable(false);
        return tab;
    }

    public record EditDataPayload(List<Product> products, List<String> versions, List<String> warehouses) {
    }
}
