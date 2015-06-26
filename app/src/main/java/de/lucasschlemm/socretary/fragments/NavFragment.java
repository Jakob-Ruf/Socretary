package de.lucasschlemm.socretary.fragments;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import de.lucasschlemm.socretary.adapter.NavAdapter;
import de.lucasschlemm.socretary.R;

/**
 * NavigationsFragment, welches seitlich eingeblendet werden kann und für unsere Navigation sorgt.
 * Created by lucas.schlemm on 04.03.2015.
 */
public class NavFragment extends Fragment
{
	// Initialisieren der notwendigen Variablen
	// TODO ImageView auch verwenden
	private ImageView             ivNavPic;
	private ListView              lvNavBar;
	private ActionBarDrawerToggle dtNavToggle;
	private DrawerLayout          dlDrawer;

	// Initialisieren des Callbacks
	private FragmentListener callback;

	// Einstellungsoption, ob der Nutzer die App zum ersten Mal startet
	private static final String PREF_USER_LEARNED_DRAWER = "nav_drawer_learned";
	private boolean bUserLearned;

	// Speichern der Itemposition
	private static final String STATE_SELECTED_POSITION = "selected_nav_drawer_pos";

	public static int getiCurPos()
	{
		return iCurPos;
	}

	private static int iCurPos = 0;

	// Boolean, ob die App aus einem gespeicherten Zustand wieder hergestellt wurde
	private boolean bFromSaved;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Auslesen der Einstellungen
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
		bUserLearned = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

		// Prüfen ob eine gespeicherte Instanz existiert
		if (savedInstanceState != null)
		{
			// Laden der voher gewählten Position
			iCurPos = savedInstanceState.getInt(STATE_SELECTED_POSITION);
			bFromSaved = true;
		}
		// Wiederherstellen der alten Position
		selectItem(iCurPos);
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		// Beeinflussung der Toolbar andeuten
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
	                         @Nullable Bundle savedInstanceState)
	{
		View v = inflater.inflate(R.layout.fragment_nav_bar, container, false);
		lvNavBar = (ListView) v.findViewById(R.id.nav_list);

		String[] navBarItems = getResources().getStringArray(R.array.drawer_list);

		final NavAdapter navAdapter = new NavAdapter(getActivity(), R.layout.listview_nav_item, navBarItems);
		lvNavBar.setAdapter(navAdapter);

		lvNavBar.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				int editedPosition = position + 1;
				selectItem(position);
				callback.onNavSelected(position);
				navAdapter.notifyDataSetChanged();
			}
		});
		return v;
	}

	public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar)
	{
		View vContainer = getActivity().findViewById(fragmentId);
		dlDrawer = drawerLayout;

		dtNavToggle = new ActionBarDrawerToggle(getActivity(), dlDrawer, toolbar, R.string.drawer_open, R.string.drawer_close)
		{
			@Override
			public void onDrawerClosed(View drawerView)
			{
				super.onDrawerClosed(drawerView);
			}

			@Override
			public void onDrawerOpened(View drawerView)
			{
				super.onDrawerOpened(drawerView);
				if (!isAdded())
				{
					return;
				}

				if (!bUserLearned)
				{
					bUserLearned = true;
					SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
				}
			}
		};

		if (!bUserLearned && !bFromSaved)
		{
			dlDrawer.openDrawer(vContainer);
		}

		dlDrawer.post(new Runnable()
		{
			@Override
			public void run()
			{
				dtNavToggle.syncState();
			}
		});
		dlDrawer.setDrawerListener(dtNavToggle);
		lvNavBar.setItemChecked(iCurPos, true);
	}

	// Aktionen beim Drücken eines Eintrags in der Navigation
	private void selectItem(int position)
	{
		iCurPos = position;
		if (lvNavBar != null)
		{
			lvNavBar.setItemChecked(position, true);
		}
		if (dlDrawer != null)
		{
			dlDrawer.closeDrawer(GravityCompat.START);
		}
	}

	// Setzen des Callbacks bei Attach
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		try
		{
			callback = (FragmentListener) activity;
		} catch (ClassCastException e)
		{
			throw new ClassCastException("Activity must implement NavFragmentCallback");
		}
	}

	// Entfernen des Callbacks bei Detach
	@Override
	public void onDetach()
	{
		super.onDetach();
		callback = null;
	}

	// festhalten des zuletzt gewählten Menüeintrages bei beenden
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, iCurPos);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		dtNavToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
	}

}
