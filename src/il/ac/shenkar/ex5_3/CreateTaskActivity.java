package il.ac.shenkar.ex5_3;

import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class CreateTaskActivity extends Activity 
{
	private DatabaseHandler db;
	 // This is a handle so that we can call methods on our service
 //   private ScheduleClient scheduleClient;
    // This is the date picker used to select the date for our notification
    private DatePicker datePicker;
    private TimePicker timePicker;
    public static String EXTRA_MESSAGE = "com.shenkar.todolist.MESSAGE";
    private String titleTask = "";
    // The context to start the service in
    private Context mContext;
    // The android system alarm manager
    private AlarmManager am;

    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        // Get a reference to our date picker
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        db = DatabaseHandler.getInstance(this);
        final EditText description = (EditText) findViewById(R.id.enter_task);
        final Button newTaskButton = (Button) findViewById(R.id.create_button);
        newTaskButton.setOnClickListener(new View.OnClickListener() 
        {
            @Override
            public void onClick(View view) 
            {              	
            	titleTask = description.getText().toString();
            	db.pushTask(titleTask);
            	onDateSelectedButtonClick(view);
                finish();
            }
        });
        final Button BackButton = (Button) findViewById(R.id.back_button);
        BackButton.setOnClickListener(new View.OnClickListener() 
        {
        	@Override
            public void onClick(View view) 
            {  
        		finish();
            }
        });
    }
    
    /**
     * This is the onClick called from the XML to set a new notification 
     */
    public void onDateSelectedButtonClick(View v)
    {
    	mContext = getApplicationContext();
        // Create a new calendar set to the date chosen
        // we set the time to midnight (i.e. the first minute of that day)
        Calendar c = Calendar.getInstance();
        c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth(), timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
        // Ask our service to set an alarm for that date, this activity talks to the client that talks to the service
        am = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        // Request to start are service when the alarm date is upon us
        // We don't start an activity as we just want to pop up a notification into the system bar not a full activity
        Intent intent = new Intent(mContext, NotifyService.class);
        intent.putExtra(NotifyService.INTENT_NOTIFY, true);
        intent.putExtra(EXTRA_MESSAGE,titleTask);
        PendingIntent pendingIntent = PendingIntent.getService(mContext, 0, intent, 0);
         
        // Sets an alarm - note this alarm will be lost if the phone is turned off and on again
        am.set(AlarmManager.RTC, c.getTimeInMillis(), pendingIntent);
        // Notify the user what they just did
        Toast.makeText(this, "Notification set for:\n  "+ datePicker.getDayOfMonth() +"/"+ (datePicker.getMonth()+1) +"/"+ datePicker.getYear() + "\t\t" + timePicker.getCurrentHour() + ":" + timePicker.getCurrentMinute(), Toast.LENGTH_LONG).show();
    }
}
