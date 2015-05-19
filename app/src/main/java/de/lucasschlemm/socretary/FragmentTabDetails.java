package de.lucasschlemm.socretary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by lucas.schlemm on 19.05.2015.
 */
public class FragmentTabDetails extends Fragment
{
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_tab_details, container, false);
		return rootView;
	}

}
