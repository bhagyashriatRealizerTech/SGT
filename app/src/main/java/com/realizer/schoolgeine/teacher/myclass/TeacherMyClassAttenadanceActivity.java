package com.realizer.schoolgeine.teacher.myclass;

import android.content.SharedPreferences;
import android.os.AsyncTask;
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
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.realizer.schoolgeine.teacher.Utils.Singlton;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgeine.teacher.R;
import com.realizer.schoolgeine.teacher.Utils.AlphabetListAdapter;
import com.realizer.schoolgeine.teacher.Utils.Config;
import com.realizer.schoolgeine.teacher.Utils.OnTaskCompleted;
import com.realizer.schoolgeine.teacher.backend.DatabaseQueries;
import com.realizer.schoolgeine.teacher.chat.model.AddedContactModel;
import com.realizer.schoolgeine.teacher.myclass.asynctask.TeacherAttendanceAsyncTaskPost;
import com.realizer.schoolgeine.teacher.myclass.model.TeacherAttendanceListModel;
import com.realizer.schoolgeine.teacher.queue.QueueListModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class TeacherMyClassAttenadanceActivity extends AppCompatActivity implements OnTouchListener , View.OnClickListener , OnTaskCompleted {

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
    private MenuItem done;
    ArrayList<AddedContactModel> contactlist;

    private LinearLayout searchWidgetLayout;
    private EditText mSearchView;
    private ImageView searchBackPress;
    private LinearLayout newLayoutRef;
    ArrayList<AddedContactModel> studentNameList;
    ArrayList<AddedContactModel> mAllData;
    DatabaseQueries qr;
    String scode;
    int attId;

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
                            Config.showSoftKeyboard(TeacherMyClassAttenadanceActivity.this, mSearchView);
                            return false;
                        }
                    }
                    return false;

                default:
                    if (mSearchView != null) {
                        Config.hideSoftKeyboardWithoutReq(TeacherMyClassAttenadanceActivity.this, mSearchView);
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
        Config.hideSoftKeyboardWithoutReq(TeacherMyClassAttenadanceActivity.this, mSearchView);
        searchWidgetLayout.setVisibility(View.GONE);
        getSupportActionBar().show();

    }

    @Override
    public void onTaskCompleted(String s,QueueListModel queueListModel) {
        if(s.equals("true"))
        {
            long n = qr.deleteQueueRow(attId,"Attendance");
            if(n>=0)
            {
                n=-1;
                n = qr.updateAttendanceSyncFlag(qr.GetAttendanceID(attId));
                if(n>=0)
                {
                    Singlton.setSelectedFragment(Singlton.getMainFragment());
                   finish();
                }
            }
        }
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
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(TeacherMyClassAttenadanceActivity.this));
        setContentView(R.layout.list_alphabet);
        //In select contact screen, there should be cross icon instead of back icon.
       // getSupportActionBar().setHomeAsUpIndicator(ContextCompat.getDrawable(this, R.drawable.ic_action_cancel));
        getSupportActionBar().setTitle(Config.actionBarTitle("Attendance", TeacherMyClassAttenadanceActivity.this));
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
        selectedTeam=new ArrayList<>();
        contactlist = GetStudentList();
        studentNameList = contactlist;
        qr = new DatabaseQueries(TeacherMyClassAttenadanceActivity.this);
        getList(contactlist);
        updateList();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TeacherMyClassAttenadanceActivity.this);
        scode= preferences.getString("SchoolCode", "");
        Config.hideSoftKeyboardWithoutReq(TeacherMyClassAttenadanceActivity.this,mSearchView );
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position) instanceof AlphabetListAdapter.Section)
                    return;

                AddedContactModel modelView = ((AlphabetListAdapter.Item) parent.getItemAtPosition(position)).text;
             if(selectedTeam.size()>0) {
                 if (!selectedTeam.contains(modelView)) {
                     selectedTeam.add(modelView);
                     listView.setItemChecked(position, true);
                 }
                 else
                 {
                     listView.setItemChecked(position, false);
                     selectedTeam.remove(modelView);
                 }
               }
             else
              {
                 selectedTeam.add(modelView);
                 listView.setItemChecked(position, true);
              }

                adapter.setCheckedEmployeeList(selectedTeam);
                adapter.setFlag("Attendance");
                adapter.notifyDataSetChanged();
            }
        });

    }

    public void setSearchResult(String str) {
        ArrayList<AddedContactModel> mSearch = new ArrayList<>();
        //studentNameList = GetStudentList();
        mAllData = new ArrayList<>();
        mAllData = studentNameList;

        for (AddedContactModel temp : mAllData) {
            if (temp.getUserName().toLowerCase().contains(str.toLowerCase())) {
                mSearch.add(temp);
            }
        }
       getList(mSearch);
    }

    public void setData() {

        mAllData = new ArrayList<>();
        mAllData = studentNameList;

            getList(mAllData);

    }


    public ArrayList<AddedContactModel> GetStudentList() {
        ArrayList<AddedContactModel> listForDailyHomework = new ArrayList<>();
        DatabaseQueries qr = new DatabaseQueries(TeacherMyClassAttenadanceActivity.this);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TeacherMyClassAttenadanceActivity.this);
        String std = preferences.getString("STANDARD", "");
        String div = preferences.getString("DIVISION", "");
        try {
            JSONArray arr = new JSONArray(qr.GetAllTableData(std,div));

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


    public void setLayoutRef(LinearLayout searchWidgetLayout) {
        this.newLayoutRef = searchWidgetLayout;
    }

    private class SelectContactFilter implements Filterable {
        @Override
        public Filter getFilter() {
            return new ListFilter();
        }
    }

    /**
     * ListFilter will work from SelectRecipientsAdapter.
     * filter class for search to employees
     */
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
    }


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
        adapter.setFlag("Attendance");
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
                finish();
                return true;
            case R.id.add_contact_done:
                saveAttendance();
                //finish();
                return true;
            case R.id.action_search:
                getSupportActionBar().hide();
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
                //getSupportActionBar().lockDrawer();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Singlton.setSelectedFragment(Singlton.getMainFragment());
    }

    public void saveAttendance()
    {

        int len = studentNameList.size();
        //SparseBooleanArray checked = lstStudentName.getCheckedItemPositions();
        int lenthchkd = selectedTeam.size();
        String pre[]=new String[lenthchkd];
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(TeacherMyClassAttenadanceActivity.this);
        String std = preferences.getString("STANDARD", "");
        String div = preferences.getString("DIVISION", "");

        String attende = "";
        String abscente = "";
        String absc[] = new String[len-lenthchkd];
        String arruid [] =new String[lenthchkd];
        String arruidab[]=new String[len-lenthchkd];
        int j=0;
        int k=0;

        for(int i=0; i<selectedTeam.size();i++)
        {
            pre[i] = selectedTeam.get(i).getUserName();
            arruid[i] = selectedTeam.get(i).getUserId();
            j = j + 1;
        }

        for (int i = 0; i < studentNameList.size(); i++) {
            for (int l = 0; l < selectedTeam.size(); l++) {
                if(!studentNameList.get(i).getUserId().equalsIgnoreCase(selectedTeam.get(l).getUserId())) {

                    if (l == selectedTeam.size() - 1) {
                        absc[k] = studentNameList.get(i).getUserName();
                        arruidab[k] = studentNameList.get(i).getUserId();
                        k = k + 1;
                    }
                }
                else
                {
                    break;
                }
            }
        }



        for (int i = 0; i < arruid.length; i++) {
            if (i == arruid.length - 1) {
                attende = attende + arruid[i];
            } else {
                attende = attende + arruid[i] + ",,";
            }
        }




        for (int i = 0; i < arruidab.length; i++) {
            if (i == arruidab.length - 1) {
                abscente = abscente + arruidab[i];
            } else {
                abscente = abscente + arruidab[i] + ",,";
            }
        }



        Log.d("Present", attende);
        Log.d("Abscent", abscente);


        Calendar calendar = Calendar.getInstance();
        int y = calendar.get(Calendar.YEAR);
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat df1 = new SimpleDateFormat("hh:mm");


        String date = df.format(calendar.getTime());
        String year = String.valueOf(y);

        int id=0;
        String sendby=  preferences.getString("UidName", "");
        String syncflag = "false";


        String time = df1.format(calendar.getTime());

        ArrayList<String> chkdate = qr.GetAllAdtes(std,div);

        long n=-1;
        if(chkdate.size()==0)
        {

            n = qr.insertAttendance(date,scode,std,div,sendby,attende,abscente,pre.length,absc.length);
            id  =  qr.getAttendanceId();
        }
        else {

            for (int i = 0; i < chkdate.size(); i++) {

                if (date.equals(chkdate.get(i).toString())) {

                    TeacherAttendanceListModel obj1 = qr.GetAttendanceByDate(date);
                    obj1.setAttendanceDate(date);
                    obj1.setStd(std);
                    obj1.setDiv(div);
                    obj1.setSyncflag("false");
                    id = obj1.getAttendanceId();
                    obj1.setAttendees(attende);
                    obj1.setAbsenties(abscente);
                    obj1.setPresenceCnt(pre.length);
                    obj1.setAbsentCnt(absc.length);
                    n = qr.updateAttendanceSyncFlag(obj1);
                    break;
                }

                else {
                    if (i == chkdate.size() - 1) {

                        n = qr.insertAttendance(date,scode, std, div, sendby, attende, abscente, pre.length, absc.length);
                        id = qr.getAttendanceId();
                    }
                }
            }
        }


        if(n>=0)
        {
            n=-1;
            SimpleDateFormat df2 = new SimpleDateFormat("dd MMM hh:mm:ss a");
            n = qr.insertQueue(id,"Attendance","3",df2.format(calendar.getTime()));
            if(n>=0)
            {

                n=-1;
                if(Config.isConnectingToInternet(TeacherMyClassAttenadanceActivity.this))
                {
                    TeacherAttendanceListModel o = qr.GetAttendanceID(id);
                    Log.d("ID", "" + o.getAttendanceId());
                    attId = o.getAttendanceId();
                    TeacherAttendanceAsyncTaskPost obj = new TeacherAttendanceAsyncTaskPost(o,TeacherMyClassAttenadanceActivity.this,TeacherMyClassAttenadanceActivity.this,"true");
                    obj.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
                else
                {
                    finish();
                }
            }
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
