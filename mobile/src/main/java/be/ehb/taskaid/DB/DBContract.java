package be.ehb.taskaid.DB;

import android.provider.BaseColumns;

/**
 * Created by davy.van.belle on 19/08/2015.
 */
public final class DBContract {

    public DBContract(){}

    public static abstract class Tasks implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String TASK_NAME = "name";
        public static final String BEACON_ADDRESS = "beacon";
    }

    public static abstract class Cards implements BaseColumns {
        public static final String TABLE_NAME = "cards";
        public static final String TASK_ID = "task_id";
        public static final String TEXT = "text";
        public static final String PICTURE = "picture";
        public static final String ORDER = "_order";
    }
}
