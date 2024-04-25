package com.pnambic.depanfx.perspective;

import com.google.common.base.Strings;

public class DepanFxDialogChecks {

  private DepanFxDialogChecks() {
    // Avoid instantiation.
  }

  public static void checkDestinationFile(
      DepanFxProctor proctor, String source) {
    if (Strings.isNullOrEmpty(source)) {
      proctor.addError("Destination field is not usable",
          "Blank value for destination field");
    }
  }
}
