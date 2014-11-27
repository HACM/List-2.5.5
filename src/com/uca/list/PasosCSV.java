package com.uca.list;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import pager.Pasocinco;
import pager.Pasocuatro;
import pager.Pasodos;
import pager.Pasotres;
import pager.Pasouno;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager; 
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.TabPageIndicator;

public class PasosCSV extends FragmentActivity {
	private static final String[] CONTENT = new String[] { "Paso 1", "Paso 2",
			"Paso 3", "Paso 4", "Paso 5" };

	Activity actividad;
	int CLASE_ID, LISTA_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lista_detalles); 
		actividad = this;
		iniciar();
	}

    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }
	@SuppressLint("CutPasteId")
	private void iniciar() {
		FragmentPagerAdapter adapter = new Adaptador(
				getSupportFragmentManager());

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);
		indicator.setViewPager(pager);
	}

	class Adaptador extends FragmentPagerAdapter {
		public Adaptador(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) { 
			switch (position) {
			case 0:
				return Pasouno.newInstance(actividad, 1);
			case 1:
				return Pasodos.newInstance(actividad, 2);
			case 2:
				return Pasotres.newInstance(actividad, 3);
			case 3:
				return Pasocuatro.newInstance(actividad, 4);
			case 4:
				return Pasocinco.newInstance(actividad, 5);
			default:
				return null;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length].toUpperCase();
		}

		@Override
		public int getCount() {
			return CONTENT.length;
		}
	}
}
