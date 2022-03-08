package com.gidisoft.sdk.tool;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gidisoft.paul.sdk.R;


public class CustomDialog extends Dialog {

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context;
		private String title;
		private String message;
		private String positiveButtonText;
		private String negativeButtonText;
		private View contentView;
		private OnClickListener positiveButtonClickListener;
		private OnClickListener negativeButtonClickListener;

		public Builder(Context context) {
			this.context = context;

		}

		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 * 
		 * @param message
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 * 
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 * 
		 * @param title
		 * @return
		 */

		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 * 
		 * @param positiveButtonText
		 * @return
		 */
		public Builder setPositiveButton(int positiveButtonText,
				OnClickListener listener) {
			this.positiveButtonText = (String) context
					.getText(positiveButtonText);
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setPositiveButton(String positiveButtonText,
                                         OnClickListener listener) {
			this.positiveButtonText = positiveButtonText;
			this.positiveButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(int negativeButtonText,
				OnClickListener listener) {
			this.negativeButtonText = (String) context
					.getText(negativeButtonText);
			this.negativeButtonClickListener = listener;
			return this;
		}

		public Builder setNegativeButton(String negativeButtonText,
                                         OnClickListener listener) {
			this.negativeButtonText = negativeButtonText;
			this.negativeButtonClickListener = listener;
			return this;
		}
		Button nagative;
		Button positive;
		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context,
					R.style.dialogCustom);
			View layout = inflater
					.inflate(R.layout.dialog_custom_layout, null);

			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			((TextView) layout.findViewById(R.id.title)).setText(title);
			positive = (Button) layout.findViewById(R.id.positive_btn);
			// set the confirm button
			if (positiveButtonText != null) {
				positive.setText(positiveButtonText);
			} else {
				// if no confirm button just set the visibility to GONE
				positive.setVisibility(View.GONE);
			}
			if (null != positiveButtonClickListener) {
				positive.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						positiveButtonClickListener.onClick(dialog,
								DialogInterface.BUTTON_POSITIVE);
					}
				});
			}
			nagative = (Button) layout.findViewById(R.id.negative_btn);
			// set the cancel button
			if (negativeButtonText != null) {
				nagative.setText(negativeButtonText);
			} else {
				// if no confirm button just set the visibility to GONE
				nagative.setVisibility(View.GONE);
			}
			if (negativeButtonClickListener != null) {
				nagative.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						negativeButtonClickListener.onClick(dialog,
								DialogInterface.BUTTON_NEGATIVE);
					}
				});
			}
			// set the content message
			if (null != message) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} else if (null != contentView) {
				((LinearLayout) layout.findViewById(R.id.content))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.content)).addView(
						contentView, new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
			}
			// if no message set
			// add the contentView to the dialog body
			dialog.setContentView(layout);
			dialog.setOnDismissListener(new OnDismissListener() {

				@Override
				public void onDismiss(DialogInterface dialog) {

				}
			});
			return dialog;
		}
		public Button getPositiveButton(){
			return this.positive;
		}
		public Button getNegativeButton(){
			return this.nagative;
		}

	}

}
