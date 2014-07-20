package com.reyurnible.tabemon;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.reyurnible.network.CustomImageLoader;
import com.reyurnible.network.ManageUri;
import com.reyurnible.network.NetworkApiTask;
import com.reyurnible.network.NetworkTaskCallback;
import com.reyurnible.network.TranslateJSON;
import com.reyurnible.network.VolleyHelper;
import com.reyurnible.network.CustomImageLoader.ImageListener;
import com.reyurnible.object.Recipe;
import com.reyurnible.object.Weather;
import com.reyurnible.tabemon.R;
import com.reyurnible.view.CustomDialogFragment;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity implements ConnectionCallbacks , OnConnectionFailedListener, LocationListener, NetworkTaskCallback, OnClickListener{

	//通信状態の確認
	private ConnectivityManager connectivityManager;
	private NetworkInfo networkInfo;
	
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private Location mLocation;
	private static CustomImageLoader imageLoader;
	private ImageListener listener;
	//レシピのリスト
	private ArrayList<Recipe> recipeList;
	private ArrayList<String> recipeJSONList;
	//表示画像インデックス
	private int recipeIndex = 0;
	private int requestPage = 1;
	private Weather weather;
	
	private ImageView characterImage;
	private ImageView recipeImage;
	private Button yesButton;
	private Button noButton;
	
	private Timer progressTimer;
	private android.os.Handler mHandler = new android.os.Handler();
	private int loadImageNum = 0;
	
	private Boolean isConnect = true;
	
	//位置情報の取得
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		recipeList = new ArrayList<Recipe>();
		recipeJSONList = new ArrayList<String>();
		findViews();
		setViews();
		setTimer();
		/* ConnectivityManagerの取得 */
		connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		
		//現在位置の取得
		mLocationClient = new LocationClient(this,this,this);
		//インスタンスの取得
		mLocationRequest = LocationRequest.create();
		//リクエストのインターバル設定５秒
		mLocationRequest.setInterval(5*1000);
		//リクエストのプライオリティー設定
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		//最短のリクエスト取得
		mLocationRequest.setFastestInterval(1*1000);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		/*通信状態の取得*/
		networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo==null||!networkInfo.isConnected()){
			FragmentManager manager = getFragmentManager();
			CustomDialogFragment dialog = new CustomDialogFragment("エラー","ネットワークに接続できません\n通信状態を確認してください。");
			dialog.show(manager,"network error");
			isConnect = false;
		}else{
			isConnect = true;
		}
	}
	
	private void findViews(){
		characterImage = (ImageView)findViewById(R.id.main_ImageViewCharacter);
		recipeImage = (ImageView)findViewById(R.id.main_ImageViewThumnail);
		yesButton = (Button)findViewById(R.id.main_ButtonYes);
		noButton = (Button)findViewById(R.id.main_ButtonNo);
	}
	private void setViews(){
		yesButton.setOnClickListener(this);
		noButton.setOnClickListener(this);
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		mLocationClient.connect();
		VolleyHelper.RequestStart();
		onImageLoad();
	}
	@Override
	protected void onStop(){
		super.onStop();
		mLocationClient.disconnect();
		VolleyHelper.RequestStop();
		VolleyHelper.RequestCancell("MainActivity");		
	}
	private void onRequest(){
		if(!isConnect)return ;
		String url;
		if(weather!=null){
			url = ManageUri.getRecipes(this,requestPage++,weather);
		}else{
			url = ManageUri.getRecipes(this,requestPage++);
		}
		Log.d("MainActivity","onRequest"+url);
		NetworkApiTask recipeListTask = new NetworkApiTask(this, "MainActivity");
		recipeListTask.onRequest(
				VolleyHelper.getRequestQueue(getApplicationContext()), 
				Method.GET, 
				Priority.NORMAL,
				url,
				NetworkTaskCallback.RecipesTask);
	}
	private void onImageLoad(){
		
		Log.d("MainActivity","OnImageLoad");
		if(recipeIndex<recipeList.size()){
			imageLoader = VolleyHelper.getImageLoader(getApplicationContext());
			// リクエストのキャンセル処理
	        if (recipeList.get(recipeIndex).imageContainer != null) {
	        	recipeList.get(recipeIndex).imageContainer.cancelRequest();
	        }
			listener = CustomImageLoader.getImageListener(
					recipeImage,
					R.drawable.bg_image_loading,/* 表示待ち時の画像 白いだけの画像*/
					R.drawable.failed_image /* エラー時の画像 */);
			recipeList.get(recipeIndex).imageContainer = imageLoader.get(recipeList.get(recipeIndex++).foodImageUrl,Priority.IMMEDIATE,listener); /* URLから画像を取得する */
			if(recipeIndex+3>recipeList.size()){
				onRequest();
			}
		}
	}
	//接続出来た時のコールバック
	@Override
	public void onConnected(Bundle arg0) {
		//位置情報の取得開始
		if(mLocationClient.isConnected()){
			mLocationClient.requestLocationUpdates(mLocationRequest, this);
		}
	}
	//接続失敗時のコールバック
	@Override
	public void onDisconnected() {
		// 接続を切断した後の処理
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Log.d("MainActivity","onFailed");
	}
	@Override
	public void onLocationChanged(Location location) {
		if(mLocationClient.isConnected()){
			//位置情報が取れたらトーストを表示
			mLocation = location;
			Log.d("Location",location.toString());
			mLocationClient.removeLocationUpdates(this);
			if(!isConnect)return ;
			NetworkApiTask tempTask = new NetworkApiTask(this, "MainActivity");
			tempTask.onRequest(
					VolleyHelper.getRequestQueue(getApplicationContext()), 
					Method.GET, 
					Priority.HIGH,
					ManageUri.getTempalature(location),
					NetworkTaskCallback.TempalatureTask);
		}
	}
	
	@Override
	public void onSuccessNetworkTask(int taskId, Object object) {
		if(taskId == NetworkTaskCallback.TempalatureTask){
			weather = TranslateJSON.weatherParse((JSONObject)object);
			onRequest();
			Log.d("MainActivity","OnSuccessNetworkTask:TempalatureTask");
		}else if(taskId == NetworkTaskCallback.RecipesTask){
			Log.d("MainActivity","OnSuccessNetworkTask:RecipesTask");
			recipeList.addAll(TranslateJSON.recipesParse((JSONObject)object));
			recipeJSONList.addAll(TranslateJSON.recipesJSONParse((JSONObject)object));
			onImageLoad();
		}
	}

	@Override
	public void onFailedNetworkTask(int taskId) {
		if(taskId == NetworkTaskCallback.TempalatureTask){
			
		}else if(taskId == NetworkTaskCallback.RecipesTask){
			Log.d("MainActivity","OnFailedNetworkTask:RecipesTask");
		}
	}

	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.main_ButtonYes:
			if(recipeIndex>0 && !recipeJSONList.isEmpty()){
				Intent intent = new Intent(getApplicationContext(),DetailsActivity.class);
				intent.putExtra(DetailsActivity.IntentRecipeJSON, recipeJSONList.get(recipeIndex-1));
				startActivity(intent);
				recipeIndex--;
			}
			break;
		case R.id.main_ButtonNo:
			onImageLoad();
			break;
		}
	}
	
	public void setTimer(){
		// タイマーの設定
     	progressTimer = new Timer(false);
     	progressTimer.schedule(new TimerTask(){
     		@Override
     		public void run() {
     			mHandler.post(new Runnable() {
     				@Override
     				public void run() {
     					if(recipeList.size()>0){
     						characterImage.setImageResource(R.drawable.character);
     						progressTimer.cancel();
     					}else{
     						if(loadImageNum==0){
     							characterImage.setImageResource(R.drawable.character_load1);
     						}else{
     							characterImage.setImageResource(R.drawable.character_load2);
     						}
     						loadImageNum = (loadImageNum+1)%2;
     					}
     				}
     			});
     		}
     	}, 0, 200); // 300 ミリ秒ごとにタイマーを起動
	}
	
}
