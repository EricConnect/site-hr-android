package ericconnect.sitehr;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    //private TextView mTextMessage;
    //private CaptureManager mCaptureManager;
    private DecoratedBarcodeView mBarcodeView;
    //private Button switchFlashlightButton;

    private BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {

            if(result.getText() != null){
                String str = result.getText();
                Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();

            }

        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {

        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_setting:
                    //mTextMessage.setText(R.string.title_setting);
                    setTitle(getString(R.string.title_setting));
                    return true;
                case R.id.navigation_sign_in:
                    //mTextMessage.setText(R.string.title_checkin);
                    setTitle(getString(R.string.title_checkin));
                    return true;
                case R.id.navigation_sign_out:
                    //mTextMessage.setText(R.string.title_checkout);
                    setTitle(getString(R.string.title_checkout));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mBarcodeView = findViewById(R.id.zxing_barcode_holder);
        mBarcodeView.decodeContinuous(callback);
    }
    @Override
    protected void onResume() {
        super.onResume();

        mBarcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mBarcodeView.pause();
    }

}
