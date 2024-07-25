package com.pnambic.depanfx.nodefilters.gui;

import com.pnambic.depanfx.nodefilters.tooldata.DepanFxBaseFilterData;
import com.pnambic.depanfx.scene.DepanFxContextMenuBuilder;
import com.pnambic.depanfx.scene.DepanFxDialogRunner;
import com.pnambic.depanfx.workspace.DepanFxWorkspace;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;

public interface DepanFxNodeFiltersDialogContribution {

  /**
   * True if the contribution is suitable for the filter.
   */
  boolean accepts(DepanFxBaseFilterData filter);

  /**
   *  A text used to order contributions in a display.
   */
  String getOrderKey();

  List<Class<?>> getContribTypes();

  TreeItem<DepanFxNodeFiltersTableMember> buildTableMember(
      DepanFxNodeFiltersTableMember parentMember,
      DepanFxBaseFilterData filter,
      DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry);

  void runSaveFilter(
      DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData saveFilter);

  Optional<? extends DepanFxBaseFilterData> runUpdateFilter(
      DepanFxDialogRunner dialogRunner, DepanFxBaseFilterData updateFilter);

  MenuItem appendCreateActionItem(
      DepanFxContextMenuBuilder builder,
      DepanFxWorkspace workspace,
      DepanFxDialogRunner dialogRunner,
      Scene scene,
      DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry,
      Consumer<DepanFxBaseFilterData> onAddFilter);

  public abstract class Basic<T extends DepanFxBaseFilterData>
      implements DepanFxNodeFiltersDialogContribution {

    private final String orderKey;

    private final String addLabel;

    private final Class<T> forType;

    public Basic(String orderKey, String addLabel, Class<T> forType) {
      this.orderKey = orderKey;
      this.addLabel = addLabel;
      this.forType = forType;
    }

    @Override
    public boolean accepts(DepanFxBaseFilterData filter) {
      return forType.isAssignableFrom(filter.getClass());
    }

    @Override
    public String getOrderKey() {
      return orderKey;
    }

    @Override
    public MenuItem appendCreateActionItem(
        DepanFxContextMenuBuilder builder,
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry,
        Consumer<DepanFxBaseFilterData> onAddFilter) {
      return builder.appendActionItem(addLabel,
          e -> runCreateFilter(
                    e, workspace, dialogRunner, scene, nodeFiltersDialogRegistry)
                .ifPresent(onAddFilter::accept));
    }

    @Override
    public List<Class<?>> getContribTypes() {
      return Collections.singletonList(forType);
    };

    protected T asType(DepanFxBaseFilterData filter) {
      return forType.cast(filter);
    }

    protected abstract Optional<? extends DepanFxBaseFilterData>
    runCreateFilter(
        ActionEvent e,
        DepanFxWorkspace workspace,
        DepanFxDialogRunner dialogRunner,
        Scene scene,
        DepanFxNodeFiltersDialogRegistry nodeFiltersDialogRegistry);
  }
}
