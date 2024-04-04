package com.pnambic.depanfx.nodelist.gui.sections;

import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxBaseSectionData;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderBy;
import com.pnambic.depanfx.nodelist.gui.tooldata.DepanFxNodeListSectionData.OrderDirection;
import com.pnambic.depanfx.perspective.DepanFxBaseToolDialog;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import org.springframework.beans.factory.annotation.Autowired;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public abstract class DepanFxBaseSectionToolDialog<T extends DepanFxBaseSectionData>
    extends DepanFxBaseToolDialog<T> {

  @FXML
  private TextField sectionLabelField;

  @FXML
  private CheckBox displayNodeCountField;

  @FXML
  private ComboBox<OrderDirection> orderDirectionField;

  @Autowired
  public DepanFxBaseSectionToolDialog(
      DepanFxWorkspace workspace, Class<T> dataType) {
    super(workspace, dataType);
  }

  public void initialize() {
    orderDirectionField.getItems().add(OrderDirection.FORWARD);
    orderDirectionField.getItems().add(OrderDirection.REVERSE);
  }

  @Override
  public void setTooldata(T sectionData) {
    super.setTooldata(sectionData);

    sectionLabelField.setText(sectionData.getSectionLabel());
    displayNodeCountField.setSelected(sectionData.displayNodeCount());

    orderDirectionField.setValue(sectionData.getOrderDirection());
  }

  protected String getSectionLabel() {
    return sectionLabelField.getText();
  }

  protected boolean displayNodeCount() {
    return displayNodeCountField.isSelected();
  }

  protected OrderDirection getOrderDirection() {
    return orderDirectionField.getValue();
  }

  /////////////////////////////////////
  // Helper methods for derived classes.

  /**
   * OrderBy is common, but not fundamental to a section.
   */
  protected void populateOrderBy(ComboBox<OrderBy> orderByField) {
    orderByField.getItems().add(OrderBy.NODE_LEAF);
    orderByField.getItems().add(OrderBy.NODE_KEY);
    orderByField.getItems().add(OrderBy.NODE_ID);
  }
}
