package com.Digitalsign;

import java.util.ArrayList;

import com.mysign.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PreferenceAdapter extends ArrayAdapter<KeyValue> {
	
	private ArrayList<KeyValue> items;
	private Context c = null;
	
	public PreferenceAdapter(Context context, int textViewResourceId, ArrayList<KeyValue> items) {
		 super(context, textViewResourceId, items);
		 this.items = items;
		 this.c = context;
		 
	 }
	
	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) {
		 
		 View v = convertView;
		 
		 if (v == null) {
			 LayoutInflater vi = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			 v = vi.inflate(R.layout.pref_item, null);
		 }
		 
		 TextView title = null;
		 TextView desc = null;
		 
		 KeyValue it = items.get(position);
		 if(it!=null)
		 {
			 title = (TextView)v.findViewById(R.id.title);
			 desc = (TextView)v.findViewById(R.id.description);
		 }
		 		
		 if(it.getKey() == Config.PKCS12)
		 {
			 title.setText("Chứng thư số ký");
			 if(it.getValue() != "")
			 {
				 desc.setText(it.getValue());
			 }
			 else
			 {
				 desc.setText("");
				 desc.setHint("Chưa thiết lập");
			 }
		 }
				 
		 return v;
	 }
}
