package in.dunder.celeris.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import java.util.Objects;

import in.dunder.celeris.db.AuthDatabaseHelper;
import in.dunder.celeris.frontend.ErrorActivity;
import in.dunder.celeris.frontend.SuccessActivity;

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
        String[] messages = message.split("\\|");
        int amount = Integer.parseInt(messages[0]);
        String status = messages[1];
        String type = messages[2];

        if (Objects.equals(status, "unsuccessful") || Objects.equals(status, "failed")) {
            Intent intent = new Intent(context, ErrorActivity.class);
            intent.putExtra("amount", amount);
            intent.putExtra("type", type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (Objects.equals(status, "successful")) {
            Intent intent = new Intent(context, SuccessActivity.class);
            intent.putExtra("amount", amount);
            intent.putExtra("type", type);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}