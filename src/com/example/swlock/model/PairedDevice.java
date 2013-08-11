package com.example.swlock.model;

public class PairedDevice
{
	String name;
	String mac;
	public PairedDevice(String name, String mac)
	{
		super();
		this.name = name;
		this.mac = mac;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getMac()
	{
		return mac;
	}
	public void setMac(String mac)
	{
		this.mac = mac;
	}
	
	
}
