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
package com.pnambic.depanfx.nodelist.export;

import com.pnambic.depanfx.nodelist.gui.columns.DepanFxCategoryColumn;
import com.pnambic.depanfx.nodelist.gui.columns.DepanFxNodeListColumn;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * Describes the UI options for exporting node list columns.
 */
public class ExportData {

  public enum NodeIdHandling {
    KIND("Node kind and node key"),
    MODEL("Full node key"),
    NODE("Node key only"),
    PARTS("As Parts (3 columns)");

    private final String label;

    NodeIdHandling(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
  }

  public enum ColumnHandling {
    OMIT("Omit"),
    INCLUDE("Include"),
    EXPAND("Expand");

    private final String label;

    ColumnHandling(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
  }

  public static final List<ColumnHandling> SIMPLE_HANDLING =
      Arrays.asList(new ColumnHandling[] {
          ColumnHandling.OMIT,
          ColumnHandling.INCLUDE
      });

  public static final List<ColumnHandling> EXPAND_HANDLING =
      Arrays.asList(new ColumnHandling[] {
          ColumnHandling.OMIT,
          ColumnHandling.INCLUDE,
          ColumnHandling.EXPAND
      });

  private ColumnHandling handling;

  private final DepanFxNodeListColumn column;

  private final List<ColumnHandling> choices;

  private final StringProperty columnLabelProp;

  private final ObjectProperty<ColumnHandling> columnHandlingProp;

  public ExportData(
      DepanFxNodeListColumn column, ColumnHandling handling,
      List<ColumnHandling> choices) {
    this.column = column;
    this.handling = handling;
    this.choices = choices;

    columnLabelProp = new SimpleStringProperty(column.getColumnLabel());
    columnHandlingProp = new SimpleObjectProperty<>(handling);
  }

  public static void prepareExportTable(TableView<ExportData> exportTable) {

    @SuppressWarnings("unchecked")
    TableColumn<ExportData, String> labelColumn =
        (TableColumn<ExportData, String>) exportTable.getColumns().get(0);
    labelColumn.setCellValueFactory(p -> p.getValue().columnLabelProp);

    @SuppressWarnings("unchecked")
    TableColumn<ExportData, ColumnHandling> handlingColumn =
        (TableColumn<ExportData, ColumnHandling>)
            exportTable.getColumns().get(1);
    handlingColumn.setCellValueFactory(p -> p.getValue().columnHandlingProp);
    handlingColumn.setCellFactory(c -> new ColumnHandlingCell());
  }

  public static void populateExportTable(
      TableView<ExportData> exportTable,
      Stream<DepanFxNodeListColumn> columns) {
    List<ExportData> exportColumns =  columns
          .map(ExportData::buildExportData)
          .collect(Collectors.toList());

    ObservableList<ExportData> exportTableData =
        FXCollections.observableArrayList(exportColumns);
    exportTable.setItems(exportTableData);
  }

  public DepanFxNodeListColumn getColumn() {
    return column;
  }

  public ColumnHandling getHandling() {
    return handling;
  }

  public void setHandling(ColumnHandling handling) {
    this.handling = handling;
    columnHandlingProp.setValue(handling);
  }

  public List<ColumnHandling> getChoices() {
    return choices;
  }

  private static ExportData buildExportData(DepanFxNodeListColumn column) {
    if (column instanceof DepanFxCategoryColumn) {
      return new ExportData(column, ColumnHandling.EXPAND, EXPAND_HANDLING);
    }
    return new ExportData(column, ColumnHandling.INCLUDE, SIMPLE_HANDLING);
  }

  private static class ColumnHandlingCell
       extends TableCell<ExportData, ColumnHandling> {

     private ChoiceBox<ColumnHandling> choiceBox;

     public ColumnHandlingCell() {
       choiceBox = new ChoiceBox<>();
       choiceBox.setOnAction(this::handleHandlingChangeAction);
     }

     @Override
     protected void updateItem(ColumnHandling item, boolean empty) {
       super.updateItem(item, empty);
       if (!empty) {
         populateChoices();
         choiceBox.setValue(item);
         setGraphic(choiceBox);
         return;
       }
       setGraphic(null);
     }

     private void populateChoices() {
       choiceBox.getItems().clear();
       choiceBox.getItems().addAll(getTableRow().getItem().getChoices());
     }

     private void handleHandlingChangeAction(ActionEvent ignored) {
       ExportData exportData = getTableRow().getItem();
       exportData.setHandling(choiceBox.getValue());
     }
   }
}
