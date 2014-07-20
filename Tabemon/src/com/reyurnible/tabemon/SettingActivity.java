package com.reyurnible.tabemon;

import com.reyurnible.object.MyAccount;
import com.reyurnible.view.CustomListDialogCallback;
import com.reyurnible.view.CustomListDialogFragment;
import com.reyurnible.tabemon.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ToggleButton;

public class SettingActivity extends Activity implements OnCheckedChangeListener, OnClickListener, CustomListDialogCallback {
	
	private final String[] contents = {"10~20","20~30","30~40","40~50","50~60"};
	
	private ToggleButton maleButton;
	private ToggleButton femaleButton;
	private Button ageButton;
	private Button doneButton;
	private Boolean isMale = true;
	private int ageSelect = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		findViews();
		setViews();
		MyAccount account = MyAccount.getAccount(getApplicationContext());
		if(account.sex!=-1&&account.age!=-1){
			startIntent();
		}
	}
	
	private void findViews(){
		maleButton = (ToggleButton)findViewById(R.id.setting_ButtonMale);
		femaleButton = (ToggleButton)findViewById(R.id.setting_ButtonFemale);
		ageButton = (Button)findViewById(R.id.setting_ButtonAge);
		doneButton = (Button)findViewById(R.id.setting_ButtonDone);
	}
	private void setViews(){
		maleButton.setChecked(true);
		maleButton.setOnCheckedChangeListener(this);
		femaleButton.setOnCheckedChangeListener(this);
		ageButton.setOnClickListener(this);
		doneButton.setOnClickListener(this);
	}
	@Override
	public void onClick(View view) {
		if(view.getId()==R.id.setting_ButtonAge){
			CustomListDialogFragment dialogFragment = new CustomListDialogFragment("Select Age", contents);
			dialogFragment.show(getFragmentManager().beginTransaction(), "SettingActivity");
			dialogFragment.setCallBack(this);
		}else if(view.getId()==R.id.setting_ButtonDone){
			if(ageSelect!=-1){
				MyAccount.saveAccount(getApplicationContext(), isMale?0:1, ageSelect);
				startIntent();
			}
		}
	}
	@Override
	public void onCheckedChanged(CompoundButton view, boolean isChecked) {
		maleButton.setOnCheckedChangeListener(null);
		femaleButton.setOnCheckedChangeListener(null);
		Log.d("SettingActivity","onCheckedChanged:"+isChecked);
		isMale = ((view.getId()==R.id.setting_ButtonMale)&&isChecked)||((view.getId()==R.id.setting_ButtonFemale)&&!isChecked);
		maleButton.setChecked(isMale);
		femaleButton.setChecked(!isMale);
		
		maleButton.setOnCheckedChangeListener(this);
		femaleButton.setOnCheckedChangeListener(this);
	}
	@Override
	public void onClickedContentButtons(int position) {
		try{
			ageSelect = position;
			ageButton.setText("Age?"+"\n"+contents[position]);
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}
	}
	@Override
	public void onClickedCloseButton() {
		
	}
	
	private void startIntent(){
		DailyScheduler dailySchedular = new DailyScheduler(getApplicationContext());
		dailySchedular.setByTime(18, 0, 101);
		Intent intent = new Intent(getApplicationContext(),MainActivity.class);
		startActivity(intent);
		finish();
	}
}
