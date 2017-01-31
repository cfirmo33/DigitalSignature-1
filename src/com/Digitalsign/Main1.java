package com.Digitalsign;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.mysign.R;

public class Main1 extends ActionBarActivity {
	final Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		intview();
		
		Button btQuit = (Button) findViewById(R.id.btQuit);
		btQuit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doQuit();
			}
		});
		
		Button btConfig = (Button) findViewById(R.id.btConfig);
		btConfig.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Main1.this, Config.class);
				startActivity(intent);
			}
		});
		
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		try {
//
//			// Doi mau chu Title
//			int titleId = getResources().getIdentifier("action_bar_title",
//					"id", "android");
//			TextView yourTextView = (TextView) findViewById(titleId);
//			yourTextView.setTextColor(getResources().getColor(
//					R.color.text_title));
//			// Doi mau nen title
//			getSupportActionBar().setBackgroundDrawable(
//					new ColorDrawable(getResources().getColor(
//							R.color.background_title)));
//
//			getSupportActionBar().setHomeButtonEnabled(true);
//			getMenuInflater().inflate(R.menu.menumain, menu);
//		} catch (Exception e) {
//		}
//		return true;
//	}
//	
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		int id = item.getItemId();
//		if (id == android.R.id.home) {
//			doQuit();
//		}
//		if (id == R.id.action_settings) {
//			Intent intent = new Intent(this, Config.class);
//			startActivity(intent);
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}

	public void intview() {
		final String strValue[] = { "Ký số", "Xác thực",
				"Chọn chứng thư số", "Gửi file đã ký", "Thoát" };
		ListView lv = (ListView) findViewById(R.id.listMain);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(Main1.this,
				android.R.layout.simple_list_item_1, strValue);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View agr1, int arg2,
					long arg3) {
				if (arg2 == 0) {
					String s = "Chọn đường dẫn đến file cần ký";
					Toast.makeText(getApplication(), s, Toast.LENGTH_SHORT)
							.show();
			       doOpenIntent();
				}
				if (arg2 == 1) {
					String s = "Chọn đường dẫn đến file cần xác thực";
					Toast.makeText(getApplication(), s, Toast.LENGTH_SHORT)
							.show();
                    doOpenIntent();
				}

				if (arg2 == 2) {
					String s = "Chọn đường dẫn đến file chứng thư số";
					Toast.makeText(getApplication(), s, Toast.LENGTH_SHORT)
							.show();
					doOpenIntent();
				}
				if (arg2 == 3) {
					String s = "Chọn đường dẫn đến file đã ký(.p7s) bạn muốn gửi";
					Toast.makeText(getApplication(), s, Toast.LENGTH_SHORT)
							.show();
					doOpenIntent();
				}
				if (arg2 == 4) {
					doQuit();
//					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//							context);
//
//					// set title
//					alertDialogBuilder.setTitle("Digital Signature");
//
//					// set dialog message
//					alertDialogBuilder
//							.setMessage("Bạn muốn thoát khỏi phần mềm ?")
//							.setCancelable(false)
//							.setPositiveButton("Yes",
//									new DialogInterface.OnClickListener() {
//										public void onClick(
//												DialogInterface dialog, int id) {
//											// if this button is clicked, close
//											// current activity
//											Main1.this.finish();
//										}
//									})
//							.setNegativeButton("No",
//									new DialogInterface.OnClickListener() {
//										public void onClick(
//												DialogInterface dialog, int id) {
//											// if this button is clicked, just
//											// close
//											// the dialog box and do nothing
//											dialog.cancel();
//										}
//									});
//
//					// create alert dialog
//					AlertDialog alertDialog = alertDialogBuilder.create();
//
//					// show it
//					alertDialog.show();
				}
			}

		});

	}
	public void doOpenIntent(){
	    Intent intent = new Intent(this, Main.class);
		startActivity(intent);
	}
	
	public void doQuit(){
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle("Digital Signature");

		// set dialog message
		alertDialogBuilder
				.setMessage("Bạn muốn thoát khỏi phần mềm ?")
				.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog, int id) {
								// if this button is clicked, close
								// current activity
								Main1.this.finish();
							}
						})
				.setNegativeButton("No",
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialog, int id) {
								// if this button is clicked, just
								// close
								// the dialog box and do nothing
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
}
