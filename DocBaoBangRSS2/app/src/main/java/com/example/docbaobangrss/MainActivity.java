package com.example.docbaobangrss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    ListView listView;
    CustomAdapter customAdapter;
    ArrayList<DocBao> docBaos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listview);
        docBaos = new ArrayList<DocBao>();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new ReadData().execute("https://vnexpress.net/rss/tin-moi-nhat.rss");
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DocBao2.class);
                intent.putExtra("link", docBaos.get(position).link);
                startActivity(intent);
            }
        });
    }

    class ReadData extends AsyncTask<String, Integer, String> {


        @Override
        protected String doInBackground(String... strings) {
            return docNoiDung_Tu_URL(strings[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            XMLDOMParser parser = new XMLDOMParser();
            Document document = parser.getDocument(s);
            NodeList nodeList = document.getElementsByTagName("item");
            NodeList nodeListdescription = document.getElementsByTagName("description");

            String hinhanh = "";
            String link = "";
            String title = "";


            for (int i = 0; i < nodeList.getLength(); i++) {
                String cData = nodeListdescription.item(i + 1).getTextContent();
                Pattern p = Pattern.compile("<img[^>]+src\\s*=\\s*['\"]([^'\"]+)['\"][^>]*>");
                Matcher matcher = p.matcher(cData);
                if (matcher.find()) {
                    hinhanh = matcher.group(1);
                    Log.d("hinhanh",hinhanh + ".............."+i);

                }
                Element element = (Element) nodeList.item(i);

                title = parser.getValue(element, "title");
                link = parser.getValue(element, "link");
                docBaos.add(new DocBao(title, link, hinhanh));

            }

            customAdapter = new CustomAdapter(MainActivity.this, android.R.layout.simple_list_item_1, docBaos);
            listView.setAdapter(customAdapter);

            super.onPostExecute(s);

        }
    }

    private String docNoiDung_Tu_URL(String theUrl) {

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        HttpPost httpPost = new HttpPost(theUrl);
        HttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            /*mRssCallBack.onFail(e.toString());*/
            Log.d(TAG, "IOException:" + e.getMessage());
        }
        String responseBody = "";
        BufferedReader buffer = null;
        try {
            buffer = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        } catch (IOException e) {
            /*mRssCallBack.onFail(e.toString());*/
            Log.d(TAG, "IOException:" + e.getMessage());
        }
        String s = "";
        try {
            while ((s = buffer.readLine()) != null)
                responseBody += s;
        } catch (IOException e) {
            /*mRssCallBack.onFail(e.toString());*/
            Log.d(TAG, "IOException:" + e.getMessage());
        }
        Log.d(TAG, "response body is:" + responseBody);
        return responseBody;
    }

}
