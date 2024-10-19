package com.sarthkh.todoreminderapp.desktop.controller;

import com.sarthkh.todoreminderapp.desktop.util.FileUtils;
import com.sarthkh.todoreminderapp.model.Todo;
import com.sarthkh.todoreminderapp.service.TodoService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class TodoDialogController {
    @FXML
    private TextField titleField;
    @FXML
    private TextArea descriptionField;
    @FXML
    private ComboBox<Todo.Priority> priorityCombo;
    @FXML
    private DatePicker reminderDate;
    @FXML
    private ComboBox<String> reminderHour;
    @FXML
    private ComboBox<String> reminderMinute;
    @FXML
    private TextField emailField;
    @FXML
    private ListView<String> attachmentList;
    @FXML
    private Label fileCountLabel;

    private final TodoService todoService;
    private final List<File> selectedFiles = new ArrayList<>();

    @Setter
    private Stage dialogStage;

    @Getter
    private boolean saveClicked = false;

    public TodoDialogController(TodoService todoService) {
        this.todoService = todoService;
    }

    @FXML
    public void initialize() {
        setupPriorityComboBox();
        setupTimeComboBoxes();
        reminderDate.setValue(LocalDate.now().plusDays(1));
        resetForm();
    }

    private void resetForm() {
        titleField.clear();
        descriptionField.clear();
        priorityCombo.setValue(Todo.Priority.MEDIUM);
        emailField.clear();
        selectedFiles.clear();
        updateAttachmentList();
    }

    private void setupPriorityComboBox() {
        priorityCombo.setItems(FXCollections.observableArrayList(Todo.Priority.values()));
        priorityCombo.setValue(Todo.Priority.MEDIUM);
    }

    private void setupTimeComboBoxes() {
//        hours
        reminderHour.setItems(FXCollections.observableArrayList(
                IntStream.range(0, 24)
                        .mapToObj(i -> String.format("%02d", i))
                        .toList()
        ));
        reminderHour.setValue("09"); // 9am default

//        minutes
        reminderMinute.setItems(FXCollections.observableArrayList(
                IntStream.range(0, 60)
                        .mapToObj(i -> String.format("%02d", i))
                        .toList()
        ));
        reminderMinute.setValue("00");
    }

    @FXML
    private void handleSave() {
        if (isInputValid()) {
            try {
                Todo todo = createTodoFromInput();
                List<MultipartFile> multipartFiles = convertToMultipartFiles();

                todoService.createTodo(todo, multipartFiles);

                saveClicked = true;
                dialogStage.close();
            } catch (Exception e) {
                showError("Error saving todo", e.getMessage());
            }
        }
    }

    private Todo createTodoFromInput() {
        Todo todo = new Todo();
        todo.setTitle(titleField.getText().trim());
        todo.setDescription(descriptionField.getText().trim());
        todo.setPriority(priorityCombo.getValue());
        todo.setUserEmail(emailField.getText().trim());

        LocalTime time = LocalTime.of(
                Integer.parseInt(reminderHour.getValue()),
                Integer.parseInt(reminderMinute.getValue())
        );
        todo.setReminderDateTime(LocalDateTime.of(reminderDate.getValue(), time));

        return todo;
    }

    private List<MultipartFile> convertToMultipartFiles() {
        return selectedFiles.stream()
                .map(FileUtils::convertToMultipartFile)
                .collect(Collectors.toList());
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (titleField.getText().trim().isEmpty()) {
            errorMessage += "Title is required.\n";
        }
        if (emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            errorMessage += "Valid email is required.\n";
        }
        if (reminderDate.getValue() == null) {
            errorMessage += "Reminder date is required.\n";
        }

        if (errorMessage.isEmpty()) {
            return true;
        } else {
            showError("validation error", errorMessage);
            return false;
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    @FXML
    private void handleAddFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Attachment");
        List<File> files = fileChooser.showOpenMultipleDialog(titleField.getScene().getWindow());

        if (files != null) {
            selectedFiles.addAll(files);
            updateAttachmentList();
        }
    }

    private void updateAttachmentList() {
        attachmentList.setItems(FXCollections.observableArrayList(
                selectedFiles.stream().map(File::getName).toList()
        ));
        fileCountLabel.setText(selectedFiles.size() + " file(s) attached");
    }

    @FXML
    private void handleRemoveAttachment() {
        String selectedFile = attachmentList.getSelectionModel().getSelectedItem();
        if (selectedFile != null) {
            selectedFiles.removeIf(file -> file.getName().equals(selectedFile));
            updateAttachmentList();
        }
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}