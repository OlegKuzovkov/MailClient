package Package;

import java.io.UnsupportedEncodingException;

public class Test {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		String hexString = "\u041F\u0440\u0438\u0432\u0435\u0442\u0020\u0432\u0441\u0435\u043C\u0021";    
		String string = "\u003c and \u003e";
	    byte[] converttoBytes = hexString.getBytes("UTF-8");
		System.out.println(new String(converttoBytes, "UTF8"));
	    /*String test = "Привет всем!";//
        for(char c : test.toCharArray()){
        StringBuilder converted = new StringBuilder(test.length()*6);
           converted.append(getCharRepresentation(c));
        }
        System.out.println("Initial:   "+test);
        System.out.println("Converted: "+converted.toString());
        */      
    }
	/*private static String getCharRepresentation(char c){
        StringBuilder cs = new StringBuilder("000").append(Integer.toHexString(c));
        return "\\u"+cs.substring(cs.length() - 4).toUpperCase();
    }*/
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}



