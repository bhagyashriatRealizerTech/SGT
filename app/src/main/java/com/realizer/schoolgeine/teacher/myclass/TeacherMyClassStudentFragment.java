package com.realizer.schoolgeine.teacher.myclass;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.realizer.schoolgeine.teacher.DrawerActivity;
import com.realizer.schoolgeine.teacher.FragmentBackPressedListener;
import com.realizer.schoolgeine.teacher.funcenter.adapter.TeacherFunCenterGalleryAdapter;
import com.realizer.schoolgeine.teacher.funcenter.model.TeacherFunCenterGalleryModel;
import com.realizer.schoolgeine.teacher.view.ProgressWheel;
import com.realizer.schoolgenie.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.AlphabetListAdapter;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.chat.model.AddedContactModel;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.myclass.adapter.TeacherMyClassAttListAdapter;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherAttendanceListModel;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

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

/**
 * Created by shree on 11/26/2015.
 */
public class TeacherMyClassStudentFragment extends Fragment implements SearchView.OnQueryTextListener,OnTaskCompleted, View.OnClickListener,View.OnTouchListener , FragmentBackPressedListener, CompoundButton.OnCheckedChangeListener{
   // RadioButton rdStdList,rdStdAttendance;
    ListView lstStudentName;
    TextView txtstd,txtclss,lstgl;
    FloatingActionButton save , selectAll, cancel, btnnew;
    //EditText inputsearch;
    int change;
    int attId;
    ArrayList<AddedContactModel> studentNameList;
    ArrayList<TeacherAttendanceListModel> attlst;
    ArrayList<AddedContactModel> mAllData;
    ArrayAdapter<String> mAdapter;
    DatabaseQueries qr;
    View root;
    private LinearLayout searchWidgetLayout;
    private EditText mSearchView;
    private ImageView searchBackPress;
    LinearLayout ln;
    private LinearLayout newLayoutRef;
    ToggleButton studentList,attendance;
    //setting student data
    private LinearLayout sideIndex;
    private AlphabetListAdapter adapter = new AlphabetListAdapter();
    private GestureDetector mGestureDetector;
    private List<Object[]> alphabet = new ArrayList<Object[]>();
    private HashMap<String, Integer> sections = new HashMap<String, Integer>();
    private int sideIndexHeight;
    private static float sideIndexX;
    private static float sideIndexY;
    private int indexListSize;
    MenuItem done,search;
    ProgressWheel loading;
    boolean isOnCreate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_myclass_fragment, container, false);
        root = rootView;

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("My Class", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        setHasOptionsMenu(true);
        qr = new DatabaseQueries(getActivity());
        lstStudentName = (ListView) rootView.findViewById(R.id.lstStudentclasslist);
        studentList = (ToggleButton)rootView.findViewById(R.id.studentlist);
        attendance = (ToggleButton)rootView.findViewById(R.id.attendance);
        loading = (ProgressWheel) rootView.findViewById(R.id.loading);

        studentList.setOnCheckedChangeListener(this);
        attendance.setOnCheckedChangeListener(this);


        lstgl = (TextView) rootView.findViewById(R.id.txtgreenlst);
        btnnew = (FloatingActionButton) rootView.findViewById(R.id.newAttendance);
        ln = (LinearLayout) rootView.findViewById(R.id.lntext);
        txtstd = (TextView) rootView.findViewById(R.id.txtstdname);
        txtclss = (TextView) rootView.findViewById(R.id.txtclassname);
        sideIndex = (LinearLayout) rootView.findViewById(R.id.sideIndex);
        mGestureDetector = new GestureDetector(getActivity(), new SideIndexGestureListener());

        mSearchView = (EditText) rootView.findViewById(R.id.search_view);
        mSearchView.setHint("Search");
        searchWidgetLayout = (LinearLayout) rootView.findViewById(R.id.id_searchWidget);
        setLayoutRef(searchWidgetLayout);
        searchWidgetLayout.setVisibility(View.GONE);
        searchBackPress = (ImageView) rootView.findViewById(R.id.id_searchViewBackPressed);
        searchBackPress.setOnClickListener(TeacherMyClassStudentFragment.this);


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));


        isOnCreate = true;


        btnnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TeacherMyClassAttenadanceActivity.class);
                getActivity().startActivity(intent);
            }
        });

        return rootView;
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


    public void displayListItem() {
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
            lstStudentName.setSelection(subitemPosition);
        }
    }

    public ArrayList<AddedContactModel> GetStudentNameList() {
        ArrayList<AddedContactModel> listForDailyHomework = new ArrayList<>();
        DatabaseQueries qr = new DatabaseQueries(getActivity());
        try {
            JSONArray arr = new JSONArray(qr.GetAllTableData(txtstd.getText().toString(),txtclss.getText().toString()));
            Log.d("JSONARR", qr.GetAllTableData(txtstd.getText().toString(), txtclss.getText().toString()));
            for(int i=0;i<arr.length();i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                AddedContactModel singleClassname = new AddedContactModel();
                singleClassname.setRollNo(obj.getString("classRollNo"));
                singleClassname.setUserName(obj.getString("fName") + " " + obj.getString("mName") + " " + obj.getString("lName"));
                singleClassname.setUserId(obj.getString("userId"));
                singleClassname.setProfileimage(obj.getString("ThumbnailURL"));
                listForDailyHomework.add(singleClassname);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listForDailyHomework;
    }

    public void updateList() {
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

            tmpTV = new TextView(getActivity());
            tmpTV.setText(tmpLetter);
            tmpTV.setGravity(Gravity.CENTER);
            tmpTV.setTextSize(15);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            tmpTV.setLayoutParams(params);
            sideIndex.addView(tmpTV);
        }

        sideIndexHeight = sideIndex.getHeight();

        sideIndex.setOnTouchListener(new View.OnTouchListener() {
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


    public void studentClassList(View v,String setFlag)
    {
        String flagStudentList=setFlag;

        new GetStudentList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

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

        lstStudentName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                mSearchView.setText("");
                Config.hideSoftKeyboardWithoutReq(getActivity(), mSearchView);

                if (adapterView.getItemAtPosition(i) instanceof AlphabetListAdapter.Section)
                    return;

                Object o = ((AlphabetListAdapter.Item) adapterView.getItemAtPosition(i)).text;
                AddedContactModel contactModel = (AddedContactModel) o;

                String rno = contactModel.getRollNo();
                Bundle bundle = new Bundle();
                TeacherMyPupilInfoFragment fragment = new TeacherMyPupilInfoFragment();
                Singlton.setSelectedFragment(fragment);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                bundle.putString("RollNo", rno);
                fragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
            }
        });

    }
    public class ChatNoCaseComparator implements Comparator<AddedContactModel> {
        public int compare(AddedContactModel s1, AddedContactModel s2) {
            return s1.getUserName().compareToIgnoreCase(s2.getUserName());
        }
    }

    public void getList(List<AddedContactModel> list)
    {
        Collections.sort(list, new ChatNoCaseComparator());

        List<AlphabetListAdapter.Row> rows = new ArrayList<AlphabetListAdapter.Row>();
        sections.clear();
        alphabet.clear();
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
        adapter.setFlag("MyClass");
        lstStudentName.setAdapter(adapter);
    }



    public void setData() {
        //studentNameList = GetStudentNameList();
        String data[] = new String[studentNameList.size()];
        for(int i=0;i<studentNameList.size();i++)
        {
            data[i] = studentNameList.get(i).getRollNo()+" - "+ studentNameList.get(i).getUserName();
        }
        mAllData = new ArrayList<>();
        mAllData = studentNameList;

        if(studentList.isChecked()==true) {
           getList(mAllData);
        }

        else
        {
            studentClassAtt(root, "Visible");
        }
    }

    public void setSearchResult(String str) {
        ArrayList<AddedContactModel> mSearch = new ArrayList<>();
       // studentNameList = GetStudentNameList();

        String data[] = new String[studentNameList.size()];
        for(int i=0;i<studentNameList.size();i++)
        {
            data[i] = studentNameList.get(i).getRollNo()+" - "+ studentNameList.get(i).getUserName();
        }
        mAllData = new ArrayList<>();
        mAllData = studentNameList;

        for (AddedContactModel temp : mAllData) {
            if (temp.getUserName().toLowerCase().contains(str.toLowerCase())) {
                mSearch.add(temp);
            }
        }
        if(studentList.isChecked()==true) {
           getList(mSearch);
        }
        else
        {
            studentClassAtt(root,"Visible");
        }
    }

    public void studentClassAtt(View v,String setFlag)
    {

        new GetAttendanceList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        lstStudentName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = lstStudentName.getItemAtPosition(position);

                TeacherAttendanceListModel attObj = (TeacherAttendanceListModel) o;
                String prs = attObj.getAttendees();
                String abs = attObj.getAbsenties();

                Bundle bundle = new Bundle();
                TeacherMyClassAttDetails fragment = new TeacherMyClassAttDetails();
                Singlton.setSelectedFragment(fragment);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                bundle.putString("PRS", prs);
                bundle.putString("ABS", abs);
                fragment.setArguments(bundle);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();

            }
        });

    }

    public void SwitchClass()
    {

        String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
        Singlton.setSelectedFragment(newTermDialogFragment);
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b =new Bundle();
        b.putString("StudentClassList", classList);
        b.putInt("MYCLASS",1);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        done = menu.findItem(R.id.action_done);
        done.setVisible(false);
        search = menu.findItem(R.id.action_search);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                ((DrawerActivity) getActivity()).getSupportActionBar().hide();
                ((DrawerActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
                ((DrawerActivity) getActivity()).lockDrawer();
                showMySearchWidget();
                return true;
            case R.id.action_switchclass:
                SwitchClass();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        Bundle b= getArguments();
        String statechk = "";
        if(isOnCreate) {
            statechk = b.getString("CheckState", "");
            isOnCreate = false;
        }
        else
        statechk = "true";

        if(statechk.equals("true"))
        {
            attendance.setChecked(true);
            studentList.setChecked(false);
            ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Attendance", getActivity()));
            ((DrawerActivity) getActivity()).getSupportActionBar().show();
            attendance.setBackgroundResource(R.drawable.team_toggle_on_round_shape);
            attendance.setTextColor(getResources().getColor(R.color.colorAccent));
            studentList.setBackgroundResource(R.drawable.team_toggle_off_round_shape);
            studentList.setTextColor(getResources().getColor(R.color.abc_primary_text_disable_only_material_dark));
            studentClassAtt(root, "Visible");
            btnnew.setVisibility(View.VISIBLE);
            ln.setVisibility(View.VISIBLE);
            lstgl.setVisibility(View.VISIBLE);
            sideIndex.setVisibility(View.GONE);
            if(search != null)
                search.setVisible(false);
        }
        else {

            attendance.setChecked(false);
            studentList.setChecked(true);
            studentList.setBackgroundResource(R.drawable.team_toggle_on_round_shape);
            studentList.setTextColor(getResources().getColor(R.color.colorAccent));
            attendance.setBackgroundResource(R.drawable.team_toggle_off_round_shape);
            attendance.setTextColor(getResources().getColor(R.color.abc_primary_text_disable_only_material_dark));
            btnnew.setVisibility(View.GONE);
            lstgl.setVisibility(View.GONE);
            ln.setVisibility(View.GONE);
            sideIndex.setVisibility(View.VISIBLE);
            studentClassList(root, "Invisible");
            change = 0;
            if(search != null)
                search.setVisible(true);
        }




        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        change=0;
        if(done != null)
        done.setVisible(false);
        if(search != null)
            search.setVisible(true);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {

        if (s.equals("true"))
        {
            long n = qr.deleteQueueRow(attId, "Attendance");
            if(n>=0)
            {
              //  Toast.makeText(getActivity(), "Queue Deleted Successfully", Toast.LENGTH_SHORT).show();
                n=-1;

                n = qr.updateAttendanceSyncFlag(qr.GetAttendanceID(attId));
                if(n>=0)
                {
                    TeacherMyClassStudentFragment fragment = new TeacherMyClassStudentFragment();
                    Singlton.setSelectedFragment(fragment);
                    Singlton.setMainFragment(fragment);
                    Bundle bundle = new Bundle();
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragment.setArguments(bundle);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.frame_container,fragment);
                    fragmentTransaction.commit();
                    // Toast.makeText(getActivity(), "Queue Updated Successfully", Toast.LENGTH_SHORT).show();
                    n=-1;
                }
            }
        }
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getActivity().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }


    public void setLayoutRef(LinearLayout searchWidgetLayout) {
        this.newLayoutRef = searchWidgetLayout;
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
        Config.hideSoftKeyboardWithoutReq(getActivity(), mSearchView);
        searchWidgetLayout.setVisibility(View.GONE);
        ((DrawerActivity) getActivity()).showMyActionBar();
        ((DrawerActivity) getActivity()).unlockDrawer();

    }

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
                            Config.showSoftKeyboard(getActivity(), mSearchView);
                            return false;
                        }
                    }
                    return false;

                default:
                    if (mSearchView != null) {
                        Config.hideSoftKeyboardWithoutReq(getActivity(), mSearchView);
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
    public void onFragmentBackPressed() {
        Intent i = new Intent(getActivity(),DrawerActivity.class);
        startActivity(i);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId())
        {
            case R.id.studentlist:
                if(isChecked)
                {
                    attendance.setChecked(false);
                    ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("My Class", getActivity()));
                    ((DrawerActivity) getActivity()).getSupportActionBar().show();
                    studentList.setBackgroundResource(R.drawable.team_toggle_on_round_shape);
                    studentList.setTextColor(getResources().getColor(R.color.colorAccent));
                    attendance.setBackgroundResource(R.drawable.team_toggle_off_round_shape);
                    attendance.setTextColor(getResources().getColor(R.color.abc_primary_text_disable_only_material_dark));
                    btnnew.setVisibility(View.GONE);
                    lstgl.setVisibility(View.GONE);
                    ln.setVisibility(View.GONE);
                    sideIndex.setVisibility(View.VISIBLE);
                    studentClassList(root, "Invisible");

                    updateList();
                    if(search != null)
                        search.setVisible(true);

                }
                break;
            case R.id.attendance:
                if(isChecked)
                {
                    searchBackPress();

                    studentList.setChecked(false);
                    ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("Attendance", getActivity()));
                    ((DrawerActivity) getActivity()).getSupportActionBar().show();
                    attendance.setBackgroundResource(R.drawable.team_toggle_on_round_shape);
                    attendance.setTextColor(getResources().getColor(R.color.colorAccent));
                    studentList.setBackgroundResource(R.drawable.team_toggle_off_round_shape);
                    studentList.setTextColor(getResources().getColor(R.color.abc_primary_text_disable_only_material_dark));
                    studentClassAtt(root, "Visible");
                    btnnew.setVisibility(View.VISIBLE);
                    ln.setVisibility(View.VISIBLE);
                    lstgl.setVisibility(View.VISIBLE);
                    sideIndex.setVisibility(View.GONE);
                    if(search != null)
                        search.setVisible(false);
                }
                break;
        }
    }


    public class GetStudentList extends AsyncTask<Void, Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... params) {

            studentNameList = GetStudentNameList();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getList(studentNameList);
            sideIndex.setVisibility(View.VISIBLE);
            updateList();
            loading.setVisibility(View.GONE);
        }
    }


    public class GetAttendanceList extends AsyncTask<Void, Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... params) {

            DatabaseQueries qr  = new DatabaseQueries(getActivity());
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            attlst = qr.GetAttData(preferences.getString("STANDARD", ""), preferences.getString("DIVISION", ""));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            lstStudentName.setAdapter(new TeacherMyClassAttListAdapter(getActivity(), attlst));
            loading.setVisibility(View.GONE);
        }
    }

}
