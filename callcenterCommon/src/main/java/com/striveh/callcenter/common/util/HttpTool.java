/** */
package com.striveh.callcenter.common.util;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

public class HttpTool {
	/** 日志对象 */
	protected static Logger logger = LogManager.getLogger(HttpTool.class);
	private static int defConnectTimeout = 30000;
	private static int defSocketTimeout = 60000;
	private static CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
	/**
	 * 发起http/https的get请求
	 *
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String requestGet(String url) throws Exception {
		return requestGet(null, url, null, "UTF-8", false, defConnectTimeout, defSocketTimeout);
	}

	/**
	 * 发起http/https的get请求
	 *
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String requestGet(SSLContext sslContext, String url) throws Exception {
		return requestGet(sslContext, url, null, "UTF-8", false, defConnectTimeout, defSocketTimeout);
	}

	/**
	 * 发起http/https的get请求
	 *
	 * @param sslContext
	 *            https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param headers
	 * @param charsetStr
	 * @param keepAlive
	 * @return
	 * @throws Exception
	 */
	public static String requestGet(SSLContext sslContext, String url, Map<String, String> headers, String charsetStr,
			boolean keepAlive, int connectTimeout, int socketTimeout) throws Exception {
		HttpClient httpClient = null;
		HttpGet get = null;
		try {
			HttpClientBuilder httpClientBuilder = getHttpClientBuilder(sslContext, headers, connectTimeout,
					socketTimeout);

			// 发送get请求
			get = new HttpGet(url);
			if (keepAlive) {
				get.addHeader("Connection", "Keep-Alive");
			} else {
				get.addHeader("Connection", "close");
			}
			httpClient = httpClientBuilder.build();
			HttpResponse response = httpClient.execute(get);// 返回数据
			return EntityUtils.toString(response.getEntity(), charsetStr);
		} finally {
			if (get != null) {
				get.abort();
			}
		}
	}

	/**
	 * 以EntityBuilder发起http/https的post请求
	 *
	 * @param sslContext
	 *            https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param str
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static String requestPost(SSLContext sslContext, String url, String str, ContentType type) throws Exception {
		return requestPost(sslContext, url, null, null, str, null, type, "UTF-8", false, defConnectTimeout,
				defSocketTimeout);
	}

	/**
	 * 以EntityBuilder发起http/https的post请求
	 *
	 * @param sslContext
	 *            https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param headers
	 * @param str
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static String requestPost(SSLContext sslContext, String url, Map<String, String> headers, String str,
			ContentType type) throws Exception {
		return requestPost(sslContext, url, headers, null, str, null, type, "UTF-8", false, defConnectTimeout,
				defSocketTimeout);
	}

	/**
	 * 以EntityBuilder发起http/https的post请求
	 *
	 * @param sslContext
	 *            https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param parames
	 * @return
	 * @throws Exception
	 */
	public static String requestPost(SSLContext sslContext, String url, Map<String, String> parames) throws Exception {
		return requestPost(sslContext, url, null, parames, null, null, ContentType.APPLICATION_FORM_URLENCODED, "UTF-8",
				false, defConnectTimeout, defSocketTimeout);
	}

	/**
	 * 以EntityBuilder发起http/https的post请求
	 *
	 * @param sslContext
	 *            https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param parames
	 * @return
	 * @throws Exception
	 */
	public static String requestPost(SSLContext sslContext, String url, Map<String, String> parames,int defConnectTimeout, int defSocketTimeout) throws Exception {
		return requestPost(sslContext, url, null, parames, null, null, ContentType.APPLICATION_FORM_URLENCODED, "UTF-8",
				false, defConnectTimeout, defSocketTimeout);
	}

	/**
	 * 以EntityBuilder发起http/https的post请求
	 *
	 * @param sslContext
	 *            https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param headers
	 * @param parames
	 * @return
	 * @throws Exception
	 */
	public static String requestPost(SSLContext sslContext, String url, Map<String, String> headers,
			Map<String, String> parames) throws Exception {
		return requestPost(sslContext, url, headers, parames, null, null, ContentType.APPLICATION_FORM_URLENCODED,
				"UTF-8", false, defConnectTimeout, defSocketTimeout);
	}

	/**
	 * 以EntityBuilder发起http/https的post请求
	 *
	 * @param sslContext
	 *            https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param inputStream
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static String requestPost(SSLContext sslContext, String url, InputStream inputStream, ContentType type)
			throws Exception {
		return requestPost(sslContext, url, null, null, null, inputStream, type, "UTF-8", false, defConnectTimeout,
				defSocketTimeout);
	}

	/**
	 * 以EntityBuilder发起http/https的post请求
	 *
	 * @param sslContext
	 *            https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param headers
	 * @param inputStream
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static String requestPost(SSLContext sslContext, String url, Map<String, String> headers,
			InputStream inputStream, ContentType type) throws Exception {
		return requestPost(sslContext, url, headers, null, null, inputStream, type, "UTF-8", false, defConnectTimeout,
				defSocketTimeout);
	}

	/**
	 * 请求http返回一个输入流
	 * <p> add by liuyiming 2017-10-18<p/>
	 * @param sslContext
	 * @param url
	 * @param parames
	 * @return
	 * @throws Exception
	 */
	public static InputStream  requestPostAndInputStream(SSLContext sslContext, String url,Map<String, String> headers,Map<String, String> parames,byte[] signFile, byte[] sealdata)
			throws Exception {
		return requestPostWithInputStream(sslContext,null,null, url, headers, parames, "UTF-8", false, defConnectTimeout,
				defSocketTimeout,signFile,sealdata);
	}
	/**
	 *
	 * @param sslContext
	 * @param url
	 * @param headers
	 * @param parames
	 * @param charsetStr
	 * @param keepAlive
	 * @return
	 * @throws Exception
	 */
	public static InputStream requestPostWithInputStream(SSLContext sslContext, Credentials credentials , HttpHost targetHost, String url, Map<String, String> headers,
														 Map<String, String> parames, String charsetStr, boolean keepAlive,
														 int connectTimeout, int socketTimeout, byte[] signFile, byte[] sealdata) throws Exception {

		Charset charset = Charset.forName(charsetStr);
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// 设置浏览器兼容模式
		entityBuilder.setCharset(charset);


		// 设置参数
		if (parames != null) {
			Iterator<Entry<String, String>> paramIterator = parames.entrySet().iterator();
			Entry<String, String> paramEntry = null;
			while (paramIterator.hasNext()) {
				paramEntry = paramIterator.next();
				entityBuilder.addTextBody(paramEntry.getKey(), paramEntry.getValue(),
						ContentType.create("text/plain", charset));
			}
		}
		// 发送的文件
		entityBuilder.addBinaryBody("signFile",signFile,ContentType.DEFAULT_BINARY,"signFile");
		entityBuilder.addBinaryBody("sealdata",sealdata,ContentType.DEFAULT_BINARY,"sealdata");


		return postWithInputStream(sslContext,credentials ,targetHost, url, headers, entityBuilder.build(), charset, keepAlive, connectTimeout, socketTimeout);
	}

	private static byte[] getBytes(File file) {
		byte[] buffer = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
			byte[] b = new byte[1000];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			fis.close();
			bos.close();
			buffer = bos.toByteArray();
		} catch (FileNotFoundException e) {
			logger.error("文件转byte数组时，文件不存在", e);
		} catch (IOException e) {
			logger.error("文件转byte数组时，发生IO异常", e);
		} finally {
		}
		return buffer;
	}

	/**
	 * 发起http/https的post请求
	 *
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param headers
	 * @param entiy
	 * @param charset
	 * @param keepAlive
	 * @return
	 * @throws Exception
	 */
	private static InputStream postWithInputStream(SSLContext sslContext, Credentials credentials , HttpHost targetHost, String url, Map<String, String> headers, HttpEntity entiy,
												   Charset charset, boolean keepAlive, int connectTimeout, int socketTimeout) throws Exception {
		HttpClient httpClient = null;
		HttpPost post = null;
		try {
			HttpClientBuilder httpClientBuilder = getHttpClientBuilder(sslContext, headers, connectTimeout,
					socketTimeout);

			// 发送post请求
			post = new HttpPost(url);
			if (keepAlive) {
				post.addHeader("Connection", "Keep-Alive");
			} else {
				post.addHeader("Connection", "close");
			}
			post.setEntity(entiy);
			httpClient = httpClientBuilder.build();
			HttpResponse response =null;
			//设置BasicAuth
			if(credentials!=null&&targetHost!=null){
				CredentialsProvider provider = new BasicCredentialsProvider();
				provider.setCredentials(AuthScope.ANY, credentials);
				AuthCache authCache = new BasicAuthCache();
				authCache.put(targetHost, new BasicScheme());
				final HttpClientContext context = HttpClientContext.create();
				context.setCredentialsProvider(provider);
				context.setAuthCache(authCache);
				response = httpClient.execute(post,context);// 返回数据
			}else {
				response = httpClient.execute(post);// 返回数据
			}
			return response.getEntity().getContent();// 返回输入流
		} finally {
			if (post != null) {
//				post.abort();
			}
		}
	}
	/**
	 * 以EntityBuilder发起http/https的post请求
	 *
	 * @param sslContext
	 *            https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param headers
	 * @param parames
	 * @param str
	 * @param inputStream
	 * @param type
	 * @param charsetStr
	 * @param keepAlive
	 * @return
	 * @throws Exception
	 */
	public static String requestPost(SSLContext sslContext, String url, Map<String, String> headers,
			Map<String, String> parames, String str, InputStream inputStream, ContentType type, String charsetStr,
			boolean keepAlive, int connectTimeout, int socketTimeout) throws Exception {
		Charset charset = Charset.forName(charsetStr);
		EntityBuilder entityBuilder = EntityBuilder.create();
		entityBuilder.setContentType(type.withCharset(charset));
		entityBuilder.setContentEncoding(charsetStr);

		// 设置参数
		if (parames != null) {
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> paramIterator = parames.entrySet().iterator();
			Entry<String, String> paramEntry = null;
			while (paramIterator.hasNext()) {
				paramEntry = paramIterator.next();
				parameters.add(new BasicNameValuePair(paramEntry.getKey(), paramEntry.getValue()));
			}
			entityBuilder.setParameters(parameters);
		}
		if (str != null) {
			entityBuilder.setText(str);
		}

		if (inputStream != null) {
			entityBuilder.setStream(inputStream);
		}

		return post(sslContext, url, headers, entityBuilder.build(), charset, keepAlive, connectTimeout, socketTimeout);
	}

	/**
	 *
	 * @param sslContext
	 * @param url
	 * @param headers
	 * @param parames
	 * @param files
	 * @param charsetStr
	 * @param keepAlive
	 * @return
	 * @throws Exception
	 */
	public static String requestPost(SSLContext sslContext, String url, Map<String, String> headers,
									 Map<String, String> parames, Map<String, List<File>> files, String charsetStr, boolean keepAlive,
									 int connectTimeout, int socketTimeout) throws Exception {

		Charset charset = Charset.forName(charsetStr);
		MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
		entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);// 设置浏览器兼容模式
		entityBuilder.setCharset(charset);

		// 设置参数
		if (parames != null) {
			Iterator<Entry<String, String>> paramIterator = parames.entrySet().iterator();
			Entry<String, String> paramEntry = null;
			while (paramIterator.hasNext()) {
				paramEntry = paramIterator.next();
				entityBuilder.addTextBody(paramEntry.getKey(), paramEntry.getValue(),
						ContentType.create("text/plain", charset));
			}
		}

		// 发送的文件
		if (files != null) {
			Iterator<Entry<String, List<File>>> fileIterator = files.entrySet().iterator();
			Entry<String, List<File>> fileEntry = null;
			while (fileIterator.hasNext()) {
				fileEntry = fileIterator.next();
				for (File f : fileEntry.getValue()) {
					entityBuilder.addBinaryBody(fileEntry.getKey(), f);
				}
			}
		}

		return post(sslContext, url, headers, entityBuilder.build(), charset, keepAlive, connectTimeout, socketTimeout);
	}

	/**
	 * 发起http/https的post请求
	 *
	 * @param sslContext
	 *            https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param headers
	 * @param entiy
	 * @param charset
	 * @param keepAlive
	 * @return
	 * @throws Exception
	 */
	private static String post(SSLContext sslContext, String url, Map<String, String> headers, HttpEntity entiy,
			Charset charset, boolean keepAlive, int connectTimeout, int socketTimeout) throws Exception {
		HttpClient httpClient = null;
		HttpPost post = null;
		try {
			HttpClientBuilder httpClientBuilder = getHttpClientBuilder(sslContext, headers, connectTimeout,
					socketTimeout);

			// 发送post请求
			post = new HttpPost(url);
			if (keepAlive) {
				post.addHeader("Connection", "Keep-Alive");
			} else {
				post.addHeader("Connection", "close");
			}
			post.setEntity(entiy);
			httpClient = httpClientBuilder.build();
			HttpResponse response = httpClient.execute(post);// 返回数据
			return EntityUtils.toString(response.getEntity(), charset);
		} finally {
			if (post != null) {
				post.abort();
			}
		}
	}

	/**
	 * 取得HttpClientBuilder
	 *
	 * @param sslContext
	 * @param headers
	 * @return
	 */
	private static HttpClientBuilder getHttpClientBuilder(SSLContext sslContext, Map<String, String> headers,
			int connectTimeout, int socketTimeout) {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		// 设置header
		if (headers != null) {
			ArrayList<Header> headerList = new ArrayList<Header>();
			Iterator<Entry<String, String>> headerIterator = headers.entrySet().iterator();
			Entry<String, String> headerEntry = null;
			while (headerIterator.hasNext()) {
				headerEntry = headerIterator.next();
				headerList.add(new BasicHeader(headerEntry.getKey(), headerEntry.getValue()));
			}
			httpClientBuilder.setDefaultHeaders(headerList);
		}

		// 设置证书
		if (sslContext != null) {
			httpClientBuilder.setSSLSocketFactory(
					new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" }, null, new HostnameVerifier() {
						@Override
						public boolean verify(String arg0, SSLSession arg1) {
							return true;// 信任所有主机
						}
					}));
		}
		httpClientBuilder.setDefaultRequestConfig(
				RequestConfig.custom().setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout).build());
		return httpClientBuilder;
	}

	/**
	 * 取得不带证书的SSLContext
	 *
	 * @return
	 * @throws Exception
	 */
	public static SSLContext getSSLContext() throws Exception {
		return getSSLContextFromStream(null, null);
	}

	/**
	 * 取得带证书的SSLContext
	 *
	 * @param keyStorePath
	 *            密钥库路径
	 * @param keyStorepass
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	public static SSLContext getSSLContext(String keyStorePath, String keyStorepass) throws Exception {
		return getSSLContextFromStream(new FileInputStream(new File(keyStorePath)), keyStorepass);
	}

	/**
	 * 取得带证书的SSLContext
	 *
	 * @param instream
	 *            密钥库
	 * @param keyStorepass
	 *            密钥库密码
	 * @return
	 * @throws Exception
	 */
	public static SSLContext getSSLContextFromStream(InputStream instream, String keyStorepass) throws Exception {
		try {
			KeyStore trustStore = null;
			if (instream != null && keyStorepass != null) {
				trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
				trustStore.load(instream, keyStorepass.toCharArray());
			}
			// 相信自己的CA和所有自签名的证书
			return SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
		} finally {
			if (instream != null) {
				instream.close();
			}
		}
	}

	/**
	 * 获取ip地址
	 *
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		if (ip.startsWith("192")) {
			// ip = "116.231.127.142";// 上海ip
		}
		if (ip.contains(",")){
			String [] ips=ip.split(",");
			return ips[0];
		}else {
			return ip;
		}
	}

	/**
	 * 从客户端读取cookie，返回一个字符串
	 *
	 * @param request
	 * @param cookieName
	 *            cookie的名称
	 * @return
	 */
	public static String getStrFromCookie(HttpServletRequest request, String cookieName) {
		try {
			if (request.getCookies() != null) {
				for (Cookie c : request.getCookies()) {
					if (cookieName != null && cookieName.equals(c.getName())) {
						return URLDecoder.decode(c.getValue(), "utf-8");
					}
				}
			}
		} catch (Exception ex) {
			logger.error("从客户端读取cookie失败", ex);
		}
		return "";
	}

	/**
	 * 从客户端读取cookie，返回对象
	 *
	 * @param request
	 * @param cookieName
	 *            cookie的名称
	 * @param object
	 * @return
	 */
	public static <T> T getObjectFromCookie(HttpServletRequest request, String cookieName, Class<T> object) {
		String value = getStrFromCookie(request, cookieName);
		try {
			return JsonTool.getObj(value, object);
		} catch (Exception ex) {
			logger.error("从客户端读取cookie，返回对象失败", ex);
		}
		return null;
	}

	/**
	 * 删除cookie
	 *
	 * @param response
	 * @param cookieName
	 *            cookie的名字
	 */
	public static void deleteClientCookie(HttpServletResponse response, String cookieName) {
		try {
			Cookie myCookie = new Cookie(cookieName, null);
			myCookie.setMaxAge(0);
			myCookie.setPath("/");
			response.addCookie(myCookie);
		} catch (Exception ex) {
			logger.error("清空Cookies发生异常！", ex);
		}
	}

	/**
	 * 从输入流中获取网络参数
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Object> getParameterFromStream(HttpServletRequest request) throws Exception {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		InputStream in = request.getInputStream();
		byte[] b = new byte[1024];
		int len = 0;
		while ((len = in.read(b)) != -1) {
			out.write(b, 0, len);
		}
		out.close();

		String paramStr = out.toString();
		String[] paramArray = paramStr.split("&");
		for (int i = 0; i < paramArray.length; i++) {
			String[] tempArr = paramArray[i].split("=");
			if (tempArr.length > 1) {
				paramMap.put(tempArr[0], tempArr[1]);
			}
		}
		return paramMap;
	}

	/**
	 * response返回数据
	 *
	 * @param response
	 * @param obj
	 * @return
	 * @throws IOException
	 */
	public static void httpResponse(HttpServletResponse response, Object obj) {
		httpResponse(response, obj, ResponseContentType.json, "UTF-8");
	}

	/**
	 * response返回数据
	 *
	 * @param response
	 * @param obj
	 * @param type
	 * @param encode
	 * @return
	 * @throws IOException
	 */
	public static void httpResponse(HttpServletResponse response, Object obj, ResponseContentType type, String encode) {
		PrintWriter writer = null;
		try {
			String str = null;
			if (type == ResponseContentType.json) {
				str = JsonTool.getJsonString(obj);
			} else {
				str = obj.toString();
			}
			response.setContentType(type.getType());
			response.setCharacterEncoding(encode);
			writer = response.getWriter();
			writer.write(str);
			writer.flush();
		} catch (Exception ex) {
			logger.error("服务端返回异常", ex);
			try {
				response.sendError(HttpStatus.SC_INTERNAL_SERVER_ERROR);
			} catch (IOException e) {
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

	public enum ResponseContentType {
		/** javascript */
		javascript("text/javascript"),
		/** text */
		text("text/plain"),
		/** html */
		html("text/html"),
		/** xml */
		xml("text/xml"),
		/** json */
		json("application/json");

		/** response返回内容的类型 */
		private String type;

		private ResponseContentType(String type) {
			this.type = type;
		}

		/**
		 * 取得response返回内容的类型
		 *
		 * @return
		 */
		public String getType() {
			return type;
		}
	}

	/**
	 * 检查浏览器是否是微信浏览器，并且版本是否大于等于version
	 *
	 * @param request
	 * @param version
	 * @return
	 */
	public static boolean checkBrower(HttpServletRequest request, double version) {
		// String browser = "Mobile/10B329 MicroMessenger/4.0.1".toLowerCase();
		String browser = request.getHeader("USER-AGENT").toLowerCase();
		Pattern pat = Pattern.compile("micromessenger\\/([\\d]+[\\.]{0,1}[\\d]{0,1})");
		Matcher matcher = pat.matcher(browser);
		Double v = 0D;// 微信版本号，5.0才支持微信支付
		if (matcher.find()) {
			v = new Double(matcher.group(1));
			if (v >= version) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 通过指定key从request获取值<br>
	 * 优先从hedders里面获取，如果hedders没有从parameters里面获取
	 * @param request
	 * @param key
	 * @return
	 */
	public static String getParameterFromRequest(HttpServletRequest request, String key){
		return getParameterFromRequest(request, key, null);
	}

	/**
	 * 通过指定key从request获取值<br>
	 * 优先从hedders里面获取，如果hedders没有从parameters里面获取,如果没有就指定defaultValue
	 * @param request
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static String getParameterFromRequest(HttpServletRequest request, String key,String defaultValue){
		String value = request.getHeader(key);
		String contentType = request.getContentType();
		if (StringUtils.isEmpty(value)) {
			value = request.getParameter(key);
			if(StringUtils.isEmpty(value)&&contentType!=null&&contentType.contains("multipart/form-data")){
				value=multipartResolver.resolveMultipart(request).getParameter(key);
			}
		}
		return StringUtils.isEmpty(value) ? defaultValue : value;
	}
}
