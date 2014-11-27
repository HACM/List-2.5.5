package com.uca.list;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import pager.Pasocinco;
import pager.Pasocuatro;
import pager.Pasodos;
import pager.Pasotres; 
import tutorial.agregarclase;
import tutorial.agregardiaasistencia;
import tutorial.clases;
import tutorial.crearcuenta;
import tutorial.inicio;
import tutorial.menualumno;
import tutorial.menuasistencia;
import tutorial.menuprincipal;
import tutorial.nuevaclase;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager; 
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.viewpagerindicator.TabPageIndicator;

public class Tutorial extends FragmentActivity {
	private static final String[] CONTENT = new String[] { "Inicio", "Principal",
			"Opciones Clase", "Alumnos Detalles", "Entrar Clase", "Agregar Lista", "Opciones Lista", "Menu asistencia" };

	Activity actividad;
	int CLASE_ID, LISTA_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.lista_detalles); 
		actividad = this;
		iniciar();
	}

	@SuppressLint("CutPasteId")
	private void iniciar() {
		FragmentPagerAdapter adapter = new Adaptador(
				getSupportFragmentManager());

		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.indicator);

		indicator.setViewPager(pager);
		indicator.setVisibility(View.GONE);
	}

    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

	class Adaptador extends FragmentPagerAdapter {
		public Adaptador(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) { 
			switch (position) {
			case 0:
				return inicio.newInstance(actividad, 1);
			case 1:
				return crearcuenta.newInstance(actividad, 2);
			case 2:
				return agregarclase.newInstance(actividad, 3);
			case 3:
				return nuevaclase.newInstance(actividad, 4);
			case 4:
				return menuprincipal.newInstance(actividad, 5);
			case 5:
				return menualumno.newInstance(actividad, 5);
			case 6:
				return agregardiaasistencia.newInstance(actividad, 5);
			case 7:
				return menuasistencia.newInstance(actividad, 5); 
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
