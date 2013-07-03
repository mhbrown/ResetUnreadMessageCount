package com.brownhouse.resetunreadmessagecount;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.brownhouse.resetunreadmessagecount.R;

public class MainActivity extends Activity {
	private BroadcastReceiver mReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerBroadcastReceiver();
		refreshInfo();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterBroadcastReceiver();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return false;
	}

	public void onClickMarkAllMessagesRead(View view) {
		final Uri SMS_INBOX_URI = Uri.parse("content://sms");
		Cursor cursor = getContentResolver().query(SMS_INBOX_URI, null,
				"read = 0", null, null);
		int unreadMessagesCount = cursor.getCount();
		markAllMessagesInCursorRead(SMS_INBOX_URI);
		cursor.close();
		Toast.makeText(this,
				"Marked " + unreadMessagesCount + " SMS messages as read.",
				Toast.LENGTH_SHORT).show();

		final Uri MMS_INBOX_URI = Uri.parse("content://mms");
		cursor = getContentResolver().query(MMS_INBOX_URI, null, "read = 0",
				null, null);
		unreadMessagesCount = cursor.getCount();
		markAllMessagesInCursorRead(MMS_INBOX_URI);
		cursor.close();
		Toast.makeText(this,
				"Marked " + unreadMessagesCount + " MMS messages as read.",
				Toast.LENGTH_SHORT).show();
		
		refreshInfo();
	}

	private void refreshInfo() {
		final Uri SMS_INBOX_URI = Uri.parse("content://sms");
		Cursor cursor = getContentResolver().query(SMS_INBOX_URI, null,
				"read = 0", null, null);
		int smsUnreadMessagesCount = cursor.getCount();
		cursor.close();

		final Uri MMS_INBOX_URI = Uri.parse("content://mms");
		cursor = getContentResolver().query(MMS_INBOX_URI, null, "read = 0",
				null, null);
		int mmsUnreadMessagesCount = cursor.getCount();
		cursor.close();

		TextView infoText = (TextView) findViewById(R.id.text_info);
		infoText.setText("\nNumber of SMS Messages Marked Unread: "
				+ smsUnreadMessagesCount
				+ "\nNumber of MMS Messages Marked Unread: "
				+ mmsUnreadMessagesCount + "\n");
	}

	private void markAllMessagesInCursorRead(Uri uri) {
		ContentValues values = new ContentValues();
		values.put("read", true);
		getContentResolver().update(uri, values, "read = 0", null);
	}
	
	private void registerBroadcastReceiver() {
		IntentFilter intentFilter = new IntentFilter(
				"android.provider.Telephony.SMS_RECEIVED");
		intentFilter.addAction("android.provider.Telephony.MMS_RECEIVED");
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
            	Toast.makeText(context, "Got Message", Toast.LENGTH_SHORT).show();
            	refreshInfo();
            }
        };
        registerReceiver(mReceiver, intentFilter);
	}
	
	private void unregisterBroadcastReceiver() {
		unregisterReceiver(mReceiver);
	}

}
