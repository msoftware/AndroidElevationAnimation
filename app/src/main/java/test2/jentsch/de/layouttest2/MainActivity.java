package test2.jentsch.de.layouttest2;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    LinearLayout newRecipeLayout1;
    Button newRecipeButton1;
    LinearLayout newRecipeLayout2;
    Button newRecipeButton2;
    LinearLayout newRecipeLayout3;
    Button newRecipeButton3;
    LinearLayout newRecipeLayout4;
    Button newRecipeButton4;

    LinearLayout activeRecipeLayout;
    Button activeRecipeButton;

    private static final int animationDuration = 300;
    private float lowerElevation;
    private float upperElevation;
    
    private int sdk;
    private ColorDrawable inactivColorDrawable;
    private ColorDrawable activColorDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        // Get SDK Version for setBackground
        sdk = android.os.Build.VERSION.SDK_INT;

        // Get Elevation values
        lowerElevation = getResources().getDimension(R.dimen.lower_elevation);
        upperElevation = getResources().getDimension(R.dimen.upper_elevation);

        activColorDrawable = new ColorDrawable(getResources().getColor(R.color.active_tab_color));
        inactivColorDrawable = new ColorDrawable(getResources().getColor(R.color.inactive_tab_color));

        activate(newRecipeButton1, newRecipeLayout1);
    }

    private void initViews() {
        newRecipeLayout1 = (LinearLayout)findViewById(R.id.newRecipeLayout1);
        newRecipeButton1 = (Button)findViewById(R.id.newRecipeButton1);

        newRecipeLayout2 = (LinearLayout)findViewById(R.id.newRecipeLayout2);
        newRecipeButton2 = (Button)findViewById(R.id.newRecipeButton2);

        newRecipeLayout3 = (LinearLayout)findViewById(R.id.newRecipeLayout3);
        newRecipeButton3 = (Button)findViewById(R.id.newRecipeButton3);

        newRecipeLayout4 = (LinearLayout)findViewById(R.id.newRecipeLayout4);
        newRecipeButton4 = (Button)findViewById(R.id.newRecipeButton4);
    }

    public void onNewRecipeButton1 (View btn) {
        deactivate (activeRecipeButton, activeRecipeLayout);
        activate (newRecipeButton1, newRecipeLayout1);
    }

    public void onNewRecipeButton2 (View btn) {
        deactivate(activeRecipeButton, activeRecipeLayout);
        activate(newRecipeButton2, newRecipeLayout2);
    }

    public void onNewRecipeButton3 (View btn) {
        deactivate(activeRecipeButton, activeRecipeLayout);
        activate(newRecipeButton3, newRecipeLayout3);
    }

    public void onNewRecipeButton4 (View btn) {
        deactivate(activeRecipeButton, activeRecipeLayout);
        activate(newRecipeButton4, newRecipeLayout4);
    }

    public void deactivate (Button btn, LinearLayout l)
    {
        ColorDrawable[] color = {activColorDrawable, inactivColorDrawable};
        TransitionDrawable trans = new TransitionDrawable(color);
        
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            btn.setBackgroundDrawable(trans);
        } else {
            btn.setBackground(trans);
        }
        trans.startTransition(animationDuration);
        setElevation(l,upperElevation, lowerElevation, animationDuration);
    }

    public void activate (Button btn, LinearLayout l)
    {
        activeRecipeLayout = l;
        activeRecipeButton = btn;

        ColorDrawable[] color = {inactivColorDrawable, activColorDrawable};
        TransitionDrawable trans = new TransitionDrawable(color);
        
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) 
        {
            btn.setBackgroundDrawable(trans);
        } else {
            btn.setBackground(trans);
        }
        trans.startTransition(animationDuration);
        setElevation(l,lowerElevation, upperElevation, animationDuration);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void setElevation (View layout, float startElevation, float endElevation, long duration)
    {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AnimateElevation animateElevation = new AnimateElevation ();
            animateElevation.setParams (layout, startElevation, endElevation, duration);
            animateElevation.execute("");

        }
    }

    private class AnimateElevation extends AsyncTask<String, Integer, Long>
    {
        View view;
        float startElevation, endElevation, currentElevation, elevationDelta;
        long duration;

        public void setParams (View v, float startElevation, float endElevation, long duration)
        {
            this.view = v;
            this.startElevation = startElevation;
            this.endElevation = endElevation;
            this.currentElevation = startElevation;
            this.elevationDelta = endElevation - startElevation;
            this.duration = duration;
        }


        protected Long doInBackground(String... params) 
        {
            long startTime = System.currentTimeMillis();
            long endTime = startTime + duration;
            long now = startTime;
            float percentage = 0;
            while (now <= endTime)
            {
                now = System.currentTimeMillis();
                percentage = (100f / duration * (now - startTime)) / 100;
                currentElevation = startElevation + (elevationDelta * percentage);
                publishProgress(0);
                try { Thread.sleep(50); }catch(Exception e) {};
            }
            currentElevation = endElevation;
            publishProgress(0);
            return new Long (0);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        protected void onProgressUpdate(Integer... progress) 
        {
            if (sdk >= Build.VERSION_CODES.LOLLIPOP) 
            {
                view.setElevation(currentElevation);
            }
        }

        protected void onPostExecute(Long result) 
        {
            Toast.makeText(MainActivity.this, "Ready", Toast.LENGTH_LONG).show();
        }
    }
}
