package com.tsing.iptv;

public interface DBConnector extends MacWritingListener{

  @Override
  public void MacWritingPerformed(MacWritingEvent e);

  public String getMac(String Sn);

  public String getMacCrc(String Sn);
}
