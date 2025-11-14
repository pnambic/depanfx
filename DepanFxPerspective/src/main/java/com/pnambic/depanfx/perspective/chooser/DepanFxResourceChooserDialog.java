package com.pnambic.depanfx.perspective.chooser;

import com.pnambic.depanfx.perspective.plugins.DepanFxResourceMenuRegistry;
import com.pnambic.depanfx.perspective.plugins.DepanFxResourceRegistry;
import com.pnambic.depanfx.perspective.workspace.controls.DepanFxProjectListCell;
import com.pnambic.depanfx.perspective.workspace.controls.DepanFxProjectTreeCell;
import com.pnambic.depanfx.perspective.workspace.controls.DepanFxWorkspaceItem;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxFxmlDialog;
import com.pnambic.depanfx.workspace.DepanFxProjectContainer;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxProjectMember;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceMember;

import net.rgielen.fxweaver.core.FxmlView;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

@DepanFxFxmlDialog
@FxmlView("resource-chooser-dialog.fxml")
public class DepanFxResourceChooserDialog {

  // Injected
  private final DepanFxDialogRunner dialogRunner;

  // Injected - passthrough
  private final DepanFxResourceRegistry rsrcRegistry;

  // Injected - passthrough
  private final DepanFxResourceMenuRegistry rsrcMenuRegistry;

  @FXML
  private TreeView<DepanFxWorkspaceMember> directoryTreeView;

  @FXML
  private ListView<DepanFxWorkspaceMember> fileListView;

  @FXML
  private ComboBox<DepanFxResourceFilterModel> fileTypeComboBox;

  @FXML
  private TextField resourceNameField;

  private DepanFxWorkspace workspace;

  private DepanFxWorkspaceMember selectedResource;

  private DepanFxResourceFilterModel activeFilter;

  private String initialResourceName;

  private DepanFxProjectContainer initialContainer;

  private List<PathMatcher> activeMatchers;

  public DepanFxResourceChooserDialog(
      DepanFxDialogRunner dialogRunner,
      DepanFxResourceRegistry rsrcRegistry,
      DepanFxResourceMenuRegistry rsrcMenuRegistry) {
    this.dialogRunner = dialogRunner;
    this.rsrcRegistry = rsrcRegistry;
    this.rsrcMenuRegistry = rsrcMenuRegistry;
  }

  @FXML
  public void initialize() {
    initTreeView();
    initListView();
    initComboBox();
  }

  public void setExtension(ObservableList<DepanFxResourceFilterModel> items) {
    fileTypeComboBox.setItems(items);
  }

  public void setActiveFilter(DepanFxResourceFilterModel activeFilter) {
    this.activeFilter = activeFilter;
    fileTypeComboBox.setValue(activeFilter);

    if (activeFilter == null) {
      activeMatchers = null;
      return;
    }
    this.activeMatchers = activeFilter.getPathMatchers();

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
            workspace, dialogRunner, rsrcRegistry, rsrcMenuRegistry,
            d -> rsrcRegistry.openDialog(workspace, dialogRunner, d)));

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
            workspace, dialogRunner, rsrcRegistry, rsrcMenuRegistry));
    fileListView.getSelectionModel().selectedItemProperty()
        .addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
              updateSelectedResource(newValue);
            }
        });
    fileListView.setOnMouseClicked(this::handleMouseClick);
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
      ObservableList<DepanFxWorkspaceMember> items = fileListView.getItems();
      items.clear();
      // First the matching documents
      container.getMembers()
          .filter(this::isIncluded)
          .sorted(DepanFxWorkspaceMember.COMPARE)
          .forEach(items::add);
      // Then any containers
      container.getMembers()
          .filter(m -> m instanceof DepanFxProjectContainer)
          .filter(m -> !isIncluded(m))
          .sorted(DepanFxWorkspaceMember.COMPARE)
          .forEach(items::add);
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

  private void handleMouseClick(MouseEvent event) {

    // Double click on primary opens ..
    if (event.getButton().equals(MouseButton.PRIMARY)
        && event.getClickCount() == 2) {
      DepanFxWorkspaceMember item =
          fileListView.getSelectionModel().getSelectedItem();
      if (item == null) {
        return;
      }
      // Double click on container opens its contents.
      if (item instanceof DepanFxProjectContainer container) {
        updateFileListView(item);
        TreeItem<DepanFxWorkspaceMember> treeItem = getTreeItem(container);
        if (treeItem != null) {
          directoryTreeView.getSelectionModel().select(treeItem);
          treeItem.setExpanded(true);
        }
      }
      // Double click on document same is selection.
      if (item instanceof DepanFxProjectDocument) {
        updateSelectedResource(item);
        handleOpen();
      }
    }
  }

  private TreeItem<DepanFxWorkspaceMember> getTreeItem(
      DepanFxProjectContainer container) {
    TreeItem<DepanFxWorkspaceMember> projectRoot =
        directoryTreeView.getRoot().getChildren()
            .filtered(p -> p.getValue() == container.getProject())
            .getFirst();

    return findTreeItem(projectRoot, container.getMemberPath());
  }

  private TreeItem<DepanFxWorkspaceMember> findTreeItem(
      TreeItem<DepanFxWorkspaceMember> currentTree, Path targetPath) {
    Path currentPath =
        ((DepanFxProjectMember) currentTree.getValue()).getMemberPath();
    if (currentPath.equals(targetPath)) {
      return currentTree;
    }
    if (currentTree.isLeaf() ) {
      return null;
    }
    if (!startWith(targetPath, currentPath)) {
      return null;
    }

    return currentTree.getChildren().stream()
        .map(c -> findTreeItem(c, targetPath))
        .filter(item -> item != null)
        .findFirst()
        .orElse(null);
  }

  private boolean startWith(Path targetPath, Path prefixPath) {
    if (targetPath.startsWith(prefixPath)) {
      return true;
    }
    // Odd root path behavior
    return prefixPath.toString().isBlank();
  }

  private boolean matchesFilter(DepanFxProjectDocument document) {
    if (activeFilter == null) {
      return true;
    }
    Path namePath = Path.of(document.getMemberName());
    boolean byName = activeMatchers.stream()
        .filter(m -> m.matches(namePath))
        .findFirst()
        .isPresent();
    if (byName)
      return true;

    // In the BuiltIn project, try the actual object ('cuz loads are cheap).
    if (workspace.getBuiltInProjectTree().equals(document.getProject())) {
      return workspace.getWorkspaceResource(document, "resource chooser")
          .map(r -> r.getResource())
          .filter(activeFilter::matchDocument)
          .isPresent();
    }
    return false;
  }

  private void updateSelectedResource(DepanFxWorkspaceMember member) {
    // Don't pick a container.
    if (member instanceof DepanFxProjectDocument) {
      selectedResource = member;
      resourceNameField.setText(selectedResource.getMemberName());
    }
  }

  private class ComboBoxCell extends ListCell<DepanFxResourceFilterModel> {

    @Override
    protected void updateItem(DepanFxResourceFilterModel item, boolean empty) {
        super.updateItem(item, empty);

        if (item == null || empty) {
            setGraphic(null);
        } else {
            setText(item.getDescription());
        }
    }
  }
}
