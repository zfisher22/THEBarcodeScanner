package com.example.skom.thebarcodescanner.barcodescanner;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skom.thebarcodescanner.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Zach on 2/16/2015.
 */
//public class MainModel extends AsyncTask<Params, progress, result> {
public class MainModel extends Activity {
    private List<Item> myItems = new ArrayList<Item>();
    TextView prodName;
    ImageView prodPic;
    Button history, google;
    String n;
    MainActivity m = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        prodName = (TextView) findViewById(R.id.tvItemName);
        prodPic = (ImageView) findViewById(R.id.ivProd);
        history = (Button) findViewById(R.id.bHistory);
        google = (Button) findViewById(R.id.bGoogle);

        history.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m.find();
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                m.search();
            }
        });
        populateItem();
        populateListView();
        registerClickCallBack();
        prodName.setText(n);
    }

    public void populateItem() {
        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            Log.d("null", "error");
        }
        int length = extras.getInt("Length");

        for (int i = 0; i < length; i++) {
            String n = extras.getString("Site" + i);
            String p = extras.getString("Price" + i);
            if (n != null && p != null) {
                myItems.add(new Item(n, p));
            }

        }
        String name = extras.getString("Name");
        n = name;
    }

    public void populateListView() {
        ArrayAdapter<Item> adapter = new MyListAdapter();
        ListView l = (ListView) findViewById(R.id.itemList);
        l.setAdapter(adapter);
    }

    public void registerClickCallBack() {
        ListView list = (ListView) findViewById(R.id.itemList);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                String url;
                Item clickedItem = myItems.get(position);

                switch (position) {
                    case 0:
                        url = "http://ebay.com";
                        try {
                            Class ourClass = Class.forName("com.example.skom.thebarcodescanner.barcodescanner.webSearch");
                            Intent intent = new Intent(MainModel.this, ourClass);
                            intent.putExtra("url", url);
                            startActivity(intent);

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        //startActivity(intent);
                        //webView(url);

                        break;
                    case 1:
                        url = "http://amazon.com";
                        try {
                            Class ourClass = Class.forName("com.example.skom.thebarcodescanner.barcodescanner.webSearch");
                            Intent intent = new Intent(MainModel.this, ourClass);
                            intent.putExtra("url", url);
                            startActivity(intent);

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        //webView(url);
                        break;
                }


            }
        });
    }


    public void moreInfo(View view) {
        //Intent intent = new Intent(this, display_message.class);
    }

    private void webView(String url) {
        WebView webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        try {
            setContentView(R.layout.webview);
            webView.loadUrl("http://google.com");
        } catch (ActivityNotFoundException e) {
            Toast.makeText(MainModel.this, "No application can handle this request."
                    + " Please install a web browser", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }


    }

    private class MyListAdapter extends ArrayAdapter<Item> {
        public MyListAdapter() {
            super(MainModel.this, R.layout.main_view, myItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //make sure we have a view
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.prices, parent, false);
            }

            //current item
            Item i = myItems.get(position);

            //fill the view
            //ImageView iv = (ImageView) view.findViewById(R.id.iv_Icon);
            //iv.setImageResource(i.getIconId());

            //Site
            TextView t1 = (TextView) view.findViewById(R.id.tv_Site);
            t1.setText(i.getSite());

            //price
            TextView t2 = (TextView) view.findViewById(R.id.tv_Price);
            t2.setText(i.getPrice());
            return view;
        }
    }
}


