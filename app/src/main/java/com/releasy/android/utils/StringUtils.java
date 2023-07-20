package com.releasy.android.utils;

public class StringUtils {
	/**
	 * 判断字符串是否为"空格",""或null
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
	 * @param cs
	 * @return
	 */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if ((Character.isWhitespace(cs.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }
    
	/**
	 * 判断字符串是否不�?空格",""或null
	 * @see StringUtils#isBlank(CharSequence)
	 * @param cs
	 * @return
	 */
    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtils.isBlank(cs);
    }
    
    /**
	 * 判断字符串是�?"或null
     * StringUtils.isEmpty(null)      = true
     * StringUtils.isEmpty("")        = true
     * StringUtils.isEmpty(" ")       = false
     * StringUtils.isEmpty("bob")     = false
     * StringUtils.isEmpty("  bob  ") = false
	 * @param cs
	 * @return
	 */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
    
    /**
     * 判断字符串是不为""或null
     * @see StringUtils#isEmpty(CharSequence)
     * @param cs
     * @return
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !StringUtils.isEmpty(cs);
    }
    
    /**
     * 判断字符串是否只包含unicode数字
     * StringUtils.isNumeric(null)   = false
     * StringUtils.isNumeric("")     = false
     * StringUtils.isNumeric("  ")   = false
     * StringUtils.isNumeric("123")  = true
     * StringUtils.isNumeric("12 3") = false
     * StringUtils.isNumeric("ab2c") = false
     * StringUtils.isNumeric("12-3") = false
     * StringUtils.isNumeric("12.3") = false
     * @param cs
     * @return
     */
    public static boolean isNumeric(CharSequence cs) {
        if (cs == null || cs.length() == 0) {
            return false;
        }
        int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 注意,只替换首字符replace("112341234", "1", "c") 结果�?c12341234
     * @param line
     * @param oldString
     * @param newString
     * @return
     */
	public static final String replace(String line, String oldString,
			String newString) {
		int i = 0;
		if ((i = line.indexOf(oldString, i)) >= 0) {
			char[] line2 = line.toCharArray();
			char[] newString2 = newString.toCharArray();
			int oLength = oldString.length();
			StringBuffer buf = new StringBuffer(line2.length);
			buf.append(line2, 0, i).append(newString2);
			i += oLength;
			buf.append(line2, i, line2.length - i);
			return buf.toString();
		}
		return line;
	}
	
	public static int toInt(String str, int def) {
		
		if(str == null || isBlank(str) || !isNumeric(str)) {
			return def;
		}
		
		return Integer.valueOf(str);
	}
	
    public static byte[] getBytesUtf8(String string) {
    	if (string == null) {
    		return null;
    	}
    	
    	try {
    		return string.getBytes("UTF-8");
    	} catch (Exception e) {
            System.out.println(e);
            return null;
        }
    }
    
   /*** 半角转换为全�?
   *
   * @param input    
   * @return      
   */      
   public static String ToDBC(String input) {          
            char[] c = input.toCharArray();
            for (int i = 0; i < c.length; i++) {              
            if (c[i] == 12288) {                 
            c[i] = (char) 32;                  
            continue;
             }
             if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
            }
            return new String(c);
        }  
}
