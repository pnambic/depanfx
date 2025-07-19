package com.pnambic.depanfx.nodeview.jogl;

import com.pnambic.depanfx.jogl.JoglModule;
import com.pnambic.depanfx.jogl.JoglMouseActionListener;
import com.pnambic.depanfx.jogl.JoglShape;
import com.pnambic.depanfx.nodeview.gui.CameraControl;
import com.pnambic.depanfx.nodeview.gui.DepanFxNodeViewStatusPanel;
import com.pnambic.depanfx.nodeview.gui.FlightController;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewCameraData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;

import net.rgielen.fxweaver.core.FxControllerAndView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class JoglPane extends BorderPane {

  private static final Logger LOG =
      LoggerFactory.getLogger(JoglPane.class);

  private final JoglModule jogl;

  private final CameraControl cameraControl;

  private final FlightController flightControl;

  private final DepanFxDialogRunner dialogRunner;

  private ScrollBar hScrollBar;

  private ScrollBar vScrollBar;

  private FxControllerAndView<DepanFxNodeViewStatusPanel, Node> statusPanel;

  private Pane viewport;

  public JoglPane(JoglModule jogl, DepanFxDialogRunner dialogRunner) {
    this.jogl = jogl;
    this.dialogRunner = dialogRunner;
    this.cameraControl = new CameraControl(jogl);
    this.flightControl = new FlightController(jogl, cameraControl);
    flightControl.addActions();
  }

  public static JoglPane createJoglPane(
      DepanFxNodeViewCameraData cameraInfo,
      DepanFxDialogRunner dialogRunner) {
    return new JoglPane(
        new JoglModule(JoglCameras.of(cameraInfo)),
        dialogRunner);
  }

  public void activate() {
    Parent parent = getParent();
    Bounds bounds = parent.getBoundsInLocal();

    hScrollBar = createHScrollBar(bounds.getWidth());
    setBottom(hScrollBar);

    vScrollBar = createVScrollBar(bounds.getHeight());
    setRight(vScrollBar);

    statusPanel = createStatusPanel();
    setTop(statusPanel.getView().get());

    viewport = getJoglViewport();
    setCenter(viewport);

    jogl.demoDisplay();

    flightControl.start();

    // JogAmp Bug #1504: Should start jogl rendering here,
    // not in viewport layout children.
    // jogl.start();
  }

  public void release() {
    LOG.info("JoglPane release");
    jogl.stop();

    flightControl.stop();

    // May not have allocated if never activated.
    if (statusPanel != null) {
      statusPanel.getController().stop();
    }
  }

  public void close() {
    release();
    jogl.destroy();
  }

  public void addMouseActionListener(JoglMouseActionListener listener) {
    jogl.addMouseActionListener(listener);
  }

  public JoglShape getShape(Object key) {
    return jogl.getShape(key);
  }

  public void updateShape(Object key, JoglShape shape) {
    jogl.updateShape(key, shape);
  }

  public DepanFxNodeViewCameraData getCameraData() {
    return JoglCameras.of(jogl.getCurrentCamera());
  }

  public void dolly(double dollyX, double dollyY, double dollyZ ) {
    cameraControl.dolly(dollyX, dollyY, dollyZ);
  }

  public BufferedImage takeScreenshot() {
    return jogl.takeScreenshot();
  }

  private Pane getJoglViewport() {
    return jogl.getCanvasPane();
  }

  private ScrollBar createHScrollBar(double width) {
    ScrollBar result = new ScrollBar();
    result.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
    result.setPrefWidth(width);
    result.setMin(-100.0d);
    result.setMax(100.0d);
    result.setValue(cameraControl.cameraX.get());

    // X-Axis is positive to right (+).
    cameraControl.cameraX.addListener((observable, oldValue, newValue) ->
      result.valueProperty().setValue(newValue.doubleValue()));

    result.valueProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue.doubleValue() != cameraControl.cameraX.get() ) {
          updateLayoutX(newValue.doubleValue() - oldValue.doubleValue());
        }
    });
    return result;
  }

  private ScrollBar createVScrollBar(double height) {
    ScrollBar result = new ScrollBar();
    result.setOrientation(javafx.geometry.Orientation.VERTICAL);
    result.setMin(-100.0d);
    result.setMax(100.0d);
    result.setValue(0.0d);
    result.setPrefHeight(height);

    // Y-Axis is positive to down (-).
    cameraControl.cameraY.addListener((observable, oldValue, newValue) ->
        result.valueProperty().setValue(-newValue.doubleValue()));

    result.valueProperty().addListener((observable, oldValue, newValue) -> {
      if (newValue.doubleValue() != -cameraControl.cameraY.get()) {
        this.updateLayoutY(oldValue.doubleValue() - newValue.doubleValue());
      }
    });
    return result;
  }

  private FxControllerAndView<DepanFxNodeViewStatusPanel, Node> createStatusPanel() {
    FxControllerAndView<DepanFxNodeViewStatusPanel, Node> result =
        dialogRunner.weaveFxmlView(DepanFxNodeViewStatusPanel.class);
    result.getController().setCameraControl(cameraControl);
    return result;
  }

  private void updateLayoutX(double deltaX) {
    cameraControl.dolly(deltaX, 0.0d, 0.0d);
    LOG.info("X scroll by {}", deltaX);
  }

  private void updateLayoutY(double deltaY) {
    cameraControl.dolly(0.0d, deltaY, 0.0d);
    LOG.info("Y scroll by {}", deltaY);
  }
}
