package games.whitetiger.beacarthief;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        setFragment(new MapFragment());

        String sDefSystemLanguage = Locale.getDefault().getLanguage();
        Log.d("LANGUAGE", sDefSystemLanguage);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_map:
                        Toast.makeText(MainActivity.this, "Action Map clicked", Toast.LENGTH_SHORT).show();
                        setFragment(new MapFragment());
                        break;
                    case R.id.action_user:
                        Toast.makeText(MainActivity.this, "Action User clicked", Toast.LENGTH_SHORT).show();
                        setFragment(new UserFragment());
                        break;
                    case R.id.action_garage:
                        Toast.makeText(MainActivity.this, "Action Garage clicked", Toast.LENGTH_SHORT).show();
                        setFragment(new GarageFragment());
                        break;
                    case R.id.action_settings:
                        Toast.makeText(MainActivity.this, "Action Settings clicked", Toast.LENGTH_SHORT).show();
                        setFragment(new SettingsFragment());
                        break;
                }
                return true;
            }
        });
    }

    public void setFragment(Fragment fragment) {
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment)
                .commit();
    }

    public void changeSelectedItem(int selectedItemId) {
        bottomNavigationView.setSelectedItemId(selectedItemId);
    }
}
