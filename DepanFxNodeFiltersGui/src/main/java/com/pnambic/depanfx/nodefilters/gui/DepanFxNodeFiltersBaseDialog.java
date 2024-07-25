/*
 * Copyright 2024 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.nodefilters.tooldata.FilterMergeMode;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxProjectDocument;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;
import com.pnambic.depanfx.workspace.DepanFxWorkspaceResource;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

/**
 * Bundles the UX for several common filter properties as a base class.
 */
public abstract class DepanFxNodeFiltersBaseDialog<T extends DepanFxBaseFilterData>
    extends DepanFxBaseToolDialog<T> {

  private enum DialogMode { FOR_SAVE, FOR_UPDATE };

  private final DepanFxDialogRunner dialogRunner;

  private final DepanFxNodeFiltersRegistry nodeFiltersRegistry;

  @FXML
  private Label saveDestinationLabel;

  @FXML
  private HBox saveDestinationBox;

  @FXML
  private HBox saveActionBox;

  @FXML
  private HBox updateActionBox;

  @FXML
  private ComboBox<FilterMergeMode> mergeModeComboBox;

  @FXML
  private CheckBox useClosureCheckBox;

  @FXML
  private TextField filterRsrcField;

  /**
   * The update filter data associated with the dialog is empty
   * unless there has been a successful apply.
   */
  protected Optional<T> optUpdateFilter = Optional.empty();

  private DepanFxWorkspaceResource<?> filterRsrc;

  private Optional<DepanFxWorkspaceResource<T>> savedRsrc = Optional.empty();

  private DialogMode dialogMode;

  @Autowired
  public DepanFxNodeFiltersBaseDialog(
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      DepanFxNodeFiltersRegistry nodeFiltersRegistry,
      Class<T> dataType) {
    super(workspace, dataType);
    this.dialogRunner = dialogRunner;
    this.nodeFiltersRegistry = nodeFiltersRegistry;
  }

  @FXML
  public void initialize() {
    mergeModeComboBox.getItems().setAll(
        FilterMergeMode.class.getEnumConstants());
  }

  public void setForSave() {
    dialogMode = DialogMode.FOR_SAVE;
    showFields();
  }

  public void setForUpdate() {
    dialogMode = DialogMode.FOR_UPDATE;
    showFields();
  }

  public void setFilter(T filterData) {
    setToolName(filterData.getToolName());
    setToolDescription(filterData.getToolDescription());
    setMergeMode(filterData.getMergeMode());

    Optional<Boolean> hasClosure =
        nodeFiltersRegistry.getClosure(filterData);
    if (hasClosure.isPresent()) {
      setUseClosure(hasClosure.get());
    }
    else {
      setUseClosure(false);
      useClosureCheckBox.setDisable(true);
    }
  }

  public Optional<T> getUpdateFilterData() {
    return optUpdateFilter;
  }

  public Optional<DepanFxWorkspaceResource<T>> getSavedResource() {
    return savedRsrc;
  }

  protected  DepanFxDialogRunner getDialogRunner() {
    return dialogRunner;
  }

  protected DepanFxNodeFiltersRegistry getNodeFiltersRegistry() {
    return nodeFiltersRegistry;
  }

  protected FilterMergeMode getMergeMode() {
    return mergeModeComboBox.getValue();
  }

  protected void setMergeMode(FilterMergeMode mergeMode) {
    mergeModeComboBox.setValue(mergeMode);
  }

  protected boolean useClosure() {
    return useClosureCheckBox.isSelected();
  }

  protected void setUseClosure(boolean useClosure) {
    useClosureCheckBox.setSelected(useClosure);
  }

  protected void setFilterResource(
      DepanFxWorkspaceResource<?> filterRsrc) {

    this.filterRsrc = filterRsrc;

    DepanFxProjectDocument filterDoc = filterRsrc.getDocument();
    filterRsrcField.setText(filterDoc.getProject().toLabel(filterDoc));
  }

  @FXML
  protected void handleApply() {
    optUpdateFilter = Optional.of(prepareResult());
    closeDialog();
  }

  @SuppressWarnings("unchecked")
  protected <R> DepanFxWorkspaceResource<R> getFilterResource(Class<R> type) {
    return (DepanFxWorkspaceResource<R>) filterRsrc;
  }

  protected File buildFilterInitialDestinationFile(String toolExt) {
    return buildToolInitialDestination(
        toolExt, DepanFxBaseFilterData.NODE_FILTERS_TOOL_PATH);
  }

  private void showFields() {
    setShown(saveDestinationLabel, dialogMode == DialogMode.FOR_SAVE);
    setShown(saveDestinationBox, dialogMode == DialogMode.FOR_SAVE);
    setShown(saveActionBox, dialogMode == DialogMode.FOR_SAVE);

    setShown(updateActionBox, dialogMode == DialogMode.FOR_UPDATE);
  }

  private void setShown(Node node, boolean isShown) {
    node.setManaged(isShown);
    node.setVisible(isShown);
  }
}
