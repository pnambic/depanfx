package com.pnambic.depanfx.nodelist.gui.columns;

import com.google.common.base.Strings;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData.CategoryEntry;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceFactory;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;
import com.pnambic.depanfx.workspace.projects.DepanFxProjects;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("category-column-tool-dialog.fxml")
public class DepanFxCategoryColumnToolDialog
    extends DepanFxBaseColumnToolDialog<DepanFxCategoryColumnData> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxCategoryColumnToolDialog.class);

  public static final ExtensionFilter CATEGORY_COLUMN_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Category Columns",
          DepanFxCategoryColumnData.CATEGORY_COLUMN_TOOL_EXT);

  public static final DepanFxResourceFilter CATEGORY_COLUMN__RSRC_FILTER =
      DepanFxResourceFilter.buildResourceFilter(
          "Category Columns",
          DepanFxCategoryColumnData.CATEGORY_COLUMN_TOOL_EXT,
          DepanFxCategoryColumnData.class);

  @FXML
  private TableView<EditCategory> categoriesTable;

  private ObservableList<EditCategory> categoryTableData;

  @Autowired
  public DepanFxCategoryColumnToolDialog(DepanFxWorkspace workspace) {
    super(workspace, DepanFxCategoryColumnData.class);
  }

  public static Dialog<DepanFxCategoryColumnToolDialog> runEditDialog(
      DepanFxProjectDocument projDoc,
      DepanFxCategoryColumnData categoryColumnData,
      DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runEditDialog(
        projDoc, categoryColumnData, dialogRunner,
        DepanFxCategoryColumnToolDialog.class,
        DepanFxCategoryColumn.EDIT_CATEGORY_COLUMN);
  }

  public static Dialog<DepanFxCategoryColumnToolDialog> runCreateDialog(
      DepanFxCategoryColumnData columnData, DepanFxDialogRunner dialogRunner) {

    return DepanFxResourcePerspectives.runCreateDialog(
        columnData, dialogRunner,
        DepanFxCategoryColumnToolDialog.class,
        DepanFxCategoryColumn.NEW_CATEGORY_COLUMN);
  }

  public static void setCategoryColumnTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(CATEGORY_COLUMN_FILTER);
    result.setSelectedExtensionFilter(CATEGORY_COLUMN_FILTER);
  }

  @FXML
  @SuppressWarnings("unchecked")
  public void initialize() {
    TableColumn<EditCategory, String> labelColumn =
        (TableColumn<EditCategory, String>) categoriesTable.getColumns().get(0);

    labelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    labelColumn.setOnEditCommit(this::onUpdateLabelEvent);
    labelColumn.setCellValueFactory(
        new PropertyValueFactory<>("categoryLabel"));

    TableColumn<EditCategory, String> filePathColumn =
        (TableColumn<EditCategory, String>) categoriesTable.getColumns().get(1);
    filePathColumn.setCellValueFactory(
        new PropertyValueFactory<>("nodeListName"));

    TableColumn<EditCategory, String> findActionColumn =
        (TableColumn<EditCategory, String>) categoriesTable.getColumns().get(2);
    findActionColumn.setCellFactory(
        p -> new ButtonActionCell<EditCategory, String>(
            "Find...", this::runNodeListFinder));

    TableColumn<EditCategory, String> deleteActionColumn =
        (TableColumn<EditCategory, String>) categoriesTable.getColumns().get(3);
    deleteActionColumn.setCellFactory(
        p -> new ButtonActionCell<EditCategory, String>(
            "Delete", this::onDeleteEntryLabelEvent));

    // Size filePath to remaining room
    filePathColumn.prefWidthProperty().bind(
        categoriesTable.widthProperty()
            .subtract(labelColumn.widthProperty())
            .subtract(findActionColumn.widthProperty())
            .subtract(deleteActionColumn.widthProperty())
            .subtract(2));
  }

  @Override // DepanFxBaseColumnToolDialog
  public void setTooldata(DepanFxCategoryColumnData columnData) {
    super.setTooldata(columnData);

    List<EditCategory> editCategories = columnData.getCategories().stream()
        .map(c -> new EditCategory(c))
        .collect(Collectors.toList());

    categoryTableData = FXCollections.observableArrayList(editCategories);
    categoriesTable.setItems(categoryTableData);
  }

  @FXML
  private void addCategoryRow() {
    categoryTableData.add(new EditCategory("", null));
  }

  private void onUpdateLabelEvent(
      CellEditEvent<EditCategory, String> updateEvent) {
    EditCategory editEntry = updateEvent.getTableView().getItems().get(
        updateEvent.getTablePosition().getRow());
    editEntry.categoryLabelProperty().set(updateEvent.getNewValue());
  }

  private void onDeleteEntryLabelEvent(TableCell<?, ?> cell) {
    categoriesTable.getItems().remove(cell.getIndex());
  }

  private List<CategoryEntry> buildCategories() {
    return categoryTableData.stream()
        .map(c -> c.toData())
        .collect(Collectors.toList());
  }

  private void runNodeListFinder(TableCell<?, ?> cell) {
    EditCategory editData =  categoryTableData.get(cell.getIndex());
    DepanFxNodeListChooser.runNodeListFinder(
        getWorkspace(), categoriesTable.getScene().getWindow())
        .ifPresent(editData::setNodeListResource);
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxCategoryColumnData prepareResult() {
    return new DepanFxCategoryColumnData(
            toolNameField.getText(), toolDescriptionField.getText(),
            columnLabelField.getText(), parseWidthMs(widthMsField.getText()),
            buildCategories());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return DepanFxWorkspaceFactory.bestDocumentFile(
        toolNameField.getText(), DepanFxCategoryColumnData.CATEGORY_COLUMN_TOOL_EXT,
        getWorkspace(), DepanFxNodeListColumnData.COLUMNS_TOOL_PATH,
        DepanFxProjects.getCurrentTools(getWorkspace()));
  }

  @Override
  protected void setColumnTooldataFilters(FileChooser result) {
    DepanFxCategoryColumnToolDialog.setCategoryColumnTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Node Key Column Save Confirmation Error";
  }

  /////////////////////////////////////
  // Internal Table Classes

  private static class ButtonActionCell<S, T> extends TableCell<S, T> {

    private final Consumer<TableCell<S, T>> cellAction;

    private final Button actionButton;

    public ButtonActionCell(String label, Consumer<TableCell<S, T>> cellAction) {
      this.actionButton = new Button(label);
      this.cellAction = cellAction;
      actionButton.setOnAction(this::onAction);
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        if (empty) {
            setGraphic(null);
        } else {
            setGraphic(actionButton);
        }
    }

    private void onAction(ActionEvent event) {
      cellAction.accept(this);
    }
  }

  /**
   *  Must be public for property lookup.
   */
  public static class EditCategory {

    public StringProperty categoryLabelProp;

    public StringProperty nodeListNameProp;

    public DepanFxWorkspaceResource nodeListRsrc;

    public EditCategory(
        String categoryLabel, DepanFxWorkspaceResource nodeListRsrc) {
      this.categoryLabelProp =  new SimpleStringProperty(categoryLabel);
      this.nodeListRsrc = nodeListRsrc;
      this.nodeListNameProp =  new SimpleStringProperty();
      updateNodeNameProp();
    }

    public EditCategory(CategoryEntry categoryData) {
      this(categoryData.getCategoryLabel(), categoryData.getNodeListRsrc());
    }

    public CategoryEntry toData() {
      return new CategoryEntry(categoryLabelProp.getValue(), nodeListRsrc);
    }

    public StringProperty categoryLabelProperty() {
      return categoryLabelProp;
    }

    public StringProperty nodeListNameProperty() {
      return nodeListNameProp;
    }

    public void setNodeListResource(DepanFxWorkspaceResource nodeListRsrc) {
      this.nodeListRsrc = nodeListRsrc;
      updateNodeNameProp();
      updateCategoryLabelProp();
    }

    private void updateNodeNameProp() {
      if (nodeListRsrc != null) {
        nodeListNameProp.setValue(
            nodeListRsrc.getDocument().getMemberPath().toString());
        return;
      }
      nodeListNameProp.setValue("");
    }

    private void updateCategoryLabelProp() {
      if (Strings.isNullOrEmpty(categoryLabelProp.getValue())) {
        if (nodeListRsrc != null) {
          categoryLabelProp.setValue(
              ((DepanFxNodeList) nodeListRsrc.getResource()).getNodeListName());
        }
      }
    }
  }
}
