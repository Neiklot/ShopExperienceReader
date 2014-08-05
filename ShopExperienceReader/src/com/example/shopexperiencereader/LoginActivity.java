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
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.shopexperiencereader.dialogs.AlertDialogManager;
import com.example.shopexperiencereader.loginsupport.SessionManager;

public class LoginActivity extends Activity {

	// Email, password edittext
	EditText txtUsername, txtPassword;

	// login button
	Button btnLogin;

	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();

	// Session Manager Class
	SessionManager session;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.detectAll().penaltyLog().build();
		StrictMode.setThreadPolicy(policy);

		// Session Manager
		session = new SessionManager(getApplicationContext());

		// Email, Password input text
		txtUsername = (EditText) findViewById(R.id.txtUsername);
		txtPassword = (EditText) findViewById(R.id.txtPassword);

		Toast.makeText(getApplicationContext(),
				"User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG)
				.show();

		// Login button
		btnLogin = (Button) findViewById(R.id.btnLogin);

		// Login button click event
		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Get username, password from EditText
				String username = txtUsername.getText().toString();
				String password = txtPassword.getText().toString();

				if (username.trim().length() > 0
						&& password.trim().length() > 0) {
					
				 	ValidateLogin validateLogin=new ValidateLogin();
					URL url1;
					
					try {
						url1 = new URL("http://localhost:8080/shopExperience/reading");
						validateLogin.execute(url1);
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
			    	
					 if (username.equals("test") && password.equals("test")) {
//					try {
//						if (httpLoginRequest()) {

							// Creating user login session
							// For testing i am stroing name, email as follow
							// Use user real data
							session.createLoginSession("Android Hive",
									"anroidhive@gmail.com");

							// Staring MainActivity
							Intent i = new Intent(getApplicationContext(),
									MainActivity.class);
							startActivity(i);
							finish();

						} else {
							// username / password doesn't match
							alert.showAlertDialog(LoginActivity.this,
									"Login failed..",
									"Username/Password is incorrect", false);
						}
//					} catch (Exception e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
				} else {
					// user didn't entered username or password
					// Show alert asking him to enter the details
					alert.showAlertDialog(LoginActivity.this, "Login failed..",
							"Please enter username and password", false);
				}

			}
		});
	}

	private class ValidateLogin extends AsyncTask <URL, Integer, Long> {
   	 
        public void sendForm(View v) { // Método enviarDatos
            try {
     
                // Crear un cliente por defecto
                HttpClient mClient = new DefaultHttpClient();
     
                // Indicar la url
                StringBuilder sb=new StringBuilder("http://127.0.0.1:8080/shopExperience/loginFromReader");
     
                // Establecer la conexión después de indicar la url
                HttpPost mpost = new HttpPost(sb.toString());
     
                // NameValuePair : Es una clase simple que encapsula el nombre del atributo y el valor que contiene.
                // Creamos una lista de 8 atributos
                List nameValuepairs = new ArrayList(2);
     
                // Añadimos los elementos a la lista
                nameValuepairs.add(new BasicNameValuePair("userName",txtUsername.getText().toString()));
     
                // UrlEncodedFormEntity : Codificamos la lista para el envio por post
                mpost.setEntity(new UrlEncodedFormEntity(nameValuepairs));
     
                // Ejecutamos la solicitud y obtenemos una respuesta
                HttpResponse responce = mClient.execute(mpost);
     
                //  Obtenemos el contenido de la respuesta
                HttpEntity entity = responce.getEntity();
     
                // Convertimos el stream a un String
                BufferedReader buf = new BufferedReader(new InputStreamReader(entity.getContent()));
                StringBuilder sb1 = new StringBuilder();
                String line = null;
     
                while ((line = buf.readLine()) != null) {
                    sb1.append(line+"\r\n");
                }
     
            } catch (Exception e) {
                //Si se produce un error, lo mostramos
                Log.w(" error ", e.toString()+" "+e.getCause());
            }
        }

		@Override
		protected Long doInBackground(URL... params) {
			this.sendForm(getWindow().getDecorView());
			return null;
		}
    }
 }
