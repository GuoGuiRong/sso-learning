package com.ggr.sso;

import java.util.HashMap;
import java.util.Map;

import sun.security.jca.GetInstance.Instance;

import com.sun.org.apache.bcel.internal.generic.NEW;
/**
 * 模拟数据库
 * @author GuiRunning
 *
 */
public class UserManager {
		
	private Map<String,Object> userMap = new HashMap<String, Object>();
	
	public static class innerClass{
		public static final UserManager INSTANCE = new UserManager();
	}
	public static final UserManager getInstance(){
		return innerClass.INSTANCE;
	}
	
	private UserManager(){}
	public User getUserByName(String userName){
		return (User) getInstance().userMap.get(userName);
	}
	
	public void addUser(String userName,String password){
		getInstance().userMap.put(userName,new User(userName,MD5.getMD5Str(password)));
	}
	
}
