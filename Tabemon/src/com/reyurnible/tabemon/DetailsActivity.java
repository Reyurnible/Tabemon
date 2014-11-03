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
import com.reyurnible.view.RoundedTransformation;
import com.reyurnible.tabemon.R;
import com.squareup.picasso.Picasso;

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
	
	private Recipe mRecipe;
	private String mRecipeJSON;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_details);
		Intent intent = getIntent();
		mRecipeJSON = intent.getStringExtra(IntentRecipeJSON);
		try {
			mRecipe = TranslateJSON.recipeParse(new JSONObject(mRecipeJSON));
			setViews();
		} catch (JSONException e) {
			e.printStackTrace();
			finish();
		}
	}
	
	private void setViews(){
		Picasso.with(getApplicationContext())
		    .load(mRecipe.foodImageUrl)
		    .placeholder(R.drawable.bg_image_loading)
		    .error(R.drawable.failed_image)
		    .transform(new RoundedTransformation(32, 0))
		    .into((ImageView) findViewById(R.id.details_ImageViewThumnails));
		
		((TextView) findViewById(R.id.details_TextViewName)).setText(mRecipe.recipeTitle);
		
		TextView materialsText = (TextView) findViewById(R.id.details_TextViewMaterials);
		StringBuilder materialsBuilder = new StringBuilder();
		for(String material : mRecipe.recipeMaterial){
			materialsBuilder.append(material);
			materialsBuilder.append("\n");
		}
		materialsText.setText(materialsBuilder.toString());
		((TextView) findViewById(R.id.details_TextViewMoney)).setText(mRecipe.recipeCost);
		((TextView) findViewById(R.id.details_TextViewTime)).setText(mRecipe.recipeIndication);

		findViewById(R.id.details_ButtonMake).setOnClickListener(this);
		findViewById(R.id.details_ButtonRemind).setOnClickListener(this);
	}
	@Override
	protected void onStart() {
		super.onStart();
		VolleyHelper.RequestStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		VolleyHelper.RequestStop();
		VolleyHelper.RequestCancell("DetailsActivity");
	}
	
	@Override
	public void onClick(View view) {
		switch(view.getId()){
		case R.id.details_ButtonMake:
			Uri uri = Uri.parse(mRecipe.recipeUrl);
			Intent intent = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(intent);
			break;
		case R.id.details_ButtonRemind:
			//ダイアログを出す
			String[] contents = {"30分後","１時間後","2時間後","4時間後","6時間後"};
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
		//受けっとった値で何分後に通知かを設定
		//呼び出す日時を設定する
		Calendar triggerTime = Calendar.getInstance();
		switch(position) {
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
		intent.putExtra(RemindLaterReceiver.IntentRecipeJSON, mRecipeJSON);
		PendingIntent sender = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		//日時と発行するIntentをAlarmManagerにセットします
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		manager.set(AlarmManager.RTC_WAKEUP, triggerTime.getTimeInMillis(), sender);
	}

	@Override
	public void onClickedCloseButton() {
		
	}
}
