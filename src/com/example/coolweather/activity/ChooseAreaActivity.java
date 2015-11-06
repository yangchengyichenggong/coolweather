package com.example.coolweather.activity;

import java.util.ArrayList;
import java.util.List;

import com.example.coolweather.model.City;
import com.example.coolweather.model.CoolWeatherDB;
import com.example.coolweather.model.County;
import com.example.coolweather.model.Province;
import com.example.coolweather.util.HttpCallbackListener;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import android.R;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.*;
import com.example.coolweather.model.*;

public class ChooseAreaActivity extends Activity{
	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTY=2;
	
	private ProgressDialog progressDialog;
	private TextView titleText;
	private ListView listView;
	private ArrayAdapter<String>adapter;
	private CoolWeatherDB coolWeatherDB;
	
	private List<String>dataList=new ArrayList<String>();
	/*
	 * ʡ�б�
	 * */
	private List<Province>provinceList;
	/*
	 * ���б�
	 * */
	private List<City>cityList;
	/*
	 * ���б�
	 * */
	private List<County>countyList;
	/*
	 * ѡ�е�ʡ��
	 * */
	private Province selectedProvince;
	/*
	 * ѡ�еĳ���
	 * */
	private City selectedCity;
	/*
	 * ��ǰѡ�еļ���
	 * */
	private int currentLevel;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(com.example.coolweather.R.layout.choose_area);
		
		listView=(ListView)findViewById(com.example.coolweather.R.id.list_view);
		titleText=(TextView)findViewById(com.example.coolweather.R.id.title_text);
		adapter=new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		coolWeatherDB=CoolWeatherDB.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			
			public void onItemClick(AdapterView<?>arg0,View view,int index,
					long arg3){
				
				if(currentLevel==LEVEL_PROVINCE){
					selectedProvince=provinceList.get(index);
					queryCities();
					
				}else if(currentLevel== LEVEL_CITY){
					selectedCity=cityList.get(index);
					queryCounties();
				}
			}
		});
		
		queryProvinces();
		
		
		
		
		
	}
	
	/*
	 * ��ѯȫ�����е�ʡ�����ȴ����ݿ��ѯ��û�ҵ���ȥ���ݿ��ѯ
	 * */
	private void queryProvinces(){
		
		provinceList=coolWeatherDB.loadProvinces();
		
		if(provinceList.size()>0){
			dataList.clear();
			
			
			for(Province province:provinceList){
				dataList.add(province.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText("�й�");
			currentLevel=LEVEL_PROVINCE;
		}else{
			
			queryFromServer(null,"province");
			
			
		}
	}
	
	
	/*
	 * ��ѯȫ�����е��У����ȴ����ݿ��ѯ��û�ҵ���ȥ���ݿ��ѯ
	 * */
	private void queryCities(){
		cityList=coolWeatherDB.loadCities(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City city:cityList){
				dataList.add(city.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedProvince.getProvinceName());
			currentLevel=LEVEL_CITY;
		}else{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	
	/*
	 * ��ѯȫ�����е��أ����ȴ����ݿ��ѯ��û�ҵ���ȥ���ݿ��ѯ
	 * */
	private void queryCounties(){
		countyList=coolWeatherDB.loadCounties(selectedCity.getId());
		if(countyList.size()>0){
			dataList.clear();
			for(County county:countyList){
				dataList.add(county.getCountyName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			titleText.setText(selectedCity.getCityName());
			currentLevel=LEVEL_COUNTY;
		}else{
			queryFromServer(selectedCity.getCityCode(),"county");
		}
	}
	
	/*
	 * ���ݴ���Ĵ��ź����ʹӷ������ϲ�ѯʡ��������
	 * */
	private void queryFromServer(final String code,final String type){
		String address;
		
		
		
		if(!TextUtils.isEmpty(code)){
			address="http://www.weather.com.cn/data/list3/city"+code+".xml";
			Log.d("ChooseAreaActivity", "a");
		}else{
		
			address="http://www.weather.com.cn/data/list3/city.xml";
		
		}
	
		   showProgressDialog();//��ʾ���ȶԻ���
		   
		
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener(){
			
			@Override
			public void onFinish(String response){
				Log.d("ChooseAreaActivity", "b");
				boolean result=false;
				if("province".equals(type)){
					result=Utility.handleProvincesResponse(coolWeatherDB,
							response);	
				}else if("city".equals(type)){
					result=Utility.handleCitiesResponse(coolWeatherDB,
							response,selectedProvince.getId());
					Log.d("ChooseAreaActivity", "c");
				}else if("county".equals(type)){
					result=Utility.handleCountiesResponse(coolWeatherDB,
							response, selectedCity.getId());
				}
				
				if(result){
					
					//ͨ��runOnUiThread()�����ص����̴߳����߼�
					runOnUiThread(new Runnable(){
						@Override
						public void run(){
							closeProgressDialog();
							
							if("province".equals(type)){
								queryProvinces();
								
								Toast.makeText(ChooseAreaActivity.this, "����",
										Toast.LENGTH_SHORT).show();
							}else if("city".equals(type)){
								queryCities();
							}else if("county".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e){
			
				//ͨ��runOnUiThread()�����ص����̴߳����߼�
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this,
								"����ʧ��", Toast.LENGTH_SHORT).show();
					}
				});
			}
			
		});
	}
	
	/*
	 * ��ʾ���ȶԻ���
	 * */
	
	private void showProgressDialog(){
		if(progressDialog==null){
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage("���ڼ���...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	
	/*
	 * �رնԻ���
	 * */
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	/*
	 * ����Back���������ݼ����жϣ���ʱ�÷������б�ʡ�б�����ֱ���˳�
	 * */
	public void onBackPressed(){
		if(currentLevel==LEVEL_COUNTY){
			queryCities();
		}else if(currentLevel==LEVEL_CITY){
			queryProvinces();
		}else{
			finish();
		}
	}
	
	
	
	
}
