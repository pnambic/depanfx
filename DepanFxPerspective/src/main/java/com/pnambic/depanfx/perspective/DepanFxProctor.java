package com.pnambic.depanfx.perspective;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Stream;

/**
 * Oversees input validation and testing.
 *
 * Different output and reporting mechanisms are available separately.
 */
public interface DepanFxProctor {

  public static interface ErrorInfo {

    String getSummary();

    String getDetails();
  }

  void addError(String summary, String details);

  String getSummaryText();

  String getDetailText();

  Stream<ErrorInfo> streamErrorInfo();

  boolean hasErrors();

  public static class Simple implements DepanFxProctor {

    public class SimpleInfo implements ErrorInfo {

      private final String summary;

      private final String details;

      public SimpleInfo(String summary, String details) {
        this.summary = summary;
        this.details = details;
      }

      @Override
      public String getSummary() {
        return summary;
      }

      @Override
      public String getDetails() {
        return details;
      }
    }

    private int errors;

    private List<ErrorInfo> messages = new ArrayList<>();

    @Override
    public boolean hasErrors() {
      return errors > 0;
    }

    @Override
    public void addError(String summary, String details) {
      bumpErrors();
      messages.add(new SimpleInfo(summary, details));
    }

    public List<ErrorInfo> getMessages() {
      return new ArrayList<>(messages);
    }

    protected void bumpErrors() {
      errors++;
    }

    @Override
    public String getSummaryText() {
      if (messages.isEmpty()) {
        return "No errors";
      }
      if (messages.size() > 1) {
        return "Multiple Errors";
      }
      return messages.get(0).getSummary();
    }

    @Override
    public String getDetailText() {
      StringJoiner joiner = new StringJoiner(System.lineSeparator());
      streamErrorInfo()
          .map(i -> i.getDetails())
          .forEach(joiner::add);
      return joiner.toString();
    }

    @Override
    public Stream<ErrorInfo> streamErrorInfo() {
      return messages.stream();
    }
  }
}
