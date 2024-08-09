package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.nodelist.gui.link.DepanFxLinkMatcherChooser;
import com.pnambic.depanfx.nodelist.tooldata.DepanFxLinkMatcherDocument;
import com.pnambic.depanfx.nodeview.jogl.JoglColors;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineArrow;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDirection;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineForm;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineLabel;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxLineStyle;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLinkDisplayData.LinkDisplayEntry;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.perspective.DepanFxResourcePerspectives;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.scene.DepanFxDialogRunner.Dialog;
import com.pnambic.depanfx.scene.DepanFxSceneControls;
import com.pnambic.depanfx.scene.DepanFxTableColumnBinder;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import net.rgielen.fxweaver.core.FxmlView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

@Component
@FxmlView("node-view-link-display-dialog.fxml")
public class DepanFxNodeViewLinkDisplayDialog
    extends DepanFxBaseToolDialog<DepanFxNodeViewLinkDisplayData> {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxNodeViewLinkDisplayDialog.class);

  public static final String EDIT_LINK_DISPLAY = "Edit Link Display...";

  public static final String NEW_LINK_DISPLAY = "New Link Display...";

  public static final ExtensionFilter NODE_VIEW_LINK_DISPLAY_FILTER =
      DepanFxSceneControls.buildExtFilter(
          "Link Display",
          DepanFxNodeViewLinkDisplayData.NODE_VIEW_LINK_DISPLAY_EXT);

  private static final double MAX_LINE_WIDTH = 5.0d;

  private static final double MIN_LINE_WIDTH = 0.0d;

  private final DepanFxDialogRunner dialogRunner;

  @FXML
  private TableView<EditLinkDisplay> linksDisplayTable;

  private ObservableList<EditLinkDisplay> linksDiplayTableData;

  /**
   * Source of non-mutated data (e.g. context model)
   */
  private DepanFxNodeViewLinkDisplayData linkDisplayData;

  /**
   * Where live changes happen.
   */
  private EdgeDisplayController displayControl;

  public DepanFxNodeViewLinkDisplayDialog(
      DepanFxWorkspace workspace, DepanFxDialogRunner dialogRunner) {
    super(workspace, DepanFxNodeViewLinkDisplayData.class);
    this.dialogRunner = dialogRunner;
  }

  /**
   * Edge Display Editor is a modeless dialog coupled to the graph view.
   */
  public static Stage runEditDialog(
      EdgeDisplayController displayControl,
      DepanFxProjectDocument projDoc,
      DepanFxDialogRunner dialogRunner) {

    Dialog<DepanFxNodeViewLinkDisplayDialog> dlg =
        DepanFxResourcePerspectives.prepareDialog(
            displayControl.getLinkDisplayInfo(), dialogRunner,
            DepanFxNodeViewLinkDisplayDialog.class);
    dlg.getController().setEdgeDisplayControl(displayControl);
    dlg.getController().setDestination(projDoc);
    return dlg.runModeless(EDIT_LINK_DISPLAY);
  }

  public static void setNodeViewLinkDisplayTooldataFilters(FileChooser chooser) {
    chooser.getExtensionFilters().add(NODE_VIEW_LINK_DISPLAY_FILTER);
    chooser.setSelectedExtensionFilter(NODE_VIEW_LINK_DISPLAY_FILTER);
  }

  @FXML
  @SuppressWarnings("unused")
  public void initialize() {
    DepanFxTableColumnBinder<EditLinkDisplay> columnBinder =
        new DepanFxTableColumnBinder<>(linksDisplayTable);

    TableColumn<EditLinkDisplay, String> labelColumn =
        columnBinder.bind("linkDisplayLabel");
    labelColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    labelColumn.setOnEditCommit(this::onUpdateLabelEvent);

    TableColumn<EditLinkDisplay, String> filePathColumn =
        columnBinder.bind("linkDisplayName");

    // filePathColumn.setCellFactory(column -> new DisplayNameCellFactory());
    filePathColumn.setCellFactory(c ->
        new DepanFxLinkMatcherChooser.LinkMatcherCell<>(
            getWorkspace(), dialogRunner,
            linksDisplayTable.getScene(),
            (t, r) -> updateMatcher(t, r)));

    TableColumn<EditLinkDisplay, Number> countColumn = columnBinder.next();
    countColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
    Object blix;
    countColumn.setCellValueFactory(
        r -> new SimpleIntegerProperty(
            displayControl.getDisplayMatcherEdgeCount(
                r.getValue().linkDisplayRsrc.getResource())));

    TableColumn<EditLinkDisplay, DepanFxLineForm> lineFormColumn =
        columnBinder.bind("lineForm", DepanFxLineForm.class);

    TableColumn<EditLinkDisplay, DepanFxLineStyle> lineStyleColumn =
        columnBinder.bind("lineStyle", DepanFxLineStyle.class);

    TableColumn<EditLinkDisplay, Color> lineColorColumn =
        columnBinder.bind("lineColor");
    lineColorColumn.setEditable(true);
    lineColorColumn.setCellFactory(column -> new ColorCellFactory());

    TableColumn<EditLinkDisplay, Double> lineWidthColumn =
        columnBinder.bind("lineWidth");
    lineWidthColumn.setCellFactory(e -> new WidthTableCell());

    TableColumn<EditLinkDisplay, DepanFxLineLabel> lineLabelColumn =
        columnBinder.bind("lineLabel", DepanFxLineLabel.class);

    TableColumn<EditLinkDisplay, DepanFxLineArrow> sourceArrowColumn =
        columnBinder.bind("sourceArrow", DepanFxLineArrow.class);

    TableColumn<EditLinkDisplay, DepanFxLineArrow> targetArrowColumn =
        columnBinder.bind("targetArrow", DepanFxLineArrow.class);

    TableColumn<EditLinkDisplay, DepanFxLineDirection> lineDirectionColumn =
        columnBinder.bind("lineDirection", DepanFxLineDirection.class);

    TableColumn<EditLinkDisplay, String> rowActionColumn =
        columnBinder.next();
    rowActionColumn.setCellFactory(p -> new ActionCell());
  }

  /**
   * Both tooldata and view panel are required to populate the display table.
   */
  public void setEdgeDisplayControl(EdgeDisplayController displayControl) {
    this.displayControl = displayControl;
    prepareDisplayTable();
  }

  /**
   * Both view panel and tooldata are required to populate the display table.
   */
  @Override // DepanFxBaseToolDialog
  public void setTooldata(DepanFxNodeViewLinkDisplayData linkDisplayData) {
    super.setTooldata(linkDisplayData);
    this.linkDisplayData = linkDisplayData;
    prepareDisplayTable();
  }

  private void prepareDisplayTable() {
    // Wait for both to be configured.
    if ((displayControl == null) || (linkDisplayData == null)) {
      return;
    }
    List<EditLinkDisplay> editLinkDisplay =
        linkDisplayData.streamLinkDisplay()
        .map(e -> new EditLinkDisplay(displayControl, e))
        .collect(Collectors.toList());

    linksDiplayTableData = FXCollections.observableArrayList(editLinkDisplay);
    linksDisplayTable.setItems(linksDiplayTableData);
  }

  @FXML
  private void addLinkDisplayRow() {
    DepanFxLinkMatcherChooser.runLinkMatcherFinder(
        workspace, dialogRunner, getScene())
        .ifPresent(this::addLinkDisplayRow);
  }

  private void addLinkDisplayRow(
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {
    DepanFxLineDisplayData lineDisplay =
        DepanFxLineDisplayData.buildSimpleLineDisplayData();
    LinkDisplayEntry rowDisplay = new LinkDisplayEntry(
        matcherRsrc.getResource().getToolName(), matcherRsrc, lineDisplay);
    linksDiplayTableData.add(new EditLinkDisplay(displayControl, rowDisplay));
  }

  private void onUpdateLabelEvent(
      CellEditEvent<EditLinkDisplay, String> updateEvent) {
    getEventLinkDisplay(updateEvent)
        .linkDisplayLabelProp.set(updateEvent.getNewValue());
  }

  /////////////////////////////////////
  // Tool Dialog protected overrides

  @Override
  protected DepanFxNodeViewLinkDisplayData prepareResult() {
    List<LinkDisplayEntry> displayEntries = linksDiplayTableData.stream()
        .map(e -> toLinkDisplayEntry(e))
        .collect(Collectors.toList());

    return new DepanFxNodeViewLinkDisplayData(
            getToolName(), getToolDescription(),
            linkDisplayData.getContextModelId(), displayEntries);
  }

  @Override
  protected File buildInitialDestinationFile() {
    return buildToolInitialDestination(
        DepanFxNodeViewLinkDisplayData.NODE_VIEW_LINK_DISPLAY_EXT,
        DepanFxNodeViewLinkDisplayData.LINK_DISPLAY_PATH);
  }

  @Override
  protected void setTooldataFilters(FileChooser result) {
    DepanFxNodeViewLinkDisplayDialog
      .setNodeViewLinkDisplayTooldataFilters(result);
  }

  @Override
  protected String getInputCheckFailureText() {
    return  "Link Display Save Confirmation Error";
  }

  private EditLinkDisplay getEventLinkDisplay(
      CellEditEvent<EditLinkDisplay, ?> updateEvent) {
    return updateEvent.getTableView().getItems().get(
        updateEvent.getTablePosition().getRow());
  }

  private void updateMatcher(
      EditLinkDisplay editLinkDisplay,
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {
    editLinkDisplay.setLinkDisplayRsrc(matcherRsrc);
  }

  /////////////////////////////////////
  // FXML handlers.

  @FXML
  protected void handleRevert() {
    closeDialog();
    displayControl.revertLinkDisplay();
  }

  @FXML
  protected void handleApply() {
    DepanFxNodeViewLinkDisplayData toolData = prepareResult();
    displayControl.setLinkDisplayInfo(toolData);
  }

  @Override
  @FXML
  protected void handleConfirm() {
    super.handleConfirm();
    getWorkspaceResource().ifPresent(displayControl::setLinkDisplayResource);
  }

  /////////////////////////////////////
  // Table Cell Classes

  /////////////////////////////////////
  // Internal Table Classes

  private static class ColorCellFactory
      extends TableCell<EditLinkDisplay, Color> {

    private final ColorPicker colorPicker = new ColorPicker();

    @Override
    protected void updateItem(Color color, boolean empty) {
      super.updateItem(color, empty);

      if (empty) {
        setGraphic(null);
        return;
      }
      colorPicker.setValue(color != null ? color : Color.WHITE);
      setGraphic(colorPicker);
      colorPicker.setOnAction(event -> {
        EditLinkDisplay linkDisplay = getTableRow().getItem();
        linkDisplay.lineColorProp.setValue(colorPicker.getValue());
      });
    }
  }

  private static class WidthTableCell
      extends TextFieldTableCell<EditLinkDisplay, Double> {

    private class DoubleConverter extends DoubleStringConverter {

      @Override
      public Double fromString(String lineWidth) {
        double current = WidthTableCell.this.getItem();
        return parseLineWidth(lineWidth, current);
      }

      private double parseLineWidth(String lineWidth, double current) {
        double result = current;
        try {
          Double parsed  = super.fromString(lineWidth);
          if (parsed != null) {
            result = parsed.doubleValue();
          }
        } catch (NumberFormatException errFmt) {
          LOG.warn("Bad user value for line width {}", lineWidth);
        }
        return Math.min(MAX_LINE_WIDTH, Math.max(MIN_LINE_WIDTH, result));
      }
    }

    public WidthTableCell() {
      super();
      setConverter(new DoubleConverter());
    }
  }

  public class ActionCell extends TableCell<EditLinkDisplay, String> {

    @Override
    protected void updateItem(String item, boolean empty) {
      super.updateItem(item, empty);

      if (!empty) {
        setText("...");
        setGraphic(null);
        stylizeCell();
        return;
      }
      setText(null);
      setGraphic(null);
    }

    private void stylizeCell() {
      DepanFxContextMenuBuilder builder = new DepanFxContextMenuBuilder();
      builder.appendActionItem("Select Matcher...",
          e -> runMatcherChooser(getIndex()));
      appendMoveOps(builder);
      builder.appendSeparator();
      builder.appendActionItem("Delete",
          e -> deleteFilter(getIndex()));
      setContextMenu(builder.build());
    }

    private void appendMoveOps(DepanFxContextMenuBuilder builder) {
      int index = getIndex();
      boolean hasUp = index > 0;
      boolean hasDown = index < linksDiplayTableData.size() - 1;
      if (!hasUp && !hasDown ) {
        return;
      }
      builder.appendSeparator();
      if (hasUp) {
        builder.appendActionItem("Up", e -> moveDisplayEntry(getIndex(), -1));
      }
      if (hasDown) {
        builder.appendActionItem("Down", e -> moveDisplayEntry(getIndex(), 1));
      }
    }
  }

  private void runMatcherChooser(int index) {
    DepanFxLinkMatcherChooser.runLinkMatcherFinder(
        workspace, dialogRunner, getScene())
    .ifPresent(r -> updateMatcher(index, r));
  }

  private void moveDisplayEntry(int srcIndex, int moveBy) {

    int dstIndex = srcIndex + moveBy;
    if (dstIndex < 0 || dstIndex >= linksDiplayTableData.size()) {
      LOG.error("Bad filter move to {} from {} by {}",
          dstIndex, srcIndex, moveBy);
      return;
    }
    EditLinkDisplay moveItem = linksDiplayTableData.remove(srcIndex);
    linksDiplayTableData.add(dstIndex, moveItem);
  }

  private void deleteFilter(int index) {
    linksDiplayTableData.remove(index);
  }

  private void updateMatcher(
      int index,
      DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> matcherRsrc) {

    updateMatcher(linksDiplayTableData.get(index), matcherRsrc);
  }

  /////////////////////////////////////
  // Editable Link properties, with helpers

  private static class LinkDisplayUpdater implements ChangeListener<Object> {

    private final EdgeDisplayController displayControl;

    private final EditLinkDisplay linkDisplay;

    public LinkDisplayUpdater(
        EdgeDisplayController displayControl, EditLinkDisplay linkDisplay) {
      this.displayControl = displayControl;
      this.linkDisplay = linkDisplay;
    }

    @Override
    public void changed(
        ObservableValue<? extends Object> observable,
        Object oldValue, Object newValue) {
      LOG.info("update display for {}",
          linkDisplay.linkDisplayLabelProp.getValue());
      LinkDisplayEntry display = toLinkDisplayEntry(linkDisplay);
      displayControl.updateEdgeDisplayByMatcher(
          linkDisplay.linkDisplayRsrc.getResource(), display);
    }
  }

  private static LinkDisplayEntry toLinkDisplayEntry(EditLinkDisplay editData) {
    DepanFxLineDisplayData lineDisplayData = new DepanFxLineDisplayData(
        editData.lineFormProperty().getValue(),
        editData.lineStyleProperty().getValue(),
        JoglColors.of(editData.lineColorProperty().getValue()),
        editData.lineWidthProperty().getValue(),

        editData.lineLabelProperty().getValue(),
        editData.sourceArrowProperty().getValue(),
        editData.targetArrowProperty().getValue(),
        editData.lineDirectionProperty().getValue());

    LinkDisplayEntry result = new LinkDisplayEntry(
        editData.linkDisplayLabelProperty().getValue(),
        editData.linkDisplayRsrc,
        lineDisplayData);
    return result ;
  }

  /**
   * Must be public for property lookup.
   *
   * Use xxxProp suffix for property fields to avoid colliding
   * with the required property getter method xxxProperty().
   */
  public static class EditLinkDisplay {

    private final LinkDisplayUpdater updater;

    public StringProperty linkDisplayLabelProp;

    public StringProperty linkDisplayNameProp;

    public DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> linkDisplayRsrc;

    public ObjectProperty<DepanFxLineForm> lineFormProp;

    public ObjectProperty<DepanFxLineStyle> lineStyleProp;

    public ObjectProperty<Color> lineColorProp;

    public SimpleDoubleProperty lineWidthProp;

    public ObjectProperty<DepanFxLineLabel> lineLabelProp;

    public ObjectProperty<DepanFxLineArrow> sourceArrowProp;

    public ObjectProperty<DepanFxLineArrow> targetArrowProp;

    public ObjectProperty<DepanFxLineDirection> lineDirectionProp;

    public EditLinkDisplay(
        EdgeDisplayController displayControl, LinkDisplayEntry linkDisplay) {
      updater = new LinkDisplayUpdater(displayControl, this);

      // Unpack data from source.
      linkDisplayLabelProp =
          new SimpleStringProperty(linkDisplay.getLinkLabel());
      linkDisplayLabelProp.addListener(updater);

      linkDisplayNameProp = new SimpleStringProperty();
      setLinkDisplayRsrc(linkDisplay.getLinkRsrc());

      DepanFxLineDisplayData lineDisplay = linkDisplay.getLineDisplay();
      lineFormProp = new SimpleObjectProperty<>(lineDisplay.lineForm);
      lineFormProp.addListener(updater);

      lineStyleProp = new SimpleObjectProperty<>(lineDisplay.lineStyle);
      lineStyleProp.addListener(updater);

      lineColorProp =
          new SimpleObjectProperty<>(JoglColors.of(lineDisplay.lineColor));
      lineColorProp.addListener(updater);

      lineWidthProp = new SimpleDoubleProperty(lineDisplay.lineWidth);
      lineWidthProp.addListener(updater);

      lineLabelProp = new SimpleObjectProperty<>(lineDisplay.lineLabel);
      lineLabelProp.addListener(updater);

      sourceArrowProp = new SimpleObjectProperty<>(lineDisplay.sourceArrow);
      sourceArrowProp.addListener(updater);

      targetArrowProp = new SimpleObjectProperty<>(lineDisplay.targetArrow);
      targetArrowProp.addListener(updater);

      lineDirectionProp = new SimpleObjectProperty<>(lineDisplay.lineDir);
      lineDirectionProp.addListener(updater);
    }

    public void setLinkDisplayRsrc(
        DepanFxWorkspaceResource<DepanFxLinkMatcherDocument> linkDisplayRsrc) {
      this.linkDisplayRsrc = linkDisplayRsrc;
      if (this.linkDisplayRsrc != null) {
        linkDisplayNameProp.setValue(
            linkDisplayRsrc.getDocument().getMemberName());
        return;
      }

      linkDisplayNameProp.setValue("");
    }

    ///////////////////////////////////
    // For the support of PropertyValueFactory

    public StringProperty linkDisplayLabelProperty() {
      return linkDisplayLabelProp;
    }

    public StringProperty linkDisplayNameProperty() {
      return linkDisplayNameProp;
    }

    public ObjectProperty<DepanFxLineForm> lineFormProperty() {
      return lineFormProp;
    }

    public ObjectProperty<DepanFxLineStyle> lineStyleProperty() {
      return lineStyleProp;
    }

    public ObjectProperty<Color> lineColorProperty() {
      return lineColorProp;
    }

    public SimpleDoubleProperty lineWidthProperty() {
      return lineWidthProp;
    }

    public ObjectProperty<DepanFxLineLabel> lineLabelProperty() {
      return lineLabelProp;
    }

    public ObjectProperty<DepanFxLineArrow> sourceArrowProperty() {
      return sourceArrowProp;
    }

    public ObjectProperty<DepanFxLineArrow> targetArrowProperty() {
      return targetArrowProp;
    }

    public ObjectProperty<DepanFxLineDirection> lineDirectionProperty() {
      return lineDirectionProp;
    }
  }
}
