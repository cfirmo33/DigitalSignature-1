package com.Digitalsign;

public class KeyValue {
	private String _key = "";
	private String _value = "";

	public void setItem(String key, String value)
	{
		_key=key;
		_value=value;
	}
	
	public String getKey()
	{
		return _key;		
	}
	
	public String getValue()
	{
		return _value;
	}
}
