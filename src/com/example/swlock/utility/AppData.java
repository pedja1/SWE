package com.example.swlock.utility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AppData
{
	public static final boolean LOGGING = true;
	public static final String SP_KEY_SERVICE_ENABLED = "IS_SERVICE_ENABLED";//boolean
	public static final String SP_KEY_DEVICE_MAC = "SELECTED_DEVICE_MAC";//string
	public static final String SP_KEY_FIRST_RUN = "FIRST_RUN";//boolean
	public static final String SP_KEY_DEVICE_CONNECTED = "IS_DEVICE_CONNECTED";//boolean
	public static final String SP_KEY_LOCK_ON_WAKE = "LOCK_ON_WAKE";//boolean
	public static final String SP_KEY_PASSWORD = "UP";//string
	public static final int CANCEL_LOCK = 1001;
	
	private static char[] hextable = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public static String md5(String s)
	{
	    MessageDigest digest;
	    try
	    {
	        digest = MessageDigest.getInstance("MD5");
	        digest.update(s.getBytes(), 0, s.length());
	        byte[] bytes = digest.digest();

	        String hash = "";
	        for (int i = 0; i < bytes.length; ++i)
	        {
	            int di = (bytes[i] + 256) & 0xFF;
	            hash = hash + hextable[(di >> 4) & 0xF] + hextable[di & 0xF];
	        }

	        return hash;
	    }
	    catch (NoSuchAlgorithmException e)
	    {
	    	
	    }

	    return "";
	}
}
