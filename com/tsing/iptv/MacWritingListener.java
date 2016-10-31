package com.tsing.iptv;

public interface MacWritingListener {
  public <E extends Event> void actionPerformed(E e);
}
