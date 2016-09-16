package com.realizer.schoolgeine.teacher.exceptionhandler;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.realizer.schoolgenie.teacher.R;

public class AnotherActivity extends Activity {

	TextView error;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
		setContentView(R.layout.activity_excption);

		error = (TextView) findViewById(R.id.error);

		error.setText(getIntent().getStringExtra("error"));
	}
}
