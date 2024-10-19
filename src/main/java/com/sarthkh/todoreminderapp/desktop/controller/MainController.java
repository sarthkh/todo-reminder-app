package com.sarthkh.todoreminderapp.desktop.controller;

import com.sarthkh.todoreminderapp.model.Todo;
import com.sarthkh.todoreminderapp.service.TodoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class MainController {
    private final TodoService todoService;
    private final ApplicationContext applicationContext;
    private final ObservableList<Todo> todos = FXCollections.observableArrayList();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<Todo.Priority> priorityFilter;
    @FXML
    private TableView<Todo> todoTable;
    @FXML
    private TableColumn<Todo, String> titleColumn;
    @FXML
    private TableColumn<Todo, String> descriptionColumn;
    @FXML
    private TableColumn<Todo, LocalDateTime> reminderColumn;
    @FXML
    private TableColumn<Todo, Todo.Priority> priorityColumn;
    @FXML
    private TableColumn<Todo, Void> actionsColumn;

    @Autowired
    public MainController(TodoService todoService, ApplicationContext applicationContext) {
        this.todoService = todoService;
        this.applicationContext = applicationContext;
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupPriorityFilter();
        setupSearchField();
        refreshTodos();
    }

    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        reminderColumn.setCellValueFactory(new PropertyValueFactory<>("reminderDateTime"));
        priorityColumn.setCellValueFactory(new PropertyValueFactory<>("priority"));

        reminderColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(DATE_FORMATTER.format(item));
                }
            }
        });

        setupActionsColumn();
    }

    private void setupPriorityFilter() {
        priorityFilter.setItems(FXCollections.observableArrayList(Todo.Priority.values()));
        priorityFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                todos.setAll(todoService.getTodosByPriority(newValue));
            } else {
                refreshTodos();
            }
        });
    }

    private void setupSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
//            todo
        });
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final Button snoozeButton = new Button("Snooze");
            private final HBox buttons = new HBox(5, editButton, deleteButton, snoozeButton);

            {
                editButton.setOnAction(event -> handleEditTodo(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> handleDeleteTodo(getTableView().getItems().get(getIndex())));
                snoozeButton.setOnAction(event -> handleSnoozeTodo(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    @FXML
    private void handleAddTodo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/todo-dialog.fxml"));
            loader.setControllerFactory(applicationContext::getBean);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(todoTable.getScene().getWindow());
            dialogStage.setTitle("Add New Todo");

            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            TodoDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                refreshTodos();
            }
        } catch (IOException e) {
            showError("Error", "Could not load todo dialog: " + e.getMessage());
        }
    }

    private void handleEditTodo(Todo todo) {
//        todo
    }

    private void handleDeleteTodo(Todo todo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to delete this todo?",
                ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    todoService.deleteTodo(todo.getId());
                    refreshTodos();
                } catch (Exception e) {
                    showError("Error deleting todo", e.getMessage());
                }
            }
        });
    }

    private void handleSnoozeTodo(Todo todo) {
//        todo
    }

    private void refreshTodos() {
        todos.setAll(todoService.getAllTodos());
        todoTable.setItems(todos);
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}