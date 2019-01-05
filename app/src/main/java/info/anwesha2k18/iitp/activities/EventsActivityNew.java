package info.anwesha2k18.iitp.activities;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import info.anwesha2k18.iitp.R;
import info.anwesha2k18.iitp.adapters.EventFragmentAdapter;

public class EventsActivityNew extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_tabs);

        getSupportActionBar().setElevation(0);

        ViewPager viewPager = (ViewPager) findViewById(R.id.event_viewpager);
        EventFragmentAdapter pagerAdapter = new EventFragmentAdapter(this, getSupportFragmentManager());

        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.event_tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

}
