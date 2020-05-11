package com.example.docbaobangrss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = "MainActivity";
    CustomAdapter customAdapter;
    ArrayList<DocBao> docBaos;
    Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ContentsFragment contentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavigationView navigationView = findViewById(R.id.navigation_view);
        drawerLayout = findViewById(R.id.drawer_layout);


        // Navigation
        navigationView.setNavigationItemSelectedListener(this);
        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();



        toolbar.setNavigationIcon(R.drawable.ten);
        docBaos = new ArrayList<DocBao>();
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                new ReadData().execute("https://vnexpress.net/rss/tin-moi-nhat.rss");
//            }
//        });

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(MainActivity.this, DocBao2.class);
//                intent.putExtra("link", docBaos.get(position).link);
//                startActivity(intent);
//            }
//        });


        contentFragment = ContentsFragment.newInstance("https://vnexpress.net/rss/tin-moi-nhat.rss");
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, contentFragment).commit();
        navigationView.setCheckedItem(R.id.nav_trangchu);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_trangchu:
                if (contentFragment != null) {
                    contentFragment.refreshData("https://vnexpress.net/rss/tin-moi-nhat.rss");
                }

                break;
            case R.id.nav_thegioi:
                if (contentFragment != null) {
                    contentFragment.refreshData("URL2");
                }
                break;
            case R.id.nav_thoisu:
                if (contentFragment != null) {
                    contentFragment.refreshData("URL3");
                }
                break;
            case R.id.nav_kinhdoanh:
                if (contentFragment != null) {
                    contentFragment.refreshData("URL4");
                }
                break;
            case R.id.nav_thethao:
                if (contentFragment != null) {
                    contentFragment.refreshData("URL5");
                }
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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


            super.onPostExecute(s);

        }
    }

    private String docNoiDung_Tu_URL(String theUrl) {
        /*StringBuilder content = new StringBuilder();
        try {
            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;

            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();*/

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
