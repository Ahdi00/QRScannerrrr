package com.example.tugaspertemuanke4;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrcodescanerti21c3.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //view object
    private Button buttonscanning;
    private TextView TextViewNama,textViewClass,textViewId;
    //ar scanning object
    private IntentIntegrator qrScan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //view object
        buttonscanning = (Button) findViewById(R.id.buttonscan);
        TextViewNama = (TextView) findViewById(R.id.textViewNama);
        textViewClass = (TextView) findViewById(R.id.textViewKelas);
        textViewId = (TextView) findViewById(R.id.textViewNim);
        //inisosialisasi sacan Object
        qrScan = new IntentIntegrator(this);

        //implementasi onclick listener
        buttonscanning.setOnClickListener(this);

    }
    //untuk menghasilkan hasil scanning
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //jika qr code tidak ada sama sekali
            if (result.getContents() == null) {
                Toast.makeText(this, "hasil scanning tidak ada", Toast.LENGTH_LONG);
            } else if (Patterns.WEB_URL.matcher(result.getContents()).matches()) {
                Intent visitUrl = new Intent(Intent.ACTION_VIEW, Uri.parse(result.getContents()));
                startActivity(visitUrl);
            } else if (Patterns.PHONE.matcher(result.getContents()).matches()) {
                String telp = String.valueOf(result.getContents());
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + telp));
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                try {
                    startActivity(Intent.createChooser(intent, "waiting"));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(MainActivity.this, "no phone apk installed", Toast.LENGTH_SHORT).show();
                }
            } else if (result.getContents().startsWith("geo:")) {
                String mapscan = String.valueOf(result.getContents());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mapscan));

                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }

            } else if (result.getContents().startsWith("MATMSG")) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                String isiqr = String.valueOf(result.getContents());


                String kalimat = isiqr;
                String[] lokasi = kalimat.split(":");

                String variabel1 = lokasi[0];
                String variabel2 = lokasi[1];
                String variabel3 = lokasi[2];
                String variabel4 = lokasi[3];
                String variabel5 = lokasi[4];
                String hasil4 = variabel4.replace(";BODY", "");
                String hasil5 = variabel5.replace(";;", "");
                String email = kalimat.replace("MATMSG:TO:", "");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
                intent.putExtra(Intent.EXTRA_SUBJECT, hasil4);
                intent.putExtra(Intent.EXTRA_TEXT, hasil5);

                //jika qrcode tidak di temukan datanya
                try {
                    //konversi datanya ke json
                    JSONObject obj = new JSONObject(result.getContents());
                    //dishare nilai datanya ke text view
                    TextViewNama.setText(obj.getString("nama"));
                    textViewClass.setText(obj.getString("kelas"));
                    textViewId.setText(obj.getString("nim"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(),
                            Toast.LENGTH_LONG).show();

                }
            } else {
                super.onActivityResult(requestCode, resultCode, data);

            }

        }

    }

    @Override
    public void onClick(View v) {

    }
}