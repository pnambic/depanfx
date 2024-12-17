package com.pnambic.depanfx.nodelist.gui.columns;

import com.google.common.base.Strings;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListChooser;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableAdapter;
import com.pnambic.depanfx.nodelist.gui.DepanFxNodeListTableCommands;
import com.pnambic.depanfx.nodelist.gui.DepanFxSaveNodeListDialog;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxCategoryColumnData.CategoryEntry;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListColumnData;
import com.pnambic.depanfx.nodelist.model.DepanFxNodeList;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.perspective.chooser.DepanFxResourceFilter;
import com.pnambic.depanfx.scene.DepanFxActionCell;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxTableColumnBinder;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

@Component
@FxmlView("category-column-tool-dialog.fxml")
public class DepanFxCategoryColumnToolDialog
    extends DepanFxBaseColumnToolDialog<DepanFxCategoryColumnData> {

  @SuppressWarnings("unused")
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

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TableView<EditCategory> categoriesTable;

  private ObservableList<EditCategory> categoryTableData;

  // If null, avoid creating node lists for categories.
  private DepanFxNodeListTableAdapter tableAdapter;

  private MenuItem sectionItem;

  private MenuItem newItem;

  @Autowired
  public DepanFxCategoryColumnToolDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxCategoryColumnData.class);
    this.dialogRunner = dialogRunner;
  }

  public static Dialog<DepanFxCategoryColumnToolDialog> runEditDialog(
      DepanFxWorkspaceResource<DepanFxCategoryColumnData> columnRsrc,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeListTableAdapter tableAdapter) {

    Dialog<DepanFxCategoryColumnToolDialog> result =
        DepanFxResourcePerspectives.prepareDialog(
            columnRsrc, dialogRunner,
            DepanFxCategoryColumnToolDialog.class);
    result.getController().setTableAdapter(tableAdapter);
    result.runDialog(DepanFxCategoryColumn.EDIT_CATEGORY_COLUMN);
    return result;
  }

  public static Dialog<DepanFxCategoryColumnToolDialog> runCreateDialog(
      DepanFxWorkspaceResource<DepanFxCategoryColumnData> columnRsrc,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeListTableAdapter tableAdapter) {

    Dialog<DepanFxCategoryColumnToolDialog> result =
        DepanFxResourcePerspectives.prepareDialog(
            columnRsrc, dialogRunner,
            DepanFxCategoryColumnToolDialog.class);
    result.getController().setTableAdapter(tableAdapter);
    result.runDialog(DepanFxCategoryColumn.NEW_CATEGORY_COLUMN);
    return result;
  }

  public static void setCategoryColumnTooldataFilters(FileChooser result) {
    result.getExtensionFilters().add(CATEGORY_COLUMN_FILTER);
    result.setSelectedExtensionFilter(CATEGORY_COLUMN_FILTER);
  }

  public void setTableAdapter(DepanFxNodeListTableAdapter tableAdapter) {
    this.tableAdapter = tableAdapter;
  }

  @FXML
  public void initialize() {
    categoriesTable.setContextMenu(buildCategoriesTableMenu());
    categoriesTable.setOnContextMenuRequested(e -> enableTableMenuItems());

    DepanFxTableColumnBinder<EditCategory> columnBinder =
        new DepanFxTableColumnBinder<>(categoriesTable);

    TableColumn<EditCategory, String> labelColumn =
        columnBinder.bind("categoryLabel");
    labelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    labelColumn.setOnEditCommit(this::onUpdateLabelEvent);

    TableColumn<EditCategory, String> filePathColumn =
        columnBinder.bind("nodeListName");
    filePathColumn.setCellFactory(c ->
        new DepanFxNodeListChooser.NodeListCell<>(
            getWorkspace(), dialogRunner,
            categoriesTable.getScene(),
            (t, r) -> updateNodeList(t, r)));

    TableColumn<EditCategory, String> rowActionColumn =
        columnBinder.next();
    DepanFxActionCell.prepareColumn(
        rowActionColumn, p -> new CategoryActions());

    // Size filePath to remaining room
    filePathColumn.prefWidthProperty().bind(
        categoriesTable.widthProperty()
            .subtract(labelColumn.widthProperty())
            .subtract(rowActionColumn.widthProperty())
            .subtract(2));
  }

  @Override
  public void setToolResource(
      DepanFxWorkspaceResource<DepanFxCategoryColumnData> columnRsrc) {
    super.setToolResource(columnRsrc);

    DepanFxCategoryColumnData columnData = columnRsrc.getResource();
    List<EditCategory> editCategories = columnData.getCategories().stream()
        .map(c -> new EditCategory(c))
        .collect(Collectors.toList());

    categoryTableData = FXCollections.observableArrayList(editCategories);
    categoriesTable.setItems(categoryTableData);
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxCategoryColumnData prepareResult() {
    return new DepanFxCategoryColumnData(
            getToolName(), getToolDescription(),
            getColumnLabel(), getColumnWidthMs(),
            buildCategories());
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxCategoryColumnData.CATEGORY_COLUMN_TOOL_EXT,
        DepanFxNodeListColumnData.COLUMNS_TOOL_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    DepanFxCategoryColumnToolDialog.setCategoryColumnTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Node Key Column Save Confirmation Error";
  }

  @FXML
  private void addCategoryRow() {
    addEmptyCategory();
  }

  private void onUpdateLabelEvent(
      CellEditEvent<EditCategory, String> updateEvent) {
    EditCategory editEntry = updateEvent.getTableView().getItems().get(
        updateEvent.getTablePosition().getRow());
    editEntry.categoryLabelProperty().set(updateEvent.getNewValue());
  }

  private List<CategoryEntry> buildCategories() {
    return categoryTableData.stream()
        .map(c -> c.toData())
        .collect(Collectors.toList());
  }

  private void updateNodeList(
      EditCategory editCategory,
      DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
    editCategory.setNodeListResource(nodeListRsrc);
  }

  /////////////////////////////////////
  // Actions for add category menu

  private void enableTableMenuItems() {
    boolean hasAdapter = tableAdapter != null;
    sectionItem.setVisible(hasAdapter);
    newItem.setVisible(hasAdapter);
  }

  private ContextMenu buildCategoriesTableMenu() {
    DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
    builder.appendActionItem("Node List category", this::addNewList);
    sectionItem = builder.appendActionItem("Selection category", this::addSelectionCategory);
    newItem = builder.appendActionItem("New category", this::addNewCategory);
    builder.appendActionItem("Empty category", this::addEmptyCategory);
    return builder.build();
  }

  private void addNewList(Event event) {
    DepanFxNodeListChooser.runNodeListChooser(
        getWorkspace(), dialogRunner, categoriesTable.getScene())
        .map(this::toEditCategory)
        .ifPresent(categoryTableData::add);
  }

  private void addSelectionCategory(Event event) {
    DepanFxNodeList selectList = tableAdapter.getSelection();
    DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc =
        workspace.addScratchResource(selectList);
    DepanFxSaveNodeListDialog.runSaveNodeList(dialogRunner, nodeListRsrc)
        .map(this::toEditCategory)
        .ifPresent(categoryTableData::add);
  }

  private void addNewCategory(Event event) {
    DepanFxNodeList emptyList = tableAdapter.buildEmptyList();
    DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc =
        workspace.addScratchResource(emptyList);
    DepanFxSaveNodeListDialog.runSaveNodeList(dialogRunner, nodeListRsrc)
        .map(this::toEditCategory)
        .ifPresent(categoryTableData::add);
  }

  private void addEmptyCategory(Event event) {
    addEmptyCategory();
  }

  private void addEmptyCategory() {
    categoryTableData.add(new EditCategory("", null));
  }

  private EditCategory toEditCategory(
      DepanFxWorkspaceResource<DepanFxNodeList> listRsrc) {
    return new EditCategory(listRsrc.getResource().getNodeListName(), listRsrc);
  }

  /////////////////////////////////////
  // Internal Table Classes

  private class CategoryActions extends DepanFxActionCell<EditCategory> {

    CategoryActions() {
      super(categoryTableData);
    }

    @Override
    protected void populateContextMenu(DepanFxContextMenuBuilder builder) {
      builder.appendActionItem(DepanFxNodeListTableCommands.SELECT_NODE_LIST,
          e -> runNodeListChooser(getIndex()));
    }

    private void runNodeListChooser(int index) {
      DepanFxNodeListChooser.runNodeListChooser(
          workspace, dialogRunner, getScene())
      .ifPresent(r -> updateCellResource(index, r));
    }

    private void updateCellResource(
        int cellIndex,
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
      updateNodeList(getRowData(cellIndex), nodeListRsrc);
    }
  }

  /**
   *  Must be public for property lookup.
   */
  public static class EditCategory {

    public StringProperty categoryLabelProp;

    public StringProperty nodeListNameProp;

    public DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc;

    public EditCategory(
        String categoryLabel,
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
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

    public void setNodeListResource(
        DepanFxWorkspaceResource<DepanFxNodeList> nodeListRsrc) {
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
              nodeListRsrc.getResource().getNodeListName());
        }
      }
    }
  }
}
