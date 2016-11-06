package com.tsing.iptv;

import java.net.*;
import java.util.LinkedHashMap;
import java.util.ArrayList;

/**
 * compile OK : 2016年 11月 04日 星期五 00:15:43 CST
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
   * initialize socket;
   * pass a XmlParser instance of XmlParser to the constructor 
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
  public boolean checkAdv() {
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("cmd", "check_adv_security");
		
		String cmdXml = CmdXml.CHECK_ADV_XML;
		String retXml = getRet(cmdXml); // send request to stb and get result
		xmlParser.parse(retXml);  // parse result returned from stb;
		String advSecurity = xmlParser.getValue("adv_security"); // get value
		if (advSecurity == "enable") {
			result.put("status", "pass");
			processEvent(new MacWritingEvent(this, result));
			return true;
		} else {
			result.put("statas", "fail");
			processEvent(new MacWritingEvent(this, result));
			return false;
		}
  } ///^ untested
  
	/** enable advanced security function of stb */
	public boolean setAdv() {
		LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("cmd", "enable_adv_security");

		String cmdXml = CmdXml.SET_ADV_XML;
		String retXml = getRet(cmdXml); // send cmd and get returned data
		xmlParser.parse(retXml); //parse result returned from stb;
		String status = xmlParser.getValue("result");
		if (status == "ok") {
			result.put("statas", "pass");
			processEvent(new MacWritingEvent(this, result));
			return true;
		} else { // if status == "failure"
			result.put("statas", "fail");
			result.put("err_info", xmlParser.getValue("error"));
			processEvent(new MacWritingEvent(this, result));
			return false;
		}
	} ///^ untested

  /** 
   * compare Mac and SN with DB; 
   * to check if both and crc of each are validate;
   * return true if all of those are validate, false if not;
	 * @param stbMac String
	 * @param stbSN String
   */
  public boolean checkMacWithDB(String stbMac, String stbSN) { 
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("cmd", "check_mac_with_DB");

    String stbMacCRC = getCRC(stbMac);
    if (isValidate(stbSN, stbMac, stbMacCRC)) {
			result.put("statas", "pass");
      processEvent(new MacWritingEvent(this, result));
      return true;
    } else {
			result.put("statas", "fail");
      processEvent(new MacWritingEvent(this, result));
      return false;
		}
  } ///^ untested

  /**
   * erase Mac and SN from STB;
   * if successfully erased, return true, else return false
   */
  public boolean eraseMac() {
    // unimplemented
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("cmd", "erase_mac");

		LinkedHashMap<String, String> ret = getMac();
		String mac = ret.get("mac"); // get mac for recirling before being erased
		String sn = ret.get("sn");
		result.put("stb_sn", sn); // record mac and sn
		result.put("stb_mac", mac);
		if (dbConnector.checkSN(sn) == "used") {
			// inform DB to recircle mac mapping this sn
			try {
				dbConnector.validate("sn");
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

		// continue erasing even not successfully recircled; need to solve
		String cmdXml = CmdXml.ERASE_XML;
		String retXml = getRet(cmdXml);
		xmlParser.parse(retXml);
		String statas = xmlParser.getValue("result");
		if (statas == "ok") {
			result.put("statas", "pass");
			processEvent(new MacWritingEvent(this, result));
			return true;
		} else {
			result.put("statas", "fail");
			processEvent(new MacWritingEvent(this, result));
			return false;
		}
	} ///^ untested
  
  /**
   * get Mac and SN from STB;
   * retrun a map with values of Mac and SN
   */
  public LinkedHashMap<String, String> getMac() {
    // unimplemented
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("cmd", "get_mac_from_stb");

		// use an LinkedHashMap to receive returned sn and mac
    LinkedHashMap<String, String> map = null;

		String cmdXml = CmdXml.GET_XML;
		String retXml = getRet(cmdXml);
		xmlParser.parse(retXml);
		if (xmlParser.getValue("sn") != null && 
				xmlParser.getValue("mac") != null) {
			String sn = xmlParser.getValue("sn");
			String mac = xmlParser.getValue("mac");
			map.put("sn", sn); 
			map.put("mac", mac);
			result.put("statas", "pass");
			processEvent(new MacWritingEvent(this, result));
		} else {
			result.put("status", "fail");
			processEvent(new MacWritingEvent(this, result));
			return null;
		}
		return null;
	} ///^ untested

  /**
   * write Mac and SN to STB;
	 * THE MOST IMPORTANT PART OF THE WHOLE TEST;
   * return true if writing successfully;
	 * arguments are passed in through UI's JTextField using barcode scanner;
	 * @param mac String
	 * @param sn String
   */
  public boolean setMac(String mac, String sn) {
    // unimplemented
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
		result.put("cmd", "write_mac_to_stb");

		String macCRC = getCRC(mac);
		String snCRC = getCRC(sn);

		if (checkMacWithDB(mac, sn)) {
			String cmdXml = String.format(CmdXml.SET_XML, mac, macCRC, sn, snCRC);
			String retXml = getRet(cmdXml);
			xmlParser.parse(retXml);

			if (xmlParser.getValue("result") == "ok") {
				result.put("statas", "pass");
				result.put("mac", mac);
				result.put("mac_crc", macCRC);
				result.put("sn", sn);
				result.put("sn_crc", snCRC);
				processEvent(new MacWritingEvent(this, result));
				dbConnector.SNUsed(sn); // inform DB the sn has been successfully used
				// rebootSTB() 
				return true;
			} else {
				result.put("statas", "fail");
				result.put("err_info", xmlParser.getValue("error"));
				processEvent(new MacWritingEvent(this, result));
				return false;
			}
		} //else : handled by UI

		return false;
  }

  /**
   * reboot STB 
   */
  public boolean rebootSTB() {
    // unimplemented
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
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
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    // sapces ares intended here and will be trimed when logging:
    result.put("cmd", "   connect_to_stb");
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
        result.put("ret_xml", ret);
        result.put("status", "pass");
        processEvent(new MacWritingEvent(this, result));
        return ret;
      } catch (Exception ex) {
        ex.printStackTrace();
        retry += 1;
        System.out.println("retry+" + retry); // for debugging
        result.put("status", "retry+"+new Integer(retry).toString());
        processEvent(new MacWritingEvent(this, result));
      }
    } 

    result.put("statas", "fail");
    processEvent(new MacWritingEvent(this, result));
    return null;
  }///~ tested OK; date: Wed  2 Nov 08:40:17 CST 2016
  
  public boolean isValidate(String sn, String stbMac, String stbMacCRC) {
    LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
    result.put("cmd", "connect_to_DB");
    try {
      if (dbConnector.checkSN(sn) == "validate") { //check by UI;not neccesary
        String mac = dbConnector.getMac(sn);
        String macCRC = dbConnector.getMacCRC(sn);
        result.put("statas", "pass");
        processEvent(new MacWritingEvent(this, result));
        if (mac == stbMac && macCRC == stbMacCRC) {
          return true;
        } else {
          LinkedHashMap<String, String> map = 
						new LinkedHashMap<String, String>();
          map.put("cmd", "check_mac_with_DB");
          map.put("status", "fail");
          map.put("stb_sn", sn);
          map.put("stb_mac", stbMac);
          map.put("db_mac", mac);
          map.put("stb_mac_crc", stbMacCRC);
          map.put("db_mac_crc", macCRC);
          processEvent(new MacWritingEvent(this, map));
          
          return false;
        }
			}
    } catch (Exception ex) {
      ex.printStackTrace();
      result.put("statas", "fail");
      processEvent(new MacWritingEvent(this, result));
    }
		return false;
  }

	/**
	 * this method acctually will function before the test starts,
	 * and the UI will called a similar method to make sure the input sn
	 * is validate; 
	 * this class call this method to insure the sn and mac be revalidated
	 * before they are erased;
	 * @param sn String
	 */
	public String checkSN(String sn) { // return a String to stand for -
		return dbConnector.checkSN(sn);			 // the status of the SN: -
	}															// either "used" or "validate" or "invalidate" 


	/** get a string's crc */
	public String getCRC(String mac) { // unimplemented
		return "unimplemented";
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
      listener.macWritingPerformed(e);
  }
}
