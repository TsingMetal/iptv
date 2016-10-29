package com.tsing.iptv;

import java.net.*;
import java.util.HashMap;

/**
 * provide functions to:
 * 1, check Mac and SN with database and STB,
 * 2, erase Mac and SN from STB and 
 * 3, write Mac and SN to STB and 
 * 4, others
 */
public class MacWriter {

  public static final int STBPORT = 1300; // STB port
  public static final int RECVPORT = 1301; // local port for receiving data
  private InetAddress ADDR;
  private DatagramSocket socket;
  private Log log; // take a Log object to log informations
  private DBConnector dbConnector; // dbConnector is responsible for 
                                   // geting data from db
  private XmlParser xmlParser; // xmlParser is responsible for parsing cmd xml

  /**
   * initialize socket
   */
  public MacWriter() {
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
  public boolean checkAdcSercurity() {
    // unimplemented
    return true;
  }
  
  /** 
   * compare Mac and SN with DB 
   * to check if both and crc of each are validate;
   * return true if all of those are validate, false if not
   */
  public boolean checkMacWithDB(String Mac, String SN) {
    // unimplemented
    return true;
  }

  /**
   * erase Mac and SN from STB,
   * if successfully erased, return true, else return false
   */
  public boolean eraseMac() {
    // unimplemented
    return true;
  }
  
  /**
   * get Mac and SN from STB;
   * retrun a map with values of Mac and SN
   */
  public HashMap<String, String> getMac() {
    // unimplemented
    HashMap<String, String> map = null;
    return map;
  }

  /**
   * write Mac and SN to SBT;
   * return true if writing successfully
   */
  public boolean writeMac(String Mac, String SN) {
    // unimplemented
    return true;
  }

  /**
   * reboot SBT
   */
  public boolean reboot() {
    // unimplemented
    return true;
  }

  /**
   * handle sending XML String cmd and
   * receicing XML string result from STB;
   * return XML string received from STB if query OK, 
   * else return warning message
   */
  public String handleCmd(String cmdXml) {
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
      return "operation failed";
    }
  }
}
