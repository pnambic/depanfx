package com.pnambic.depanfx.nodeview.layouts;

import com.pnambic.depanfx.nodeview.tooldata.DepanFxNodeViewLayoutData;
import com.pnambic.depanfx.nodeview.tooldata.DepanFxShiftLayoutData;
import com.pnambic.depanfx.workspace.projects.DepanFxBuiltInContribution;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.text.MessageFormat;

/**
 * Predefine a bunch of standard shift layouts.
 */
@Configuration
public class DepanFxShiftLayoutConfiguration {

  private int[] POS_X = new int[] { 1, 0, 0 };

  private int[] NEG_X = new int[] { -1, 0, 0 };

  private int[] POS_Y = new int[] { 0, 1, 0 };

  private int[] NEG_Y = new int[] { 0, -1, 0 };

  private int[] POS_Z = new int[] { 0, 0, 1 };

  private int[] NEG_Z = new int[] { 0, 0, -1 };

  public static final Path SHIFT_LAYOUT_PATH =
      DepanFxNodeViewLayoutData.LAYOUT_TOOL_PATH.resolve("Shift");

  // X Axis
  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftRightOne() {
    return buildShiftContribution(buildShift("right", 1, POS_X));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftRightFive() {
    return buildShiftContribution(buildShift("right", 5, POS_X));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftRightTen() {
    return buildShiftContribution(buildShift("right", 10, POS_X));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftLeftOne() {
    return buildShiftContribution(buildShift("left", 1, NEG_X));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftLeftFive() {
    return buildShiftContribution(buildShift("left", 5, NEG_X));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftLeftTen() {
    return buildShiftContribution(buildShift("left", 10, NEG_X));
  }

  // Y Axis
  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftUpOne() {
    return buildShiftContribution(buildShift("up", 1, POS_Y));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftUpFive() {
    return buildShiftContribution(buildShift("up", 5, POS_Y));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftUpTen() {
    return buildShiftContribution(buildShift("up", 10, POS_Y));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftDownOne() {
    return buildShiftContribution(buildShift("down", 1, NEG_Y));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftDownFive() {
    return buildShiftContribution(buildShift("down", 5, NEG_Y));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftDownTen() {
    return buildShiftContribution(buildShift("down", 10, NEG_Y));
  }

  // Z Axis
  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftPullOne() {
    return buildShiftContribution(buildShift("pull", 1, POS_Z));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftPullFive() {
    return buildShiftContribution(buildShift("pull", 5, POS_Z));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftPullTen() {
    return buildShiftContribution(buildShift("pull", 10, POS_Z));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftPushOne() {
    return buildShiftContribution(buildShift("push", 1, NEG_Z));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftPushFive() {
    return buildShiftContribution(buildShift("push", 5, NEG_Z));
  }

  @Bean
  public DepanFxBuiltInContribution<DepanFxShiftLayoutData> shiftPushTen() {
    return buildShiftContribution(buildShift("push", 10, NEG_Z));
  }

  /////////////////////////////////////
  // Helpers

  private DepanFxBuiltInContribution<DepanFxShiftLayoutData> buildShiftContribution(
      DepanFxShiftLayoutData toolData) {

    Path shiftPath = SHIFT_LAYOUT_PATH.resolve(toolData.getToolName());
    return new DepanFxBuiltInContribution.Simple<>(shiftPath, toolData);
  }

  private DepanFxShiftLayoutData buildShift(
      String axisLabel, int axisAmount, int[] axisDirection) {

    return new DepanFxShiftLayoutData(
        buildToolName(axisLabel, axisAmount),
        buildToolDescription(axisLabel, axisAmount),
        axisAmount * axisDirection[0],
        axisAmount * axisDirection[1],
        axisAmount * axisDirection[2]);
  }

  private String buildToolName(String axis, int amount) {
    return MessageFormat.format("{0} {1}", axis, amount);
  }

  private String buildToolDescription(String axis, int amount) {
    return MessageFormat.format("Shift {0} by {1}.", axis, amount);
  }
}
