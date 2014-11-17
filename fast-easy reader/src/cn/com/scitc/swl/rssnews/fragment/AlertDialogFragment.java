package cn.com.scitc.swl.rssnews.fragment;

import cn.com.scitc.swl.rssnews.R;
import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class AlertDialogFragment {
	private Context context;
	private String title;
	private AlertDialog dialog;

	private Button btnOK;

	public AlertDialogFragment(Context context, String title) {
		this.setContext(context);
		this.setTitle(title);
		dialog = new AlertDialog.Builder(getContext()).create();
	}

	public void show(View.OnClickListener l) {
		dialog.show();
		Window window = dialog.getWindow();
		window.setContentView(R.layout.normal_alert_dialog);
		TextView title = (TextView) window.findViewById(R.id.dialog_title);
		title.setText(getTitle() + "");
		btnOK = (Button) window.findViewById(R.id.dialog_ok);
		Button cancel = (Button) window.findViewById(R.id.dialog_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dialog.cancel();
			}
		});
		btnOK.setOnClickListener(l);
	}

	public void dismiss(){
		dialog.dismiss();
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
}
