package com.tsing.iptv;

public class CheckMacWithDBEvent extends Event {
  String cmdString;
  String stbMac;
  String dbMac;
  String stbSn;
  String status;
  String errInfo;

  public CheckMacWithDBEvent(Object source,
                             String cmd,
                             String stbSn,
                             String stbMac,
                             String dbMac,
                             String status)
  {
    super(source);
    this.cmdString = cmd;
    this.stbSn = stbSn;
    this.stbMac = stbMac;
    this.dbMac = dbMac;
    this.status = status;
  }

  public String getCmd() {
    return cmdString;
  }

  public String getStbSn() {
    return stbSn;
  }

  public String getStbMac() {
    return stbMac;
  }

  public String getdbMac() {
    return dbMac;
  }

  public String getStatus() {
    return status;
  }
