package com.uca.list; 

import de.keyboardsurfer.android.widget.crouton.Crouton;
import pager.Ausentes;
import pager.Presentes;
import pager.Tardes;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager; 
import com.viewpagerindicator.TabPageIndicator;

public class ListaDetalles extends FragmentActivity {
    private static final String[] CONTENT = new String[] { "Presentes", "Ausentes", "Tardes"};
	
	Activity actividad;
	int layoutalumnos;
	int CLASE_ID, LISTA_ID, TIPO;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_detalles);
        
        actividad = this;
        layoutalumnos = R.layout.listview_detallesalumnos;

		CLASE_ID = Integer.parseInt(getIntent().getStringExtra("CLASE_ID"));
        LISTA_ID = Integer.parseInt(getIntent().getStringExtra("LISTA_ID"));
        TIPO = Integer.parseInt(getIntent().getStringExtra("TIPO"));
        iniciar();
    }

    @SuppressLint("CutPasteId")
    private void iniciar() {
        
    	 FragmentPagerAdapter adapter = new Adaptador(getSupportFragmentManager());

         ViewPager pager = (ViewPager)findViewById(R.id.pager);
         pager.setAdapter(adapter);

         TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
         indicator.setViewPager(pager);

        switch (TIPO){
            case 2:
                indicator.setViewPager(pager,2);
                break;
            case 3:
                indicator.setViewPager(pager,1);
                break;
        }
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
        	if(position==0)
                return Presentes.newInstance(actividad, layoutalumnos, CLASE_ID, LISTA_ID);
            else if(position==1)
                return Ausentes.newInstance(actividad, layoutalumnos, CLASE_ID, LISTA_ID);
            else if(position==2)
                return Tardes.newInstance(actividad, layoutalumnos, CLASE_ID, LISTA_ID);
			return null; 
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
