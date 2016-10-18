package com.realizer.schoolgeine.teacher.funcenter;

import android.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.realizer.schoolgeine.teacher.exceptionhandler.ExceptionHandler;
import com.realizer.schoolgenie.teacher.R;

/**
 * Created by Win on 02/04/2016.
 */
public class BaseActivity extends Fragment
{
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    MenuItem search,switchclass;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        search = menu.findItem(R.id.action_search);
        search.setVisible(false);
        switchclass = menu.findItem(R.id.action_switchclass);
        switchclass.setVisible(false);

    }

    public void hideMenu()
    {
        if(search != null && switchclass != null)
        {
            search.setVisible(false);
            switchclass.setVisible(false);
        }
    }
}
