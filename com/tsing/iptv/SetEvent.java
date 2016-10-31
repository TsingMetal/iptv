package com.tsing.iptv;

public class SetEvent extends Event {
  String cmdString;
  String mac;
  String sn;
  String status;

  public SetEvent(Object source, String cmdString, String mac, 
      String sn, String status) 
  {
    super(source);
    this.cmdString = cmdString;
    this.mac = mac;
    this.sn = sn;
    this.status = status;
  }
  

  public String getCmd() {
    return cmdString;
  }

  public String getMac() {
    return mac;
  }

  public String getSn() {
    return sn;
  }

  public String getStatus() {
    return status;
  }
}
