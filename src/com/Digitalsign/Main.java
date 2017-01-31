package com.Digitalsign;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;

import org.spongycastle.asn1.DERUTCTime;
import org.spongycastle.asn1.cms.Attribute;
import org.spongycastle.asn1.cms.AttributeTable;
import org.spongycastle.asn1.cms.CMSAttributes;
import org.spongycastle.asn1.x500.RDN;
import org.spongycastle.asn1.x500.X500Name;
import org.spongycastle.asn1.x500.style.BCStyle;
import org.spongycastle.asn1.x500.style.IETFUtils;
import org.spongycastle.cert.X509CertificateHolder;
import org.spongycastle.cert.jcajce.JcaX509CertificateConverter;
import org.spongycastle.cert.jcajce.JcaX509CertificateHolder;
import org.spongycastle.cms.CMSProcessableByteArray;
import org.spongycastle.cms.CMSSignedData;
import org.spongycastle.cms.CMSSignedDataGenerator;
import org.spongycastle.cms.CMSSignedGenerator;
import org.spongycastle.cms.SignerInformation;
import org.spongycastle.cms.SignerInformationStore;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.util.Store;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.mysign.R;

public class Main extends ListActivity implements android.view.View.OnClickListener {
	
	private File mCurrentNode = null;
	private File mLastNode = null;
	private File mRootNode = null;
	private ArrayList<File> mFiles = new ArrayList<File>();
	private CustomAdapter mAdapter = null;
	EditText input;
	File selectedFile;
    
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hinh2);
        mAdapter = new CustomAdapter(this, R.layout.list_row, mFiles);
	    setListAdapter(mAdapter);
	    if (savedInstanceState != null) {
	    	mRootNode = (File)savedInstanceState.getSerializable("root_node");
	    	mLastNode = (File)savedInstanceState.getSerializable("last_node");
	    	mCurrentNode = (File)savedInstanceState.getSerializable("current_node");
	    }
	    refreshFileList();
	    	    
	    registerForContextMenu(getListView());
	    
	    Button btBackMain = (Button) findViewById(R.id.btBackMain);
	    btBackMain.setOnClickListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
    		ContextMenuInfo menuInfo) {
    	if (v.getId()==getListView().getId()) {
    	    AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
    	    if(mFiles.get(info.position).isFile())
    	    {
    	    	menu.setHeaderTitle(mFiles.get(info.position).getName());
    	    	String[] menuItems = new String[]{"Ký số", "Xác thực", "Chọn làm chứng thư số ký", "Gửi file đã ký"}; 
    	    	for (int i = 0; i<menuItems.length; i++) {
    	    		menu.add(Menu.NONE, i, i, menuItems[i]);
    	    	}
    	    }
    	}
    }
    public static String getExtension(String fileName) {
    	String ext = "";
    	fileName = fileName.toLowerCase(); 
    	try 
    	{ 
    		ext = MimeTypeMap.getFileExtensionFromUrl(fileName); 
    	} 
    	catch (Exception e) 
    	{ 
    		e.printStackTrace(); 
    	}
    	return ext;
    }
    
    byte[] readFile(String path)
	{
		File file = new File(path);
	    int size = (int) file.length();
	    byte[] bytes = new byte[size];
	    try {
	        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
	        buf.read(bytes, 0, bytes.length);
	        buf.close();
	        return bytes;
	    } catch (FileNotFoundException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return null;
	}
    
    String Sign(char[] pass, String filename, String saveto)
	{			
    	String _result = "";
		try {
			SharedPreferences _pref = getSharedPreferences(Config.PreferenceName, Context.MODE_PRIVATE);
			String keyPath = _pref.getString(Config.PKCS12, "");
			
			InputStream in = new BufferedInputStream(new FileInputStream(keyPath));
			
			KeyStore keystore = KeyStore.getInstance("PKCS12");
		    keystore.load(in, pass);

		    PrivateKey prk = null;
		    Enumeration enum1 = keystore.aliases();
		    String sAlias = (String) enum1.nextElement();
		    while(enum1.hasMoreElements())
		    	sAlias = (String) enum1.nextElement();
	        //String sAlias = (String) enum1.nextElement();
	        prk = (PrivateKey) (keystore.getKey(sAlias, pass));
	        X509Certificate c = (X509Certificate)keystore.getCertificate(sAlias);
	        	        
	        X500Name x500name = new JcaX509CertificateHolder(c).getSubject();	        
	        RDN cn = x500name.getRDNs(BCStyle.CN)[0];	        	        
	        _result += "Ngươi ký: " + IETFUtils.valueToString(cn.getFirst().getValue()) + "\r\n";
	        	        
	        x500name = new JcaX509CertificateHolder(c).getIssuer();
	        cn = x500name.getRDNs(BCStyle.CN)[0];
	        _result += "Cơ quan cấp phát: " + IETFUtils.valueToString(cn.getFirst().getValue()) + "\r\n";
	        
	        byte[] buff =  readFile(filename);
	        byte[] signature = null;
	        ArrayList certList = new ArrayList();
            certList.add(c);

            CertStore certs = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), new BouncyCastleProvider());
            
            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            gen.addCertificatesAndCRLs(certs);
            gen.addSigner(prk, c, CMSSignedGenerator.DIGEST_SHA256);
            
            CMSSignedData signedData = gen.generate(new CMSProcessableByteArray(buff), true, new BouncyCastleProvider());
            signature = signedData.getEncoded();            
            _result += "Th�?i gian ký: " + (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(Calendar.getInstance().getTime())) + "\n\r";
                       
            File f = new File(saveto);
            FileOutputStream fo = null;
            try
            {
            	fo = new FileOutputStream(f);
            	fo.write(signature);
            }
            finally
            {
            	try{
            		if(fo!=null)
            		{
            			fo.close();
            		}
            	}catch(IOException e)
            	{}        	       
            }
            _result += "Tệp ký số: " + saveto + "\n\r";
            
		} catch (Exception e) {
			// TODO Auto-generated catch block
			_result += "Lối: " + e.getMessage();
			e.printStackTrace();
		}
		
		return _result;
	}
    
    public static String getFilename(String filePath) {
    	String fname = filePath.replace(".p7s", "");
    	String sufix = new SimpleDateFormat("ddMMyyyyHHmmss").format(Calendar.getInstance().getTime());       
        // if there is no extention, don't do anything
        if (!fname.contains(".")) return fname + sufix;
        // Otherwise, remove the last 'extension type thing'
        return fname.substring(0, fname.lastIndexOf('.')) + sufix + fname.substring(fname.lastIndexOf('.'));
    }
    
    void Verify(String filename)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Thông tin xác thực");
		builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
		
			@Override
			public void onClick(DialogInterface dialog, int which) {		
				dialog.cancel();
				refreshFileList();
			}
		});
		
		String msg = "";
		try
		{
			byte[] signature = readFile(filename);
			String signTime = "";
			X509Certificate usercert = null;
			byte[] content = null;
			
			CMSSignedData cms = new CMSSignedData(signature);
			//CertStore certs = cms.getCertificatesAndCRLs("Collection", new BouncyCastleProvider());
			Store certs = cms.getCertificates();
			SignerInformationStore signerInfos = cms.getSignerInfos();
			
			Format formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			
			Collection<SignerInformation> signers = signerInfos.getSigners();
			for (SignerInformation signerInformation : signers) 
			{
	            AttributeTable signedAttr = signerInformation.getSignedAttributes();
	            //get signing time
	            Attribute signingTime = signedAttr.get(CMSAttributes.signingTime);	            
				if (signingTime != null) {
	                Enumeration en = signingTime.getAttrValues().getObjects();
	                while (en.hasMoreElements()) {
	                    Object obj = en.nextElement();
	                    System.out.println(obj.getClass().getName());
	                    if (obj instanceof DERUTCTime) {
	                        try {
	                            DERUTCTime derTime = (DERUTCTime) obj;
	                            System.out.println("Test2:" + derTime.getDate());
	                            signTime = formatter.format(derTime.getDate()).toString();
	                        } catch (ParseException ex) {
	                            ex.printStackTrace();
	                            signTime = "";
	                        }
	                    }
	                }
	            } else {
	                System.out.println("No signature time found!");
	                signTime = "";
	            }
				
				
			}					
			
			//get content
			String saveto = getFilename(filename);			
	        
	        File f = new File(saveto);
            FileOutputStream fo = null;
            try
            {
            	fo = new FileOutputStream(f);
            	((CMSProcessableByteArray)cms.getSignedContent()).write(fo);            	
            }
            finally
            {
            	try{
            		if(fo!=null)
            		{
            			fo.close();
            		}
            	}catch(IOException e)
            	{}        	       
            }
	        	        	       
	        boolean verifies = false;
	        Collection c = signerInfos.getSigners();
	        Iterator it = c.iterator();	        
	        while (it.hasNext()) {
	            SignerInformation signer = (SignerInformation) it.next();
	            Collection certCollection = certs.getMatches(signer.getSID());
	            //Collection certCollection = certs.getCertificates(null);
	            Iterator certIt = certCollection.iterator();
	            X509CertificateHolder holder = (X509CertificateHolder)certIt.next();
	            usercert = new JcaX509CertificateConverter().setProvider("BC").getCertificate(holder);
	            //SignerInformationVerifier siv = new JcaSimpleSignerInfoVerifierBuilder().setProvider("BC").build(holder);	            
	            //usercert = (X509Certificate) certIt.next();
	            boolean validalg = signer.getDigestAlgOID().equals(CMSSignedGenerator.DIGEST_SHA256);	           
	            verifies = validalg && signer.verify(usercert.getPublicKey(), new BouncyCastleProvider());
	        }
	        
	        X500Name x500name = new JcaX509CertificateHolder(usercert).getSubject();
	        RDN cn = x500name.getRDNs(BCStyle.CN)[0];	        
	        
	        msg += "Ngươi ký: " + IETFUtils.valueToString(cn.getFirst().getValue()) + "\r\n";
	        msg += "Thời gian ký: " + signTime.toString() + "\r\n";
	        msg += "Tệp: " + saveto + "\r\n";
	        msg += "Tình trạng chữ ký: " + (verifies?"Hợp lệ":"Không hợp lệ");
			
		}catch(Exception e)
		{
			msg = "Cảnh báo: Nội dung file đã bị thay đổi";
		}		
		builder.setMessage(msg);
		builder.create().show();
	}
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
	    int menuItemIndex = item.getItemId();
	    selectedFile = mFiles.get(info.position);
	    if(menuItemIndex==0)
	    {	    	
	    	input = new EditText(this);
	    	input.setHint("Mật khẩu");	    	
	    	input.setTransformationMethod(PasswordTransformationMethod.getInstance());
	    	input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
	    	
	    	AlertDialog.Builder signDlg = new Builder(Main.this);
	    	signDlg.setTitle("Nhập mật khẩu truy cập khóa ký");
	    	signDlg.setView(input);
	    	signDlg.setPositiveButton("OK", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					//Toast.makeText(Main.this, tokenPassword.getText().toString() + " Ky so "+selectedFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
					
					char[] pass = input.getText().toString().toCharArray();
					String filename = selectedFile.getAbsolutePath();
					String saveto = filename + ".p7s";
					
					String res = Sign(pass, filename, saveto);
					
					AlertDialog.Builder builder = new AlertDialog.Builder(Main.this);
					builder.setTitle("Thông tin ký số");
					builder.setPositiveButton("Đóng", new DialogInterface.OnClickListener() {
					
						@Override
						public void onClick(DialogInterface dialog, int which) {		
							dialog.cancel();
							refreshFileList();
						}
					});					
					builder.setMessage(res);
					builder.create().show();
				}
			});
	    	
	    	signDlg.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.cancel();
	            }
	        });
	    	signDlg.show();
	    	
	    	
	    }else if(menuItemIndex==1)
	    {
	    	//Toast.makeText(this, "Xác thực "+selectedFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
	    	Verify(selectedFile.getAbsolutePath());
	    }else if(menuItemIndex==2)
	    {	    	
	    	String ext = getExtension(selectedFile.getAbsolutePath());
	    	//Toast.makeText(this, "EXT: "+ ext, Toast.LENGTH_SHORT).show();
	    	if(ext.equalsIgnoreCase("p12") || ext.equalsIgnoreCase("pfx"))
	    	{
	    		SharedPreferences _pref = getSharedPreferences(Config.PreferenceName, Context.MODE_PRIVATE);
				Editor editor = _pref.edit();
				editor.putString(Config.PKCS12, selectedFile.getAbsolutePath());
				editor.commit();
				Toast.makeText(this, "Lưu cấu hình thành công!", Toast.LENGTH_SHORT).show();
	    	}
	    	else
	    	{
	    		Toast.makeText(this, "Tệp không đúng định dạng chứng thư số", Toast.LENGTH_SHORT).show();
	    	}
	    }
	    else if(menuItemIndex==3)
	    {	    	
	    	Intent i = new Intent(Intent.ACTION_SEND);
	    	i.putExtra(Intent.EXTRA_SUBJECT, "Title");
	    	i.putExtra(Intent.EXTRA_TEXT, "Content");
	    	i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(selectedFile));
	    	i.setType("*/*");
	    	startActivity(Intent.createChooser(i, "Send mail"));
	    }
	    	
    	return true;
    }
    
    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menumain, menu);
		return true;
	}
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {    	
    	switch(item.getItemId())
    	 {
    	 	case R.id.action_settings:
    	 		//Toast.makeText(this, "Cấu hình", Toast.LENGTH_SHORT).show();
    	 		Intent it = new Intent(Main.this, Config.class);
				startActivity(it);
    	 	break;
    	 }
    	return super.onOptionsItemSelected(item);
    }
    
    private void refreshFileList() {
		if (mRootNode == null) mRootNode = new File(Environment.getExternalStorageDirectory().toString());
		if (mCurrentNode == null) mCurrentNode = mRootNode; 
		mLastNode = mCurrentNode;
		File[] files = mCurrentNode.listFiles();
		mFiles.clear();
		mFiles.add(mRootNode);
		mFiles.add(mLastNode);
		if (files!=null) {
			for (int i = 0; i< files.length; i++) mFiles.add(files[i]);
		}
		mAdapter.notifyDataSetChanged();
	}
    
    @Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putSerializable("root_node", mRootNode);
		outState.putSerializable("current_node", mCurrentNode);
		outState.putSerializable("last_node", mLastNode);
		super.onSaveInstanceState(outState);
	} 
    
    /**
     * Listview on click handler.
     */
	@Override
	public void onListItemClick(ListView parent, View v, int position, long id){   
		File f = (File) parent.getItemAtPosition(position);
		if (position == 1) {
			if (mCurrentNode.compareTo(mRootNode)!=0) {
				mCurrentNode = f.getParentFile();
				refreshFileList();
			}
		} else if (f.isDirectory()) {
			mCurrentNode = f;
			refreshFileList();
		} else {
			
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.btBackMain){
			finish();
		}
	}
}