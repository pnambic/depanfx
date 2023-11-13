package com.pnambic.depanfx.scene.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javafx.scene.control.MenuItem;

@Component
public class DepanFxNewResourceRegistry {

  private final List<DepanFxNewResourceContribution> resourceContribs;

  private final List<DepanFxNewAnalysisContribution> analysisContribs;

  @Autowired
  public DepanFxNewResourceRegistry(
      List<DepanFxNewResourceContribution> resourceContribs,
      List<DepanFxNewAnalysisContribution> analysisContribs) {
    this.resourceContribs = resourceContribs;
    this.analysisContribs = analysisContribs;
  }

  public Collection<MenuItem> buildNewResourceItems() {
    int contribCnt = resourceContribs.size();
    if (contribCnt > 0) {
      List<MenuItem> result = new ArrayList<>(contribCnt);
      for (DepanFxNewResourceContribution contribution : resourceContribs) {
        result.add(contribution.createNewResourceMenuItem());
      }
      result.sort((a,b) -> a.getText().compareTo(b.getText()));
      return result;
    }
    return Collections.emptyList();
  };

  public Collection<MenuItem> buildNewAnalysisItems() {
    int contribCnt = analysisContribs.size();
    if (contribCnt > 0) {
      List<MenuItem> result = new ArrayList<>(contribCnt);
      for (DepanFxNewAnalysisContribution contribution : analysisContribs) {
        result.add(contribution.createNewResourceMenuItem());
      }
      result.sort((a,b) -> a.getText().compareTo(b.getText()));
      return result;
    }
    return Collections.emptyList();
  };
}
