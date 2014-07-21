package com.reyurnible.tabemon;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.reyurnible.network.BitmapNormalCache;
import com.reyurnible.network.TranslateJSON;
import com.reyurnible.network.VolleyHelper;
import com.reyurnible.object.Recipe;
import com.reyurnible.tabemon.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class RemindLaterReceiver extends BroadcastReceiver implements ImageListener {
	//Intent key
	public static final String IntentRecipeJSON ="string_RecipeJSON";
	private Recipe recipe;
	private Context context;
	private PendingIntent sender;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		//intentからレシピ情報を受け取る
		String recipeJSON = intent.getStringExtra(IntentRecipeJSON);
		Intent sendIntent;
		recipe = null;
		if(recipeJSON==null){
			sendIntent = new Intent(context, MainActivity.class);
			sender = PendingIntent.getActivity(context, 0, sendIntent, 0);
			//通知オブジェクトの生成
			Notification noti = new NotificationCompat.Builder(context)
	            .setTicker("レシピのお届け")
	            .setContentTitle("レシピのお届け")
	            .setContentText("タップで確認")
	            .setSmallIcon(R.drawable.app_icon)
	            .setVibrate(new long[]{0, 200, 100, 200, 100, 200})
	            .setAutoCancel(true)
	            .setContentIntent(sender)
	            .build();
			NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
	        manager.notify(0, noti);
		}else{
			//通知がクリックされた時に発行されるIntentの生成
			sendIntent = new Intent(context, DetailsActivity.class);
			sendIntent.putExtra(DetailsActivity.IntentRecipeJSON, recipeJSON);
			try {
				recipe = TranslateJSON.recipeParse(new JSONObject(recipeJSON));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		sender = PendingIntent.getActivity(context, 0, sendIntent, 0);
		if(recipe!=null){
			ImageLoader imageLoader = new ImageLoader(VolleyHelper.getRequestQueue(context),new BitmapNormalCache());
			Log.d("RemindLaterReceiver",recipe.foodImageUrl);
			imageLoader.get(recipe.foodImageUrl,this);
		}
	}
	
	@Override
	public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
    	StringBuilder materialsBuilder = new StringBuilder();
		for(String material : recipe.recipeMaterial){
			materialsBuilder.append(material);
			materialsBuilder.append("\n");
		}
    	//通知オブジェクトの生成
		Notification noti = new NotificationCompat.Builder(context)
            .setTicker("レシピのお届け")
            .setContentTitle(recipe.recipeTitle)
            .setContentText(materialsBuilder.toString())
            .setSmallIcon(R.drawable.app_icon)
            .setLargeIcon(response.getBitmap())
            .setVibrate(new long[]{0, 200, 100, 200, 100, 200})
            .setAutoCancel(true)
            .setContentIntent(sender)
            .build();
        NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, noti);
	}
	@Override
	public void onErrorResponse(VolleyError error) {
	}

}
