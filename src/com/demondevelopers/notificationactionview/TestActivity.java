package com.demondevelopers.notificationactionview;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;


public class TestActivity extends FragmentActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.ab_notification, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu)
	{
		MenuItem itemData = menu.findItem(R.id.notifications);
		NotificationActionView actionView = (NotificationActionView)itemData.getActionView();
		actionView.setItemData(menu, itemData);
		//actionView.setCount(10); // initial value
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if(item.getItemId() == R.id.notifications){
			NotificationActionView.setCountDelta(this, 3);
			return true;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
