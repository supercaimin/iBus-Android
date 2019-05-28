package cn.homecaught.ibus_jhr_wx.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;


public class ImageUntils {
	/**
	 * 根据图片返回的url 获取路径
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	@SuppressLint("NewApi")
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= 19;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}

				// TODO handle non-primary volumes
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	/**
	 * 从本地取图片(在cdcard中获取)
	 * 
	 * @param url
	 * @return
	 */
	public static Bitmap getLoacalBitmap(String url) {
		File file = new File(url);
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			try {
				long size = fileInputStream.available();
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inPurgeable = true;
				options.inInputShareable = true;
				if (size > 1024 * 1024 * 1) {// 大于1M 直接缩小1/2读取图片
					options.inSampleSize = 2;//
					return BitmapFactory.decodeStream(fileInputStream, null,
							options);
				} else
					return BitmapFactory.decodeStream(fileInputStream, null,
							options); // /把流转化为Bitmap图片
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return null;
		}

	}

	/**
	 * 压缩图片
	 * 
	 * @param image
	 *            大于1M压缩一半
	 * @return
	 */
	public static Bitmap compBitmap(Bitmap image, int sizeKb) {

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int quality = 100;
		image.compress(CompressFormat.JPEG, quality, baos);
		Log.e("原图", "大小kb：" + (baos.toByteArray().length / 1024));
		int countSize = baos.toByteArray().length / 1024;
		while (countSize > sizeKb) {
			baos.reset();
			quality /= 2;
			image.compress(CompressFormat.JPEG, quality, baos);
			countSize = baos.toByteArray().length;
			if (quality < 1) {
				break;
			}
		}

		if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			image.compress(CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;
		float ww = 480f;
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		try {
			baos.close();
			isBm.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 
	 * 将bitmap转换成base64字符串
	 * 
	 *
	 * 
	 * @param bitmap
	 * 
	 * @return base64 字符串
	 */

	public static String bitmaptoString(Bitmap bitmap, int bitmapQuality) {

		// 将Bitmap转换成字符串

		String string = null;

		ByteArrayOutputStream bStream = new ByteArrayOutputStream();

		bitmap.compress(CompressFormat.PNG, bitmapQuality, bStream);

		byte[] bytes = bStream.toByteArray();

		string = Base64.encodeToString(bytes, Base64.DEFAULT);

		return string;

	}

	/**
	 * 质量压缩 大于1M继续压缩
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 1024)// 大于1024
		{
			image.compress(CompressFormat.JPEG, 35, baos);
		}
		Log.e("压缩", "大小kb：" + (baos.toByteArray().length / 1024));
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());

		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		try {
			baos.close();
			isBm.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bitmap;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	/**
	 * Get the value of the data column for this Uri. This is useful for
	 * MediaStore Uris, and other file-based ContentProviders.
	 * 
	 * @param context
	 *            The context.
	 * @param uri
	 *            The Uri to query.
	 * @param selection
	 *            (Optional) Filter used in the query.
	 * @param selectionArgs
	 *            (Optional) Selection arguments used in the query.
	 * @return The value of the _data column, which is typically a file path.
	 */
	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * bitmap保存为文件
	 * 
	 * @param bitmap
	 *            存储图片路径
	 * @return
	 * @throws IOException
	 */
	public static String saveMyBitmap(Bitmap bitmap, String filePath) {

		File file = new File(filePath);

		File parentFile = file.getParentFile();
		if (!parentFile.exists()) {
			parentFile.mkdirs();
		}
		FileOutputStream fos = null;
		try {
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();
			fos = new FileOutputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (fos == null) {
			return null;
		}
		try {
			//bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		try {
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return file.toString();
	}

	/**
	 * 
	 * 
	 * 1508576 143869
	 * 
	 * @param inFile
	 * @param sizeKB
	 * @param outFile
	 * @return
	 */
	public static final Bitmap compressFromFile(File inFile, int sizeKB,
			File outFile) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(outFile);
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			Bitmap inBmp = BitmapFactory.decodeStream(new FileInputStream(
					inFile), null, opt);
			int quality = 1;
			if (inFile.length() > 1024 * 1024 * 5) {
				quality = 2;
			} else if (inFile.length() > 1024 * 1024 * 3) {
				quality = 5;
			} else if (inFile.length() > 1024 * 1024) {
				quality = 20;
			} else {
				quality = 80;
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			inBmp.compress(CompressFormat.PNG, quality, baos);
			while (baos.toByteArray().length / 1024 > sizeKB && quality > 0) {
				quality /= 2;
				baos.reset();
				inBmp.compress(CompressFormat.PNG, quality, baos);

			}
			inBmp.recycle();
			ByteArrayInputStream isBm = new ByteArrayInputStream(
					baos.toByteArray());
			Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, opt);

			if (baos.toByteArray().length > sizeKB * 1024) {
				quality = 100 / (baos.toByteArray().length / 1024 / sizeKB);
			}
			bitmap.compress(CompressFormat.PNG, quality, fileOutputStream);
			bitmap.recycle();
			baos.flush();
			baos.close();
			fileOutputStream.flush();
			fileOutputStream.close();
			isBm.close();
			return bitmap;

		} catch (IOException e) {

		}
		return null;
	}

	public static boolean compressBmpByOptions(File inFile, int width,
			int hight, File outFile) {
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(outFile);
			Bitmap bitmap = getSmallBitmap(inFile.getAbsolutePath(), width,
					hight);
			bitmap.compress(CompressFormat.PNG, 100, fileOutputStream);
			fileOutputStream.flush();
			fileOutputStream.close();
			bitmap.recycle();
			return true;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	public static Bitmap compressBmpByOptions(File inFile, int width, int hight) {
		if (inFile == null || !inFile.exists()) {
			return null;
		}
		return getSmallBitmap(inFile.getAbsolutePath(), width, hight);

	}

	private static Bitmap getSmallBitmap(String filePath, int width, int hight) {

		Log.e("getSmallBitmap_filePath==>", "==filePath=>" + filePath);
		try {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);
			options.inJustDecodeBounds = false;
			options.inSampleSize = calculateInSampleSize(options, width, hight);
			Log.e("options.inSampleSize", "options.inSampleSize==>>"
					+ calculateInSampleSize(options, width, hight));
			options.inPreferredConfig = Bitmap.Config.RGB_565;
			return BitmapFactory.decodeFile(filePath, options);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}

		return null;
	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

}
