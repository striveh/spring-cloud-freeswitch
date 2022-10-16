/**
 * 
 */
package com.striveh.callcenter.common.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FileTool {
	/** 日志记录对象 */
	private static Logger logger = LogManager.getLogger(FileTool.class);

	/**
	 * 根据日期生成文件名的前缀
	 *
	 * @param date 格式，默认yyyyMMddHHmmss_
	 * @return
	 */
	public static String getFilePrefixByDate(Date date) {
		date = (date == null ? new Date() : date);
		String fmt = "yyyyMMddHHmmssSSS_";
		SimpleDateFormat df = new SimpleDateFormat(fmt);
		return df.format(date);
	}

	/**
	 * 根据日期生成文件夹，默认为：/2016/03/30
	 * 
	 * @param date 日期
	 * @param useSystemSep 使用系统文件分隔符
	 * @return
	 */
	public static String getFileDirByDate(Date date, boolean useSystemSep) {
		date = (date == null ? new Date() : date);
		String fmt = "/yyyy/MM/dd";
		if (useSystemSep) {
			fmt = File.separatorChar + "yyyy" + File.separatorChar + "MM" + File.separatorChar + "dd";
		}
		SimpleDateFormat df = new SimpleDateFormat(fmt);
		return df.format(date);
	}

	/**
	 * 得到文件大小，带单位
	 * 
	 * @param l
	 * @return
	 */
	public static String getSizeByUnit(Long l) {
		String str = "K";
		Double length = new Double(l) / 1024;
		if (length >= 1024) {
			str = "M";
			length = length / 1024;
		}

		if (length >= 1024) {
			str = "G";
			length = length / 1024;
		}
		if (length >= 1024) {
			str = "T";
			length = length / 1024;
		}
		DecimalFormat df = new DecimalFormat("#0.00");
		return df.format(length) + str;
	}

	/**
	 * 复制文件到目标文件夹
	 * 
	 * @param oldFile
	 * @param dirPath
	 * @param destFileName
	 * @return 0:空文件，-1：文件传输失败，1：文件正常
	 */
	public static int copyFile(File oldFile, String dirPath, String destFileName) {
		try {
			if (oldFile.length() < 1) {
				return 0;
			}
			return copyFile(new FileInputStream(oldFile), dirPath, destFileName);
		} catch (FileNotFoundException e) {
			logger.error("文件复制失败", e);
			return -1;
		}

	}

	/**
	 * 复制文件到目标文件夹
	 * 
	 * @param input
	 * @param dirPath
	 * @param destFileName
	 * @return -1：文件传输失败，1：文件正常
	 */
	public static int copyFile(InputStream input, String dirPath, String destFileName) {
		FileOutputStream out = null;
		try {
			File dir = new File(dirPath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			// 写文件
			File destFile = new File(dirPath, destFileName);
			out = new FileOutputStream(destFile);
			copyFile(input, out);
			return 1;
		} catch (Exception ex) {
			logger.error("文件复制失败", ex);
			return -1;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				if (input != null) {
					input.close();
				}
			} catch (Exception e) {
				logger.error("文件复制失败", e);
				return -1;
			}
		}
	}

	/**
	 * 复制缓冲输入流到缓冲输出流
	 * 
	 * @param input
	 * @param output
	 * @return 1成功
	 * @throws IOException
	 */
	public static Long copyFile(InputStream input, OutputStream output) throws IOException {
		BufferedOutputStream bufferOutput = null;
		BufferedInputStream bufferInput = null;
		try {
			bufferOutput = new BufferedOutputStream(output);
			bufferInput = new BufferedInputStream(input);

			byte[] buffer = new byte[4096];
			Long size = 0L;
			int length = -1;
			while ((length = bufferInput.read(buffer)) != -1) {
				size += length;
				bufferOutput.write(buffer, 0, length);
				bufferOutput.flush();
			}
			return size;
		} finally {
			if (bufferOutput != null) {
				bufferOutput.close();
			}
			if (bufferInput != null) {
				bufferInput.close();
			}
			if (input != null) {
				input.close();
			}
			if (output != null) {
				output.close();
			}
		}
	}

	/**
	 * 读取文件内容
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static String readFile( MultipartFile file ) throws IOException {
		StringBuffer sb = new StringBuffer( );
		BufferedReader reader = null;
		InputStream in = null;
		try
		{
			if ( file != null )
			{
				in = file.getInputStream( );
				// 一次读一行
				reader = new BufferedReader(
						new InputStreamReader( in ,
								"UTF-8" ) );
				String line = null;
				while ( ( line = reader.readLine( ) ) != null )
				{
					sb.append(line);
				}
			}
		}
		catch ( Exception e )
		{
			e.printStackTrace( );
		}
		finally
		{
			if (reader != null) {
				reader.close();
			}
			if (in != null) {
				in.close();
			}
		}
		return sb.toString( );
	}

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {


	}

}
