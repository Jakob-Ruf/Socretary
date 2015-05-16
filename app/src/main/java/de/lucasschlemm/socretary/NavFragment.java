package de.lucasschlemm.socretary;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by lucas.schlemm on 04.03.2015.
 */
public class NavFragment extends Fragment
{
	// Initialisieren der notwendigen Variablen
	// TODO ImageView auch verwenden
	private ImageView             ivNavPic;
	private ListView              lvNavBar;
	private ActionBarDrawerToggle dtNavToggle;
	private View                  vContainer;
	private DrawerLayout          dlDrawer;

	// Initialisieren des Callbacks
	private FragmentListener callback;

	// Einstellungsoption, ob der Nutzer die App zum ersten Mal startet
	private static final String PREF_USER_LEARNED_DRAWER = "nav_drawer_learned";
	private boolean bUserLearned;

	// Speichern der Itemposition
	private static final String STATE_SELECTED_POSITION = "selected_nav_drawer_pos";
	private              int    iCurPos                 = 0;

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
		// TODO Layout der Listeneinträge anpassen
		lvNavBar.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_single_choice, navBarItems));

		lvNavBar.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				int editedPosition = position + 1;
				Toast.makeText(getActivity(), "You selected item " + editedPosition, Toast.LENGTH_SHORT).show();
				selectItem(position);
				callback.onNavSelected(position);
			}
		});
		return v;
	}

	public boolean isDrawerOpen()
	{
		return dlDrawer != null && dlDrawer.isDrawerOpen(vContainer);
	}

	public void setUp(int fragmentId, DrawerLayout drawerLayout, Toolbar toolbar)
	{
		vContainer = getActivity().findViewById(fragmentId);
		dlDrawer = drawerLayout;

		dtNavToggle = new ActionBarDrawerToggle(getActivity(), dlDrawer, toolbar, R.string.drawer_open, R.string.drawer_close)
		{
			@Override
			public void onDrawerClosed(View drawerView)
			{
				super.onDrawerClosed(drawerView);
				if (!isAdded())
				{
					return;
				}
				// TODO Optionsmenu einführen in MainActivity
				// getActivity().supportInvalidateOptionsMenu();
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
				// TODO Optionsmenu einführen in MainActivity
				// getActivity().supportInvalidateOptionsMenu();
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
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		if (dlDrawer != null && isDrawerOpen())
		{
			inflater.inflate(R.menu.menu_main, menu);
			//@TODO
			//showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (dtNavToggle.onOptionsItemSelected(item))
		{
			return true;
		}

		// Example Action
/*        if (item.getItemId() == R.id.action_abc)
        {
            Toast.makeText(getActivity(), "Example", Toast.LENGTH_SHORT).show();
            return true;
        }*/
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);
	}

}
