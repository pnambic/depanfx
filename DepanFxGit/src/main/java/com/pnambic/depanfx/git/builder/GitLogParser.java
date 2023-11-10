package com.pnambic.depanfx.git.builder;

import com.google.common.base.Splitter;

import java.util.Iterator;
import java.util.Optional;

class GitLogParser {

  private static final int SHA_1_MIN_LEN = 7;

  private static final int SHA_1_MAX_LEN = 40;

  private static final Splitter SPACE_SPLITTER = Splitter.on(' ');

  private String logLine;

  public void parseLine(String logLine) {
    this.logLine = logLine;
  }

  public String getFileName() {
    return logLine;
  }

  public boolean isFileName() {
    return parseCommitId().isEmpty();
  }

  public String getCommitId() {
    return parseCommitId().get();
  }

  public boolean isNewCommit() {
    return parseCommitId().isPresent();
  }

  private Optional<String> parseCommitId() {
    if (logLine.isEmpty()) {
      return Optional.empty();
    }
    // Not an initial hex character: not a commit id
    if (!isHexChar(logLine.charAt(0))) {
      return Optional.empty();
    }
    Iterator<String> segments = SPACE_SPLITTER.split(logLine).iterator();
    if (!segments.hasNext()) {
      return Optional.empty();
    }
    String lineId = segments.next();
    if (isValidCommitId(lineId)) {
      return Optional.of(lineId);
    }
    return Optional.empty();
  }

  private boolean isValidCommitId(String lineId) {
    if (lineId.length() < SHA_1_MIN_LEN) {
      return false;
    }
    if (lineId.length() > SHA_1_MAX_LEN) {
      return false;
    }
    for (char c : lineId.toCharArray()) {
      if (!isHexChar(c)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Git uses only lower case hex symbols.
   */
  private boolean isHexChar(char c) {
    switch (c) {
    case '0': case '1': case '2': case '3':
    case '4': case '5': case '6': case '7':
    case '8': case '9': case 'a': case 'b':
    case 'c': case 'd': case 'e': case 'f':
      return true;
    }
    return false;
  }
}
