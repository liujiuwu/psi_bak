package com.psi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.psi.model.PSIModel;
import com.psi.utils.DateUtils;
import com.psi.utils.Lunar;

public class PSIView extends View {
	private static final String BG_PAINT = "bgPaint";
	private static final int[] month_imgs = { R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six, R.drawable.seven, R.drawable.eight, R.drawable.nine, R.drawable.ten, R.drawable.eleven, R.drawable.twelve };
	private static final String INTELLECTUAL_TEXT_PAINT = "intellectualTextPaint";
	private static final String SENSITIVE_TEXT_PAINT = "sensitiveTextPaint";
	private static final String PHYSICAL_TEXT_PAINT = "physicalTextPaint";
	private static final String TODAY_TEXT_PAINT = "todayTextPaint";
	private static final String CURRENT_DATE_TEXT_PAINT = "currentDateTextPaint";
	private static final String CURRENT_DATE_PAINT = "currentDayPaint";
	private static final String AXIS_GRID_PAINT = "axisGridPaint";
	private static final String AXIS_LABEL_PAINT = "axisLabelPaint";
	private static final String AXIS_PAINT = "axisPaint";
	private static final String INTELLECTUAL_PAINT = "intellectualPaint";
	private static final String SENSITIVE_PAINT = "sensitivePaint";
	private static final String PHYSICAL_PAINT = "physicalPaint";
	private Map<String, Paint> paints = new HashMap<String, Paint>();
	private Map<String, TextPaint> textPaints = new HashMap<String, TextPaint>();
	private PSIModel psiModel;
	private int viewWidth;
	private int curveHeight;
	private int startX = 33;
	private int startY = 60;
	private SimpleDateFormat displayDateFormat;

	public PSIView(Context context) {
		super(context);
		initPSIView();
	}

	public PSIView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initPSIView();
	}

	public PSIView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initPSIView();
	}

	private void initPSIView() {
		float curveStrokeWidth = 1.8f;
		// 背景画笔
		Paint bgPaint = new Paint();
		paints.put(BG_PAINT, bgPaint);

		// 体力画笔
		Paint physicalPaint = new Paint();
		physicalPaint.setColor(Color.MAGENTA);
		physicalPaint.setAntiAlias(true);
		physicalPaint.setStrokeWidth(curveStrokeWidth);
		physicalPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paints.put(PHYSICAL_PAINT, physicalPaint);

		// 情绪画笔
		Paint sensitivePaint = new Paint();
		sensitivePaint.setColor(Color.BLUE);
		sensitivePaint.setAntiAlias(true);
		sensitivePaint.setStrokeWidth(curveStrokeWidth);
		sensitivePaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paints.put(SENSITIVE_PAINT, sensitivePaint);

		// 智力画笔
		Paint intellectualPaint = new Paint();
		intellectualPaint.setColor(Color.GREEN);
		intellectualPaint.setAntiAlias(true);
		intellectualPaint.setStrokeWidth(curveStrokeWidth);
		intellectualPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		paints.put(INTELLECTUAL_PAINT, intellectualPaint);

		// X、Y轴画笔
		Paint axisPaint = new Paint();
		axisPaint.setColor(Color.WHITE);
		axisPaint.setStrokeWidth(1);
		paints.put(AXIS_PAINT, axisPaint);

		//X、Y轴网格画笔
		Paint axisGridPaint = new Paint();
		axisGridPaint.setColor(Color.WHITE);
		axisGridPaint.setStrokeWidth(1);
		axisGridPaint.setAlpha(40);
		paints.put(AXIS_GRID_PAINT, axisGridPaint);

		//当前日期线画笔
		Paint currentDatePaint = new Paint();
		currentDatePaint.setColor(Color.YELLOW);
		currentDatePaint.setStrokeWidth(1);
		paints.put(CURRENT_DATE_PAINT, currentDatePaint);

		// X、Y轴标签文字画笔
		TextPaint axisLabelPaint = new TextPaint();
		axisLabelPaint.setColor(Color.WHITE);
		axisLabelPaint.setAntiAlias(true);
		axisLabelPaint.setTextSize(12);
		axisLabelPaint.setTextAlign(Align.CENTER);
		textPaints.put(AXIS_LABEL_PAINT, axisLabelPaint);

		// 当前日期文字画笔
		TextPaint currentDateTextPaint = new TextPaint();
		currentDateTextPaint.setColor(Color.WHITE);
		currentDateTextPaint.setAntiAlias(true);
		currentDateTextPaint.setTextSize(14);
		textPaints.put(CURRENT_DATE_TEXT_PAINT, currentDateTextPaint);

		// 今天文字画笔
		TextPaint todayTextPaint = new TextPaint();
		todayTextPaint.setColor(Color.YELLOW);
		todayTextPaint.setAntiAlias(true);
		todayTextPaint.setTextSize(12);
		todayTextPaint.setTextAlign(Align.CENTER);
		textPaints.put(TODAY_TEXT_PAINT, todayTextPaint);

		// 体力文字画笔
		TextPaint physicalTextPaint = new TextPaint();
		physicalTextPaint.setColor(Color.MAGENTA);
		physicalTextPaint.setAntiAlias(true);
		physicalTextPaint.setTextSize(14);
		textPaints.put(PHYSICAL_TEXT_PAINT, physicalTextPaint);

		// 情绪文字画笔
		TextPaint sensitiveTextPaint = new TextPaint();
		sensitiveTextPaint.setColor(Color.BLUE);
		sensitiveTextPaint.setAntiAlias(true);
		sensitiveTextPaint.setTextSize(14);
		textPaints.put(SENSITIVE_TEXT_PAINT, sensitiveTextPaint);

		// 智力文字画笔
		TextPaint intellectualTextPaint = new TextPaint();
		intellectualTextPaint.setColor(Color.GREEN);
		intellectualTextPaint.setAntiAlias(true);
		intellectualTextPaint.setTextSize(14);
		textPaints.put(INTELLECTUAL_TEXT_PAINT, intellectualTextPaint);

		displayDateFormat = new SimpleDateFormat(getResources().getString(R.string.date_display_format));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (null != psiModel) {
			viewWidth = getWidth();
			psiModel.setYAxistPosition(startY);
			psiModel.calcPSIModel();
			curveHeight = psiModel.getRealWaveCrest() + startY;
			onDrawAxis(canvas);
		}
	}

	private void onDrawAxis(Canvas canvas) {
		List<String> XAxisLabels = psiModel.getXAxisLabels();
		int XAxisLabelLen = XAxisLabels.size();
		int XAxisLabelWidth = (viewWidth - startX) / XAxisLabels.size();
		int i = startX;
		int day = psiModel.getCurrentDate().getDate();
		int month = psiModel.getCurrentDate().getMonth();
		for (int j = 0; j < XAxisLabelLen; j++) {
			if (i != startX) {
				canvas.drawLine(i, startY, i, curveHeight, paints.get(AXIS_GRID_PAINT));
			}
			if ((j + 1) == day) {
				canvas.drawText(XAxisLabels.get(j), i, curveHeight + 15, textPaints.get(TODAY_TEXT_PAINT));
			} else {
				canvas.drawText(XAxisLabels.get(j), i, curveHeight + 15, textPaints.get(AXIS_LABEL_PAINT));
			}
			i += XAxisLabelWidth;
		}

		Bitmap bg = BitmapFactory.decodeResource(getResources(), month_imgs[month]);
		canvas.drawBitmap(bg, (viewWidth - bg.getWidth()) / 2, startY-3, paints.get(BG_PAINT));
		//Bitmap logo = BitmapFactory.decodeResource(getResources(), R.drawable.psi);
		//canvas.drawBitmap(logo, 0, 0, paints.get(BG_PAINT));

		i = XAxisLabelWidth;
		long days = DateUtils.getDaysBetween(psiModel.getBirthday(), psiModel.getCurrentDate());
		canvas.drawLine(i * day - XAxisLabelWidth + startX, startY, i * day - XAxisLabelWidth + startX, curveHeight, paints.get(CURRENT_DATE_PAINT));

		Calendar birthday = Calendar.getInstance();
		birthday.setTime(psiModel.getBirthday());
		Lunar lunar = new Lunar(birthday);
		String lunarBirthday = isSupportLunar() ? getResources().getString(R.string.your_lunar_birthday, lunar.toString(), lunar.animalsYear()) : "";

		int textStartY = startY - 30;
		int textStartX = startX + 15;
		canvas.drawText(getResources().getString(R.string.your_birthday, displayDateFormat.format(psiModel.getBirthday())) + lunarBirthday, textStartX, textStartY, textPaints.get(CURRENT_DATE_TEXT_PAINT));
		if (days > 0) {
			canvas.drawText(getResources().getString(R.string.your_life, displayDateFormat.format(psiModel.getCurrentDate()), days), textStartX, textStartY + 20, textPaints.get(CURRENT_DATE_TEXT_PAINT));
		}
		if (days == 0) {
			canvas.drawText(getResources().getString(R.string.your_life_0), textStartX, textStartY + 20, textPaints.get(CURRENT_DATE_TEXT_PAINT));
		}
		if (days < 0) {
			canvas.drawText(getResources().getString(R.string.your_life_1), textStartX, textStartY + 20, textPaints.get(CURRENT_DATE_TEXT_PAINT));
		}

		canvas.drawLine(startX, startY, startX, curveHeight, paints.get(AXIS_PAINT));

		int yMidpoint = (curveHeight - startY) / 2 + startY;
		canvas.drawLine(startX - 5, yMidpoint, viewWidth, yMidpoint, paints.get(AXIS_PAINT));
		canvas.drawLine(viewWidth - 5, yMidpoint - 5, viewWidth, yMidpoint, paints.get(AXIS_PAINT));
		canvas.drawLine(viewWidth - 5, yMidpoint + 5, viewWidth, yMidpoint, paints.get(AXIS_PAINT));
		canvas.drawText("0", startX - 11, yMidpoint + 5, textPaints.get(AXIS_LABEL_PAINT));

		canvas.drawLine(startX, yMidpoint - psiModel.getWaveCrest() / 2 * psiModel.getScaling(), viewWidth, yMidpoint - psiModel.getWaveCrest() / 2 * psiModel.getScaling(), paints.get(AXIS_GRID_PAINT));
		canvas.drawLine(startX + 1, yMidpoint - psiModel.getWaveCrest() / 2 * psiModel.getScaling(), startX - 5, yMidpoint - psiModel.getWaveCrest() / 2 * psiModel.getScaling(), paints.get(AXIS_PAINT));
		canvas.drawText("50", startX - 14, yMidpoint - psiModel.getWaveCrest() / 2 * psiModel.getScaling() + 5, textPaints.get(AXIS_LABEL_PAINT));

		canvas.drawLine(startX, yMidpoint + psiModel.getWaveCrest() / 2 * psiModel.getScaling(), viewWidth, yMidpoint + psiModel.getWaveCrest() / 2 * psiModel.getScaling(), paints.get(AXIS_GRID_PAINT));
		canvas.drawLine(startX + 1, yMidpoint + psiModel.getWaveCrest() / 2 * psiModel.getScaling(), startX - 5, yMidpoint + psiModel.getWaveCrest() / 2 * psiModel.getScaling(), paints.get(AXIS_PAINT));
		canvas.drawText("-50", startX - 16, yMidpoint + psiModel.getWaveCrest() / 2 * psiModel.getScaling() + 5, textPaints.get(AXIS_LABEL_PAINT));

		canvas.drawLine(startX, yMidpoint - psiModel.getWaveCrest() * psiModel.getScaling(), viewWidth, yMidpoint - psiModel.getWaveCrest() * psiModel.getScaling(), paints.get(AXIS_GRID_PAINT));
		canvas.drawLine(startX + 1, yMidpoint - psiModel.getWaveCrest() * psiModel.getScaling(), startX - 5, yMidpoint - psiModel.getWaveCrest() * psiModel.getScaling(), paints.get(AXIS_PAINT));
		canvas.drawText("100", startX - 18, yMidpoint - psiModel.getWaveCrest() * psiModel.getScaling() + 5, textPaints.get(AXIS_LABEL_PAINT));

		canvas.drawLine(startX - 5, curveHeight, viewWidth, curveHeight, paints.get(AXIS_PAINT));
		canvas.drawText("-100", startX - 20, curveHeight + 5, textPaints.get(AXIS_LABEL_PAINT));

		onDrawCurve(canvas, XAxisLabelWidth);
	}

	private void onDrawCurve(Canvas canvas, int XAxisLabelWidth) {
		List<Long> physicalDatas = psiModel.getPhysicalDatas();
		List<Long> sensitiveDatas = psiModel.getSensitiveDatas();
		List<Long> intellectualDatas = psiModel.getIntellectualDatas();
		int dataSize = physicalDatas.size();

		int x = startX;
		int day = psiModel.getCurrentDate().getDate();
		int pointRadius = 5;
		for (int index = 0; index < dataSize; index++) {
			if (physicalDatas.get(index) != null && sensitiveDatas.get(index) != null && intellectualDatas.get(index) != null) {
				if (index < dataSize - 1) {
					canvas.drawLine(x, physicalDatas.get(index), x + XAxisLabelWidth, physicalDatas.get(index + 1), paints.get(PHYSICAL_PAINT));
					canvas.drawLine(x, sensitiveDatas.get(index), x + XAxisLabelWidth, sensitiveDatas.get(index + 1), paints.get(SENSITIVE_PAINT));
					canvas.drawLine(x, intellectualDatas.get(index), x + XAxisLabelWidth, intellectualDatas.get(index + 1), paints.get(INTELLECTUAL_PAINT));
				}

				if ((index + 1) == day) {
					canvas.drawCircle(x, physicalDatas.get(index), pointRadius, paints.get(PHYSICAL_PAINT));
					canvas.drawCircle(x, sensitiveDatas.get(index), pointRadius, paints.get(SENSITIVE_PAINT));
					canvas.drawCircle(x, intellectualDatas.get(index), pointRadius, paints.get(INTELLECTUAL_PAINT));
				}
			}
			x += XAxisLabelWidth;
		}

		int seriesY = curveHeight + 30;
		int seriesTextY = seriesY + 5;
		canvas.drawCircle(startX + 10, seriesY, pointRadius, paints.get(PHYSICAL_PAINT));
		canvas.drawLine(startX, seriesY, startX + 20, seriesY, paints.get(PHYSICAL_PAINT));
		canvas.drawText(getResources().getString(R.string.physical) + " " + psiModel.getRealPhysical(), startX + 25, seriesTextY, textPaints.get(PHYSICAL_TEXT_PAINT));

		canvas.drawCircle(startX + 140, seriesY, pointRadius, paints.get(SENSITIVE_PAINT));
		canvas.drawLine(startX + 130, seriesY, startX + 150, seriesY, paints.get(SENSITIVE_PAINT));
		canvas.drawText(getResources().getString(R.string.sensitive) + " " + psiModel.getRealSensitive(), startX + 155, seriesTextY, textPaints.get(SENSITIVE_TEXT_PAINT));

		canvas.drawCircle(startX + 270, seriesY, pointRadius, paints.get(INTELLECTUAL_PAINT));
		canvas.drawLine(startX + 260, seriesY, startX + 280, seriesY, paints.get(INTELLECTUAL_PAINT));
		canvas.drawText(getResources().getString(R.string.intellectual) + " " + psiModel.getRealIntellectual(), startX + 285, seriesTextY, textPaints.get(INTELLECTUAL_TEXT_PAINT));
	}

	public PSIModel getPsiModel() {
		return psiModel;
	}

	public void setPsiModel(PSIModel psiModel) {
		this.psiModel = psiModel;
	}

	public boolean isSupportLunar() {
		for (String locale : getResources().getStringArray(R.array.lunarLocales)) {
			if (locale.equalsIgnoreCase(Locale.getDefault().toString())) {
				return true;
			}
		}
		return false;
	}
}
