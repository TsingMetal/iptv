package com.tsing.iptv;

import java.net.*;
import java.util.HashMap;
import java.util.ArrayList;

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
  private DBConnector dbConnector;
  private ArrayList<MacWritingListener> listenerList;

  /**
   * initialize socket
   * pass a XmlParser instance of XmlParser to the constructor,
   * so this class could share it with others
   * @param XmlParser, DBConnector
   */
  public MacWriter(XmlParser parser, DBConnector connector) {
    xmlParser = parser;
    dbConnector = connector;
    try {
      ADDR = InetAddress.getByName("225.0.0.1"); // broadcast address
      socket = new DatagramSocket(1301); // use UDP socket; bind port 1301
      socket.setSoTimeout(1000); // set time for timeout as 1 sec;
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }
  
  /**
   * check advanced sercurity before testing
   */
  public boolean checkAdc() {
    // unimplemented
    HashMap<String, String> result = new HashMap<String, String>();
    processEvent(new MacWritingEvent(this, result));
    return true;
  }
  
  /** 
   * compare Mac and SN with DB 
   * to check if both and crc of each are validate;
   * return true if all of those are validate, false if not
   */
  public boolean checkMacWithDB(String stbMac, String stbSN) {
    // unimplemented
    HashMap<String, String> result = new HashMap<String, String>();
    processEvent(new MacWritingEvent(this, result));
    return true;
  }

  /**
   * erase Mac and SN from STB,
   * if successfully erased, return true, else return false
   */
  public boolean eraseMac() {
    // unimplemented
    HashMap<String, String> result = new HashMap<String, String>();
    processEvent(new MacWritingEvent(this, result));
    return true;
  }
  
  /**
   * get Mac and SN from STB;
   * retrun a map with values of Mac and SN
   */
  public HashMap<String, String> getMac() {
    // unimplemented
    HashMap<String, String> result = new HashMap<String, String>();
    processEvent(new MacWritingEvent(this, result));
    HashMap<String, String> map = null;
    return map;
  }

  /**
   * write Mac and SN to SBT;
   * return true if writing successfully
   */
  public boolean setMac(String Mac, String SN) {
    // unimplemented
    HashMap<String, String> result = new HashMap<String, String>();
    processEvent(new MacWritingEvent(this, result));
    return true;
  }

  /**
   * reboot SBT
   */
  public boolean reboot() {
    // unimplemented
    HashMap<String, String> result = new HashMap<String, String>();
    processEvent(new MacWritingEvent(this, result));
    return true;
  }

  /**
   * handle sending XML String cmd and
   * receicing XML string result from STB;
   * return XML string received from STB if query OK, 
   * else return warning message
   */
  public String getRet(String cmdXml) {
    HashMap<String, String> result = new HashMap<String, String>();
    String cmd = "connect_to_SBT";
    int retry = 0; //record retry times;
    DatagramPacket dp = new DatagramPacket(cmdXml.getBytes(),
       cmdXml.length(), ADDR, STBPORT);
    for (int i = 0; i < 5; i++) { // if fails, retry 5 times
      try {
        socket.send(dp); // send cmd xml to STB by multicasting
        System.out.println("dp sent"); /* for debugging */

        // get result from STB:
        byte[] buff = new byte[1024];
        DatagramPacket recvDp = new DatagramPacket(buff, 1024);
        socket.receive(recvDp);
        String ret = new String(buff);
        processEvent(new MacWritingEvent(this, result));
        return ret;
      } catch (Exception ex) {
        ex.printStackTrace();
        retry += 1;
        System.out.println("retry+" + retry); // for debugging
        result.put("retry", new Integer(retry).toString());
        processEvent(new MacWritingEvent(this, result));
      }
    } ///~ tested OK; date: Wed  2 Nov 08:40:17 CST 2016

    return null;
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
  private void processEvent(MacWritingEvent e) {
    ArrayList<MacWritingListener> listeners;

    synchronized (this) {
      if (listenerList == null) return;
      listeners = (ArrayList<MacWritingListener>)listenerList.clone();
    }

    for (MacWritingListener listener : listeners)
      listener.MacWritingPerformed(e);
  }
}
