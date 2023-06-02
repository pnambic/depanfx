package com.pnambic.depanfx.scene.plugins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import javafx.scene.control.MenuItem;

@Component
public class DepanFxNewResourceRegistry {
  
  private final List<DepanFxNewResourceContribution> contributions;

  public DepanFxNewResourceRegistry(List<DepanFxNewResourceContribution> contributions) {
    this.contributions = contributions;
  }

  public Collection<MenuItem> buildNewResourceItems() {
    int contribCnt = contributions.size();
    if (contribCnt > 0) {
      List<MenuItem> result = new ArrayList<>(contribCnt);
      for (DepanFxNewResourceContribution contribution : contributions) {
        result.add(contribution.createNewResourceMenuItem());
      }
      return result;
    }
    return Collections.emptyList();
  };
}
