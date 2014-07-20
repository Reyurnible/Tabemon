package com.reyurnible.tabemon;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request.Priority;
import com.reyurnible.network.CustomImageLoader;
import com.reyurnible.network.CustomImageLoader.ImageListener;
import com.reyurnible.network.TranslateJSON;
import com.reyurnible.network.VolleyHelper;
import com.reyurnible.object.Recipe;
import com.reyurnible.view.CustomListDialogCallback;
import com.reyurnible.view.CustomListDialogFragment;
import com.reyurnible.tabemon.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class DetailsActivity extends Activity implements OnClickListener, CustomListDialogCallback {
	//Intent key
	public static final String IntentRecipeJSON ="string_RecipeJSON";
	
	private ImageView thumnailImage;
	private TextView nameText;
	private TextView materialsText;
	private TextView moneyText;
	private TextView timeText;
	//private Button recipeButton;
	private Button makeButton;
	private Button remindButton;
	//private Button moreButton;
	
	private Recipe recipe;
	private String recipeJSON;
	private static CustomImageLoader imageLoader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		findViews();
		Intent intent = getIntent();
		recipeJSON = intent.getStringExtra(IntentRecipeJSON);
		try {
			recipe = TranslateJSON.recipeParse(new JSONObject(recipeJSON));
			setViews();
		} catch (JSONException e) {
			e.printStackTrace();
			finish();
		}
	}
	
	private void findViews(){
		thumnailImage = (ImageView)findViewById(R.id.details_ImageViewThumnails);
		nameText = (TextView)findViewById(R.id.details_TextViewName);
		materialsText = (TextView)findViewById(R.id.details_TextViewMaterials);
		moneyText = (TextView)findViewById(R.id.details_TextViewMoney);
		timeText = (TextView)findViewById(R.id.details_TextViewTime);
		//recipeButton = (Button)findViewById(R.id.details_ButtonRecipe);
		makeButton = (Button)findViewById(R.id.details_ButtonMake);
		remindButton = (Button)findViewById(R.id.details_ButtonRemind);
		//moreButton = (Button)findViewById(R.id.details_ButtonMore);
	}
	private void setViews(){
		imageLoader = VolleyHelper.getImageLoader(getApplicationContext());
		ImageListener listener = CustomImageLoader.getImageListener(thumnailImage,
				  R.drawable.bg_image_loading,/* 表示待ち時の画像 白いだけの画像*/
				  R.drawable.failed_image /* エラー時の画像 */);
		imageLoader.get(recipe.foodImageUrl,Priority.HIGH,listener); /* URLから画像を取得する */
		
		nameText.setText(recipe.recipeTitle);
		StringBuilder materialsBuilder = new StringBuilder();
		for(String material : recipe.recipeMaterial){
			materialsBuilder.append(material);
			materialsBuilder.append("\n");
		}
		materialsText.setText(materialsBuilder.toString());
		moneyText.setText(recipe.recipeCost);
		timeText.setText(recipe.recipeIndication);
		//recipeButton.setOnClickListener(this);
		makeButton.setOnClickListener(this);
		remindButton.setOnClickListener(this);
		//moreButton.setOnClickListener(this);
	}
	@Override
	protected void onStart(){
		super.onStart();
		VolleyHelper.RequestStart();
	}
	@Override
	protected void onStop(){
		super.onStop();
		VolleyHelper.RequestStop();
		VolleyHelper.RequestCancell("DetailsActivity");
	}
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		/*case R.id.details_ButtonRecipe:
			break;
		*/
		case R.id.details_ButtonMake:
			Uri uri = Uri.parse(recipe.recipeUrl);
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(intent);
			break;
		case R.id.details_ButtonRemind:
			//ダイアログを出す
			String[] contents = {"30分後","１時間後","2時間語","4時間語","6時間語"};
			CustomListDialogFragment dialogFragment = new CustomListDialogFragment("Remind later", contents);
			dialogFragment.show(getFragmentManager().beginTransaction(), "DetailsActivity");
			dialogFragment.setCallBack(this); 
			//後で通知するようにインテントを設定
			break;
		/*case R.id.details_ButtonMore:
			break;
		*/
		}
	}

	@Override
	public void onClickedContentButtons(int position) {
		/*-----------後で通知するようにインテントを設定-----------*/
		//String[] contents = {"30分後","１時間後","2時間語","4時間語","6時間語"};
		//受けっとった値で何分後に通知かを設定
		//呼び出す日時を設定する
		Calendar triggerTime = Calendar.getInstance();
		switch(position){
		case 0:
			//"30分後"
			triggerTime.add(Calendar.MINUTE, 30);
			break;
		case 1:
			//"１時間後"
			triggerTime.add(Calendar.HOUR, 1);
			break;
		case 2:
			//"２時間後"
			triggerTime.add(Calendar.HOUR, 2);
			break;
		case 3:
			//"４時間後"
			triggerTime.add(Calendar.HOUR, 4);
			break;
		case 4:
			//"６時間後"
			triggerTime.add(Calendar.HOUR, 6);
			break;
		}
		//設定した日時で発行するIntentを生成
		Intent intent = new Intent(getApplicationContext(), RemindLaterReceiver.class);
		intent.putExtra(RemindLaterReceiver.IntentRecipeJSON, recipeJSON);
		PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//日時と発行するIntentをAlarmManagerにセットします
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		manager.set(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), sender);
	}

	@Override
	public void onClickedCloseButton() {
		// TODO Auto-generated method stub
		
	}
}
