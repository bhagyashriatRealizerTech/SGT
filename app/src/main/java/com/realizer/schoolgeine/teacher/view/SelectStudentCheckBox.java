//===============================================================================
// (c) 2015 eWorkplace Apps.  All rights reserved.
// Original Author: Dheeraj Nagar
// Original Date: 12 June 2015
//===============================================================================
package com.realizer.schoolgeine.teacher.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.CheckBox;

public class SelectStudentCheckBox extends CheckBox {

	public SelectStudentCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SelectStudentCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SelectStudentCheckBox(Context context) {
		super(context);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyShortcut(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return false;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		return false;
	}
}
