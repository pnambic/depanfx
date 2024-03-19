package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import net.rgielen.fxweaver.core.FxmlView;
import com.pnambic.depanfx.jogl.JoglModule;

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
import javafx.util.Duration;

@Component
@FxmlView("node-view-status-panel.fxml")
public class DepanFxNodeViewStatusPanel {

  @FXML
  private Label cameraXField;

  @FXML
  private Label cameraYField;

  @FXML
  private Label cameraZField;

  @FXML
  private Label lookAtXField;

  @FXML
  private Label lookAtYField;

  @FXML
  private Label lookAtZField;

  @FXML
  private Label destinationField;

  @FXML
  private Label zoomField;

  @FXML
  private Label fpsField;

  private final DepanFxWorkspace workspace;

  private Timeline fpsTimeline;

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

    String initFps = String.format("%1.4g", (float) JoglModule.TARGET_FPS);
    fpsField.setText(initFps);

    int fpsRateSec =
        (JoglModule.FRAME_CNT + JoglModule.TARGET_FPS)
            / JoglModule.TARGET_FPS;
    fpsTimeline = new Timeline(new KeyFrame(Duration.seconds(fpsRateSec),
        event -> {
          String rateFps = String.format("%1.4g", cameraControl.getFps());
          fpsField.setText(rateFps);
          } ));

    fpsTimeline.setCycleCount(Timeline.INDEFINITE); // Run indefinitely
    fpsTimeline.play();
  }

  public void stop() {
    if (fpsTimeline != null) {
      fpsTimeline.stop();
      fpsTimeline = null;
    }
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
