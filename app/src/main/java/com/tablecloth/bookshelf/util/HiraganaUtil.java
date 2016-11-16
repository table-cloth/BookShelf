package com.tablecloth.bookshelf.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Util class for goo Hiragana-ka API
 * https://labs.goo.ne.jp/api/jp/hiragana-translation/
 *
 * Created on 2016/11/15.
 */

public class HiraganaUtil {

    // http://lo25131.hatenablog.com/entry/2016/01/29/005423
    // http://qiita.com/kaikusakari/items/b1dd02803bb16956010a
    public static void post() {
        final String json =
                "{\"app_id\":\"505ca5fca5074be80da987987dee15a5e97d13fd67b6eeb826ce52acd82b676f\",\"sentence\":\"僕らの七日間戦争\",\"output_type\":\"katakan\"}";
//                "{\"user\":{" +
//                        "\"name\":\"name1\","+
//                        "\"password\":\"password\","+
//                        "\"password_confirmation\":\"password\""+
//                        "}}";


        try {

            String buffer = "";
            HttpURLConnection con = null;
            URL url = new URL("https://labs.goo.ne.jp/api/hiragana");
            con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setInstanceFollowRedirects(false);
            con.setRequestProperty("Accept-Language", "jp");
            con.setDoOutput(true);
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            OutputStream os = con.getOutputStream();
            PrintStream ps = new PrintStream(os);
            ps.print(json);
            ps.close();

            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
            buffer = reader.readLine();

            JSONArray jsonArray = new JSONArray(buffer);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Log.d("HTTP REQ", jsonObject.getString("name"));
            }
            con.disconnect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
