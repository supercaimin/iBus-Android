package cn.homecaught.ibus_jhr.util;

import java.io.File;

import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.Manifest;
import android.content.pm.PackageManager;

/**
 * @author leiwei
 *
 */
public class CameraDialog {

	public static final String UPLOAD_FILE_PARENT_PATH = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ File.separatorChar + "com.kuruan.jinque" + File.separatorChar;

	public static final int CAMERA_CODE = 123;

	public static final int IMAGE_CODE = 124;

	public static final int CLIP_IMAGE = 125;

	/** dialog */
	private CharSequence[] chekdata = { "拍照", "本地相册", "取消" };
	private AlertDialog.Builder checkDialog = null;

	/**拍照*/
	private File cameraFile;

	public CameraDialog(final Activity activity) {
		init();
		checkDialog = new AlertDialog.Builder(activity);
		checkDialog.setTitle("选择");
		checkDialog.setIcon(null);
		checkDialog.setItems(chekdata, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.CAMERA)
							!= PackageManager.PERMISSION_GRANTED) {
						// Check Permissions Now
						// Callback onRequestPermissionsResult interceptado na Activity MainActivity
						ActivityCompat.requestPermissions(activity,
								new String[]{Manifest.permission.CAMERA},
								123);
					} else {
						// permission has been granted, continue as usual

						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						cameraFile = getNewFile();
						Log.e("newfile", "" + cameraFile);
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(cameraFile));
						intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024 * 100);
						activity.startActivityForResult(intent, CAMERA_CODE);
					}
					break;
				case 1:
					Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
					getAlbum.setType("image/*");
					// getAlbum.putExtra("outputX", 100); //返回数据的时候的
					// X 像素大小。
					// getAlbum.putExtra("outputY", 100); //返回的时候 Y
					// 的像素大小。
					activity.startActivityForResult(getAlbum, IMAGE_CODE);

					break;
				case 2:
					break;
				}

			}
		});
		checkDialog.create();
	}

	public CameraDialog(final Fragment fragment) {
		init();
		checkDialog = new AlertDialog.Builder(fragment.getActivity());
		checkDialog.setTitle("选择");
		checkDialog.setIcon(null);
		checkDialog.setItems(chekdata, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case 0:
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					cameraFile = getNewFile();
					Log.e("newfile", "" + cameraFile);
					intent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(cameraFile));
					fragment.startActivityForResult(intent, CAMERA_CODE);
					break;
				case 1:
					Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
					getAlbum.setType("image/*");
					// getAlbum.putExtra("outputX", 100); //返回数据的时候的
					// X 像素大小。
					// getAlbum.putExtra("outputY", 100); //返回的时候 Y
					// 的像素大小。
					fragment.startActivityForResult(getAlbum, IMAGE_CODE);
					break;
				case 2:
					break;
				}

			}
		});
		checkDialog.create();
	}

	public void init() {
		File file = new File(UPLOAD_FILE_PARENT_PATH);
		if (!file.exists())
			file.mkdir();

	}

	private File getNewFile() {
		String name = "img" + System.currentTimeMillis() + ".jpg";
		File fos = null;
		try {
			fos = new File(UPLOAD_FILE_PARENT_PATH + name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (fos.exists()) {
			getNewFile();
		}
		return fos;

	}

	public void show() {
		checkDialog.show();
	}

	public File getCameraFile() {
		return cameraFile;
	}
}
