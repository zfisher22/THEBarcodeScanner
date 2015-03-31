package com.example.skom.thebarcodescanner.barcodescanner;

import android.app.SearchManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.skom.thebarcodescanner.R;
import com.example.skom.thebarcodescanner.semantics3.api.Products;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;


public class MainActivity extends ActionBarActivity implements Serializable {

    private TextView textOut;
    public String content;
    public String itemName, itemNum, avgPrice;
    public Products products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        products = new Products(
                "SEM375FCF7D767E5417864467A9B1C8240D6",
                "MDZjNjQ5M2RmNDQzMjQ3OTUwMzFjOTFhZTlhYTZkZTc"
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void scanNow(View view) {
        Log.d("test", "SCANNING!!");
        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
        //SCAN_MODE : Decodes all barcodes that are understood by zxing.
        //QR_CODE_MODE : Decode only QR codes.
        intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "QR_CODE_MODE");
        startActivityForResult(intent, 0);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                //changed
                content = intent.getStringExtra("SCAN_RESULT");
                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
                Log.i("xZing", "contents: " + content + " format: " + format);              // Handle successful scan         }         elseif(resultCode == RESULT_CANCELED)         {              // Handle cancel             Log.i("xZing", "Cancelled");         }     } }
                byte[] rawBytes = intent.getByteArrayExtra("SCAN_RESULT_BYTES");
                int intentOrientation = intent.getIntExtra("SCAN_RESULT_ORIENTATION", Integer.MIN_VALUE);
                Integer orientation = (intentOrientation == Integer.MIN_VALUE) ? null : intentOrientation;
                textOut = (TextView) findViewById(R.id.txtOutput);
                textOut.setText(content);
            }
        }
        try {
            semanticsAPI();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void search() {
        Log.d("test", "SEARCH!!!!!!!" + content);
        //The Intent class defines an action specifically for web searches:
        Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
        intent.putExtra(SearchManager.QUERY, content); // query contains search string
        startActivity(intent);
    }

    public void find() {
        //http://camelcamelcamel.com/Android-Application-Development-For-Dummies/product/1118387104
        // /search.json?q=9781118387108
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://camelcamelcamel.com/search.json?q=" + content));
        startActivity(browserIntent);
    }

    public void semanticsAPI() throws JSONException {
        /* Set up a client to talk to the Semantics3 API using your Semantics3 API Credentials */
        String name = "", price = "", site = "";
        int k, r, i, j;
        int siteCount = 0;
        int priceCount = 0;
        String[] siteArr = new String[10];
        String[] priceArr = new String[10];

        /* Build the Request */
        if (content != null) {
            Log.d("content: ", content);
        }
        products.productsField("search", content);
        //products.field("search", "9780553582031");
        Log.d("Products API keys: ", products.toString());

    /* Make the Request */
        JSONObject results = null;
        try {
            results = products.getProducts();
            //results = products.get();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

/* View the results of the Request */
        Log.d("resultsoftherequest: ", results.toString());

        //MainModel m = new MainModel();
        // Parse through returned results
        //String name = results.getJSONObject("code");
        org.json.JSONArray arr = results.getJSONArray("results");
        if (arr != null) {
            name = arr.getJSONObject(0).getString("name");
            Log.d("length: " + arr.length(), "skldfj");
            for (i = 0; i < arr.length(); i++) {
                org.json.JSONObject elem = arr.getJSONObject(i);
                if (elem != null) {
                    org.json.JSONArray prods = elem.getJSONArray("sitedetails");
                    if (prods != null) {
                        for (j = 0; j < prods.length(); j++) {
                            org.json.JSONObject innerElem = prods.getJSONObject(j);
                            if (innerElem != null) {
                                org.json.JSONArray offers = innerElem.getJSONArray("latestoffers");
                                if (offers != null) {
                                    //usually 3 long, it is the "latestoffers" tuple in JSON
                                    for (k = 0; k < offers.length() && siteCount < 10; k++) {
                                        org.json.JSONObject sites = offers.getJSONObject(k);
                                        if (sites != null && siteCount < 10) {
                                            site = sites.getString("seller");
                                            price = sites.getString("price");
                                            if (siteCount < 2) {
                                                siteArr[siteCount] = site;
                                                priceArr[siteCount] = price;
                                                siteCount++;
                                            } else if (siteCount >= 2 && siteCount < 10) {
                                                double doubPrice = Double.parseDouble(price);
                                                boolean isEqual = false;
                                                for (int z = 0; z < siteCount && siteCount < 10; z++) {
                                                    if (siteArr[z].equals(site)) {
                                                        isEqual = true;
                                                        double thePrice = Double.parseDouble(priceArr[z]);
                                                        if (thePrice >= doubPrice) {
                                                            priceArr[z] = String.valueOf(doubPrice);
                                                        }
                                                    }
                                                }
                                                if (isEqual == false) {
                                                    siteArr[siteCount] = site;
                                                    priceArr[siteCount] = price;
                                                    siteCount++;
                                                }
                                            } else {

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //convert array to doubles so the prices can be sorted
        Double[] doubArr = new Double[priceArr.length];
        for (int l = 0; l < priceArr.length; l++) {
            doubArr[l] = Double.parseDouble(priceArr[l]);
            Log.d("arrp: " + l, priceArr[l]);
            Log.d("arrs: " + l, siteArr[l]);
        }


        //sort double array by lowest price
        double temp;
        String temp2;
        for (int a = 0; a < doubArr.length - 1; a++) {
            for (int b = a + 1; b < doubArr.length; b++) {
                if (doubArr[a] > doubArr[b]) {
                    temp = doubArr[a];
                    temp2 = siteArr[a];
                    doubArr[a] = doubArr[b];
                    doubArr[b] = temp;
                    siteArr[a] = siteArr[b];
                    siteArr[b] = temp2;
                }
            }
        }

        for (int c = 0; c < doubArr.length; c++) {
            Log.d("sorted: " + doubArr[c], "");
        }

        //convert back to string
        for (int l = 0; l < priceArr.length; l++) {
            priceArr[l] = String.valueOf(doubArr[l]);
        }

        try {
            Class ourClass = Class.forName("com.example.skom.thebarcodescanner.barcodescanner.MainModel");
            Intent intent2 = new Intent(MainActivity.this, ourClass);
            //MainModel.prodName.setText(contents);
            for (r = 0; r < priceArr.length; r++) {
                if (priceArr[r] != null && siteArr[r] != null) {
                    //Log.d("arrp: " + r, priceArr[r]);
                    //Log.d("arrs: " + r, siteArr[r]);

                    intent2.putExtra("Length", priceArr.length);
                    intent2.putExtra("Price" + r, priceArr[r]);
                    intent2.putExtra("Site" + r, siteArr[r]);
                    intent2.putExtra("Name", name);
                } else {
                    Log.d("null: " + r, "null");
                }
            }
            startActivity(intent2);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


}