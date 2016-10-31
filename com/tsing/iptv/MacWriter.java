package com.tsing.iptv;

import java.net.*;
import java.util.HashMap;

/**
 * provide functions to:
 * 1, check Mac and SN with database and STB,
 * 2, erase Mac and SN from STB and 
 * 3, write Mac and SN to STB and 
 * 4, others
 * @author Tsing
 */ 
public class MacWriter {

  public static final int STBPORT = 1300; // STB port
  public static final int RECVPORT = 1301; // local port for receiving data
  private InetAddress ADDR;
  private DatagramSocket socket;
  private XmlParser xmlParser; // xmlParser is responsible for parsing cmd xml

  private ArrayList<MacWritingListener> listenerList;

  /**
   * initialize socket
   * pass a XmlParser instance of XmlParser to the constructor,
   * so this class could share it with others
   * @param XmlParser
   */
  public MacWriter(XmlParser parser) {
    xmlParser = parser;

    try {
      ADDR = InetAddress.getByName("225.0.0.1"); // broadcast address
      socket = new DatagramSocket(1301); // use UDP socket; bind port 1301
      socket.setSoTimeout(3000); // set time for timeout as 3 secs;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * check advanced sercurity before testing
   */
  public boolean checkAdc() {
    // unimplemented
    processEvent(new CheckAdcEvent(this, "check_adv_security", "pass"))
    return true;
  }
  
  /** 
   * compare Mac and SN with DB 
   * to check if both and crc of each are validate;
   * return true if all of those are validate, false if not
   */
  public boolean checkMacWithDB(String stbMac, String stbSN) {
    // unimplemented
    processEvent(new CheckMacWithDBEvent(
          this, "check_mac_with_db", stbMac, dbMac, stbSn, dbsn, "pass"));
    return true;
  }

  /**
   * erase Mac and SN from STB,
   * if successfully erased, return true, else return false
   */
  public boolean eraseMac() {
    // unimplemented
    processEvent(new EraseEvent(this, "erase", "pass");
    return true;
  }
  
  /**
   * get Mac and SN from STB;
   * retrun a map with values of Mac and SN
   */
  public HashMap<String, String> getMac() {
    // unimplemented
    processEvent(new GetEvent(this, "get", "pass");
    HashMap<String, String> map = null;
    return map;
  }

  /**
   * write Mac and SN to SBT;
   * return true if writing successfully
   */
  public boolean setMac(String Mac, String SN) {
    // unimplemented
    processEvent(new SetEvent(this, "set", Mac, SN, "pass"));
    return true;
  }

  /**
   * reboot SBT
   */
  public boolean reboot() {
    // unimplemented
    processEvent(new RebootEvent(this, "reboot", "pass"));
    return true;
  }

  /**
   * handle sending XML String cmd and
   * receicing XML string result from STB;
   * return XML string received from STB if query OK, 
   * else return warning message
   */
  public String getRet(String cmdXml) {
    try {
      DatagramPacket dp = new DatagramPacket(cmdXml.getBytes(),
         cmdXml.length(), ADDR, STBPORT);
      socket.send(dp); // send cmd xml to STB by multicasting

      // get result from STB
      byte[] buff = new byte[1024];
      DatagramPacket recvDp = new DatagramPacket(buff, 1024);
      socket.receive(recvDp);
      return new String(buff);
    } catch (Exception ex) {
      ex.printStackTrace();
      return null;
    }
  }
  
  /** add a listener */
  public void addMacWritingListener(MacWritingListener listener) {
    if (listenerList == null)
      listenerList = 
        new ArrayList<MacWritingListener>(3); // allow 3 listeners
    listenerList.add(listener);
  }

  // method for remove listeners omitted here

  /** fire Event */
  private void processEvent(Event e) {
    ArrayList list;

    synchronized (this) {
      if (listenerList == null) return;
      list = (ArrayList)listenerList.clone();
    }

    for (MacWritingListener listener : list)
      listener.actionPerformed(Event e);
  }
}
