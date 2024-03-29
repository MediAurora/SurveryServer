package com.inn.survey.Scheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import com.inn.survey.dbconnection.DBConnection;
import com.inn.survey.pns.PNS;

public class SurveyTask1 extends TimerTask {
	Date endTime = null;
	String message = null;
	String surveyName = null;

	public SurveyTask1(Date endTime, String message, String surveyName) {
		super();
		this.endTime = endTime;
		this.message = message;
		this.surveyName = surveyName;
	}

	/**
	 * Called on a background thread by Timer
	 */

	public void sendSurvey() {
		// Do your work here; it's 7:00:00 AM!

		try {

			List<String> deviceTokens = new ArrayList<String>();
			Map<String, String> userMap = new HashMap<String, String>();
			String userid;

			String sql = "SELECT USER_ID, DEVICE_TOKEN FROM USER WHERE GROUP_NO = 1 ";

			DBConnection myDB = new DBConnection();
			Connection conn = myDB.getDBConnection();
			PreparedStatement ps = conn.prepareStatement(sql);

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				userid = rs.getString("USER_ID");
				deviceTokens.add(rs.getString("DEVICE_TOKEN"));
				userMap.put(rs.getString("DEVICE_TOKEN"), userid);
			}

			// PNS
			List<String> passedDevices = PNS.pushNotification(deviceTokens,
					surveyName, message);
			Iterator<String> iter = passedDevices.iterator();
			String deviceToken = null;

			while (iter.hasNext()) {
				deviceToken = iter.next();
				userid = userMap.get(deviceToken);

				sql = "INSERT INTO SURVEY_NOTIFICATION (USER_ID , DATE , SURVEY_NAME) VALUES ( ? , ? , ?)";
				PreparedStatement psmt = conn.prepareStatement(sql);

				psmt.setString(1, userid);

				Date date = new Date();
				long t = date.getTime();
				Timestamp timestamp = new Timestamp(t);

				psmt.setTimestamp(2, timestamp);

				psmt.setString(3, surveyName);
				psmt.executeUpdate();
			}

			myDB.closeConnection();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void run() {
		if (new Date().after(endTime))
			cancel();
		else
			sendSurvey();

	}

	// If you don't want the survey to continue
	// tomorrow (etc), cancel the timer using
	// timer.cancel();
}
