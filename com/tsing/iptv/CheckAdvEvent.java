package com.tsing.iptv;

public class CheckAdvEvent extends Event {

  String cmdString;
  String status;

  public CheckAdvEvent(Object source, String cmd, String status) {
    super(source);
    this.cmdString = cmd;
    this.status = status;
  }

  public String getCmd() {
    return cmdString;
  }

  public String getStatus() {
    return status;
  }
}
