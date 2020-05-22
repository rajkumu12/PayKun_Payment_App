package com.okzio.raj.paykun_payment_app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.paykun.sdk.PaykunApiCall;
import com.paykun.sdk.eventbus.Events;
import com.paykun.sdk.eventbus.GlobalBus;
import com.paykun.sdk.helper.PaykunHelper;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    Button btnPurchase;

    private String merchantIdLive="710730426562225"; // merchant id for live mode application id  = com.paykunsandbox.live
    private String accessTokenLive="6E51763DF010B981F214533F294D2A0C"; // access token for live mode application id  = com.paykunsandbox.live

    private String merchantIdSandbox="706947792715880"; // merchant id for sandbox mode application id = com.paykun.sandbox
    private String accessTokenSandbox="D6E0F1DE65001C0E80C8ED48F979BA00"; // access token for sandbox application id = com.paykun.sandbox

    private String customerName="Rajeev",customerPhone="2545254322",customerEmail="rajeev1995rajan@gmail.com";
    private String productName="Ball Pen",orderNo="7895812590123",amount="10";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPurchase=(Button)findViewById(R.id.btn_pay);
        btnPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("merchant_id",merchantIdSandbox);
                    object.put("access_token",accessTokenSandbox);
                    object.put("customer_name",customerName);
                    object.put("customer_email",customerEmail);
                    object.put("customer_phone",customerPhone);
                    object.put("product_name",productName);
                    object.put("order_no",System.currentTimeMillis()); // order no. should have 10 to 30 character in numeric format
                    object.put("amount",amount);  // minimum amount should be 10
                    object.put("isLive",false); // need to send false if you are in sandbox mode
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                new PaykunApiCall.Builder(MainActivity.this).sendJsonObject(object); // Paykun api to initialize your payment and send info.
            }
        });
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void getResults(Events.PaymentMessage message) {
        if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_SUCCESS)){
            // do your stuff here
            // message.getTransactionId() will return your failed or succeed transaction id
            /* if you want to get your transaction detail call message.getTransactionDetail()
             *  getTransactionDetail return all the field from server and you can use it here as per your need
             *  For Example you want to get Order id from detail use message.getTransactionDetail().order.orderId */
            if(!TextUtils.isEmpty(message.getTransactionId())) {
                Toast.makeText(MainActivity.this, "Your Transaction is succeed with transaction id : "+message.getTransactionId() , Toast.LENGTH_SHORT).show();
                Log.v(" order id "," getting order id value : "+message.getTransactionDetail().order.orderId);
            }
        }
        else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_FAILED)){
            // do your stuff here
            Toast.makeText(MainActivity.this,"Your Transaction is failed",Toast.LENGTH_SHORT).show();
        }
        else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_SERVER_ISSUE)){
            // do your stuff here
            Toast.makeText(MainActivity.this,PaykunHelper.MESSAGE_SERVER_ISSUE,Toast.LENGTH_SHORT).show();
        }else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_ACCESS_TOKEN_MISSING)){
            // do your stuff here
            Toast.makeText(MainActivity.this,"Access Token missing",Toast.LENGTH_SHORT).show();
        }
        else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_MERCHANT_ID_MISSING)){
            // do your stuff here
            Toast.makeText(MainActivity.this,"Merchant Id is missing",Toast.LENGTH_SHORT).show();
        }
        else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_INVALID_REQUEST)){
            Toast.makeText(MainActivity.this,"Invalid Request",Toast.LENGTH_SHORT).show();
        }
        else if(message.getResults().equalsIgnoreCase(PaykunHelper.MESSAGE_NETWORK_NOT_AVAILABLE)){
            Toast.makeText(MainActivity.this,"Network is not available",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Register this activity to listen to event.
        GlobalBus.getBus().register(this);
    }
    @Override
    protected void onStop() {
        super.onStop();
        // Unregister from activity
        GlobalBus.getBus().unregister(this);
    }
}

