package com.pnambic.depanfx.perspective.chooser;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.perspective.workspace.controls.DepanFxProjectListCell;
import com.pnambic.depanfx.perspective.workspace.controls.DepanFxProjectTreeCell;
import com.pnambic.depanfx.perspective.workspace.controls.DepanFxWorkspaceItem;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import net.rgielen.fxweaver.core.FxmlView;
import java.nio.file.PathMatcher;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

@Component
@FxmlView("resource-chooser-dialog.fxml")
public class DepanFxResourceChooserDialog {

  // Injected
  private final DepanFxDialogRunner dialogRunner;

  // Injected
  private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

  @FXML
  private TreeView<DepanFxWorkspaceMember> directoryTreeView;

  @FXML
  private ListView<DepanFxWorkspaceMember> fileListView;

  @FXML
  private ComboBox<ExtensionFilter> fileTypeComboBox;

  @FXML
  private TextField resourceNameField;

  private DepanFxWorkspace workspace;

  private DepanFxWorkspaceMember selectedResource;

  private ExtensionFilter activeFilter;

  private List<PathMatcher> activeMatchers;

  private String initialResourceName;

  private DepanFxProjectContainer initialContainer;

  public DepanFxResourceChooserDialog(
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    this.dialogRunner = dialogRunner;
    this.rsrcMenuRegistry = rsrcMenuRegistry;
  }

  @FXML
  public void initialize() {
    initTreeView();
    initListView();
    initComboBox();
  }

  public void setExtension(ObservableList<ExtensionFilter> items) {
    fileTypeComboBox.setItems(items);
  }

  public void setActiveFilter(ExtensionFilter activeFilter) {
    this.activeFilter = activeFilter;
    fileTypeComboBox.setValue(activeFilter);
    if (activeFilter == null) {
      activeMatchers = null;
      return;
    }

    FileSystem fileSys = FileSystems.getDefault();
    activeMatchers = activeFilter.getExtensions().stream()
        .map(ext -> fileSys.getPathMatcher("glob:" + ext))
        .collect(Collectors.toList());

    TreeItem<DepanFxWorkspaceMember> treeItem =
        directoryTreeView.getSelectionModel().getSelectedItem();
    if (treeItem != null) {
      updateFileListView(treeItem.getValue());
    }
  }

  public void setInitialContainer(DepanFxProjectContainer initialContainer) {
    this.initialContainer = initialContainer;
  }

  public void setInitialResourceName(String initialResourceName) {
    this.initialResourceName = initialResourceName;
    resourceNameField.setText(initialResourceName);
  }

  public void setWorkspace(DepanFxWorkspace workspace) {
    this.workspace = workspace;

      TreeItem<DepanFxWorkspaceMember> rootItem =
          new DepanFxWorkspaceItem(workspace,
              m -> !(m instanceof DepanFxProjectDocument));
      directoryTreeView.setRoot(rootItem);
  }

  public Optional<DepanFxWorkspaceMember> getSelectedResource() {
    return Optional.ofNullable(selectedResource);
  }

  @FXML
  private void handleOpen() {
    if (selectedResource != null) {
      closeDialog();
    }
  }

  @FXML
  private void handleCancel() {
    selectedResource = null;
    closeDialog();
  }

  private void initTreeView() {
    directoryTreeView.setShowRoot(false);
    directoryTreeView.setCellFactory(
        p -> new DepanFxProjectTreeCell(
            workspace, dialogRunner, rsrcMenuRegistry));

    directoryTreeView.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
              updateFileListView(newValue.getValue());
            }
        });
  }

  private void initListView() {
    fileListView.setCellFactory(
        p -> new DepanFxProjectListCell(
            workspace, dialogRunner, rsrcMenuRegistry));
    fileListView.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
              updateSelectedResource(newValue);
            }
        });
  }

  private void initComboBox() {
    fileTypeComboBox.setCellFactory(p -> new ComboBoxCell());
    fileTypeComboBox.setButtonCell(new ComboBoxCell());
    fileTypeComboBox.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
          setActiveFilter(newValue);
        });
  }

  private void updateFileListView(DepanFxWorkspaceMember member) {
    if (member instanceof DepanFxProjectContainer container) {
      fileListView.getItems().clear();
      container.getMembers()
          .filter(this::isIncluded)
          .forEach(fileListView.getItems()::add);
    }
  }

  private void closeDialog() {
    ((Stage) directoryTreeView.getScene().getWindow()).close();
  }


  private boolean isIncluded(DepanFxProjectMember member) {
    if (member instanceof DepanFxProjectDocument document) {
      return matchesFilter(document);
    }

    return false;
  }

  private boolean matchesFilter(DepanFxProjectDocument document) {
    if (activeMatchers == null) {
      return true;
    }
    Path namePath = Path.of(document.getMemberName());
    return activeMatchers.stream()
        .filter(m -> m.matches(namePath))
        .findFirst()
        .isPresent();
  }

  private void updateSelectedResource(DepanFxWorkspaceMember member) {
    selectedResource = member;
    resourceNameField.setText(selectedResource.getMemberName());
  }

  private class ComboBoxCell extends ListCell<ExtensionFilter> {

    @Override
    protected void updateItem(ExtensionFilter item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setGraphic(null);
        } else {
            setText(item.getDescription());
        }
    }
  }
}
