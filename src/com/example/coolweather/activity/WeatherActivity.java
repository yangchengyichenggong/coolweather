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
	
	
	//��ʾ������
	private TextView cityNameText;
	//��ʾ����ʱ��
	private TextView publishText;
	//��ʾ��ǰʱ��
	private TextView currentDateText;
	
	//��ʾ������ϸ��Ϣ
	private TextView weatherDespText;
	//��ʾ�¶�1
	private TextView temp1Text;
	//��ʾ���¶�
	private TextView temp2Text;
	
	
	/*
	 * �л����а�ť
	 * */
	private Button switchCity;
	/*
	 * ����������ť
	 * */
	private Button refreshWeather;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(com.example.coolweather.R.layout.weather_layout);
	//��ʼ�����ؼ�
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
		//���ؼ����žͲ�ѯ����
		publishText.setText("ͬ����...");
		weatherInfoLayout.setVisibility(View.INVISIBLE);
		cityNameText.setVisibility(View.INVISIBLE);
		queryWeatherCode(countyCode);
	}else{
		//û���ؼ����ž�ֱ����ʾ��������
		showWeather();
	}
	
	
	//�����л����� ������������
	switchCity=(Button)findViewById(com.example.coolweather.R.id.switch_city);
	refreshWeather=(Button)findViewById(com.example.coolweather.R.id.refresh_weather);
	switchCity.setOnClickListener(this);
	refreshWeather.setOnClickListener(this);
	}
	
	//��ťѡ����Ӧ�Ĺ���
	public void onClick(View v){
		switch(v.getId()){
		case com.example.coolweather.R.id.switch_city:
			Intent intent=new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case com.example.coolweather.R.id.refresh_weather:
			publishText.setText("ͬ����");
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
	 * ��ѯ�ؼ���������Ӧ���������� 
	 * */
	private void queryWeatherCode(String countyCode){
		String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
		queryFromServer(address,"countyCode");
	}
	/*
	 * ��ѯ������������Ӧ������
	 * */
	private void queryWeatherInfo(String weatherCode){
		String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
		queryFromServer(address,"weatherCode");
	}

	/*
	 *���ݴ����ַ������ȥ���������ѯ�������Ż���������Ϣ 
	 * */
	private void queryFromServer (final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
			@Override
			public void onFinish(final String response){
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						//�ӷ��������ص������н�������������
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
						publishText.setText("ͬ��ʧ��");
					}
				});
			}
		});
	}
	
	/*
	 * ��SharedPreferences �ļ��ж�ȡ�洢��������Ϣ������ʾ��������
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
