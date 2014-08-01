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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends Activity implements OnClickListener {

	private Button scanBtn;
	private TextView formatTxt, contentTxt;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// setActivityBackgroundColor(555);
		scanBtn = (Button) findViewById(R.id.scan_button);
		formatTxt = (TextView) findViewById(R.id.scan_format);
		contentTxt = (TextView) findViewById(R.id.scan_content);
		scanBtn.setOnClickListener(this);
		setTitle("Shop&Experience Reader");
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
