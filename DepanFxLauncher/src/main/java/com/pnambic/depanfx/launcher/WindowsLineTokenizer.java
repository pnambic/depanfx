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
package com.pnambic.depanfx.launcher;

/**
 * Tokenize the lines written by the Windows launch batch files.
 */
class WindowsLineTokenizer {

  private static final char QUOTE_CHAR = '"';

  // Reverse slash - hsals.
  private static final char HSALS_CHAR = '\\';

  private final String source;

  private int parseIndex;

  private int startIndex;

  private int finalIndex;

  WindowsLineTokenizer(String source) {
    this.source = source;
    parseToken();
  }

  public boolean hasToken() {
    return startIndex < source.length();
  }

  public void advToken() {
    parseToken();
  }

  public String getToken() {
    return source.substring(startIndex, finalIndex);
  }

  private void parseToken() {
    if (parseIndex < source.length()) {
      parseChar();
      return;
    }
    startIndex = source.length();
    finalIndex = startIndex - 1;
  }

  private void parseChar() {// char srcChar) {
    char srcChar = source.charAt(parseIndex);
    if (QUOTE_CHAR == srcChar) {
      startIndex = parseIndex;
      parseIndex = endQuote(srcChar, parseIndex + 1);
      finalIndex = parseIndex;
      parseIndex = skipWhite(parseIndex);
      return;
    }
    if (!Character.isWhitespace(srcChar)) {
      startIndex = parseIndex;
      parseIndex = endToken(srcChar, parseIndex + 1);
      finalIndex = parseIndex;
      parseIndex = skipWhite(parseIndex);
      return;
    }
  }

  // Index of the first non-whitespace char after the whitespace block.
  private int skipWhite(int fromIndex) {
    int testIndex = fromIndex;
    while (testIndex < source.length()) {
      char testChar = source.charAt(testIndex);
      if (!Character.isWhitespace(testChar)) {
        return testIndex;
      }
      testIndex++;
    }
    return source.length();
  }

  // Index of the char after the end of the token.
  private int endToken(char srcChar, int fromIndex) {
    int testIndex = fromIndex;
    while (testIndex < source.length()) {
      char testChar = source.charAt(testIndex++);
      if (Character.isWhitespace(testChar)) {
        return testIndex;
      }
    }
    return source.length();
  }

  // Index of the char after the end of the quote.
  private int endQuote(char srcChar, int fromIndex) {
    int testIndex = fromIndex;
    while (testIndex < source.length()) {
      char testChar = source.charAt(testIndex++);
      if (QUOTE_CHAR == testChar) {
        return testIndex;
      }
      if (HSALS_CHAR == testChar) {
        testIndex++;
      }
    }
    return source.length();
  }
}
