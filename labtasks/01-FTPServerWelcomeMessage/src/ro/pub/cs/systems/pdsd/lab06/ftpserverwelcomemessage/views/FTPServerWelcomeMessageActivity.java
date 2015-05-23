package ro.pub.cs.systems.pdsd.lab06.ftpserverwelcomemessage.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import ro.pub.cs.systems.pdsd.lab06.ftpserverwelcomemessage.R;
import ro.pub.cs.systems.pdsd.lab06.ftpserverwelcomemessage.general.Constants;
import ro.pub.cs.systems.pdsd.lab06.ftpserverwelcomemessage.general.Utilities;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FTPServerWelcomeMessageActivity extends Activity {
	
	private EditText FTPServerAddressEditText;
	private TextView welcomeMessageTextView;
	
	protected class FTPServerCommunicationThread extends Thread {
		
		@Override
		public void run() {
			 try {
				Socket socket = new Socket(FTPServerAddressEditText.getText().toString(),
						Constants.FTP_PORT);
				BufferedReader bufferedReader = Utilities.getReader(socket);
				
				String message = bufferedReader.readLine();
				
				if(message.startsWith(Constants.FTP_MULTILINE_START_CODE)) {
					
					String line = bufferedReader.readLine();
					while(!line.startsWith(Constants.FTP_MULTILINE_END_CODE2)) {
						message += line;
						line = bufferedReader.readLine();
					}
					
					message += line;
				}
				
				final String final_message = message;
				
				welcomeMessageTextView.post(new Runnable() {
					@Override
					public void run() {
						welcomeMessageTextView.setText(final_message);
					}
				});
				socket.close();
			} catch (UnknownHostException unknownHostException) {
				Log.e(Constants.TAG, "An exception has occurred: "
						+ unknownHostException.getMessage());
				if (Constants.DEBUG) {
					unknownHostException.printStackTrace();
				}
			} catch (IOException ioException) {
				Log.e(Constants.TAG, "An exception has occurred: "
						+ ioException.getMessage());
				if (Constants.DEBUG) {
					ioException.printStackTrace();
				}
			}
		}
	}	
	
	private ButtonClickListener buttonClickListener = new ButtonClickListener();
	private class ButtonClickListener implements Button.OnClickListener {
		
		@Override
		public void onClick(View view) {
			new FTPServerCommunicationThread().start();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ftpserver_welcome_message);
		
		FTPServerAddressEditText = (EditText)findViewById(R.id.ftp_server_address_edit_text);
		welcomeMessageTextView = (TextView)findViewById(R.id.welcome_message_text_view);
		
		Button displayWelcomeMessageButton = (Button)findViewById(R.id.display_welcome_message_button);
		displayWelcomeMessageButton.setOnClickListener(buttonClickListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ftpserver_welcome_message, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
