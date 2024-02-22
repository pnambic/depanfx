package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import net.rgielen.fxweaver.core.FxmlView;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.util.Duration;

@Component
@FxmlView("node-view-status-panel.fxml")
public class DepanFxNodeViewStatusPanel {

  @FXML
  private Label cameraXField;
  // private Slider cameraXField;

  @FXML
  private Label cameraYField;
//private Slider cameraYField;

  @FXML
  private Label cameraZField;
//private Slider cameraZField;

  @FXML
  private Label lookAtXField;
//private Slider lookAtXField;

  @FXML
  private Label lookAtYField;
//private Slider lookAtYField;

  @FXML
  private Label lookAtZField;
//private Slider lookAtZField;

  @FXML
  private Label destinationField;
//private Slider destinationField;

  @FXML
  private Label zoomField;
//private Slider zoomField;

  @FXML
  private Label fpsField;

  private final DepanFxWorkspace workspace;

  @Autowired
  public DepanFxNodeViewStatusPanel(DepanFxWorkspace workspace) {
    this.workspace = workspace;
  }

  public void setCameraControl(CameraControl cameraControl) {
    bind(cameraXField, cameraControl.cameraX);
    bind(cameraYField, cameraControl.cameraY);
    bind(cameraZField, cameraControl.cameraZ);
    bind(lookAtXField, cameraControl.lookAtX);
    bind(lookAtYField, cameraControl.lookAtY);
    bind(lookAtZField, cameraControl.lookAtZ);
    bind(zoomField, cameraControl.zoom);

    Timeline fpsTimeline = new Timeline(new KeyFrame(Duration.seconds(1),
        event -> {
          String rateFps = String.format("%1.4g", cameraControl.getFps());
          fpsField.setText(rateFps);
          } ));

    fpsTimeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
    fpsTimeline.play();
  }

  private void bind(Label statusField, SimpleDoubleProperty dataSrc) {
    statusField.textProperty().bind(dataSrc.asString());

    StringBinding sourceBinding = new FormatBinding(dataSrc);
    statusField.textProperty().bind(sourceBinding);
  }

  private static class FormatBinding extends StringBinding {

    private DoubleProperty dataSrc;

    public FormatBinding(DoubleProperty dataSrc) {
      super.bind(dataSrc);
      this.dataSrc = dataSrc;
    }

    @Override
    public void dispose() {
        super.unbind(dataSrc);
    }

    @Override
    protected String computeValue() {
      return String.format("%1.4g", dataSrc.doubleValue());
    }


    @Override
    public ObservableList<ObservableValue<?>> getDependencies() {
        return FXCollections.unmodifiableObservableList(
            FXCollections.singletonObservableList(dataSrc));
    }
  }
}
