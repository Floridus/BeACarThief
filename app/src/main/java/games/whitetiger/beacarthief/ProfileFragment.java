package games.whitetiger.beacarthief;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProfileFragment extends Fragment {
    private APIAuth auth;
    private ListView mListView;
    private String achievements[] = {"Levelmeister", "Meisterdieb", "Krachmacher", "Bestechungskünstler", "Treuer Insasse"};

    // newInstance constructor for creating fragment
    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = new APIAuth(getActivity());
        TextView usernameTxt, levelTxt, expTxt, totalTxt;
        ProgressBar expProgressBar = (ProgressBar) view.findViewById(R.id.exp);

        usernameTxt = (TextView) view.findViewById(R.id.username);
        levelTxt = (TextView) view.findViewById(R.id.level);
        expTxt = (TextView) view.findViewById(R.id.current);
        totalTxt = (TextView) view.findViewById(R.id.total);
        mListView = (ListView) view.findViewById(R.id.achievementListView);

        int level = auth.getLevel();
        int exp = auth.getExperience();
        // get the current exp of actual level with max exp and percent
        double onePercent = ((double)Helper.getMaxExpForLevel(level)) / 100;
        double currentExp = exp / onePercent;

        int maxExp = Helper.getMaxExpForAllLevels(level);
        int maxCurrentExp = Helper.getMaxExpForAllLevels(level - 1) + exp;

        expProgressBar.setProgress((int)currentExp);
        usernameTxt.setText(auth.getUsername());
        levelTxt.setText("Level " + level);
        expTxt.setText(exp + "/" + Helper.getMaxExpForLevel(level));
        totalTxt.setText(maxCurrentExp + "/" + maxExp);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.list_item_achievement, R.id.textView, achievements);
        mListView.setAdapter(arrayAdapter);

        return view;
    }
}
