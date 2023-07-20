package com.releasy.android.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

public class BitmapUtils {

	/**
	* Drawable 转Bitmap
	*
	*/
	public static Bitmap drawableToBitmap(Drawable drawable){
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888: Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0,0,width,height);
		drawable.draw(canvas);
		return bitmap;
	} 
	
	/**
	* 获得圆角图片的方法
	*
	*/
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	} 
	
	/**
	 * 将图片文件转成bitmap
	 */
	public static Bitmap file2Bitmap(String absolutePath,Activity activity){
		Log.d("z17m","absolutePath : " +  absolutePath);
	    BitmapFactory.Options opt = new BitmapFactory.Options();   
        opt.inJustDecodeBounds = true;  //这个isjustdecodebounds很重要      
        opt.inSampleSize = 1;  
        Bitmap bm = BitmapFactory.decodeFile(absolutePath, opt);  
        int picWidth  = opt.outWidth;  //获取到这个图片的原始宽度和高度  
        int picHeight = opt.outHeight;  
        
        WindowManager windowManager = activity.getWindowManager();  //获取屏的宽度和高度  
        Display display = windowManager.getDefaultDisplay();  
        int screenWidth = display.getWidth();  
        int screenHeight = display.getHeight();  
        //isSampleSize是表示对图片的缩放程度，比如值为2图片的宽度和高度都变为以前的1/2  
        //opt.inSampleSize = 1;  
        //根据屏的大小和图片大小计算出缩放比例  
        if(picWidth > picHeight){  
        	if(picWidth > screenWidth)  
        		opt.inSampleSize = picWidth/screenWidth;  
        }  
        else{  
        	if(picHeight > screenHeight)  
        		opt.inSampleSize = picHeight/screenHeight;  
        }  
        //这次再真正地生成一个有像素的，经过缩放了的bitmap  
        opt.inJustDecodeBounds = false; 
        opt.inPreferredConfig = Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        Bitmap bitmap = null;
        try {
        	InputStream is = new FileInputStream(absolutePath);
        	bitmap = BitmapFactory.decodeStream(is, null, opt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			bitmap = BitmapFactory.decodeFile(absolutePath, opt);
		}
        return bitmap;
	}
	
	/**
	 * 将图片文件转成bitmap
	 */
	public static Bitmap file2Bitmap(String absolutePath){
		Bitmap bitmap = null;
		bitmap = BitmapFactory.decodeFile(absolutePath);
		return bitmap;
	}
	
	/**
	 * 将bitmap保存为文件
	 */
    public static void saveBitmap(String path,Bitmap bitmap) throws IOException { 
        File f = new File(path);  
        f.createNewFile();  
        FileOutputStream fOut = null;  
        try {  
                fOut = new FileOutputStream(f);  
        } catch (FileNotFoundException e) {  
                e.printStackTrace();  
        }  
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut); 
        try {  
                fOut.flush();  
        } catch (IOException e) {  
                e.printStackTrace();  
        }  
        try {  
                fOut.close();  
        } catch (IOException e) {  
                e.printStackTrace();  
        }  
    } 
    
	/**
	 * Drawable 保存文件
	 *
	 */
	public static void saveDrawable2File(Drawable drawable,String filePath) {
		Bitmap bm = drawableToBitmap(drawable);
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.JPEG, 80, baos);	
			byte[] photoBytes = baos.toByteArray();
			File aFile = new File(filePath);
			boolean b;
			if (aFile.exists())
				b = aFile.delete();
			aFile.createNewFile(); // need add permission to manifest
			FileOutputStream fos = new FileOutputStream(aFile);
			fos.write(photoBytes);
			fos.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(bm != null){
				bm.recycle();
				bm = null;
			}
		}
	}
	
	/** 
     * 旋转图片 
     * @param angle 
     * @param bitmap 
     * @return Bitmap 
     */  
    public static Bitmap rotaingImageView(int angle , Bitmap bitmap) {  
        //旋转图片 动作  
        Matrix matrix = new Matrix();;  
        matrix.postRotate(angle);  
        System.out.println("angle2=" + angle);  
        // 创建新的图片  
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,  
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);  
        return resizedBitmap;  
    }
    
    /** 
     * 读取图片属性：旋转的角度 
     * @param path 图片绝对路径 
     * @return degree旋转的角度 
     */  
    public static int readPictureDegree(String path) {  
    	int degree  = 0;  
    	try {  
    		ExifInterface exifInterface = new ExifInterface(path);  
    		int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);  
    		switch (orientation) {  
    		case ExifInterface.ORIENTATION_ROTATE_90:  
    			degree = 90;  
    			break;  
    		case ExifInterface.ORIENTATION_ROTATE_180:  
    			degree = 180;  
                break;  
            case ExifInterface.ORIENTATION_ROTATE_270:  
            	degree = 270;  
            	break;  
           }  
        }
    	catch (IOException e) {  
                   e.printStackTrace();  
        }  
    	return degree;  
    }  
}
