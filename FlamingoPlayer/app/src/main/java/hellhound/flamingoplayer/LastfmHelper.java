package hellhound.flamingoplayer;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.security.MessageDigest;

import javax.net.ssl.HttpsURLConnection;

public class LastfmHelper {
    private final static String TAG = "main_activity";
    String api_key;
    String secret;
    String username;
    String password;
    String sk;
    final static String BASE = "https://ws.audioscrobbler.com/2.0/?";

    private static LastfmHelper instance;

    private LastfmHelper(String api_key, String secret) {
        this.api_key = api_key;
        this.secret = secret;
    }

    public static synchronized LastfmHelper getInstance(String api_key, String secrect){
        if(instance == null){
            instance = new LastfmHelper(api_key, secrect);
        }
        return instance;
    }

    public String HashWithMd5(String input){
        try {
            byte[] bytes = input.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(bytes);
            return new BigInteger(1, digest).toString(16);
        } catch (Exception e){
            Log.i(TAG, "Error");
            return null;
        }
    }

    private String makeRequest(String request){
        try {
            Log.i(TAG, "1");
            URL url = new URL(request);
            Log.i(TAG, "2");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            Log.i(TAG, "3");
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setDoInput(true);
            Log.i(TAG, "4");
            try {
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.flush();
                wr.close();
            } catch (Exception e1) {
                Log.i(TAG, e1.getMessage());
                return null;
            }
            int responseCode = con.getResponseCode();
            Log.i(TAG, "Sending 'POST' request to URL : " + url);
            Log.i(TAG, "Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            return null;
        }
    }

    public boolean login(String username, String password){
        String api_sig = "api_key" + api_key + "methodauth.getMobileSessionpassword" + password + "username" + username + secret;
        api_sig = HashWithMd5(api_sig);
        if(api_sig == null){
            return false;
        }

        String request = BASE + "api_key=" + api_key + "&method=auth.getMobileSession" + "&password=" + password + "&username=" + username + "&api_sig=" + api_sig + "&format=json";
        Log.i(TAG, request);
        String response = makeRequest(request);
        if(response == null){
            return false;
        }
        try {
            JSONObject json = new JSONObject(response);
            String key = json.getJSONObject("session").getString("key");
            Log.i(TAG, key);
            this.sk = key;
            this.password = password;
            this.username = username;
            return true;
        } catch (Exception e){
            e.getStackTrace();
            return false;
        }

    }

    public boolean scrobble(String artist, String track){
        String _artist = artist.replaceAll(" ", "+");
        String _track = track.replaceAll(" ", "+");
        long timestamp = System.currentTimeMillis() / 1000;
        String api_sig = "api_key" + api_key + "artist" + artist + "methodtrack.scrobble" + "sk" + sk + "timestamp" + timestamp + "track" + track + secret;
        Log.i(TAG, api_sig);
        api_sig = HashWithMd5(api_sig);
        Log.i(TAG, api_sig);
        if(api_sig == null){
            return false;
        }
        String request = BASE + "api_key=" + api_key + "&api_sig=" + api_sig + "&sk=" + sk + "&method=track.scrobble" + "&artist=" + _artist + "&track=" + _track
                +"&timestamp=" + timestamp + "&format=json";
        String response = makeRequest(request);
        if(response == null){
            return false;
        }
        Log.i(TAG, response);
        return true;
    }
}
