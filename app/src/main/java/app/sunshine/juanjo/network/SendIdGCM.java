package app.sunshine.juanjo.network;

import android.os.AsyncTask;

import java.io.IOException;

/**
 * Created by juanjo on 02/09/14.
 */
public class SendIdGCM extends AsyncTask<String, Void, Void> {
    
    @Override
    protected Void doInBackground(String... params) {
        String url = params[0];

        try {
            new APIHTTP().run(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        System.out.println("=> finished send ID");
    }
}
