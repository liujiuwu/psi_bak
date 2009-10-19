package com.psi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.psi.utils.Constants;
import com.psi.utils.DateUtils;

public class SettingActivity extends Activity {
	private static final String VALUE = "value";
	private static final String KEY = "key";
	private static final String BIRTHDAY = "birthday";
	private static final String NAME = "name";
	private SimpleDateFormat storeDateFormat;
	private SimpleDateFormat displayDateFormat;
	private SharedPreferences settings;
	private ListView listView;
	private Button btOk;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		storeDateFormat = new SimpleDateFormat(getString(R.string.date_store_format));
		displayDateFormat = new SimpleDateFormat(getString(R.string.date_display_format));
		settings = getSharedPreferences(Constants.SETTING_INFOS, 0);

		listView = (ListView) findViewById(R.id.listView01);
		btOk = (Button) findViewById(R.id.ok);
		refreshListView(getListData());
		listView.setOnItemClickListener(listener);
		btOk.setOnClickListener(btOkListener);
	}

	private void refreshListView(List<Map<String, Object>> data) {
		SimpleAdapter adapter = new SimpleAdapter(this, data, android.R.layout.simple_list_item_2, new String[] { KEY, VALUE }, new int[] { android.R.id.text1, android.R.id.text2 });
		listView.setAdapter(adapter);
	}

	private List<Map<String, Object>> getListData() {
		List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> item;
		item = new HashMap<String, Object>();
		item.put(KEY, getString(R.string.setting_item_name));
		item.put(VALUE, settings.getString(NAME, getString(R.string.setting_item_no_name)).trim());
		if ("".equals(item.get(VALUE))) {
			item.put(VALUE, getString(R.string.setting_item_no_name));
		}
		data.add(item);
		item = new HashMap<String, Object>();
		item.put(KEY, getString(R.string.setting_item_birthday));
		item.put(VALUE, settings.getString(BIRTHDAY, getString(R.string.setting_item_no_birthday)).trim());

		if ("".equals(item.get(VALUE))) {
			item.put(VALUE, getString(R.string.setting_item_no_birthday));
		} else {
			String birthdayStr = item.get(VALUE).toString();
			if (birthdayStr.indexOf(getString(R.string.date_store_format_split)) != -1) {
				try {
					item.put(VALUE, displayDateFormat.format(storeDateFormat.parse(birthdayStr)));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		data.add(item);
		return data;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog result = null;
		Builder builder;
		switch (id) {
		case 0:
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(R.layout.setting_name, null);
			final EditText nameEditText = (EditText) textEntryView.findViewById(R.id.name);
			nameEditText.setText(settings.getString(NAME, "").trim());
			builder = new AlertDialog.Builder(SettingActivity.this).setTitle(getString(R.string.setting_item_name)).setView(textEntryView).setPositiveButton(getString(R.string.bt_ok), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					settings.edit().putString(NAME, nameEditText.getText().toString()).commit();
					refreshListView(getListData());
				}
			}).setNegativeButton(getString(R.string.bt_close), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
				}
			});
			result = builder.create();
			break;
		case 1:
			Calendar birthday = Calendar.getInstance();
			try {
				birthday.setTime(storeDateFormat.parse(settings.getString(BIRTHDAY, "")));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			DatePickerDialog datePickerDialog = new DatePickerDialog(this, mDateSetListener, birthday.get(Calendar.YEAR), birthday.get(Calendar.MONTH), birthday.get(Calendar.DAY_OF_MONTH));
			datePickerDialog.setTitle(getString(R.string.setting_item_birthday));
			result = datePickerDialog;
			break;
		default:
			result = super.onCreateDialog(id);
			break;
		}
		return result;
	}

	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			Calendar birthday = Calendar.getInstance();
			birthday.set(year, monthOfYear, dayOfMonth);
			long days = DateUtils.getDaysBetween(birthday.getTime(), Calendar.getInstance().getTime());
			if (days < 0) {
				new AlertDialog.Builder(SettingActivity.this).setTitle(getString(R.string.dialog_error_title)).setMessage(getString(R.string.error_message_01)).setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton(getString(R.string.bt_ok_01), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						showDialog(1);
					}
				}).setPositiveButton(getString(R.string.bt_ok_02), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
					}
				}).show();
				return;
			}

			if (days >= 0) {
				settings.edit().putString(BIRTHDAY, storeDateFormat.format(birthday.getTime())).commit();
				refreshListView(getListData());
			}
		}
	};

	private OnItemClickListener listener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			showDialog(position);
		}
	};

	private OnClickListener btOkListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			String errorMessage = null;
			String name = settings.getString(NAME, "").trim();
			String birthday = settings.getString(BIRTHDAY, "").trim();
			if ("".equals(name)) {
				errorMessage = getString(R.string.setting_item_no_name);
			}

			if ("".equals(birthday)) {
				errorMessage = getString(R.string.setting_item_no_birthday);
			}

			if (null != errorMessage) {
				new AlertDialog.Builder(SettingActivity.this).setTitle(getString(R.string.setting_title)).setMessage(errorMessage).setIcon(android.R.drawable.ic_dialog_alert).setNegativeButton(getString(R.string.bt_ok_04), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.cancel();
					}
				}).show();
			} else {
				Intent intent = new Intent(SettingActivity.this, PSIActivity.class);
				startActivity(intent);
				finish();
			}
		}
	};

}
