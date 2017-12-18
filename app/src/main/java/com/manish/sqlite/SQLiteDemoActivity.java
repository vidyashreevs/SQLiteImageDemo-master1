package com.manish.sqlite;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

public class SQLiteDemoActivity extends Activity {
	ArrayList<Contact> imageArry = new ArrayList<Contact>();
	ContactImageAdapter adapter;

	private Bitmap image, image1;
	private byte[] imageInByte;
	private byte[] imageInByte1;
	private Bitmap bmp;
	byte[] imageName;
	int imageId;
	private DataBaseHandler db;
	private ListView dataList;

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		 db = new DataBaseHandler(this);

		int row = db.numberOfRows();

		// get image from drawable
		image = BitmapFactory.decodeResource(getResources(), R.drawable.facebook);
		image1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);


		// convert bitmap to byte
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		imageInByte = stream.toByteArray();

		ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
		image1.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
		imageInByte1 = stream1.toByteArray();

		/**
		 * CRUD Operations
		 * */
		// Inserting Contacts
		Log.d("Insert: ", "Inserting ..");
		if (row == 0) {
			db.addContact(new Contact("FaceBook", imageInByte));
			db.addContact(new Contact("IC", imageInByte1));
		}
		// display main List view bcard and contact name

		// Reading all contacts from database
		List<Contact> contacts = db.getAllContacts();
		for (Contact cn : contacts) {
			String log = "ID:" + cn.getID() + " Name: " + cn.getName()
					+ " ,Image: " + cn.getImage();

			// Writing Contacts to log
			Log.d("Result: ", log);
			//add contacts data in arrayList
			imageArry.add(cn);

		}
		adapter = new ContactImageAdapter(this, R.layout.screen_list,
				imageArry);
		 dataList = (ListView) findViewById(R.id.list);
		dataList.setAdapter(adapter);


		dataList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

				imageName = imageArry.get(i).getImage();
				imageId = imageArry.get(i).getID();

				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
			//	photoPickerIntent.setType("image*//*");
				final int ACTIVITY_SELECT_IMAGE = 1234;
				startActivityForResult(photoPickerIntent, ACTIVITY_SELECT_IMAGE);



			/*	Intent i1 = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
				final int ACTIVITY_SELECT_IMAGE = 1234;
				startActivityForResult(i1, ACTIVITY_SELECT_IMAGE);*/
				return false;
			}
		});


	}

	@Override
	protected void onActivityResult(int requestCode, int resultcode, Intent intent) {
		super.onActivityResult(requestCode, resultcode, intent);

		if (requestCode == 1234) {
			if (intent != null && resultcode == RESULT_OK) {

				Uri selectedImage = intent.getData();

				String[] filePathColumn = {MediaStore.Images.Media.DATA};
				Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
				cursor.moveToFirst();
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				String filePath = cursor.getString(columnIndex);
				cursor.close();

				if (bmp != null && !bmp.isRecycled()) {
					bmp = null;
				}
				bmp = BitmapFactory.decodeFile(filePath);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte imageInByte[] = stream.toByteArray();
				//	Log.e("output before conversion", imageInByte.toString());
				// Inserting Contacts
				Log.d("Insert: ", "Inserting ..");
				db.updateContact(new Contact(imageId,"Android", imageInByte));

				/*List<Item> newItems = databaseHandler.getItems();
				ListArrayAdapter.clear();
				ListArrayAdapter.addAll(newItems);
				ListArrayAdapter.notifyDataSetChanged();
				databaseHandler.close();*/



				List<Contact> contacts = db.getAllContacts();
				adapter.clear();
				for (Contact cn : contacts) {
					String log = "ID:" + cn.getID() + " Name: " + cn.getName()
							+ " ,Image: " + cn.getImage();

					// Writing Contacts to log
					Log.d("Result: ", log);
					//add contacts data in arrayList
					imageArry.add(cn);

				}
				adapter = new ContactImageAdapter(this, R.layout.screen_list,
						imageArry);
				dataList = (ListView) findViewById(R.id.list);
				dataList.setAdapter(adapter);

			} else {
				Log.d("Status:", "Photopicker canceled");
			}
		}

	}
}