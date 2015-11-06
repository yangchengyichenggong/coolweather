package com.example.coolweather.util;

public interface HttpCallbackListener {
	void onFinish(String respone);
	
	void onError(Exception e);
}
