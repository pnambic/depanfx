package com.pnambic.depanfx.perspective;

import com.pnambic.depanfx.workspace.DepanFxWorkspace;

/**
 * Base definitions for error reporting with a proctor.
 */
public abstract class DepanFxBaseDialog extends DepanFxWorkspaceDialog {

  public DepanFxBaseDialog(DepanFxWorkspace workspace) {
    super(workspace);
  }

  /**
   * The main label to show if the input contains an error.
   */
  protected abstract String getInputCheckFailureText();

  /**
   * All validation errors should to added to
   * the supplied test (@link #proctor).
   */
  protected abstract void checkInput(DepanFxProctor proctor);

  /**
   * If the input fails validation ({@link #checkInput(DepanFxProctor)}),
   * a user error dialog is shown before the method returns.
   */
  protected boolean hasInputErrors() {
    DepanFxProctor proctor = new DepanFxProctor.Simple();
    checkInput(proctor);
    if ((!proctor.hasErrors())) {
      return false;
    }
    DepanFxResourcePerspectives.errorAlert(proctor, getInputCheckFailureText());
    return true;
  }
}
