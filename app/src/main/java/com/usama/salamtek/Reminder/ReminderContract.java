package com.usama.salamtek.Reminder;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ReminderContract {
  /**
   * The authority of the provider.
   */
  public static final String AUTHORITY =
          "com.usama.salamtek";
  /**
   * The content URI for the top-level
   * authority.
   */
  public static final Uri BASE_CONTENT_URI =
          Uri.parse("content://" + AUTHORITY);

  public static final String PATH_ALL = "all";
  public static final String PATH_ALL_ID = "all/#";
  public static final String PATH_NOTE = "note";
  public static final String PATH_NOTE_ID = "note/#";
  public static final String PATH_ALERT = "alert";
  public static final String PATH_ALERT_ID = "alert/#";

  public static final String _ID = "_id";
  public static final String TYPE = "type";
  public static final String TITLE = "title";
  public static final String CONTENT = "content";
  public static final String TIME = "time";
  public static final String FREQUENCY = "frequency";

  public static final String[] PROJECTION_ALL = {_ID, TYPE, TITLE, CONTENT, TIME, FREQUENCY};

  public static final class Notes implements BaseColumns {
    public static final String TABLE_NAME = "reminders";
    public static final String _ID = "_id";
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_NOTE).build();

    // Custom MIME types
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_NOTE;

    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_NOTE;

    public static final String[] PROJECTION_ALL = {_ID, TYPE, TITLE, CONTENT};
  }

  public static final class Alerts implements BaseColumns {
    public static final String TABLE_NAME = "reminders";
    public static final String _ID = "_id";
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final String FREQUENCY = "frequency";

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALERT).build();

    // Custom MIME types
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_ALERT;

    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_ALERT;

    public static final String[] PROJECTION_ALL = {_ID, TYPE, TITLE, CONTENT, TIME, FREQUENCY};

  }

  public static final class All implements BaseColumns {
    public static final String TABLE_NAME = "reminders";
    public static final String _ID = "_id";
    public static final String TYPE = "type";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final String FREQUENCY = "frequency";

    public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALL).build();

    // Custom MIME types
    public static final String CONTENT_TYPE =
            ContentResolver.CURSOR_DIR_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_ALL;

    public static final String CONTENT_ITEM_TYPE =
            ContentResolver.CURSOR_ITEM_BASE_TYPE +
                    "/" + AUTHORITY + "/" + PATH_ALL;

    public static final String[] PROJECTION_ALL = {_ID, TYPE, TITLE, CONTENT, TIME, FREQUENCY};

  }
}