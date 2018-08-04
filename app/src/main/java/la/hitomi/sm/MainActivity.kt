package la.hitomi.sm


import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.View
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar

class MainActivity : AppCompatActivity() , PermissionListener{
    override fun onPermissionGranted() {
        toast("permission Granted")
    }

    override fun onPermissionDenied(deniedPermissions: ArrayList<String>?) {
        toast("permission not granted")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        TedPermission.with(this)
                .setPermissionListener(this)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR)
                .check();
    }

    fun addEvent(view: View) {
        val eventTitle = "Jazzercise"
        if (isEventAlreadyExist(eventTitle)) {
            Snackbar.make(view, "Jazzercise event already exist!", Snackbar.LENGTH_SHORT).show()
            return
        }

        val calID: Long = 3
        var startMillis: Long = 0
        var endMillis: Long = 0
        val beginTime = Calendar.getInstance()
        beginTime.set(2017, 11, 18, 6, 0)
        startMillis = beginTime.timeInMillis
        val endTime = Calendar.getInstance()
        endTime.set(2017, 11, 18, 8, 0)
        endMillis = endTime.timeInMillis

        val cr = contentResolver
        val values = ContentValues()
        values.put(CalendarContract.Events.DTSTART, startMillis)
        values.put(CalendarContract.Events.DTEND, endMillis)
        values.put(CalendarContract.Events.TITLE, "Jazzercise")
        values.put(CalendarContract.Events.DESCRIPTION, "Group workout")
        values.put(CalendarContract.Events.CALENDAR_ID, calID)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "America/Los_Angeles")
        values.put(CalendarContract.Events.ORGANIZER, "google_calendar@gmail.com")

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            val uri = cr.insert(CalendarContract.Events.CONTENT_URI, values)
            val eventID = java.lang.Long.parseLong(uri!!.lastPathSegment)
            Log.i("Calendar", "Event Created, the event id is: $eventID")
            Snackbar.make(view, "Jazzercise event added!", Snackbar.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_CALENDAR), MY_PERMISSIONS_REQUEST_WRITE_CALENDAR)
        }

        showEvents(eventTitle)
    }

    fun removeEvent(view: View) {
        val eventTitle = "Jazzercise"

        val INSTANCE_PROJECTION = arrayOf(CalendarContract.Instances.EVENT_ID, // 0
                CalendarContract.Instances.BEGIN, // 1
                CalendarContract.Instances.TITLE          // 2
        )
        // The indices for the projection array above.
        val PROJECTION_ID_INDEX = 0
        val PROJECTION_BEGIN_INDEX = 1
        val PROJECTION_TITLE_INDEX = 2

        // Specify the date range you want to search for recurring event instances
        val beginTime = Calendar.getInstance()
        beginTime.set(2017, 9, 23, 8, 0)
        val startMillis = beginTime.timeInMillis
        val endTime = Calendar.getInstance()
        endTime.set(2018, 1, 24, 8, 0)
        val endMillis = endTime.timeInMillis


        // The ID of the recurring event whose instances you are searching for in the Instances table
        val selection = CalendarContract.Instances.TITLE + " = ?"
        val selectionArgs = arrayOf(eventTitle)

        // Construct the query with the desired date range.
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)

        // Submit the query
        val cur = contentResolver.query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, null)

        while (cur!!.moveToNext()) {
            // Get the field values
            val eventID = cur.getLong(PROJECTION_ID_INDEX)
            val beginVal = cur.getLong(PROJECTION_BEGIN_INDEX)
            val title = cur.getString(PROJECTION_TITLE_INDEX)

            var deleteUri: Uri? = null
            deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID)
            val rows = contentResolver.delete(deleteUri!!, null, null)
            Log.i("Calendar", "Rows deleted: $rows")
        }

        //showEvents(eventTitle)
    }

    fun searchEvent(view: View) {
        showEvents("Jazzercise")
    }

    private fun showEvents(eventTitle: String) {
        val INSTANCE_PROJECTION = arrayOf(CalendarContract.Instances.EVENT_ID, // 0
                CalendarContract.Instances.BEGIN, // 1
                CalendarContract.Instances.TITLE, // 2
                CalendarContract.Instances.ORGANIZER    //3
        )

        // The indices for the projection array above.
        val PROJECTION_ID_INDEX = 0
        val PROJECTION_BEGIN_INDEX = 1
        val PROJECTION_TITLE_INDEX = 2
        val PROJECTION_ORGANIZER_INDEX = 3

        // Specify the date range you want to search for recurring event instances
        val beginTime = Calendar.getInstance()
        beginTime.set(2017, 9, 23, 8, 0)
        val startMillis = beginTime.timeInMillis
        val endTime = Calendar.getInstance()
        endTime.set(2018, 1, 24, 8, 0)
        val endMillis = endTime.timeInMillis


        // The ID of the recurring event whose instances you are searching for in the Instances table
        val selection = CalendarContract.Instances.TITLE + " = ?"
        val selectionArgs = arrayOf(eventTitle)

        // Construct the query with the desired date range.
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)

        // Submit the query
        val cur = contentResolver.query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, null)


        val events = ArrayList<String>()
        while (cur!!.moveToNext()) {
            // Get the field values
            val eventID = cur.getLong(PROJECTION_ID_INDEX)
            val beginVal = cur.getLong(PROJECTION_BEGIN_INDEX)
            val title = cur.getString(PROJECTION_TITLE_INDEX)
            val organizer = cur.getString(PROJECTION_ORGANIZER_INDEX)

            // Do something with the values.
            Log.i("Calendar", "Event:  $title")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = beginVal
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            Log.i("Calendar", "Date: " + formatter.format(calendar.time))

            events.add("Event ID: $eventID\nEvent: $title\nOrganizer: $organizer\nDate: $formatter.format(calendar.time)")
        }

        val stringArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, events)
        listView.adapter = stringArrayAdapter
    }

    private fun isEventAlreadyExist(eventTitle: String): Boolean {
        val INSTANCE_PROJECTION = arrayOf(CalendarContract.Instances.EVENT_ID, // 0
                CalendarContract.Instances.BEGIN, // 1
                CalendarContract.Instances.TITLE          // 2
        )

        val calID: Long = 3
        var startMillis: Long = 0
        var endMillis: Long = 0
        val beginTime = Calendar.getInstance()
        beginTime.set(2017, 11, 18, 6, 0)
        startMillis = beginTime.timeInMillis
        val endTime = Calendar.getInstance()
        endTime.set(2017, 11, 18, 8, 0)
        endMillis = endTime.timeInMillis

        // The ID of the recurring event whose instances you are searching for in the Instances table
        val selection = CalendarContract.Instances.TITLE + " = ?"
        val selectionArgs = arrayOf(eventTitle)

        // Construct the query with the desired date range.
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)

        // Submit the query
        val cur = contentResolver.query(builder.build(), INSTANCE_PROJECTION, selection, selectionArgs, null)

        return cur!!.count > 0
    }


    fun readEvents(view: View) {
        val INSTANCE_PROJECTION = arrayOf(CalendarContract.Instances.EVENT_ID, // 0
                CalendarContract.Instances.BEGIN, // 1
                CalendarContract.Instances.TITLE, // 2
                CalendarContract.Instances.ORGANIZER)

        // The indices for the projection array above.
        val PROJECTION_ID_INDEX = 0
        val PROJECTION_BEGIN_INDEX = 1
        val PROJECTION_TITLE_INDEX = 2
        val PROJECTION_ORGANIZER_INDEX = 3

        // Specify the date range you want to search for recurring event instances
        val beginTime = Calendar.getInstance()
        beginTime.set(2017, 9, 23, 8, 0)
        val startMillis = beginTime.timeInMillis
        val endTime = Calendar.getInstance()
        endTime.set(2018, 1, 24, 8, 0)
        val endMillis = endTime.timeInMillis


        // The ID of the recurring event whose instances you are searching for in the Instances table
        val selection = CalendarContract.Instances.EVENT_ID + " = ?"
        val selectionArgs = arrayOf("207")

        // Construct the query with the desired date range.
        val builder = CalendarContract.Instances.CONTENT_URI.buildUpon()
        ContentUris.appendId(builder, startMillis)
        ContentUris.appendId(builder, endMillis)

        // Submit the query
        val cur = contentResolver.query(builder.build(), INSTANCE_PROJECTION, null, null, null)


        val events = ArrayList<String>()
        while (cur!!.moveToNext()) {

            // Get the field values
            val eventID = cur.getLong(PROJECTION_ID_INDEX)
            val beginVal = cur.getLong(PROJECTION_BEGIN_INDEX)
            val title = cur.getString(PROJECTION_TITLE_INDEX)
            val organizer = cur.getString(PROJECTION_ORGANIZER_INDEX)

            // Do something with the values.
            Log.i("Calendar", "Event:  $title")
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = beginVal
            val formatter = SimpleDateFormat("MM/dd/yyyy")
            Log.i("Calendar", "Date: " + formatter.format(calendar.time))

            events.add("Event ID: $eventID\nEvent: $title\nOrganizer: $organizer\nDate: ${formatter.format(calendar.time)}")
        }

        val stringArrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, events)
        listView.adapter = stringArrayAdapter
    }


    private fun updateEvent(eventID: Long) {
        val cr = contentResolver
        val values = ContentValues()
        values.put(CalendarContract.Events.TITLE, "Kickboxing")
        val updateUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID)
        val rows = contentResolver.update(updateUri, values, null, null)
        Log.i("Calendar", "Rows updated: $rows")
    }

    private fun deleteEvent(eventID: Long) {
        val deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventID)
        val rows = contentResolver.delete(deleteUri, null, null)
        Log.i("Calendar", "Rows deleted: $rows")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_READ_CALENDAR -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return
            }
            MY_PERMISSIONS_REQUEST_WRITE_CALENDAR -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                }
                return
            }
        }
    }

    companion object {

        private val MY_PERMISSIONS_REQUEST_READ_CALENDAR = 1000
        private val MY_PERMISSIONS_REQUEST_WRITE_CALENDAR = 1001
    }
}
