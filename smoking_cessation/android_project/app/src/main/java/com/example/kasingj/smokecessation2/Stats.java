package com.example.kasingj.smokecessation2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;



public class Stats extends AppCompatActivity {

    BarChart cigarettesSmokedChart;
    BarChart cravingsResistedChart;
    String[] dateArgs = new String[2];
    HashMap<String, Integer> cigData;
    ArrayList<String> dates;
    ArrayList<BarEntry> entries;

    private UserService userService;
    Context ctx = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        userService = new UserService(this);
        UserEntity user = userService.getCurrentUser(getApplicationContext());

        getCigsSmokedData(user);
//        fillDummyData();

        cigarettesSmokedChart = (BarChart) findViewById(R.id.cigarettesSmokedChart);
//        createCigarettesSmokedChart("11/01/2016", "11/18/2016");
        Log.d("********TEST: date 1 : ", dateArgs[0] + " ************");
        Log.d("********TEST: date 2 : ", dateArgs[1] + " ************");
        createCigarettesSmokedChart(dateArgs[0], dateArgs[1]);

        XAxis smokedXAxis = cigarettesSmokedChart.getXAxis();
        smokedXAxis.setValueFormatter(new MyXValueFormatter(dates));
        cigarettesSmokedChart.animateXY(0, 2000);

//        TextView resistStat = (TextView) findViewById(R.id.resiststat);
//        resistStat.setText(user.getCravingsResisted());

        TextView smokedStat = (TextView) findViewById(R.id.smokestat);
        smokedStat.setText(user.getNumCigsSmoked());

//        cravingsResistedChart = (BarChart) findViewById(R.id.cravingsResistedChart);
//        createCravingsResistedChart("2016/11/01", "2016/11/18");
//        XAxis cravingsXAxis = cravingsResistedChart.getXAxis();
//        cravingsXAxis.setValueFormatter(new MyXValueFormatter(dates));
//        cravingsResistedChart.animateXY(0, 2000);

//        ArrayList<String> months = new ArrayList<>();
//        months.add("January");
//        months.add("February");
//        months.add("March");
//        months.add("April");
//        months.add("May");
//        months.add("June");
//        months.add("July");
//        months.add("August");
//        months.add("September");
//        months.add("October");
//        months.add("November");
//        months.add("December");

        // https://www.youtube.com/watch?v=H6QxMBI2QH4#t=263.901119
    }

    // fill with fake data
    public void fillDummyData() {
        cigData = new HashMap<>();

        cigData.put("2017/02/20", 5);
        cigData.put("2017/02/21", 7);
        cigData.put("2017/02/23", 4);
        cigData.put("2017/02/25", 8);

        dateArgs[0] = "2017/02/20";
        dateArgs[1] = "2017/02/25";
    }

    // get cigs smoked data for main graph and set dates
    public void getCigsSmokedData (UserEntity user) {

        Cursor cr = userService.getUserSmokedHist(user.getUsername());

        int cigsPerDay = 0;
        String cig;
        String currentDay;
        String start = "";
        String end = "";
        String newDay;
        cigData = new HashMap<>();


        if (cr != null && cr.moveToFirst()) {

            // get first day of data descending order
            cig = cr.getString(1);
            cigsPerDay += Integer.parseInt(cig);
            end = cr.getString(0);
            currentDay = end;

            // get rest of days
            while (cr.moveToNext()) {
                // compare dates and loop through each day, adding total cigs smoked per day
                Log.d("********TEST: ", "in while loop ************");
                newDay = cr.getString(0);
                cig = cr.getString(1);
                if (currentDay.equals(newDay)) {
                    cigsPerDay += Integer.parseInt(cig);
                    Log.d("********TEST: ", "same day ************");
                }
                else {
                    cigData.put(currentDay, cigsPerDay);
                    currentDay = newDay;
                    cigsPerDay = Integer.parseInt(cig);
                    Log.d("********TEST: ", "diff day ************");
                }
            }
            cigData.put(currentDay, cigsPerDay);

            // get last start date (last date from cursor)
            cr.moveToLast();
            start = cr.getString(0);
        }
        cr.close();
        // add dates to date array
        dateArgs[0] = end;
        dateArgs[1] = start;

        Log.d("********TEST: User : ", user.getUsername() + " ************");
        Log.d("********TEST: end : ", end + " ************");
        Log.d("********TEST: start : ", start + " ************");
        Log.d("****TEST: cigsPerDay : ", cigsPerDay + " ************");
    }

    public void createCigarettesSmokedChart (String startDate, String endDate) {

        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");
        if (startDate.length() == 0 || endDate.length() == 0) {
            startDate = "2017/03/15";
            endDate = "2017/03/15";
        }
        try {
            Date start = date.parse(startDate);
            Date end = date.parse(endDate);


            Calendar mStart = Calendar.getInstance();
            Calendar mEnd = Calendar.getInstance();
            mStart.clear();
            mEnd.clear();

            mStart.setTime(start);
            mEnd.setTime(end);

            dates = new ArrayList<>();
            dates = getList(mStart, mEnd);
            Log.d("*****TEST: date size : ", dates.size() + " ************");

            entries = new ArrayList<>();
            float max = 30f;
            int value = 0;
            for (int i = 0; i < dates.size(); i++) {
//                max = 30f;
                Log.d("***** TEST: dates : ", "Date " + i + " is " + dates.get(i) + " ********");
                if (cigData.containsKey(dates.get(i))) {
                    value = cigData.get(dates.get(i));
                }
                else {
                    value = 0;
                }
                entries.add(new BarEntry(i, value));
            }
        } catch(ParseException e) {
            e.printStackTrace();
        }
        BarDataSet dataSet = new BarDataSet(entries, "Cigarettes Smoked");
        BarData data = new BarData(dataSet);
        cigarettesSmokedChart.setData(data);

        Description description = new Description();
        description.setText("");
        cigarettesSmokedChart.setDescription(description);
    }

    public void createCravingsResistedChart (String startDate, String endDate) {

        SimpleDateFormat date = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date start = date.parse(startDate);
            Date end = date.parse(endDate);

            Calendar mStart = Calendar.getInstance();
            Calendar mEnd = Calendar.getInstance();
            mStart.clear();
            mEnd.clear();

            mStart.setTime(start);
            mEnd.setTime(end);

            dates = new ArrayList<>();
            dates = getList(mStart, mEnd);

            entries = new ArrayList<>();
            float max = 30f;
            float value = 0f;
            for (int i = 0; i < dates.size(); i++) {
                value = cigData.get(i) * max;
                entries.add(new BarEntry(i, (int) value));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        BarDataSet dataSet = new BarDataSet(entries, "Cravings Resisted");
        BarData data = new BarData(dataSet);
        cravingsResistedChart.setData(data);

        Description description = new Description();
        description.setText("");
        cravingsResistedChart.setDescription(description);
    }

    public ArrayList<String> getList (Calendar startDate, Calendar endDate) {
        ArrayList<String> list = new ArrayList<>();
        while (startDate.compareTo(endDate) <= 0) {
            list.add(getDate(startDate));
            startDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        return list;
    }

    public String getDate (Calendar day) {
        String currentDate = day.get(Calendar.YEAR) + "/" + (day.get(Calendar.MONTH) + 1) +
                "/" + day.get(Calendar.DAY_OF_MONTH);

        try {
            Date date = new SimpleDateFormat("yyyy/MM/dd").parse(currentDate);
            currentDate = new SimpleDateFormat("yyyy/MM/dd").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return currentDate;
    }

    public class MyXValueFormatter implements IAxisValueFormatter {
        ArrayList<String> dates = new ArrayList<>();

        public MyXValueFormatter (ArrayList<String> dates) {
            this.dates = dates;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return dates.get((int) value);
        }

        @Override
        public int getDecimalDigits() {
            return 0;
        }
    }

    // Navigation Buttons
    public void goToDashboard (View view) {
        Intent intent = new Intent(this, Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0,0);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void goToFriends (View view) {
        Intent intent = new Intent(this, Friends.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0,0);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

    public void goToStatistics (View view) {
        Intent intent = new Intent (this, Stats.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0,0);
        startActivity(intent);
        overridePendingTransition(0,0);
    }

}
