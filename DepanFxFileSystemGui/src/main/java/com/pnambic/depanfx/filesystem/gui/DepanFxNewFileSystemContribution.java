package com.pnambic.depanfx.filesystem.gui;

import org.springframework.stereotype.Component;

import com.pnambic.depanfx.scene.plugins.DepanFxNewResourceContribution;

import javafx.scene.control.MenuItem;

@Component
public class DepanFxNewFileSystemContribution
    implements DepanFxNewResourceContribution {

  @Override
  public MenuItem createNewResourceMenuItem() {
    MenuItem result = new MenuItem("File System");
    result.setOnAction(null);
    return result;
  }
}
