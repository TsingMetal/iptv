public interface CmdXml {

  public static String setXml = 
    "<?xml version='1.0' encoding='UTF-8'?>" +  
    "<msg>" + 
    "<pwd>ff2234asdxcn2s34</pwd>" + 
    "<cmd>set</cmd>" +
    "<mac>%s</mac>" + 
    "<mac_crc>%s</mac_crc>" + 
    "<sn>%s</sn>" + 
    "<sn_crc>%s</sn_crc>" +
    "<sender>pc</sender>" +
    "</sn>" +
    "<sn_crc>%s</sn_crc>" +
    "<sender>pc</sender>" +
    "</msg>"

  public static String getXml = 
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<msg>" +
    "<pwd>ff2234asdxcn2s34</pwd>" +
    "<cmd>get</cmd>" +
    "<list>sn,mac </list>" +
    "<sender>pc</sender>" +
    "</msg>" 
  
  public static String eraseXml = 
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<msg>" +
    "<pwd>ff2234asdxcn2s34</pwd>" +
    "<cmd>set</cmd>" +
    "<mac></mac>" +
    "<sn></sn>" +
    "</msg>" 

  public static String rebootXml = 
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<msg>" +
    "<pwd>ff2234asdxcn2s34</pwd>" +
    "<cmd>set</cmd>" +
    "<stb>reboot</stb>" +
    "</msg>" 
    
  public static String getAdcXml = 
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<msg>" +
    "<pwd>ff2234asdxcn2s34</pwd>" +
    "<cmd>get</cmd>" +
    "<list>adv_security</list>" +
    "</msg>" 

  public static String setAdcXml = 
    "<?xml version='1.0' encoding='UTF-8'?>" +
    "<msg>" +
    "<pwd>ff2234asdxcn2s34</pwd>" +
    "<cmd>set</cmd>" +
    "<adv_security>enable</adv_security>" +
    "<adv_security_crc>0123</adv_security_crc> " +
    "</msg>" 
