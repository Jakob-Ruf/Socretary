package de.lucasschlemm.socretary;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.DateTime;

import java.text.DecimalFormat;

/**
 * Created by Daniel on 12.06.15.
 */
public class CallsAdapter extends ArrayAdapter<Call>
{
    Context context;
    int       resource;
    Call[] calls;

    public CallsAdapter(Context context, int resource, Call[] calls)
    {
        super(context, resource, calls);
        this.resource = resource;
        this.context = context;
        this.calls = calls;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        View          row           = convertView;
        CallHolder    callHolder    = null;

        if (row == null)
        {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);

            callHolder = new CallHolder();
            callHolder.imgContact = (ImageView) row.findViewById(R.id.imgContactIcon);
            callHolder.txtTitle = (TextView) row.findViewById(R.id.tvContactTitle);
            callHolder.txtDetails = (TextView) row.findViewById(R.id.tvContactDetails);
            callHolder.txtNextContact = (TextView) row.findViewById(R.id.tvNextContact);
            callHolder.btnSms = (Button)row.findViewById(R.id.btnSms);
            callHolder.btnAnrufen = (Button)row.findViewById(R.id.btnAnrufen);

            row.setTag(callHolder);
        }
        else
        {
            callHolder = (CallHolder) row.getTag();
        }



        final Call call = calls[position];
        callHolder.txtTitle.setText(call.getContact().getName());
        callHolder.imgContact.setImageBitmap(call.getContact().getPicture());
        final Call lcall = call;

        callHolder.btnAnrufen.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Dialer mit der Nummer des Kontakts öffnen
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + lcall.getContact().getNumber()));
                PendingIntent pCallIntent = PendingIntent.getActivity(ApplicationContext.getActivity(), 0, callIntent, 0);
                ApplicationContext.getContext().startActivity(callIntent);

                Call[] newcalls = new Call[calls.length-1];
                int j = 0;
                for(int i = 0; i<calls.length;i++){
                    if (!(calls[i] == calls[position])){
                        newcalls[j] = calls[i];
                        j++;
                    }

                }
                calls = newcalls;
            }

        });

        callHolder.btnSms.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setData(Uri.parse("sms:" + lcall.getContact().getNumber()));
                PendingIntent pSmsIntent = PendingIntent.getActivity(ApplicationContext.getActivity(), 0, smsIntent, 0);
                ApplicationContext.getContext().startActivity(smsIntent);
                calls[position].setSubject("");
            }
        });

        if (call.getSubject().equals("Melde")){
            callHolder.txtDetails.setText("Melde dich mal wieder bei ihm");
        }else{
            callHolder.txtDetails.setText("Hat heute Geburtstag");
        }

        callHolder.txtNextContact.setBackground(Utils.getNextContactBG(context, call.getContact().getLastContact(), call.getContact().getFrequency()));
        callHolder.txtNextContact.setText(Utils.getDaysLeft(call.getContact().getLastContact(), call.getContact().getFrequency()));

        return row;
    }

    static class CallHolder
    {
        ImageView imgContact;
        TextView  txtTitle;
        TextView  txtDetails;
        TextView  txtNextContact;
        Button    btnSms;
        Button    btnAnrufen;
    }
}
