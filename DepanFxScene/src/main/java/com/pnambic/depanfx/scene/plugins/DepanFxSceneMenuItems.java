/*
 * Copyright 2025 The Depan Project Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.pnambic.depanfx.scene.plugins;

/**
 * Unfortunately, the FXML parser want's an attribute value that starts with
 * a literal quote(i.e. {@code "}, so it is hard to use these constants in
 * scence.fxml's menu definition.  They just have to match.
 */
public class DepanFxSceneMenuItems {

  public static final String FILE_OPEN_ITEM = "fileOpenItem";

  public static final String FILE_OPEN_PROJECT_ITEM = "fileOpenProjectItem";

  public static final String FILE_SAVE_ITEM = "fileSaveItem";

  public static final String FILE_SAVE_AS_ITEM = "fileSaveAsItem";

  public static final String FILE_SAVE_ALL_ITEM = "fileSaveAllItem";

  public static final String FILE_IMPORT_ITEM = "fileImportItem";

  public static final String FILE_EXPORT_ITEM = "fileExportItem";

  public static final String FILE_PRINT_ITEM = "filePrintItem";

  public static final String EDIT_UNDO_ITEM = "editUndoItem";

  public static final String EDIT_REDO_ITEM = "editRedoItem";

  public static final String EDIT_CUT_ITEM = "editCutItem";

  public static final String EDIT_COPY_ITEM = "editCopyItem";

  public static final String EDIT_PASTE_ITEM = "editPasteItem";

  public static final String SELECTION_ALL_ITEM = "SelectionAllItem";

  public static final String SELECTION_NONE_ITEM = "SelectionNoneItem";

  public static final String SELECTION_INVERT_ITEM = "SelectionInvertItem";

  public static final String VIEW_PANELS = "ViewPanelsItem";

  public static final String HELP_WELCOME_TIME = "HelpWelcomeItem";

  public static final String HELP_ABOUT_ITEM = "HelpAboutItem";
}
