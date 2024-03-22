package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.jogl.JoglModule;
import com.pnambic.depanfx.jogl.JoglShape;
import com.pnambic.depanfx.jogl.shapes.SquareShape;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewCameraData;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;

import net.rgielen.fxweaver.core.FxControllerAndView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class DepanFxJoglView extends BorderPane {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxJoglView.class);

  private final JoglModule jogl;

  private final CameraControl cameraControl;

  private final DepanFxDialogRunner dialogRunner;

  private ScrollBar hScrollBar;

  private ScrollBar vScrollBar;

  private FxControllerAndView<DepanFxNodeViewStatusPanel, Node> statusPanel;

  private Pane viewport;

  public DepanFxJoglView(
      JoglModule jogl, DepanFxDialogRunner dialogRunner) {
    this.jogl = jogl;
    this.dialogRunner = dialogRunner;
    this.cameraControl = new CameraControl(jogl);
    DepanFxNodeViewKeyActions.addActions(jogl, cameraControl);
  }

  public static DepanFxJoglView createJoglView(
      DepanFxNodeViewCameraData cameraInfo,
      DepanFxDialogRunner dialogRunner) {
    return new DepanFxJoglView(
        new JoglModule(cameraInfo.getJoglCameraData()),
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

    viewport = createJoglViewport();
    setCenter(viewport);

    jogl.demoDisplay();
    updateShape(this, new SquareShape(0.8f, 0.8f, 0.8f));

    // JogAmp Bug #1504: Should start jogl rendering here,
    // not in viewport layout children.
    // jogl.start();
  }

  public void release() {
    jogl.stop();
    viewport.getChildren().clear();
    jogl.destroy();
    statusPanel.getController().stop();
  }

  public void close() {
    release();
  }

  public void updateShape(Object key, JoglShape shape) {
    jogl.updateShape(key, shape);
  }

  public DepanFxNodeViewCameraData getCameraData() {
    return DepanFxNodeViewCameraData.ofJoglCamera(
        jogl.getCurrentCamera());
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

  private Pane createJoglViewport() {
    Canvas canvas = jogl.createCanvas();
    return new NewtCanvasPane(canvas);
  }

  private void updateLayoutX(double deltaX) {
    cameraControl.dolly(deltaX, 0.0d, 0.0d);
    LOG.info("X scroll by {}", deltaX);
  }

  private void updateLayoutY(double deltaY) {
    cameraControl.dolly(0.0d, deltaY, 0.0d);
    LOG.info("Y scroll by {}", deltaY);
  }

  /**
   * Handles details of packaging the NewtCanvas.
   *
   * Encapsulates much of the work around for JogAmp Bug #1504.
   */
  public class NewtCanvasPane extends Pane {

    private final Canvas canvas;

    private boolean firstLayout = true;

    public NewtCanvasPane(Canvas canvas) {
      this.canvas = canvas;

      setPrefSize(0.0d, 0.0d);
      setMinSize(0.0d, 0.0d);
    }

    @Override
    protected void layoutChildren() {
      super.layoutChildren();

      if (firstLayout) {
        firstLayout = false;
        layoutJogAmpBug1504();
      }
    }

    private void layoutJogAmpBug1504() {
      double width = getWidth();
      double height = getHeight();

      canvas.setWidth(width);
      canvas.setHeight(height);

      widthProperty().addListener((obs, oldVal, newVal) ->
          canvas.setWidth(newVal.doubleValue()));

      heightProperty().addListener((obs, oldVal, newVal) ->
          canvas.setHeight(newVal.doubleValue()));

      // Work around #1504 with late reparent the NewtCanvas pane.
      getChildren().add(canvas);

      // Without JogAmp Bug #1504, this should happen in activate().
      // Canvas canvas = prepareCanvasBug1504(jogl);
      jogl.start();
    }
  }
}
