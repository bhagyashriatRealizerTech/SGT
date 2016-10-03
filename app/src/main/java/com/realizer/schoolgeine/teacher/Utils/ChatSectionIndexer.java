package com.realizer.schoolgeine.teacher.Utils;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.chat.model.AddedContactModel;
import com.realizer.schoolgeine.teacher.selectstudentdialog.model.TeacherQuery1model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class ChatSectionIndexer extends AppCompatActivity implements OnTouchListener , View.OnClickListener{

    private AlphabetListAdapter adapter = new AlphabetListAdapter();
    private GestureDetector mGestureDetector;
    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();
    private int sideIndexHeight;
    private static float sideIndexX;
    private static float sideIndexY;
    private int indexListSize;
    ListView listView;
    ArrayList<AddedContactModel> selectedTeam;
    ArrayList<AddedContactModel> temp;
    private MenuItem done;
    ArrayList<AddedContactModel> contactlist,mAllData;

    private LinearLayout searchWidgetLayout;
    private EditText mSearchView;
    private ImageView searchBackPress;
    private LinearLayout newLayoutRef;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        } else {

            switch (v.getId()) {
                case R.id.search_view:
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        if (event.getRawX() >= (mSearchView.getRight() - mSearchView.getCompoundDrawables()[Config.DRAWABLE_RIGHT].getBounds().width())) {
                            mSearchView.setText("");
                            mSearchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_black, 0, 0, 0);
                            return true;
                        } else {
                            Config.showSoftKeyboard(ChatSectionIndexer.this, mSearchView);
                            return false;
                        }
                    }
                    return false;

                default:
                    if (mSearchView != null) {
                        Config.hideSoftKeyboardWithoutReq(ChatSectionIndexer.this, mSearchView);
                    }
                    return false;
            }

        }
    }

    /**
     * method to show search widget on clicking on search icon at actionbar
     */
    public void showMySearchWidget() {
        searchWidgetLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.id_searchViewBackPressed:
                searchBackPress();
                break;
        }
    }

    private void searchBackPress() {
        mSearchView.setText("");
        mSearchView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_search_black, 0, 0, 0);
        Config.hideSoftKeyboardWithoutReq(ChatSectionIndexer.this, mSearchView);
        searchWidgetLayout.setVisibility(View.GONE);
        getSupportActionBar().show();
        /*((DrawerActivity) getApplicationContext()).showMyActionBar();
        ((DrawerActivity) getApplicationContext()).unlockDrawer();*/

    }

    class SideIndexGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            sideIndexX = sideIndexX - distanceX;
            sideIndexY = sideIndexY - distanceY;

            if (sideIndexX >= 0 && sideIndexY >= 0) {
                displayListItem();
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_alphabet);


        getSupportActionBar().setTitle(Config.actionBarTitle("Select Student", ChatSectionIndexer.this));
        getSupportActionBar().show();

        listView= (ListView) findViewById(R.id.list);
        mSearchView = (EditText) findViewById(R.id.search_view);
        mSearchView.setHint("Search");
        searchWidgetLayout = (LinearLayout) findViewById(R.id.id_searchWidget);
        setLayoutRef(searchWidgetLayout);
        searchWidgetLayout.setVisibility(View.GONE);
        searchBackPress = (ImageView) findViewById(R.id.id_searchViewBackPressed);
        searchBackPress.setOnClickListener(this);
        mGestureDetector = new GestureDetector(this, new SideIndexGestureListener());
        selectedTeam=new ArrayList<AddedContactModel>();
        selectedTeam = Singlton.getSelectedStudentList();
        temp = Singlton.getSelectedStudeonBackKeyPress();
        contactlist = GetStudentList();
        getList(contactlist);
        updateList();

        Config.hideSoftKeyboardWithoutReq(ChatSectionIndexer.this, mSearchView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position) instanceof AlphabetListAdapter.Section)
                    return;
                AddedContactModel modelView = ((AlphabetListAdapter.Item) parent.getItemAtPosition(position)).text;
                if (selectedTeam.size() > 0) {
                    if (!selectedTeam.contains(modelView)) {
                        selectedTeam.add(modelView);
                        listView.setItemChecked(position, true);
                    } else {
                        listView.setItemChecked(position, false);
                        selectedTeam.remove(modelView);
                    }
                } else {
                    selectedTeam.add(modelView);
                    listView.setItemChecked(position, true);
                }

                adapter.setCheckedEmployeeList(selectedTeam);
                adapter.setFlag("Attendance");
                adapter.notifyDataSetChanged();
            }
        });




        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (0 != mSearchView.getText().length()) {
                    String spnId = mSearchView.getText().toString();
                    setSearchResult(spnId);
                } else {
                    setData();
                }

            }
        });

    }


    public void setSearchResult(String str) {
        ArrayList<AddedContactModel> mSearch = new ArrayList<>();
        // studentNameList = GetStudentNameList();

        String data[] = new String[contactlist.size()];
        for(int i=0;i<contactlist.size();i++)
        {
            data[i] = contactlist.get(i).getRollNo()+" - "+ contactlist.get(i).getUserName();
        }
        mAllData = new ArrayList<>();
        mAllData = contactlist;

        for (AddedContactModel temp : mAllData) {
            if (temp.getUserName().toLowerCase().contains(str.toLowerCase())) {
                mSearch.add(temp);
            }
        }

            getList(mSearch);
    }

    public void setData() {
        //studentNameList = GetStudentNameList();
        String data[] = new String[contactlist.size()];
        for(int i=0;i<contactlist.size();i++)
        {
            data[i] = contactlist.get(i).getRollNo()+" - "+ contactlist.get(i).getUserName();
        }
        mAllData = new ArrayList<>();
        mAllData = contactlist;

            getList(mAllData);

    }



    public void setLayoutRef(LinearLayout searchWidgetLayout) {
        this.newLayoutRef = searchWidgetLayout;
    }

  /*  private class SelectContactFilter implements Filterable {
        @Override
        public Filter getFilter() {
            return new ListFilter();
        }
    }

    *//**
     * ListFilter will work from SelectRecipientsAdapter.
     * filter class for search to employees
     *//*
    public class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            //string  method replaceAll(" +", " ") will replace the multiple space to single space between two string
            String constraintStr = constraint.toString().trim().replaceAll(" +", " ").toLowerCase(Locale.getDefault());
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                List<AddedContactModel> filterItems = new ArrayList<>();
                synchronized (this) {
                    for (AddedContactModel item : contactlist) {
                        if (item.getUserName().toLowerCase(Locale.getDefault()).contains(constraintStr)) {
                            filterItems.add(item);
                        }
                    }
                    result.count = filterItems.size();
                    result.values = filterItems;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<AddedContactModel> filtered = (ArrayList<AddedContactModel>) results.values;
            if (results.values != null) {
                getList(filtered);
            } else {
                getList(contactlist);
            }
        }
    }*/


    public void getList(List<AddedContactModel> list)
    {
        Collections.sort(list, new ChatNoCaseComparator());

        List<AlphabetListAdapter.Row> rows = new ArrayList<AlphabetListAdapter.Row>();
        int start = 0;
        int end = 0;
        String previousLetter = null;
        Object[] tmpIndexItem = null;
        Pattern numberPattern = Pattern.compile("[0-9]");

        for (AddedContactModel contact : list) {
            String firstLetter = contact.getUserName().substring(0, 1);

            // Group numbers together in the scroller
            if (numberPattern.matcher(firstLetter).matches()) {
                firstLetter = "#";
            }

            // If we've changed to a new letter, add the previous letter to the alphabet scroller
            if (previousLetter != null && !firstLetter.equalsIgnoreCase(previousLetter)) {
                end = rows.size() - 1;
                tmpIndexItem = new Object[3];
                tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
                tmpIndexItem[1] = start;
                tmpIndexItem[2] = end;
                alphabet.add(tmpIndexItem);

                start = end + 1;
            }

            // Check if we need to add a header row
            if (!firstLetter.equalsIgnoreCase(previousLetter)) {
                rows.add(new AlphabetListAdapter.Section(firstLetter));
                sections.put(firstLetter.toLowerCase(), start);
            }

            // Add the country to the list
            rows.add(new AlphabetListAdapter.Item(contact));
            previousLetter = firstLetter;
        }

        if (previousLetter != null) {
            // Save the last letter
            tmpIndexItem = new Object[3];
            tmpIndexItem[0] = previousLetter.toUpperCase(Locale.UK);
            tmpIndexItem[1] = start;
            tmpIndexItem[2] = rows.size() - 1;
            alphabet.add(tmpIndexItem);
        }
        adapter.setRows(rows);
        adapter.setCheckedEmployeeList(selectedTeam);
        adapter.setFlag("SelectStudent");
        listView.setAdapter(adapter);
    }


    public class ChatNoCaseComparator implements Comparator<AddedContactModel> {
        public int compare(AddedContactModel s1, AddedContactModel s2) {
            return s1.getUserName().compareToIgnoreCase(s2.getUserName());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_select_student, menu);
        done = menu.findItem(R.id.add_contact_done);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Singlton.setSelectedFragment(Singlton.getMainFragment());
                Singlton.setSelectedStudentList(Singlton.getSelectedStudentList());
                finish();
                return true;
            case R.id.add_contact_done:
                Singlton.setSelectedFragment(Singlton.getMainFragment());
                Singlton.setSelectedStudentList(selectedTeam);
                Singlton.setIsDonclick(Boolean.TRUE);
                finish();
                return true;
            case R.id.action_search:
                getSupportActionBar().hide();
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
               /* ((DrawerActivity) getApplicationContext()).getSupportActionBar().hide();
                ((DrawerActivity) getApplicationContext()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
                ((DrawerActivity) getApplicationContext()).lockDrawer();*/
                showMySearchWidget();
                return true;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        } else {
            return false;
        }
    }


    public void updateList() {
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
        sideIndex.removeAllViews();
        indexListSize = alphabet.size();
        if (indexListSize < 1) {
            return;
        }

        int indexMaxSize = (int) Math.floor(sideIndex.getHeight() / 20);
        int tmpIndexListSize = indexListSize;
        while (tmpIndexListSize > indexMaxSize) {
            tmpIndexListSize = tmpIndexListSize / 2;
        }
        double delta;
        if (tmpIndexListSize > 0) {
            delta = indexListSize / tmpIndexListSize;
        } else {
            delta = 1;
        }

        TextView tmpTV;
        for (double i = 1; i <= indexListSize; i = i + delta) {
            Object[] tmpIndexItem = alphabet.get((int) i - 1);
            String tmpLetter = tmpIndexItem[0].toString();

            tmpTV = new TextView(this);
            tmpTV.setText(tmpLetter);
            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setTextSize(15);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }

        sideIndexHeight = sideIndex.getHeight();

        sideIndex.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // now you know coordinates of touch
                sideIndexX = event.getX();
                sideIndexY = event.getY();

                // and can display a proper item it country list
                displayListItem();

                return false;
            }
        });
    }

    public void displayListItem() {
        LinearLayout sideIndex = (LinearLayout) findViewById(R.id.sideIndex);
        sideIndexHeight = sideIndex.getHeight();
        // compute number of pixels for every side index item
        double pixelPerIndexItem = (double) sideIndexHeight / indexListSize;

        // compute the item index for given event position belongs to
        int itemPosition = (int) (sideIndexY / pixelPerIndexItem);

        // get the item (we can do it since we know item index)
        if (itemPosition < alphabet.size()) {
            Object[] indexItem = alphabet.get(itemPosition);
            int subitemPosition = sections.get(indexItem[0].toString().toLowerCase());

            //ListView listView = (ListView) findViewById(android.R.id.list);
            listView.setSelection(subitemPosition);
        }
    }


    private ArrayList<AddedContactModel> GetStudentList()
    {
        ArrayList<AddedContactModel> results = new ArrayList<>();
        DatabaseQueries qr = new DatabaseQueries(ChatSectionIndexer.this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ChatSectionIndexer.this);
        String stdC=preferences.getString("STANDARD", "");
        String divC = preferences.getString("DIVISION", "");
        try {
        JSONArray arr = new JSONArray(qr.GetAllTableData(stdC,divC));

            for(int i=0;i<arr.length();i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                AddedContactModel singleClassname = new AddedContactModel();
                singleClassname.setRollNo(obj.getString("classRollNo"));
                singleClassname.setUserName(obj.getString("fName") + " " + obj.getString("mName") + " " + obj.getString("lName"));
                singleClassname.setUserId(obj.getString("userId"));
                singleClassname.setProfileimage(obj.getString("ThumbnailURL"));
                results.add(singleClassname);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
    }


    @Override
    protected void onResume() {
        super.onResume();
        Config.hideSoftKeyboardWithoutReq(ChatSectionIndexer.this, mSearchView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Singlton.setSelectedStudentList(temp);
        Singlton.setSelectedFragment(Singlton.getMainFragment());
        finish();
    }
}
