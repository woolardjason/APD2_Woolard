package com.jasonwoolard.geoshare;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

@SuppressLint("NewApi")
public class ResetPasswordDialogFragment extends DialogFragment {
	  public static ResetPasswordDialogFragment newInstance() {
	        return new ResetPasswordDialogFragment();
	    }
	  @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	            Bundle savedInstanceState) {
	        // Defining layout to inflate, in this case: reset_pw_fragment
	        final View v = inflater.inflate(R.layout.reset_pw_fragment, container, false);
	        // Setting resetPwBtn to the id in resources
	        Button resetPwBtn = (Button) v.findViewById(R.id.button_reset_pw);
	        
	        // OnClickListener for the resetPwBtn
	        resetPwBtn.setOnClickListener(new View.OnClickListener() {

	            @Override
	            public void onClick(View view) {
	            	// Defining the inputField in the XML layout 'reset_ow_fragment'
	                EditText resetEmail = (EditText) v.findViewById(R.id.editText_reset_email);
	                // Setting inputedString to the current text in resetEmail field
	                String inputedString = resetEmail.getText().toString();
	                
	                // Running Parse Request Password Reset in Background method which doesn't boggle up the main thread.
	                ParseUser.requestPasswordResetInBackground(inputedString, new RequestPasswordResetCallback() 
	                {
	                	public void done(ParseException e) 
	                	{
	                		if (e == null) 
	                		{
	                			AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
								b.setMessage(R.string.success_message_reset_pw);
								b.setTitle(R.string.success_title_reset_pw);
								b.setPositiveButton(android.R.string.ok, null);
								AlertDialog d = b.create();
								d.show();
								  
				                // Utilizing public method dismiss(); to dismiss the reset pw fragment dialog.
				                dismiss();
	                		} 
	                		else
	                		{
	                			AlertDialog.Builder b = new AlertDialog.Builder(v.getContext());
								b.setMessage(e.getMessage());
								b.setTitle(R.string.error_title_sign_up_verify_pw);
								b.setPositiveButton(android.R.string.ok, null);
								AlertDialog d = b.create();
								d.show();
	                		}
	                	}
	                });
	            }
	        });
	        return v;
	    }
}
