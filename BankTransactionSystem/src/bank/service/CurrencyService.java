package bank.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyService {

    private static final String API_URL = "https://api.exchangerate-api.com/v4/latest/USD";

    public static double getUSDtoINR() {
        HttpURLConnection conn = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(API_URL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            InputStream stream = status >= 200 && status < 300 ? conn.getInputStream() : conn.getErrorStream();
            if (stream == null) return -1;

            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString();
            String marker = "\"INR\":";
            int idx = json.indexOf(marker);
            if (idx < 0) return -1;
            int start = idx + marker.length();
            int end = start;
            while (end < json.length()) {
                char c = json.charAt(end);
                if ((c >= '0' && c <= '9') || c == '.' || c == '-') {
                    end++;
                } else {
                    break;
                }
            }
            if (start == end) return -1;
            String value = json.substring(start, end);
            return Double.parseDouble(value);

        } catch (Exception ex) {
            return -1;
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (Exception ignored) { }
            if (conn != null) conn.disconnect();
        }
    }
}
