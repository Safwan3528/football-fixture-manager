package com.footballfixture;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class DatabaseConnectionDialog extends Dialog<Void> {
    private TextField urlField;
    private TextField userField;
    private PasswordField passwordField;
    private CheckBox bypassCheckBox;
    private CheckBox useMySqlCheckBox;

    public DatabaseConnectionDialog() {
        setTitle("Database Connection");
        setHeaderText("Configure database connection");

        ButtonType connectButtonType = new ButtonType("Connect", ButtonBar.ButtonData.OK_DONE);
        getDialogPane().getButtonTypes().addAll(connectButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        urlField = new TextField(DatabaseConnection.getURL());
        urlField.setPromptText("Database URL");
        userField = new TextField(DatabaseConnection.getUSER());
        userField.setPromptText("Username");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        bypassCheckBox = new CheckBox("Bypass database connection");
        bypassCheckBox.setSelected(DatabaseConnection.isBypassConnection());
        useMySqlCheckBox = new CheckBox("Use MySQL (instead of SQLite)");
        useMySqlCheckBox.setSelected(DatabaseConnection.isUseMySql());

        // Enable/disable MySQL fields based on checkbox
        urlField.setDisable(!useMySqlCheckBox.isSelected());
        userField.setDisable(!useMySqlCheckBox.isSelected());
        passwordField.setDisable(!useMySqlCheckBox.isSelected());

        useMySqlCheckBox.setOnAction(e -> {
            boolean useMySQL = useMySqlCheckBox.isSelected();
            urlField.setDisable(!useMySQL);
            userField.setDisable(!useMySQL);
            passwordField.setDisable(!useMySQL);
        });

        grid.add(useMySqlCheckBox, 0, 0, 2, 1);
        grid.add(new Label("Database URL:"), 0, 1);
        grid.add(urlField, 1, 1);
        grid.add(new Label("Username:"), 0, 2);
        grid.add(userField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(bypassCheckBox, 0, 4, 2, 1);

        getDialogPane().setContent(grid);

        setResultConverter(dialogButton -> {
            if (dialogButton == connectButtonType) {
                DatabaseConnection.setConnectionDetails(
                    urlField.getText(), 
                    userField.getText(), 
                    passwordField.getText(),
                    useMySqlCheckBox.isSelected()
                );
                DatabaseConnection.setBypassConnection(bypassCheckBox.isSelected());
            }
            return null;
        });
    }
}
