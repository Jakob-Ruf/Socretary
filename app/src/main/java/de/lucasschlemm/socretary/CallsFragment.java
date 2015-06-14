package de.lucasschlemm.socretary;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CallsFragment extends Fragment {

    private static final String LOG_CALLER = "CallsFragment";

    private static CallsFragment instance;
    private static LayoutInflater pInflater;
    private static ViewGroup pContainer;
    private static View pView;
    private ListView listV;
    private ListView listViewContacts;
    private FragmentListener callback;

    private static ArrayList<CallsTuple> pMessageContainer;

    public static CallsFragment getInstance()
    {
        if (instance == null)
        {
            instance = new CallsFragment();
        }
        return instance;
    }

    @Override
    public void setArguments(Bundle args) {
        pMessageContainer = (ArrayList<CallsTuple>)args.get("value");
        super.setArguments(args);
        //displayMessages(pMessageContainer);
    }


    public void displayMessages(ArrayList<CallsTuple> iMessages){

        DatabaseHelper helper = DatabaseHelper.getInstance(getActivity());
        final ArrayList<Contact> contacts = helper.getContactList();
        ArrayList<Call> callList = new ArrayList<Call>();

        for (int i=0; i<iMessages.size(); i++){
            for (int j=0; j<contacts.size(); j++){
                if (iMessages.get(i).getContact().equals(contacts.get(j).getId())){
                    callList.add(new Call(contacts.get(i), iMessages.get(i).getSubject()));
                }
            }
        }

        //Array List to Array
        Call[] callArray = new Call[callList.size()];
        for (int i=0; i<callList.size();i++){
            callArray[i] = callList.get(i);
        }


        CallsAdapter adapter = new CallsAdapter(mActivity, R.layout.listview_item_calls, callArray);

        listViewContacts.setAdapter(adapter);
        listViewContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact localContact = contacts.get(position);
                Log.d(LOG_CALLER, "Kurz geklickt: " + localContact.getName());
                callback.onContactDialogNeeded(localContact);
            }
        });
        listViewContacts.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                callback.onContactLongClick(contacts.get(pos));

                //contacts.remove(pos);
                //createListView();
                return true;
            }
        });

    }

    private Activity mActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    public static CallsFragment newInstance(String param1, String param2, Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container) {
        CallsFragment fragment = new CallsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public CallsFragment() {
        // Required empty public constructor
    }


    public void onCreate(Bundle savedInstanceState, LayoutInflater inflater, ViewGroup container, View view) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        return inflater.inflate(R.layout.fragment_calls, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewContacts = (ListView) view.findViewById(R.id.lvContacts);
        displayMessages(pMessageContainer);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
