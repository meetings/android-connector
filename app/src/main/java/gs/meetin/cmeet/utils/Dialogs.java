package gs.meetin.cmeet.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import gs.meetin.cmeet.R;

public class Dialogs {

    public static AlertDialog simpleAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

        return builder.create();
    }

    public static AlertDialog twoButtonDialog(Context context, int titleId, int messageId, int positiveButtonTextId, int negativeButtonTextId, DialogInterface.OnClickListener positiveAction, DialogInterface.OnClickListener negativeAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(messageId)
                .setTitle(titleId)
                .setPositiveButton(positiveButtonTextId, positiveAction)
                .setNegativeButton(negativeButtonTextId, negativeAction);

        return builder.create();
    }
}
