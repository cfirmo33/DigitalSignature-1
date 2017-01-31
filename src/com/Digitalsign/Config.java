package com.Digitalsign;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mysign.R;

public class Config extends ListActivity implements
		android.view.View.OnClickListener {

	public static final String PreferenceName = "com.mysign";
	public static final String PKCS12 = "PKCS12";
	EditText input;
	SharedPreferences _pref;
	private ArrayList<KeyValue> mItems = new ArrayList<KeyValue>();
	private PreferenceAdapter mAdapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.config);
		mAdapter = new PreferenceAdapter(this, R.layout.pref_item, mItems);
		setListAdapter(mAdapter);

		refreshConfig();

		Button btBackMain = (Button) findViewById(R.id.btBackMain);
		btBackMain.setOnClickListener(this);
	}

	void refreshConfig() {
		mItems.clear();
		KeyValue _it = new KeyValue();

		_pref = getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
		if (_pref.contains(PKCS12)) {
			_it.setItem(PKCS12, _pref.getString(PKCS12, ""));
		} else {
			_it.setItem(PKCS12, "");
		}
		mItems.add(_it);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onListItemClick(ListView parent, View v, int position, long id) {
		KeyValue it = (KeyValue) parent.getItemAtPosition(position);
		if (it.getKey() == Config.PKCS12) {
			input = new EditText(this);

			AlertDialog.Builder signDlg = new Builder(Config.this);
			signDlg.setTitle("Nhập đường dẫn tệp chứng thư số ký");
			signDlg.setView(input);
			signDlg.setPositiveButton("Lưu", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					String path = input.getText() + "";
					_pref = getSharedPreferences(PreferenceName,
							Context.MODE_PRIVATE);
					Editor editor = _pref.edit();
					editor.putString(Config.PKCS12, path);
					editor.commit();

					refreshConfig();
				}
			});

			signDlg.setNegativeButton("Hủy",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
			signDlg.show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btBackMain) {
			finish();
		}
	}
}
