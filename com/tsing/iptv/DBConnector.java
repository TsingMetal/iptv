package com.tsing.iptv;

public interface DBConnector extends MacWritingListener{

  @Override
  public void actionPerformed(SetEvent e);

  public String getMac(String Sn);

  public String getMacCrc(String Sn);
}
