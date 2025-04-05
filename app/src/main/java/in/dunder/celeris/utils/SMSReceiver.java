package in.dunder.celeris.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.Objects;

public class SMSReceiver extends BroadcastReceiver {
    private static final String TARGET_PHONE_NUMBER = "+917050610065";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), "android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    String format = bundle.getString("format");

                    for (Object pdu : pdus) {
                        SmsMessage message;
                        message = SmsMessage.createFromPdu((byte[]) pdu, format);

                        String sender = message.getDisplayOriginatingAddress();

                        if (sender != null && sender.equals(TARGET_PHONE_NUMBER)) {
                            String messageBody = message.getMessageBody();
                            processSMS(context, sender, messageBody);
                        }
                    }
                }
            }
        }
    }

    private void processSMS(Context context, String sender, String message) {
        Toast.makeText(context, "New SMS from: " + sender + message, Toast.LENGTH_SHORT).show();
    }
}