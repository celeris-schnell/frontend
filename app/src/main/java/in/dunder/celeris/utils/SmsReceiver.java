package in.dunder.celeris.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null &&
                intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {

            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                for (SmsMessage smsMessage : messages) {
                    if (smsMessage != null) {
                        String messageBody = smsMessage.getMessageBody();
                        String sender = smsMessage.getDisplayOriginatingAddress();

                        Log.d("SmsReceiver", "Received SMS: " + messageBody + " from: " + sender);

                        // Check if the sender matches the specified phone number
                        if (sender.equals("+917050610065")) { // Replace with your desired phone number
                            // Show a toast message using a Handler to ensure it runs on the main thread
                            new Handler(Looper.getMainLooper()).post(() ->
                                    Toast.makeText(context,
                                            "SMS from specified number: " + messageBody,
                                            Toast.LENGTH_LONG).show()
                            );
                        }
                    }
                }
            }
        }
    }
}
