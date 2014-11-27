package tutorial;

import com.uca.list.R; 
import android.app.Activity; 
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup; 
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams; 

public final class menuprincipal extends Fragment {

	static Activity actividad;
	static int pasotuto;

	public static menuprincipal newInstance(Activity activi, int pasoid) {
		menuprincipal fragment = new menuprincipal();
		actividad = activi;
		pasotuto = pasoid;
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

		ImageView paso = new ImageView(actividad);
		LinearLayout layout = new LinearLayout(getActivity());
		layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		layout.setGravity(Gravity.TOP);
		paso.setImageResource(R.drawable.menuprincipal);
		layout.addView(paso);
		return layout;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	public int convertDpToPixel(float dp) {
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		float px = dp * (metrics.densityDpi / 160f);
		return (int) px;
	}

}
