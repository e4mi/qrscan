package e4mi.qrscan;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner;
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

public class Main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GmsBarcodeScannerOptions options = new GmsBarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                .build();
        GmsBarcodeScanner scanner = GmsBarcodeScanning.getClient(getApplicationContext(), options);
        scanner
                .startScan()
                .addOnSuccessListener(barcode -> open(barcode.getRawValue()))
                .addOnCanceledListener(this::finish)
                .addOnFailureListener(e -> finish());
    }

    public void open(String value) {
        try {
            Intent intent;
            
            if (value.toLowerCase().startsWith("smsto:")) {
                String[] parts = value.split(":");
                String phoneNumber = parts[1];
                String message = parts.length > 2 ? parts[2] : "";
                intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNumber));
                intent.putExtra("sms_body", message);

            } else {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(value));
            }
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, value);
            startActivity(Intent.createChooser(intent, null));
        }
        finish();
    }
}