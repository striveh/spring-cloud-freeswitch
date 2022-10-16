/** */
package com.striveh.callcenter.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

public class StringTool {
	/** 日志 */
	protected static Logger logger = LogManager.getLogger(StringTool.class);


    /***
     * 判断字符串是否包含中文
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }
    /***
     * 秒转分
     * @param str
     * @return
     */
    public static String parseS2M(String str) {
        try {
            int seconds = Integer.valueOf(str);
            return new StringBuffer().append(seconds / 60)
                    .append("分").append(seconds % 60).append("秒").toString();
        } catch (Exception e) {
            logger.error("调用parseSeconds2Minute()出现异常:",e);
        }
            return null;
        }
	/**
	 * 压缩字符串
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(String str) throws IOException {
		if (StringUtils.isEmpty(str)) {
			return null;
		}
		GZIPOutputStream gZip = null;
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			gZip = new GZIPOutputStream(out);
			gZip.write(str.getBytes("UTf-8"));
			gZip.flush();
			gZip.finish();// 无此代码报Unexpected end of ZLIB input stream
			// return out.toString("ISO-8859-1");// 测试只能用"ISO-8859-1",否则报Not in
			// GZIP format
			return out.toByteArray();
		} finally {
			if (gZip != null) {
				gZip.close();
			}
		}
	}

	/**
	 * 判断string是否为空或空字符串
	 * 
	 * @param obj
	 * @return
	 */
	public static boolean isEmpty(String obj) {
		return obj == null || obj.trim().length() < 1;
	}

	/**
	 * 解压缩字符串
	 * 
	 * @param str
	 * @return
	 * @throws IOException
	 */
	public static String unCompress(String str) throws IOException {
		if (StringUtils.isEmpty(str)) {
			return str;
		}
		return unCompress(str.getBytes("ISO-8859-1"));
	}

	/**
	 * 解压缩字符串
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 */
	public static String unCompress(byte[] bytes) throws IOException {
		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		GZIPInputStream gUnzip = null;
		try {
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(bytes);// 测试只能用"ISO-8859-1",否则报Not in
													// GZIP format
			gUnzip = new GZIPInputStream(in);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = gUnzip.read(buffer)) > 0) {
				out.write(buffer, 0, length);
			}
			out.flush();
			return out.toString("UTf-8");
		} finally {
			if (out != null) {
				out.close();
			}
			if (in != null) {
				in.close();
			}
			if (gUnzip != null) {
				gUnzip.close();
			}
		}
	}

	/**
	 * 将java转义字符转换成html标签
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceJavaCharToHtml(String str) {
		if (str != null) {
			str = str.replaceAll("(\r\n|\r|\n|\n\r)", "<br>");
			str = str.replaceAll("(\")", "&quot;");
			str = str.replaceAll("(\')", "&quot;");
			str = str.replaceAll("\\\\", "");
			str = str.replaceAll("&quot;", "\"");
			str = str.replaceAll("&gt;", ">");
			str = str.replaceAll("&lt;", "<");
			str = str.replaceAll("&amp;", "&");
		}
		return str;
	}

	/**
	 * 用*号隐藏手机号码
	 * 
	 * @param telNo
	 * @return
	 */
	public static String hiddenTelNo(String telNo) {
		if (StringUtils.isEmpty(telNo)) {
			return null;
		}
		return telNo.replaceAll("^(\\d{3})(\\d*)(\\d{4})$", "$1****$3");
	}

	/**
	 * 用*号隐藏银行卡号
	 * 
	 * @param cardNo
	 * @return
	 */
	public static String hiddenBankCardCode(String cardNo) {
		if (StringUtils.isEmpty(cardNo)) {
			return null;
		}
		return cardNo.replaceAll("^(\\d{4})(\\d*)(\\d{4})$", "$1 **** **** $3");
	}

	public static void main(String[] args) {
	}


	/**
	 * 用*号隐藏身份证号码
	 * 
	 * @param idCard
	 * @return
	 */
	public static String hiddenIdCardCode(String idCard) {
		if (StringUtils.isEmpty(idCard)) {
			return null;
		}
		return idCard.replaceAll("^(\\d{2})(\\d*)(\\d{4})$", "$1************$3");
	}

	/**
	 * 用*号隐藏身份证号码
	 * 匹配前4位和后四位字母或者数字
	 * 
	 * @param idCard
	 * @return
	 */
	public static String hiddenIdCardCode2(String idCard) {
		if (StringUtils.isEmpty(idCard)) {
			return null;
		}
		return idCard.replaceAll("^(\\w{4})(\\w*)(\\w{4})$", "$1************$3");
	}

	/**
	 * 用*号隐藏用户真实姓名
	 * 
	 * @param idCardName
	 * @return
	 */
	public static String hiddenIdCardName(String idCardName) {
		if (StringUtils.isEmpty(idCardName)) {
			return null;
		} else {
			StringBuffer name = new StringBuffer();
			if (idCardName.length() > 3) {
				name.append(idCardName.substring(0, 2));
			} else {
				name.append(idCardName.substring(0, 1));
			}
			for (int i = name.length(); i < idCardName.length(); i++) {
				name.append("*");
			}
			return name.toString();
		}
	}

	/**
	 * 产生length长度的一个随机数
	 * 
	 * @param length
	 * @return
	 */
	public static String bringValidateCode(int length) {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < length; i++) {
			Double d = Math.random() * 10;
			s.append(d.intValue());
		}
		return s.toString();
	}

	/**
	 * 获取交易时的批次号、交易号等等
	 * 
	 * @param prefix 前缀如 DD、PC...
	 * @param dateFormat 时间格式 如 yyyyMMddHHmmssSSS
	 * @param suffixLen 末尾追加的随即串长度
	 * @return
	 */
	public static String getTimeCode(String prefix, String dateFormat, int suffixLen) {
		return prefix + new SimpleDateFormat(dateFormat).format(new Date()) + bringValidateCode(suffixLen);
	}

	/**
	 * 在字符前加0，达到一定的位数，目前最长支持补10位零
	 * 
	 * @param oldStr
	 * @param length
	 * @return
	 */
	public static String fillZero(String oldStr, int length) {
		String[] zeorArray = new String[] { "", "0", "00", "000", "0000", "00000", "000000", "0000000", "00000000",
				"000000000", "0000000000" };
		if (length < oldStr.length()) {
			return oldStr;
		}
		return zeorArray[length - oldStr.length()] + oldStr;
	}

	/**
	 * md5加密:使用UTF-8，结果转成全大写
	 * 
	 * @param str
	 * @return
	 */
	public static String MD5Encode(String str) {
		return MD5Encode(str, "UTF-8", true);
	}

	/**
	 * md5加密：使用UTF-8,根据参数转成全大写
	 * 
	 * @param str
	 * @param upperCase
	 * @return
	 */
	public static String MD5Encode(String str, boolean upperCase) {
		return MD5Encode(str, "UTF-8", upperCase);
	}

	/**
	 * md5加密
	 * 
	 * @param str 要加密的字符串
	 * @param charsetname 字符编码 UTF-8
	 * @param upperCase 是否转为大写
	 * @return
	 */
	public static String MD5Encode(String str, String charsetname, boolean upperCase) {
		StringBuffer resultSb = new StringBuffer();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] arr = md.digest(str.getBytes(charsetname));
			for (int b : arr) {
				if (b < 0) {
					b = 256 + b;
				}
				if (b <= 15) {
					resultSb.append("0");
				}
				resultSb.append(Integer.toHexString(b));
			}
			if (upperCase) {
				return resultSb.toString().toUpperCase();
			} else {
				return resultSb.toString();
			}
		} catch (Exception ex) {
			logger.error("md5加密失败！", ex);
		}
		return null;
	}

	/**
	 * sha1加密
	 * 
	 * @param str
	 * @return
	 */
	public static String sha1Encode(String str) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.reset();
			md.update(str.getBytes("UTF-8"));
			byte[] arr = md.digest();

			Formatter formatter = new Formatter();
			for (byte b : arr) {
				formatter.format("%02x", b);
			}
			String result = formatter.toString();
			formatter.close();
			return result;
		} catch (Exception ex) {
			logger.error("SHA-1加密失败！", ex);
		}
		return null;
	}

	/**
	 * 验证字符串是否是手机号码
	 * 
	 * @param string 要验证的字符串
	 * @return
	 */
	public static boolean isPhoneNumber(String string) {
		// Pattern pattern =
		// Pattern.compile("^(13[0-9]|14[57]|15[012356789]|17[01678]|18[0-9])[0-9]{8}$"); // 手机号
		Pattern pattern = Pattern.compile("^1[0-9]{10}$"); // 手机号
		Matcher matcher = pattern.matcher(string);
		if (matcher.matches()) {
			return true;
		}
		return false;
	}

	/**
	 * 检查银行卡(Luhm校验) 1、从卡号最后一位数字开始，逆向将奇数位(1、3、5等等)相加。
	 * 2、从卡号最后一位数字开始，逆向将偶数位数字，先乘以2（如果乘积为两位数，则将其减去9），再求和。
	 * 3、将奇数位总和加上偶数位总和，结果应该可以被10整除。
	 * 
	 * @param cardNo
	 * @return
	 */
	public static boolean checkBankCardNo(String cardNo) {
		if (!StringUtils.isEmpty(cardNo)) {
			try {
				int luhmSum = 0;
				int num = 0;
				int index = 1;// 逆向后奇偶标志
				for (int i = cardNo.length() - 1; i >= 0; i--) {
					num = Integer.parseInt(cardNo.charAt(i) + "");
					if (index % 2 == 0) {
						num = num * 2 > 9 ? num * 2 - 9 : num * 2;
					}
					luhmSum += num;
					index++;
				}
				return luhmSum % 10 == 0;
			} catch (Exception ex) {}
		}
		return false;
	}

	/**
	 * 检查身份证号码
	 * 
	 * @param idCardNo
	 * @return
	 */
	public static boolean checkIdCardNo(String idCardNo) {
		if (!StringUtils.isEmpty(idCardNo)) {
			idCardNo = idCardNo.toUpperCase();
			int sum = 0;
			int[] tempNum = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2 };
			String[] code = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2" };
			if (idCardNo != null && idCardNo.length() == 18) {
				try {
					for (int i = 0; i < 17; i++) {
						sum += Integer.parseInt(idCardNo.substring(i, i + 1)) * tempNum[i];
					}
					if (idCardNo.substring(17, 18).equals(code[sum % 11])) {
						return true;
					}
				} catch (Exception e) {}
			}
		}
		return false;
	}

	/**
	 * 根据身份证号码取得性别
	 * 
	 * @param idCardNo
	 * @param isCheckNo 是否检查身份证号码
	 * @return null、1男、2女
	 */
	public static Integer getSexByIdCardNo(String idCardNo, boolean isCheckNo) {
		Integer result = null;
		try {
			boolean isRight = true;
			if (isCheckNo) {
				isRight = checkIdCardNo(idCardNo);
			}
			if (isRight && idCardNo.length() == 18) {
				int num = Integer.parseInt(idCardNo.substring(16, 17)) % 2;
				if (num == 0) {
					result = 2;
				} else {
					result = 1;
				}
			}
		} catch (Exception ex) {
			logger.error("根据身份证号码取得性别失败");
		}
		return result;
	}

	/**
	 * 根据身份证号码取得生日
	 * 
	 * @param idCardNo
	 * @param isCheckNo 是否检查身份证号码
	 * @return null、1男、2女
	 */
	public static java.sql.Date getBirthdayByIdCardNo(String idCardNo, boolean isCheckNo) {
		java.sql.Date result = null;
		try {
			boolean isRight = true;
			if (isCheckNo) {
				isRight = checkIdCardNo(idCardNo);
			}

			if (isRight && idCardNo.length() == 18) {
				result = new java.sql.Date(DateTool.parseDate("yyyyMMdd", idCardNo.substring(6, 14)).getTime());
			}
		} catch (Exception ex) {
			logger.error("根据身份证号码取得生日失败");
		}
		return result;
	}

	public static String getString(String data) {
		if (!StringUtils.isEmpty(data)) {
			return data.trim();
		}
		return null;
	}

	public static Double getDouble(String data) {
		if (data != null && !StringUtils.isEmpty(data.trim())) {
			return Double.parseDouble(data.trim());
		}
		return null;
	}

	public static String getSerno() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		int min = 10000;
		int max = 99999;
		Random random = new Random();
		return "CL" + format.format(new Date()) + "00" + (random.nextInt(max) % (max - min + 1) + min);
	}

    /***
     * 生成指定位数的随机数
     * @param length
     * @return
     */
    public static String getRandom(int length){
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }
	/**

	 * Compares two version strings.

	 *

	 * Use this instead of String.compareTo() for a non-lexicographical

	 * comparison that works for version strings. e.g. "1.10".compareTo("1.6").

	 *

	 * @note It does not work if "1.10" is supposed to be equal to "1.10.0".

	 *

	 * @param str1 a string of ordinal numbers separated by decimal points.

	 * @param str2 a string of ordinal numbers separated by decimal points.

	 * @return The result is a negative integer if str1 is _numerically_ less than str2.

	 *         The result is a positive integer if str1 is _numerically_ greater than str2.

	 *         The result is zero if the strings are _numerically_ equal.

	 */

	public static int versionCompare(String str1, String str2) {

		String[] vals1 = str1.split("\\.");

		String[] vals2 = str2.split("\\.");

		int i = 0;

		// set index to first non-equal ordinal or length of shortest version string

		while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {

			i++;

		}

		// compare first non-equal ordinal number

		if (i < vals1.length && i < vals2.length) {

			int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));

			return Integer.signum(diff);

		}

		// the strings are equal or one string is a substring of the other

		// e.g. "1.2.3" = "1.2.3" or "1.2.3" < "1.2.3.4"

		return Integer.signum(vals1.length - vals2.length);

	}

	/**
	 * 字符串截取
	 * @param str
	 * @param maxLength
	 * @return
	 */
	public static String subString(String str,int maxLength){
		if (StringTool.isEmpty(str)||str.length()<=maxLength){
			return str;
		}else {
			return str.substring(0,maxLength);
		}
	}


	/**
	 *  使用* 隐藏用户的姓
	 * @param str 姓名
	 * @return 隐藏之后的姓名
	 */
	public static String hideLastName(String str) {
		if (org.apache.commons.lang3.StringUtils.isEmpty(str)) {
			return "";
		}
		return str.replace(str.charAt(0) + "", "*");
	}

}
