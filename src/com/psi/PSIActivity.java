package com.psi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.psi.utils.DateUtils;
import com.psi.utils.Lunar;

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
			psiModel.setScaling(0.75f);
			psiModel.setCurrentDate(Calendar.getInstance().getTime());
			psiView.setPsiModel(psiModel);
			happyBirthday();
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
		happyBirthday();
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

	private void happyBirthday() {
		Date birthday = psiModel.getBirthday();
		Date currentViewDate = psiModel.getCurrentDate();
		String happyBirthdayMessage = getString(R.string.birthday_message);
		String happyBirthdayWarnMessage = getString(R.string.birthday_warn_message, getString(R.string.birthday_warn_days));
		String happyBirthdayLunarMessage = getString(R.string.birthday_lunar_message);

		//过阳历生日
		if (birthday.getMonth() == currentViewDate.getMonth() && birthday.getDate() == currentViewDate.getDate()) {
			happyBirthdayToYou(happyBirthdayMessage);
		}

		//阳历生日提醒
		Date nexBirthday = new Date();
		nexBirthday.setDate(birthday.getDate());
		nexBirthday.setMonth(birthday.getMonth());
		nexBirthday.setYear(currentViewDate.getYear());
		long days = DateUtils.getDaysBetween(currentViewDate, nexBirthday);
		if (days > 0 && days == Integer.parseInt(getString(R.string.birthday_warn_days))) {
			happyBirthdayToYou(happyBirthdayWarnMessage);
		}

		//过农历生日
		if (psiView.isSupportLunar()) {
			Calendar lunarBirthday = Calendar.getInstance();
			lunarBirthday.setTime(birthday);
			Lunar lunar = new Lunar(lunarBirthday);

			Calendar lunarCurrentViewDate = Calendar.getInstance();
			lunarCurrentViewDate.setTime(currentViewDate);
			Lunar currentDateLunar = new Lunar(lunarCurrentViewDate);

			if (lunar.getMonth() == currentDateLunar.getMonth() && lunar.getDay() == currentDateLunar.getDay()) {
				happyBirthdayToYou(happyBirthdayLunarMessage);
			}
		}
	}

	private View inflateView(int resource) {
		LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return vi.inflate(resource, null);
	}

	protected void happyBirthdayToYou(String message) {
		View view = inflateView(R.layout.birthday_message_panel);
		TextView tv = (TextView) view.findViewById(R.id.message);
		tv.setTextSize(20);
		tv.setText(message);
		ImageView cakeImg = (ImageView) view.findViewById(R.id.cakeImg);
		cakeImg.setImageResource(CAKE_IMGS[Math.abs(new Random().nextInt() % CAKE_IMGS.length)]);
		Toast toast = new Toast(this);
		toast.setView(view);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}
}