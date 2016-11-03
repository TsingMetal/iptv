package com.tsing.iptv;

public interface DBConnector {

  public String checkSN(String sn);

  public String getMac(String sn);

  public String getMacCRC(String sn);

  public int SNUsed(String sn);

  public boolean validate(String sn);
}
