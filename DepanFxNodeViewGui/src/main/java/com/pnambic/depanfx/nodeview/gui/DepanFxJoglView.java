package com.pnambic.depanfx.nodeview.gui;

import com.pnambic.depanfx.jogl.JoglModule;
import com.pnambic.depanfx.jogl.JoglCamera;

import com.pnambic.depanfx.jogl.shapes.SquareShape;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class DepanFxJoglView extends BorderPane {

  private static final Logger LOG =
      LoggerFactory.getLogger(DepanFxJoglView.class);

  private final JoglModule jogl;

  private ChangeListener<? super Bounds> sizeListener;

  private ScrollBar hScrollBar;

  private ScrollBar vScrollBar;

  private Pane viewport;

  public DepanFxJoglView(JoglModule jogl) {
    this.jogl = jogl;
  }

  public static DepanFxJoglView createJoglView() {
    return new DepanFxJoglView(new JoglModule());
  }

  public void activate() {
    Parent parent = getParent();
    Bounds bounds = parent.getBoundsInLocal();

    // Start small.  Resized by listener during add to children.
    Canvas canvas = jogl.prepareCanvas(
        bounds.getHeight() / 2, bounds.getWidth() / 2);

    sizeListener = new ChangeListener<Bounds>() {

      @Override
      public void changed(
          ObservableValue<? extends Bounds> observable,
          Bounds oldValue, Bounds newValue) {
        LOG.info("Resizing Jogl to WxH {} {}", newValue.getWidth(), newValue.getHeight());
        canvas.setHeight(newValue.getHeight());
        canvas.setWidth(newValue.getWidth());
      }
    };

    hScrollBar = createHScrollBar(bounds.getHeight());
    setBottom(hScrollBar);

    vScrollBar = createVScrollBar(bounds.getHeight());
    setRight(vScrollBar);

    viewport = new Pane();
    setCenter(viewport);

    viewport.boundsInLocalProperty().addListener(sizeListener);
    viewport.getChildren().add(canvas);

    jogl.demoDisplay();
    jogl.updateShape(this, new SquareShape(0.8f, 0.8f, 0.8f));
    jogl.start();
  }

  public void release() {
    jogl.stop();
    viewport.boundsInLocalProperty().removeListener(sizeListener);
    viewport.getChildren().clear();
    jogl.destroy();
  }

  public void close() {
    release();
  }

  public void updateLayoutX(double layoutX) {
    JoglCamera.CameraData cameraData = jogl.getCurrentCamera();
    jogl.dollyCamera(layoutX - cameraData.cameraX, 0.0d, 0.0d);
    LOG.info("X scroll to {}", layoutX);
  }

  public void updateLayoutY(double layoutY) {
    JoglCamera.CameraData cameraData = jogl.getCurrentCamera();
    jogl.dollyCamera(0.0d, layoutY - cameraData.cameraY, 0.0d);
    LOG.info("Y scroll to {}", layoutY);
  }

  private ScrollBar createHScrollBar(double width) {
    ScrollBar result = new ScrollBar();
    result.setOrientation(javafx.geometry.Orientation.HORIZONTAL);
    result.setMin(-100.0d);
    result.setMax(100.0d);
    result.setValue(0.0d);
    result.setPrefWidth(width);
    result.setBorder(null);
    result.valueProperty().addListener((observable, oldValue, newValue) -> {
        this.updateLayoutX(newValue.doubleValue()); });
    return result;
  }

    private ScrollBar createVScrollBar(double height) {
      ScrollBar result = new ScrollBar();
      result.setOrientation(javafx.geometry.Orientation.VERTICAL);
      result.setMin(-100.0d);
      result.setMax(100.0d);
      result.setValue(0.0d);
      result.setPrefHeight(height);
      result.valueProperty().addListener((observable, oldValue, newValue) -> {
        this.updateLayoutY(-newValue.doubleValue()); });
      return result;
    }
}
