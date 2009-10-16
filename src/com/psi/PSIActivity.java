package com.psi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.psi.model.PSIModel;
import com.psi.utils.Constants;

public class PSIActivity extends Activity {
	private static final int[] CAKE_IMGS = { R.drawable.cake1, R.drawable.cake2, R.drawable.cake3, R.drawable.cake4 };
	private static final int MENU_SETTINGS = Menu.FIRST;
	private static final int MENU_HELP = MENU_SETTINGS + 1;
	private static final int MENU_ABOUT = MENU_HELP + 1;
	private static final int MENU_EXIT = MENU_ABOUT + 1;

	private PSIView psiView;
	private PSIModel psiModel;
	private SharedPreferences settings;
	private SimpleDateFormat storeDateFormat;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.psi);
		storeDateFormat = new SimpleDateFormat(getString(R.string.date_store_format));
		settings = getSharedPreferences(Constants.SETTING_INFOS, 0);
		psiView = (PSIView) findViewById(R.id.psi);
		initPSI();
	}

	private void initPSI() {
		Log.i("lang", Locale.getDefault().getLanguage());
		String name = settings.getString("name", "").trim();
		String birthdayStr = settings.getString("birthday", "").trim();
		if ("".equals(name) || "".equals(birthdayStr)) {
			gotoSettingActivity();
		} else {
			Calendar birthday = Calendar.getInstance();
			try {
				birthday.setTime(storeDateFormat.parse(birthdayStr));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			this.setTitle(this.getTitle() + " - " + name);
			psiModel = new PSIModel(name, birthday.getTime());
			psiModel.setScaling(0.8f);
			psiModel.setCurrentDate(Calendar.getInstance().getTime());
			psiView.setPsiModel(psiModel);
			showToast();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			changeDay(-1);
			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			changeDay(1);
			break;
		case KeyEvent.KEYCODE_DPAD_UP:
			changeMonth(-1);
			break;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			changeMonth(1);
			break;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_SETTINGS, 0, getString(R.string.menu_setting)).setIcon(android.R.drawable.ic_menu_preferences);
		menu.add(0, MENU_HELP, 0, getString(R.string.menu_help)).setIcon(android.R.drawable.ic_menu_help);
		menu.add(0, MENU_ABOUT, 0, getString(R.string.menu_about));
		menu.add(0, MENU_EXIT, 0, getString(R.string.menu_exit));
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SETTINGS:
			gotoSettingActivity();
			return true;
		case MENU_HELP:
			return true;
		case MENU_ABOUT:
			showDialog(MENU_ABOUT);
			return true;
		case MENU_EXIT:
			finish();
			return true;
		}
		return false;
	}

	private void gotoSettingActivity() {
		Intent intent = new Intent(PSIActivity.this, SettingActivity.class);
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		initPSI();
	}

	private void changeMonth(int month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(psiModel.getCurrentDate());
		calendar.add(Calendar.MONTH, month);
		refreshPsi(calendar);
	}

	private void changeDay(int day) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(psiModel.getCurrentDate());
		calendar.add(Calendar.DAY_OF_MONTH, day);
		refreshPsi(calendar);
	}

	private void refreshPsi(Calendar c) {
		psiModel.setCurrentDate(c.getTime());
		psiView.setPsiModel(psiModel);
		psiView.invalidate();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog result = null;
		Builder builder;

		switch (id) {
		case MENU_ABOUT:
			builder = new AlertDialog.Builder(this);
			builder.setTitle(getString(R.string.app_name) + " " + getString(R.string.app_version));
			builder.setMessage(getString(R.string.app_about));
			builder.setPositiveButton(getString(R.string.bt_ok), null);
			result = builder.create();
			break;
		default:
			result = super.onCreateDialog(id);
			break;
		}
		return result;
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

	private View inflateView(int resource) {
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return vi.inflate(resource, null);
	}

	protected void showToast() {
		View view = inflateView(R.layout.birthday_message_panel);
		TextView tv = (TextView) view.findViewById(R.id.message);
		tv.setTextSize(36);
		tv.setText("生日快乐");
		ImageView cakeImg = (ImageView) view.findViewById(R.id.cakeImg);
		cakeImg.setImageResource(CAKE_IMGS[Math.abs(new Random().nextInt() % CAKE_IMGS.length)]);
		Toast toast = new Toast(this);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
}