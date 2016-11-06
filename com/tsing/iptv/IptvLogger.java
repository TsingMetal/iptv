package com.tsing.iptv;

import java.util.LinkedHashMap;

public class IptvLogger implements Logger, MacWritingListener {
  XmlWriter xmlWriter;

  public IptvLogger(XmlWriter writer) {
    xmlWriter = writer;
  }

  @Override
  public void log(LinkedHashMap<String, String> result) {
    xmlWriter.write(result);
  }

  @Override
  public void macWritingPerformed(MacWritingEvent e) {
    if ((e.getCmd() == "write_mac_to_stb" && e.getStatus() == "pass")
        || e.getStatus() == "fail")
      log(e.getResultMap());
  }
}
