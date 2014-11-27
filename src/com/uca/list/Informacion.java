package com.uca.list;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import modelo.ExportacionExcel;
import modelo.ModeloClase;
import setget.Clases;

import java.util.ArrayList;

public class Informacion extends Activity {

    String correoUsuario ="";
    Button btn_correo, btn_web;
    Intent browserIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.informacion);
        correoUsuario = getIntent().getStringExtra("correo");
        browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/svsoftnicaragua"));
        iniciarEventos();
    }

    private void iniciarEventos() {
        btn_web = (Button)findViewById(R.id.btnFb);
        btn_correo = (Button)findViewById(R.id.btnCorreo);

        btn_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paginaWeb();
            }
        });
        btn_correo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                correo(correoUsuario);
            }
        });
    }

    private void paginaWeb() {
        try {
            startActivity(browserIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            mensaje("No hay correos instalados.", 3);
        }
    }

    private void correo(String correoUsuario){
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"svsoftnic@gmail.com", correoUsuario});
        i.putExtra(Intent.EXTRA_SUBJECT, "Contacto app Listin - Android");
        i.putExtra(Intent.EXTRA_TEXT   , "Mi correo es "+correoUsuario+"\nEscribir mensaje...");
        try {
            startActivity(Intent.createChooser(i, "Enviando correo..."));
        } catch (android.content.ActivityNotFoundException ex) {
            mensaje("No hay correos instalados.", 3);
        }
    }

    public void mensaje(String msg, int estilo) {
        // 1 Style.ALERT
        // 2 Style.CONFIRM
        // 3 Style.INFO
        switch (estilo) {
            case 1:
                Crouton.makeText(this, msg, Style.INFO).show();
                break;
            case 2:
                Crouton.makeText(this, msg, Style.CONFIRM).show();
                break;
            case 3:
                Crouton.makeText(this, msg, Style.ALERT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        Crouton.cancelAllCroutons();
        super.onDestroy();
    }

}
