package com.demondevelopers.notificationactionview;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class NotificationActionView extends RelativeLayout
{
	private static final String ACTION_SET_ABS   = NotificationActionView.class.getCanonicalName() + ".ACTION_SET_ABS";
	private static final String ACTION_SET_DELTA = NotificationActionView.class.getCanonicalName() + ".ACTION_SET_DELTA";
	private static final String EXTRA_COUNT = "extraCount";
	
	private ImageView mIcon;
	private TextView  mText;
	
	private Menu      mMenu;
	private MenuItem  mItemData;
	private int       mCount;
	
	
	public NotificationActionView(Context context)
	{
		this(context, null);
	}
	
	public NotificationActionView(Context context, AttributeSet attrs)
	{
		this(context, attrs, android.R.attr.actionButtonStyle);
	}
	
	public NotificationActionView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
	}
	
	@Override
	protected void onFinishInflate()
	{
		super.onFinishInflate();
		mIcon = (ImageView)findViewById(android.R.id.icon);
		mText = (TextView)findViewById(android.R.id.text1);
		mText.setVisibility(View.GONE);
	}
	
	public void setItemData(Menu menu, MenuItem itemData)
	{
		mMenu = menu;
		mItemData = itemData;
		if(mItemData != null){
			setId(itemData.getItemId());
			mIcon.setImageDrawable(itemData.getIcon());
			setContentDescription(itemData.getTitleCondensed());
			setVisibility(itemData.isVisible() ? View.VISIBLE : View.GONE);
			setEnabled(itemData.isEnabled());
			setClickable(true);
			setLongClickable(true);
		}
	}
	
	public MenuItem getItemData()
	{
		return mItemData;
	}
	
	public void setCount(int count)
	{
		mCount = count;
		mText.setText(mCount > 99 ? "99+" : String.valueOf(mCount));
		mText.setVisibility((mCount == 0) ? View.GONE : View.VISIBLE);
	}
	
	public void setCountDelta(int delta)
	{
		setCount(Math.max(0, getCount() + delta));
	}
	
	public int getCount()
	{
		return mCount;
	}
	
	private static Intent createNotificationIntent(Context context, String action, int count)
	{
		return new Intent(action)
			.setPackage(context.getPackageName())
			.putExtra(EXTRA_COUNT, count);
	}
	
	public static void setCountAbs(Context context, int count)
	{
		LocalBroadcastManager.getInstance(context)
			.sendBroadcast(createNotificationIntent(context, ACTION_SET_ABS, count));
	}
	
	public static void setCountDelta(Context context, int delta)
	{
		LocalBroadcastManager.getInstance(context)
			.sendBroadcast(createNotificationIntent(context, ACTION_SET_DELTA, delta));
	}
	
	@Override
	protected void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_SET_ABS);
		filter.addAction(ACTION_SET_DELTA);
		LocalBroadcastManager.getInstance(getContext())
			.registerReceiver(mUpdateReceiver, filter);
	}
	
	private BroadcastReceiver mUpdateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			int count = intent.getIntExtra(EXTRA_COUNT, 0);
			if(ACTION_SET_ABS.equals(intent.getAction())){
				setCount(count);
			}
			else if(ACTION_SET_DELTA.equals(intent.getAction())){
				setCountDelta(count);
			}
		}
	};
	
	@Override
	protected void onDetachedFromWindow()
	{
		super.onDetachedFromWindow();
		
		LocalBroadcastManager.getInstance(getContext())
			.unregisterReceiver(mUpdateReceiver);
	}
	
	@Override
	public boolean performClick()
	{
		boolean performed = super.performClick();
		if(mItemData != null && !performed){
			mMenu.performIdentifierAction(mItemData.getItemId(), 0);
			performed = true;
		}
		return performed;
	}
	
	@SuppressLint("RtlHardcoded")
	@Override
	public boolean performLongClick()
	{
		boolean performed = super.performLongClick();
		if(mItemData != null && !performed){
			final int[] screenPos = new int[2];
			final Rect displayFrame = new Rect();
			getLocationOnScreen(screenPos);
			getWindowVisibleDisplayFrame(displayFrame);
			
			final Context context = getContext();
			final int width = getWidth();
			final int height = getHeight();
			final int midy = screenPos[1] + height / 2;
			final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
			
			Toast cheatSheet = Toast.makeText(context, mItemData.getTitle(), Toast.LENGTH_SHORT);
			if(midy < displayFrame.height()){
				// Show along the top; follow action buttons
				cheatSheet.setGravity(Gravity.TOP | Gravity.RIGHT, screenWidth - screenPos[0] - width / 2, height);
			}
			else{
				// Show along the bottom center
				cheatSheet.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
			}
			cheatSheet.show();
			performed = true;
		}
		
		return performed;
	}
	
	@Override
	protected Parcelable onSaveInstanceState()
	{
		SavedState ss = new SavedState(super.onSaveInstanceState());
		ss.count = getCount();
		return ss;
	}
	
	@Override
	protected void onRestoreInstanceState(Parcelable state)
	{
		SavedState ss = (SavedState)state;
		super.onRestoreInstanceState(ss.getSuperState());
		setCount(ss.count);
	}
	
	static class SavedState extends BaseSavedState
	{
		int count;
		
		
		protected SavedState(Parcelable superState)
		{
			super(superState);
		}
		
		protected SavedState(Parcel source)
		{
			super(source);
			count = source.readInt();
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags)
		{
			super.writeToParcel(dest, flags);
			dest.writeInt(count);
		}
		
		public static final Parcelable.Creator<SavedState> CREATOR = 
			new Parcelable.Creator<SavedState>()
		{
			public SavedState createFromParcel(Parcel source)
			{
				return new SavedState(source);
			}
			
			public SavedState[] newArray(int size)
			{
				return new SavedState[size];
			}
		};
	}
}
