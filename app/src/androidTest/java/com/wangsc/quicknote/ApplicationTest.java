package com.wangsc.quicknote;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.wangsc.quicknote.activity.MainActivity;
import com.wangsc.quicknote.model.DataContext;
import com.wangsc.quicknote.model.Note;

import java.util.UUID;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
    public void testDataContext(){
        DataContext context = new DataContext(getContext());
        context.editNote(new Note(UUID.randomUUID()));
    }
}