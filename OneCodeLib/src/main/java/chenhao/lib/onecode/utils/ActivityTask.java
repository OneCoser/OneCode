package chenhao.lib.onecode.utils;

import android.app.Activity;
import java.util.ArrayList;
import java.util.List;

public class ActivityTask {

    private List<Activity> tasks;
    private static ActivityTask self;

    public ActivityTask() {
        tasks = new ArrayList<Activity>();
    }

    public static ActivityTask init() {
        if (null == self) {
            self = new ActivityTask();
        }
        return self;
    }

    public void addTask(Activity a) {
        if (null != a) {
            tasks.add(a);
        }
    }

    public void clearTask() {
        for (Activity a : tasks) {
            if (null != a && !a.isFinishing()) {
                a.finish();
            }
        }
        tasks.clear();
    }

    public Activity getLast() {
        if (null != tasks && tasks.size() > 0 && null != tasks.get(tasks.size() - 1) && !tasks.get(tasks.size() - 1).isFinishing()) {
            return tasks.get(tasks.size() - 1);
        }
        return null;
    }

    public void checkRemove(Activity activity) {
        if (null != tasks && tasks.size() > 0) {
            tasks.remove(activity);
        }
    }

    public List<Activity> getTaskList(){
        return tasks;
    }

}
