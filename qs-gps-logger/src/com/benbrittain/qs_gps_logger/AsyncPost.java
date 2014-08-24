package com.benbrittain.qs_gps_logger;

import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.os.AsyncTask;

public class AsyncPost extends AsyncTask<String, String, Boolean> {

	private JSONObject json;

	public AsyncPost(JSONObject pointBlob) {
		json = pointBlob;
	}

	@Override
	protected Boolean doInBackground(String... params) {
		boolean res = false;
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(params[0]);
		try {

			post.setHeader("Accept", "application/json");
			post.setHeader("Content-type", "application/json");
			post.setEntity(new StringEntity(json.toString()));
			HttpResponse response = client.execute(post);

			StatusLine statusLine = response.getStatusLine();
			if(statusLine.getStatusCode() == HttpURLConnection.HTTP_OK){
				res = true;
			}
		}
		catch (Exception e) {
		}
		return res;
	}

}
