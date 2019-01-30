package pt.simov.studentshandbook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessaging;

import pt.simov.adapters.GenericAdapter;
import pt.simov.entities.LostAndFound;
import pt.simov.helpers.Manager;


public class LostAndFoundActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    final Context context = this;
    static final int REQUEST_CODE_MAIN3ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /** TAB MENU**/
        setContentView(R.layout.activity_lostandfound_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        /** END **/

        createDialog();

        //getLostAndFoundsList(Manager.INSTANCE.getUser());
        //setLostAndFoundsListInDB();
        //showLostAndFoundsList();
    }

    public void loadFragment(int button) {
        switch (button) {
            case 1:
                //new PlaceholderFragment();
                new ListAnomaliesFragment();
                break;
            case 2:
                //selectFragment(new AddAnomalyFragment());
                break;
            case 3:
                FirebaseMessaging.getInstance().unsubscribeFromTopic("FOUND");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("LOST");
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.optionmenu_lost_and_founds, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.LostAndFoundOptionMenuList:
                loadFragment(1);
                break;
            case R.id.LostAndFoundOptionMenuListByUser:
                loadFragment(2);
                break;
            case R.id.LostAndFoundOptionStopNotifications:
                //TESTE
                FirebaseMessaging.getInstance().unsubscribeFromTopic("FOUND");
                FirebaseMessaging.getInstance().unsubscribeFromTopic("LOST");
                break;
        }
        return true;
    }

    private void createDialog() {

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LostAndFoundFormActivity.class);
                startActivityForResult(i, REQUEST_CODE_MAIN3ACTIVITY);
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        private GenericAdapter<LostAndFound> adapter;

        public void createDialogToDesactivateItem(final LostAndFound f) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(R.string.app_name).setMessage("Pretende desactivar este item?");
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Manager.getInstance().getServiceFacade().getServiceLostAndFoundInterface().setStatusByLostAndFound(f);
                    Manager.getInstance().registerEventLostAndFound();
                    Manager.getInstance().getFoundList();
                    Manager.getInstance().getLostList();
                }
            });
            builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.lostandfound_fragment_list, container, false);

            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) {

                final ListView lv = (ListView) rootView.findViewById(R.id.foundAndLostListView);

                adapter = new GenericAdapter<LostAndFound>(
                        this.getActivity(),
                        R.layout.lostandfound_simple_item,
                        Manager.getInstance().getLostList()) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View rowView;
                        Holder holder = new Holder();
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        rowView = inflater.inflate(rowLayout, null);
                        LostAndFound lostAndFound = (LostAndFound) getItem(position);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                                view.setSelected(true);
                                LostAndFound lostAndFound = (LostAndFound) getItem(position);
                                Log.i("position: ", String.valueOf(position));
                                if (lostAndFound != null &&
                                        lostAndFound.getStatus().equals("Not Solved") &&
                                        lostAndFound.getUser().getUid() != null &&
                                        lostAndFound.getUser().getUid().equals(Manager.getInstance().getUser().getUid())) {
                                    createDialogToDesactivateItem(lostAndFound);
                                }
                            }
                        });

                        //First Row
                        holder.user = (TextView) rowView.findViewById(R.id.foundAndLostPublisher);
                        holder.user.setText("Publisher: " + lostAndFound.getUser().getFirstName() + " " + lostAndFound.getUser().getLastName());

                        holder.description = (TextView) rowView.findViewById(R.id.LostAndFoundDescription);
                        holder.description.setText("Description: " + lostAndFound.getDescription());

                        holder.location = (TextView) rowView.findViewById(R.id.LostAndFoundLocation);
                        holder.location.setText("Location: " + lostAndFound.getLocation());

                        holder.observation = (TextView) rowView.findViewById(R.id.LostAndFoundObservation);
                        holder.observation.setText("Observation: " + lostAndFound.getObservation());

                        holder.subject = (TextView) rowView.findViewById(R.id.LostAndFoundSubject);
                        holder.subject.setText("Subject: " + lostAndFound.getSubject());

                        holder.date = (TextView) rowView.findViewById(R.id.LostAndFoundDate);
                        holder.date.setText("Date: " + lostAndFound.getDateNow());

                        holder.status = (TextView) rowView.findViewById(R.id.LostAndFoundStatus);
                        holder.status.setText("Status: " + lostAndFound.getStatus());

                        holder.image = (ImageView) rowView.findViewById(R.id.lostAndFoundImage);
                        if (lostAndFound.getImageLink() != null && !lostAndFound.getImageLink().equals("")) {
                            Glide.with(rowView).load(lostAndFound.getImageLink()).into(holder.image);
                        }
                        return rowView;
                    }

                    class Holder {
                        TextView description;
                        TextView observation;
                        TextView subject;
                        ImageView image;
                        TextView location;
                        TextView user;
                        TextView date;
                        TextView status;
                    }
                };
                lv.setAdapter(adapter);

            }
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) {

                final ListView lv = (ListView) rootView.findViewById(R.id.foundAndLostListView);
                adapter = new GenericAdapter<LostAndFound>(
                        this.getActivity(),
                        R.layout.lostandfound_simple_item,
                        Manager.getInstance().getFoundList()) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View rowView;
                        Holder holder = new Holder();
                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        rowView = inflater.inflate(rowLayout, null);
                        LostAndFound lostAndFound = (LostAndFound) getItem(position);

                        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long arg3) {
                                view.setSelected(true);
                                LostAndFound lostAndFound = (LostAndFound) getItem(position);
                                Log.i("position: ", String.valueOf(position));
                                if (lostAndFound != null &&
                                        lostAndFound.getStatus().equals("Not Solved") &&
                                        lostAndFound.getUser().getUid() != null &&
                                        lostAndFound.getUser().getUid().equals(Manager.getInstance().getUser().getUid())) {
                                    createDialogToDesactivateItem(lostAndFound);
                                }
                            }
                        });

                        //First Row
                        holder.user = (TextView) rowView.findViewById(R.id.foundAndLostPublisher);
                        holder.user.setText("Publisher: " + lostAndFound.getUser().getFirstName() + " " + lostAndFound.getUser().getLastName());

                        holder.description = (TextView) rowView.findViewById(R.id.LostAndFoundDescription);
                        holder.description.setText("Description: " + lostAndFound.getDescription());

                        holder.location = (TextView) rowView.findViewById(R.id.LostAndFoundLocation);
                        holder.location.setText("Location: " + lostAndFound.getLocation());

                        holder.observation = (TextView) rowView.findViewById(R.id.LostAndFoundObservation);
                        holder.observation.setText("Observation: " + lostAndFound.getObservation());

                        holder.subject = (TextView) rowView.findViewById(R.id.LostAndFoundSubject);
                        holder.subject.setText("Subject: " + lostAndFound.getSubject());

                        holder.date = (TextView) rowView.findViewById(R.id.LostAndFoundDate);
                        holder.date.setText("Date: " + lostAndFound.getDateNow());

                        holder.status = (TextView) rowView.findViewById(R.id.LostAndFoundStatus);
                        holder.status.setText("Status: " + lostAndFound.getStatus());

                        holder.image = (ImageView) rowView.findViewById(R.id.lostAndFoundImage);
                        if (lostAndFound.getImageLink() != null && !lostAndFound.getImageLink().equals("")) {
                            Glide.with(rowView).load(lostAndFound.getImageLink()).into(holder.image);
                        }
                        return rowView;
                    }

                    class Holder {
                        TextView description;
                        TextView observation;
                        TextView subject;
                        ImageView image;
                        TextView location;
                        TextView user;
                        TextView date;
                        TextView status;
                    }
                };
                lv.setAdapter(adapter);

            }
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    /*public void setLostAndFoundsListInDB() {
        for (Iterator<LostAndFound> i = lostAndFoundsList.iterator(); i.hasNext(); ) {
            LostAndFound lostAndFound = i.next();
            Manager.getInstance().getServiceFacade().getServiceLostAndFoundInterface().postLostAndFound(lostAndFound);
            Toast.makeText(this, lostAndFound.getUser() + " added", Toast.LENGTH_LONG).show();
        }
    } */

}
