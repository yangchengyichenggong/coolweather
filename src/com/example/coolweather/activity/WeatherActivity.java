package com.example.coolweather.activity;

import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import android.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherActivity extends Activity implements OnClickListener{
	private LinearLayout weatherInfoLayout;
	
	
	//显示城市名
	private TextView cityNameText;
	//显示发布时间
	private TextView publishText;
	//显示当前时间
	private TextView currentDateText;
	
	//显示天气详细信息
	private TextView weatherDespText;
	//显示温度1
	private TextView temp1Text;
	//显示气温二
	private TextView temp2Text;
	
	
	/*
	 * 切换城市按钮
	 * */
	private Button switchCity;
	/*
	 * 更新天气按钮
	 * */
	private Button refreshWeather;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(com.example.coolweather.R.layout.weather_layout);
	//初始化个控件
	weatherInfoLayout=(LinearLayout)findViewById
			(com.example.coolweather.R.id.weather_info_layout);
	
	cityNameText=(TextView)findViewById(com.example.coolweather.R.id.city_name);
	
	publishText=(TextView)findViewById(com.example.coolweather.R.id.publish_text);
	
	currentDateText=(TextView)findViewById(com.example.coolweather.R.id.current_data);
	
	weatherDespText=(TextView)findViewById(com.example.coolweather.R.id.weather_desp);
	
	temp1Text=(TextView)findViewById(com.example.coolweather.R.id.temp1);
	
	temp2Text=(TextView)findViewById(com.example.coolweather.R.id.temp2);
	
	String countyCode=getIntent().getStringExtra("county_code");
	if(!TextUtils.isEmpty(countyCode)){
		//有县级代号就查询天气
		publishText.setText("同步中...");
		weatherInfoLayout.setVisibility(View.INVISIBLE);
		cityNameText.setVisibility(View.INVISIBLE);
		queryWeatherCode(countyCode);
	}else{
		//没有县级代号就直接显示本地天气
		showWeather();
	}
	
	
	//新增切换城市 更新天气功能
	switchCity=(Button)findViewById(com.example.coolweather.R.id.switch_city);
	refreshWeather=(Button)findViewById(com.example.coolweather.R.id.refresh_weather);
	switchCity.setOnClickListener(this);
	refreshWeather.setOnClickListener(this);
	}
	
	//按钮选择相应的功能
	public void onClick(View v){
		switch(v.getId()){
		case com.example.coolweather.R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case com.example.coolweather.R.id.refresh_weather:
			publishText.setText("同步中");
			SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
			String weatherCode=prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode);
			}
			break;
			default:
				break;
		}
	}
	
	
	
	
	
	
	/*
	 * 查询县级代号所对应的天气代号 
	 * */
	private void queryWeatherCode(String countyCode){
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromServer(address,"countyCode");
	}
	/*
	 * 查询天气代号所对应的天气
	 * */
	private void queryWeatherInfo(String weatherCode){
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromServer(address,"weatherCode");
	}

	/*
	 *根据传入地址和类型去向服务器查询天气代号或者天气信息 
	 * */
	private void queryFromServer (final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
			@Override
			public void onFinish(final String response){
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						//从服务器返回的数据中解析出天气代号
						String []array=response.split("\\|");
						if(array!=null&&array.length==2){
							String weatherCode=array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					Utility.hadleWeatherResponse(WeatherActivity.this,response);
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
						showWeather();	
						}
					});
				}
			}
			@Override
			public void onError(Exception e){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						publishText.setText("同步失败");
					}
				});
			}
		});
	}
	
	/*
	 * 从SharedPreferences 文件中读取存储的天气信息，并显示到界面上
	 * */
		private void showWeather(){
			SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
			cityNameText.setText(prefs.getString("city_name", ""));
			temp1Text.setText(prefs.getString("temp1", ""));
			temp2Text.setText(prefs.getString("temp2", ""));	
			weatherDespText.setText(prefs.getString("weather_desp", ""));
			publishText.setText(prefs.getString("publish_time", ""));
			currentDateText.setText(prefs.getString("current_date", ""));
			weatherInfoLayout.setVisibility(View.VISIBLE);
			cityNameText.setVisibility(View.VISIBLE);
			
		}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
