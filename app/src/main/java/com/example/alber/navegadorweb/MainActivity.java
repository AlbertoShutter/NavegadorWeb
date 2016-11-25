package com.example.alber.navegadorweb;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebIconDatabase;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageButton parar, adelante, atras, ir, borrar, historial;
    private ImageView fav;
    private ProgressBar progreso;
    private EditText url;
    private WebView webview;
    private int error = 0;
    DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        parar = (ImageButton) findViewById(R.id.button7);
        adelante = (ImageButton) findViewById(R.id.button9);
        atras = (ImageButton) findViewById(R.id.button8);
        ir = (ImageButton) findViewById(R.id.button6);
        borrar = (ImageButton) findViewById(R.id.button10);
        historial = (ImageButton) findViewById(R.id.button11);
        fav = (ImageView) findViewById(R.id.fav);
        progreso = (ProgressBar) findViewById(R.id.progreso);
        url = (EditText) findViewById(R.id.textView4);
        webview = (WebView) findViewById(R.id.webview);

        parar.setOnClickListener(this);
        adelante.setOnClickListener(this);
        atras.setOnClickListener(this);
        ir.setOnClickListener(this);
        borrar.setOnClickListener(this);
        historial.setOnClickListener(this);

        //Pagina de inicio
        webview.loadUrl("https://www.google.com/");

        //Soporte para JavaScript
        webview.getSettings().setJavaScriptEnabled(true);

        //Soporte para zoom
        webview.getSettings().setBuiltInZoomControls(true);
        webview.getSettings().setSupportZoom(true);

        //Soporte para iconos
        WebIconDatabase.getInstance().open(getDir("icons", MODE_PRIVATE).getPath());

        webview.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_UP:
                        if (!v.hasFocus()) {
                            v.requestFocus();
                        }
                        break;
                }
                return false;
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                fav.setImageBitmap(icon);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                getWindow().setTitle("xtiyo on "+title);
            }
        });

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted (WebView view, String url, Bitmap favicon) {
                progreso.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished (WebView view, String url) {
                progreso.setVisibility(View.INVISIBLE);

                if (error == 0) {
                    Calendar c = Calendar.getInstance();

                    SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy");
                    SimpleDateFormat df2 = new SimpleDateFormat("HH:mm:ss");

                    String fecha = df1.format(c.getTime());
                    String hora = df2.format(c.getTime());

                    escribir(url, fecha, hora);
                }
                error = 0;
            }

            @Override
            public void onReceivedError (WebView view, int errorCode, String descripcion, String failingUrl) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(descripcion).setPositiveButton("Aceptar", null).setTitle("Error! web page "+failingUrl);
                builder.show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ir.getId()) {
            String pagina = url.getText().toString();
            webview.loadUrl("http://"+pagina);
        }
        if (v.getId() == parar.getId()) {
            webview.stopLoading();
        }
        if (v.getId() == adelante.getId()) {
            if (webview.canGoForward())
                webview.goForward();
            else
                Toast.makeText(this, "No existen paginas que mostrar", Toast.LENGTH_LONG).show();
        }
        if (v.getId() == atras.getId()) {
            if (webview.canGoBack())
                webview.goBack();
            else
                Toast.makeText(this, "No existen paginas que mostrar", Toast.LENGTH_LONG).show();
        }
        if (v.getId() == historial.getId()) {
            Intent j = new Intent(this, HistorialActivity.class);
            startActivity(j);
        }
        if (v.getId() == borrar.getId()) {
            borrar();
        }
    }

    public void escribir (String url, String hora, String fecha) {
        SQLHelper helper = new SQLHelper(this, "HISTORIAL", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("INSERT INTO info values('"+url+"','"+fecha+"','"+hora+"')");
        db.close();
        helper.close();
    }

    public void borrar () {
        SQLHelper helper = new SQLHelper(this, "HISTORIAL", null, 1);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("info",null,null);
    }

    public void cargar(){
        String enlace= url.getText().toString().replaceAll("\\s+","");
        Toast.makeText(getApplicationContext(), "http://"+enlace, Toast.LENGTH_SHORT).show();
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl("http://"+enlace);
        webview.setWebViewClient(new webviewClient());
        if(URLUtil.isValidUrl(enlace)){
            //agregarUrl();
        }
    }

    class webviewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
