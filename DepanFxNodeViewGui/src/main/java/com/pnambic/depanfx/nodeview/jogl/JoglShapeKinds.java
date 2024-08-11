package com.pnambic.depanfx.nodeview.jogl;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public enum JoglShapeKinds {

  SQUARE {

    @Override
    public Shape buildAwtShape() {
        return new Rectangle2D.Float(-1.0f, -1.0f, 2.0f, 2.0f);
    }
  },

  RECTANGLE {

    @Override
    public Shape buildAwtShape() {
        return new Rectangle2D.Float(-1.25f, -0.75f, 2.5f, 1.5f);
    }
  },

  ROUNDED_RECTANGLE {

    @Override
    public Shape buildAwtShape() {
        return new RoundRectangle2D.Float(
            -1.25f, -0.75f, 2.5f, 1.5f, 0.2f, 0.3f);
    }
  },

  CIRCLE {

    @Override
    public Shape buildAwtShape() {
        return new Ellipse2D.Float(-1.0f, -1.0f, 2.0f, 2.0f);
    }
  },

  ELLIPSE {

    @Override
    public Shape buildAwtShape() {
        return new Ellipse2D.Float(-1.3f, -0.8f, 2.6f, 1.6f);
    }
  },

  HEXAGON {

    @Override
    public Shape buildAwtShape() {
      Path2D.Float result =
          new Path2D.Float(java.awt.geom.Path2D.WIND_NON_ZERO, 7);
      float radius = 1.2f;
      double stepSize = 2.0f * Math.PI / 6;
      result.moveTo(radius, 0.0f);
      for (int i = 1; i < 6; i++) {
          double xPos = radius * Math.cos(i * stepSize);
          double yPos = radius * Math.sin(i * stepSize);
          result.lineTo(xPos, yPos);
      }
      result.closePath();
      return result;
    }
  };

  public abstract Shape buildAwtShape();
}
