package com.ggr.sso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.tomcat.util.codec.binary.StringUtils;

public class MD5Util {

	public static String  toMd5(String salt,String origin) throws RuntimeException{
		MessageDigest messageDigest = null;
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		if(salt!=null && salt.trim()!=""){
			origin = salt+origin;
		}
		byte[] result = messageDigest.digest(origin.getBytes());
		return toHex(result);	
	}
	
	public static String  toMd5(String origin) throws RuntimeException{
		return toMd5(null,origin);
	}
	
	public static String toHex(byte[] result){
		if(result==null || result.length==0){
			return null;
		}
		StringBuilder stringBuilder = new StringBuilder();
		for(int i=0;i<result.length;i++){
			int hi = (result[i]>>4) & 0x0f;
			int lo = result[i] & 0x0f;
			stringBuilder.append(Character.forDigit(hi, 16)).append(Character.forDigit(lo, 16));
		}
		return stringBuilder.toString();
	}
	
	public static void main(String[] args) {
		System.out.println(MD5Util.toMd5(null,"ggr"));
	}
}
