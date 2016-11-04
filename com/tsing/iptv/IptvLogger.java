package com.tsing.iptv;

import java.util.LinkedHashMap;

public class IptvLogger implements Logger {
  private XmlWriter xmlWriter;
  private LinkedHashMap<String, String> map;
  
  public IptvLogger(XmlWriter writer) {
    xmlWriter = writer;
  }

  @Override
  public void MacWritingPerformed(MacWritingEvent e) {
    if ((e.getCmd() == "write_mac_to_stb" && e.getStatus() == "pass") 
        || e.getStatus() == "fail")
    {
      map = e.getResultMap(); // map initialized here
      log(map);
    } else {
      // do nothing;
    }
  }

  @Override
  public void log(LinkedHashMap<String, String> map) {
    XmlWriter.write(map);
  }
}
