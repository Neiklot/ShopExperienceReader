package com.example.shopexperiencereader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shopexperiencereader.dialogs.AlertDialogManager;
import com.example.shopexperiencereader.loginsupport.SessionManager;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity implements OnClickListener {

	private Button scanBtn;
	private TextView formatTxt, contentTxt;
	
	// Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
    
    // Session Manager Class
    SessionManager session;
    
 // Button Logout
    Button btnLogout;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);       
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
     // Session class instance
        session = new SessionManager(getApplicationContext());
        
        TextView lblName = (TextView) findViewById(R.id.lblName);
        TextView lblEmail = (TextView) findViewById(R.id.lblEmail);
         
        // Button logout
        btnLogout = (Button) findViewById(R.id.btnLogout);
         
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
         
         
        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */
        session.checkLogin();
        if (!session.isLoggedIn()) {
        	finish();
        	}
		scanBtn = (Button) findViewById(R.id.scan_button);
		formatTxt = (TextView) findViewById(R.id.scan_format);
		contentTxt = (TextView) findViewById(R.id.scan_content);
		scanBtn.setOnClickListener(this);
		setTitle("Shop&Experience Reader");
		
		btnLogout.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                // Clear the session data
                // This will clear all session data and 
                // redirect user to LoginActivity
                session.logoutUser();
            }
        });
	}

	public void sendForm(View v) throws MalformedURLException {
		FormSender fs = new FormSender();
		fs.execute();
	}

	public void setActionBar() {

	}

	public void setActivityBackgroundColor(int color) {
		View view = this.getWindow().getDecorView();
		view.setBackgroundColor(color);
	}

	public void onClick(View v) {
		if (v.getId() == R.id.scan_button) {
			IntentIntegrator scanIntegrator = new IntentIntegrator(this);
			scanIntegrator.initiateScan();
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanningResult = IntentIntegrator.parseActivityResult(
				requestCode, resultCode, intent);
		if (scanningResult != null) {
			String scanContent = scanningResult.getContents();
			String scanFormat = scanningResult.getFormatName();
			formatTxt.setText("FORMAT: " + scanFormat);
			contentTxt.setText("CONTENT: " + scanContent);
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"No scan data received!", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private class FormSender extends AsyncTask<URL, Integer, Long> {

		public void sendForm(View v) { // Método enviarDatos
			try {

				// Crear un cliente por defecto
				HttpClient mClient = new DefaultHttpClient();

				// Indicar la url
				StringBuilder sb = new StringBuilder(
						"http://192.168.1.128:8080/shopExperience/reading");

				// Establecer la conexión después de indicar la url
				HttpPost mpost = new HttpPost(sb.toString());

				// NameValuePair : Es una clase simple que encapsula el nombre
				// del atributo y el valor que contiene.
				// Creamos una lista de 8 atributos
				List nameValuepairs = new ArrayList(2);

//				contentTxt=(TextView) findViewById(R.id.scan_format);
				
				// Añadimos los elementos a la lista
				nameValuepairs.add(new BasicNameValuePair("scanFormat", contentTxt.getText().toString()));
				nameValuepairs.add(new BasicNameValuePair("scanContent", "b"));

				// UrlEncodedFormEntity : Codificamos la lista para el envio por
				// post
				mpost.setEntity(new UrlEncodedFormEntity(nameValuepairs));

				// Ejecutamos la solicitud y obtenemos una respuesta
				HttpResponse responce = mClient.execute(mpost);

				// Obtenemos el contenido de la respuesta
				HttpEntity entity = responce.getEntity();

				// Convertimos el stream a un String
				BufferedReader buf = new BufferedReader(new InputStreamReader(
						entity.getContent()));
				StringBuilder sb1 = new StringBuilder();
				String line = null;

				while ((line = buf.readLine()) != null) {
					sb1.append(line + "\r\n");
				}

			} catch (Exception e) {
				// Si se produce un error, lo mostramos
				Log.w(" error ", e.toString() + " " + e.getCause());
			}
		}

		@Override
		protected Long doInBackground(URL... params) {
			this.sendForm(getWindow().getDecorView());
			return null;
		}
	}

}
