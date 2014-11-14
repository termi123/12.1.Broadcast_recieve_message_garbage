package vn.edu.it.nguyentannhan.id1200911;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;





public class MySmsReceive extends BroadcastReceiver {
	public static final String SMS_URI= "content://sms/inbox";
	public static final String BODY= "body";
	public static final String ADDRESS= "address";
	//BroadcastReceiver receive =null;
	void BroadCastReceive()
	{
		//IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");		
		/*receive = new BroadcastReceiver()
		{

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				
			}
			
		};*/
		//receive = new BroadcastReceiver();
	}
	@Override
	public void onReceive(Context context, Intent intent)
	{
		// TODO Auto-generated method stub
		processReceive(context, intent);

	}
	void processReceive(Context context, Intent intent)
	{
		Bundle extras = intent.getExtras();
		String message ="";
		if(extras != null)
		{
			Object []smsExtra = (Object[])extras.get("pdus");
			for (int i = 0; i < smsExtra.length; i++) 
			{
				SmsMessage sms = SmsMessage.createFromPdu((byte[])smsExtra[i]);
				String body = sms.getMessageBody();
				String address = sms.getOriginatingAddress();
				message +="SMS From: "+ address +" :\n"+body+"\n"; 
				XoaToanBoSms(context, address);
			}
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
	}

	public boolean checkDumpNumber(String f)
	{
		SQLiteDatabase sqlite;
		String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/smsgarbage.db";
		sqlite=	SQLiteDatabase.openOrCreateDatabase(path, null);
		Cursor c = sqlite.query("tblGarbage", null, "prefixNum=?", new String[]{f}, null, null, null, null);
		c.moveToFirst();
		return c.isAfterLast();
	}
	void XoaToanBoSms(Context con,  String phoneNumber)
	{
		try {
			/*for (int i = 0; i < msgs.length; i++) 
			{
				if(msgs.getOriginatingAddress().equals(phoneNumber)==false)
				{
					continue;
				}*/
				if(checkDumpNumber(phoneNumber))
				{
					con.getContentResolver().delete(Uri.parse("content://sms"), "address=?", new String[]{phoneNumber});
					Toast.makeText(con, "Xóa rồi đó " + phoneNumber, Toast.LENGTH_SHORT).show();
				}
				else
				{
					Toast.makeText(con, "Khong tìm thấy !" + phoneNumber, Toast.LENGTH_SHORT).show();
				}
			//}
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(con, "Xóa rồi đó " + e, Toast.LENGTH_SHORT).show();
		}
	}
	
	void XoaSmsMoiNhat(Context con, SmsMessage[]msgs, String phoneNumber)
	{
		try {
			for (int i = 0; i < msgs.length; i++) {
				if(msgs[i].getOriginatingAddress().equals(phoneNumber)==false)
				{
					continue;
				}
				con.getContentResolver().delete(Uri.parse("content://sms"), "address=? and date=?", 
						new String []{phoneNumber,String.valueOf(msgs[i].getTimestampMillis())});
				Toast.makeText(con, "Xóa rồi đó "+msgs[i].getOriginatingAddress(), Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(con, "Xóa rồi đó "+e, Toast.LENGTH_SHORT).show();
		}
	}
	
}
