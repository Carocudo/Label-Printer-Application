package io.github.carocudo.labelprinter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class EditDataDialog extends Dialog<Void> {
    private final ObservableList<Product> products;
    private final ObservableList<String> versions;
    private final ObservableList<String> warehouses;
    private final ResourceBundle bundle = ResourceBundle.getBundle(
            "io/github/carocudo/labelprinter/messages");

    public EditDataDialog(List<Product> products,
                          List<String> versions,
                          List<String> warehouses,
                          Consumer<EditDataPayload> onSave) {
        this.products = FXCollections.observableArrayList(products);
        this.versions = FXCollections.observableArrayList(versions);
        this.warehouses = FXCollections.observableArrayList(warehouses);

        setTitle(bundle.getString("editdata.title"));

        initModality(Modality.APPLICATION_MODAL);

        ButtonType saveButtonType = new ButtonType(bundle.getString("editdata.button.save"), ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(createProductsTab(), createSimpleListTab(bundle.getString("editdata.tab.versions"), this.versions, true),
                createSimpleListTab(bundle.getString("editdata.tab.warehouses"), this.warehouses, false));

        getDialogPane().setContent(tabPane);
        getDialogPane().setPrefSize(750, 500); // was 700x420

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
        ListView<Product> listView = new ListView<>(products);
        listView.setEditable(false);
        listView.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Product item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getCode() + " — " + item.getName());
                }
            }
        });


        TextField codeField = new TextField();
        codeField.setPromptText(bundle.getString("editdata.label.code"));

        TextField nameField = new TextField();
        nameField.setPromptText(bundle.getString("editdata.label.name"));
        HBox.setHgrow(nameField, Priority.ALWAYS);

        Button addButton = new Button(bundle.getString("editdata.button.add"));
        addButton.setOnAction(event -> {
            String code = codeField.getText().trim();
            String name = nameField.getText().trim();
            if (!code.isBlank() || !name.isBlank()) {
                products.add(new Product(code, name));
                codeField.clear();
                nameField.clear();
                codeField.requestFocus();
            }
        });

        Button removeButton = new Button(bundle.getString("editdata.button.remove"));
        removeButton.getStyleClass().add("button-danger");
        removeButton.setOnAction(event -> {
            Product selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                products.remove(selected);
            }
        });

        listView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Product selected = listView.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    codeField.setText(selected.getCode());
                    nameField.setText(selected.getName());
                    products.remove(selected);
                    codeField.requestFocus();
                }
            }
        });

        // Allow pressing Enter in nameField to trigger add
        nameField.setOnAction(event -> addButton.fire());
        codeField.setOnAction(event -> nameField.requestFocus()); // Tab through with Enter

        HBox inputRow = new HBox(8, codeField, nameField, addButton, removeButton);
        listView.setPlaceholder(new javafx.scene.control.Label(bundle.getString("editdata.placeholder.products")));

        VBox layout = new VBox(10, listView, inputRow);
        layout.setPadding(new Insets(10));
        VBox.setVgrow(listView, Priority.ALWAYS);

        Tab tab = new Tab(bundle.getString("editdata.tab.products"), layout);
        tab.setClosable(false);
        return tab;
    }

    private Tab createSimpleListTab(String title, ObservableList<String> list, boolean addSuperscriptButton) {
        ListView<String> listView = new ListView<>(list);
        listView.setEditable(true);
        listView.setCellFactory(TextFieldListCell.forListView());

        TextField inputField = new TextField();
        inputField.setPromptText(bundle.getString("editdata.field.add") + title.toLowerCase());
        Button addButton = new Button(bundle.getString("editdata.button.add"));
        addButton.setOnAction(event -> {
            if (!inputField.getText().isBlank()) {
                list.add(inputField.getText().trim());
                inputField.clear();
            }
        });
        Button removeButton = new Button(bundle.getString("editdata.button.remove"));
        removeButton.getStyleClass().add("button-danger");
        removeButton.setOnAction(event -> {
            String selected = listView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                list.remove(selected);
            }
        });

        HBox inputRow;
        if (addSuperscriptButton) {
            Button squaredButton = new Button("²");

            squaredButton.setOnAction(event -> {
                int caret = inputField.getCaretPosition();
                if (caret < 0 || caret > inputField.getText().length()) {
                    caret = inputField.getText().length();
                }
                inputField.insertText(caret, "²");
                inputField.requestFocus();
                inputField.positionCaret(caret + 1);
            });
            inputRow = new HBox(8, inputField, squaredButton, addButton, removeButton);
        } else {
            inputRow = new HBox(8, inputField, addButton, removeButton);
        }
        HBox.setHgrow(inputField, Priority.ALWAYS);

        listView.setPlaceholder(new javafx.scene.control.Label(bundle.getString("editdata.placeholder.data")));

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
