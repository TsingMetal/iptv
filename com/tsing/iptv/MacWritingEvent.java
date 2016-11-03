package com.tsing.iptv;

import java.util.HashMap;

public class MacWritingEvent extends java.util.EventObject {

  // use an HashMap to record data MacWritingListeners need:
  HashMap<String, String> result = new HashMap<String, String>();
  
  public MacWritingEvent(Object source,
      HashMap<String, String> map)
  {
    super(source);
    this.result = map;
  }

  public String getCmd() {
    return result.containsKey("cmd")?
      result.get("cmd") : "N/A";
  }

  public String getStatus() {
    return result.containsKey("status")?
      result.get("status") : "N/A";
  }

  public String getStbSn() {
    return (result.containsKey("stb_sn"))?
      result.get("stb_sn") : "N/A";
  }

  public String getStbMac() {
    return (result.containsKey("stb_mac"))?
      result.get("stb_mac") : "N/A";
  }

  public String getStbMacCrc() {
    return (result.containsKey("stb_mac_crc"))?
      result.get("stb_mac_crc") : "N/A";
  }

  public String getDbMac() {
    return (result.containsKey("db_mac"))?
      result.get("db_mac") : "N/A";
  }

  public String getDbMacCrc() {
    return result.containsKey("db_mac_crc")?
      result.get("db_mac_crc") : "N/A";
  }      

  public String getRetXml() {
    return result.containsKey("ret_xml")?
      result.get("ret_xml") : "N/A";
  }

	public HashMap<String, String> getResultMap() {
		return result;
	}
}
