package com.cyphermessenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.EditText;
//import android.widget.ImageView;

@SuppressLint("NewApi")
public class RegistrationActivity extends Activity {

	String name,password,confirmation,captchaText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		
		//SETTING ACTIVITY TITLE
		getActionBar().setTitle("Registration");
		
		EditText nameField = (EditText) findViewById(R.id.name_field);
		EditText passwordField = (EditText) findViewById(R.id.password_field);
		EditText passwordConfirmationField = (EditText) findViewById(R.id.confirm_password_field);
		//ImageView captcha = (ImageView) findViewById(R.id.captcha_image);
		EditText captchaField = (EditText) findViewById(R.id.captcha_text_field);
		
		name = String.valueOf(nameField.getText());
		password = String.valueOf(passwordField.getText());
		confirmation = String.valueOf(passwordConfirmationField.getText());
		captchaText = String.valueOf(captchaField.getText());
		
		if(password.equals(confirmation)) {
			
		} else {
			
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}

}
