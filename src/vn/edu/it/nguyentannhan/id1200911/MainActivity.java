package vn.edu.it.nguyentannhan.id1200911;


import java.util.ArrayList;


import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {

	SQLiteDatabase sqlite;
	Button btnSave;
	EditText editFrefixNum;
	ListView lstPreNum;
	//
	//String idUpdate="";
	//
	ArrayList<String>arrPreNum;
	ArrayAdapter<String>adapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//deleteDatabase();
		createDatabase();
		init();
		event();
		loaddata();
	}
	void loaddata()
	{
		try {
			arrPreNum = new ArrayList<String>();
			sqlite = openOrCreateDatabase("smsgarbage.db", MODE_PRIVATE, null);
			final String []columns ={"id","prefixNum"};
			//Cursor cusor = sqlite.query("tblGarbage", columns , "id,prefixNum", null, null, null, "prefixNum");
			Cursor cusor = sqlite.query("tblGarbage", columns , null, null, null, null, null);
			cusor.moveToFirst();
			String data="";
			while(cusor.isAfterLast()==false)
			{
				data= (String) cusor.getString(0).toString();//+" - " + (String)cusor.getString(1).toString();
				arrPreNum.add(data);
				cusor.moveToNext();
			}
			adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_expandable_list_item_1, arrPreNum);
			lstPreNum.setAdapter(adapter);
			//adapter.notifyDataSetChanged();
					
		} catch (Exception e) 
		{
			// TODO: handle exception
			Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
		}
		
	}
	void cleartext()
	{
		editFrefixNum.setText("");
	}
	void init()
	{
		btnSave = (Button) findViewById(R.id.btnSave);
		editFrefixNum = (EditText) findViewById(R.id.editPrefixPhoneNum);
		lstPreNum = (ListView) findViewById(R.id.lstviewList);		
	}
	void event()
	{
		btnSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				insertData(editFrefixNum.getText().toString());
				loaddata();
			}
		});
		
		lstPreNum.setOnItemClickListener(new OnItemClickListener() 
		{

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3) {
				// TODO Auto-generated method stub
				editFrefixNum.setText((String)arrPreNum.get(arg2).toString());
				//idUpdate =(String)arrPreNum.get(arg2).toString();
			}
		});
		
		lstPreNum.setOnItemLongClickListener(new OnItemLongClickListener() 
		{
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				// TODO Auto-generated method stub
				AlertDialog.Builder bui = new AlertDialog.Builder(MainActivity.this);
				bui.setTitle("Cảnh báo !");
				//final int index = arg2;
				final String id= (String)arrPreNum.get(arg2).toString().trim();
				bui.setPositiveButton("OK", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//arrPreNum.remove(index);
						sqlite = openOrCreateDatabase("smsgarbage.db", MODE_PRIVATE, null);
						if(sqlite.delete("tblGarbage", "id=?", new String []{id}) != -1)
						{
							loaddata();
							cleartext();
							Toast.makeText(MainActivity.this, "Xóa thành công !", Toast.LENGTH_SHORT).show();
						}
						else
							Toast.makeText(MainActivity.this, "Xóa thất bại !", Toast.LENGTH_SHORT).show();
					}
				});
				bui.setNegativeButton("Cancel", new AlertDialog.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.cancel();
					}
				});
				
				bui.create().show();
				
				return false;
			}
			
		});
	}
	void createDatabase()
	{
		String path= Environment.getExternalStorageDirectory().getAbsolutePath()+"/smsgarbage.db";
		sqlite = openOrCreateDatabase(path, MODE_PRIVATE, null);
		
		try 
		{
			String query = "Create table tblGarbage(id text primary key , prefixNum text)";
			sqlite.execSQL(query);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	void insertData(String PrefixNum)
	{
		try {
			//sqlite = openOrCreateDatabase("qlsach.db", MODE_PRIVATE, null);
			ContentValues _values = new ContentValues();
			_values.put("id", PrefixNum);
			_values.put("prefixNum", PrefixNum);
			if(sqlite.insert("tblGarbage", null, _values) != -1)
			{
				Toast.makeText(MainActivity.this, "Thêm thành công !", Toast.LENGTH_SHORT).show();
				cleartext();
			}
			else
				Toast.makeText(MainActivity.this, "Thất bại !", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(MainActivity.this, "Thất bại hoàn toàn !", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	void update(String idUpdate)
	{
		sqlite = openOrCreateDatabase("smsgarbage.db", MODE_PRIVATE, null);
		ContentValues values = new ContentValues();
		//values.put("id", idUpdate);
		values.put("prefixNum", editFrefixNum.getText().toString());
		if(sqlite.update("tblGarbage", values, "id=?", new String[]{idUpdate}) != -1)
		{						
			loaddata();
			Toast.makeText(MainActivity.this, "Cập nhật thành công !", Toast.LENGTH_SHORT).show();
		}
		else
			Toast.makeText(MainActivity.this, "Cập nhật thất bại !", Toast.LENGTH_SHORT).show();
	}
	void deleteDatabase()
	{
		if(deleteDatabase("smsgarbage.db") == true)
			Toast.makeText(MainActivity.this, "Xóa database [smsgarbage] thành công !", Toast.LENGTH_LONG).show();
	}
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/*************************************************************************************************************************/
	MediaRecorder audioRecorder = null;
	public void doStartRecord()
	{		
		try 
		{
			if(audioRecorder == null)
				audioRecorder = new MediaRecorder();//step1
			String saveto=Environment.getDataDirectory().getAbsolutePath()+"/myrecord.mp5";
			audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);//step 2
			audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);//step3
			audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);//step4
			audioRecorder.setOutputFile(saveto);
			audioRecorder.prepare();//step 6
			audioRecorder.start();//step 7
		} catch (Exception e) 
		{
			// TODO: handle exception
		}	
	}
	public void doStopRecording()
	{
		if(audioRecorder != null)
		{
			audioRecorder.stop();//step 8
			audioRecorder.release();
			audioRecorder = null;
		}
	}
	
	void doPlayMusic()
	{
		MediaPlayer player = new MediaPlayer();
		try 
		{
			String saveto= Environment.getDataDirectory().getAbsolutePath()+"/myaudio.mp3";
			player.setDataSource(saveto);
			player.prepare();
			player.start();
			
			/*
			 * player.stop();
			 * player.release();
			 * player = null;
			 * */
			
		} catch (Exception e) 
		{
			// TODO: handle exception
		}
	}
	
	/*************************************************************************************************************************/
	
	
}
