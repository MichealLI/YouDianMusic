package com.example.ming.youdianmusic.common;

public class MusicUser {
	private String username;
	private String password;
	private String telnumber;
	
	public MusicUser() {
		super();
	}

	public MusicUser(String username, String password, String telnumber) {
		super();
		this.username = username;
		this.password = password;
		this.telnumber = telnumber;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTelnumber() {
		return telnumber;
	}

	public void setTelnumber(String telnumber) {
		this.telnumber = telnumber;
	}

}
