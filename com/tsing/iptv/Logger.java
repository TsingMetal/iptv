package com.tsing.iptv;

import java.util.HashMap;

public interface Logger extends MacWritingListener {
  public void log(HashMap<String, String> result);
}
