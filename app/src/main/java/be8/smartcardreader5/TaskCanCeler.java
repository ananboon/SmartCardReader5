package be8.smartcardreader5;

import android.os.AsyncTask;
import android.util.Log;

public class TaskCanCeler implements Runnable{
    private static String TAG = TaskCanCeler.class.getName();
    private AsyncTask task;

    public TaskCanCeler(AsyncTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        Log.d(TAG,"DEBUG - Cancel Task");
        if (task.getStatus() == AsyncTask.Status.RUNNING )
            task.cancel(true);
    }
}
