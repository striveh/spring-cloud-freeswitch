/** */
package com.striveh.callcenter.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
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


public class HttpClientTool {
	/** 日志 */
	protected Logger logger = LogManager.getLogger(this.getClass());
	private static int defConnectTimeout = 30000;
	private static int defSocketTimeout = 60000;

	public static void main(String[] args) throws Exception {
	}

	/**
	 * Get方法重载：发起http/https的get请求
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String get(String url) throws Exception {
		return get(null, url);
	}

	/**
	 * Get方法重载：发起http/https的get请求
	 * 
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String get(SSLContext sslContext, String url) throws Exception {
		return get(sslContext, null, url);
	}

	/**
	 * Get方法重载：发起http/https的get请求
	 * 
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param httpClientContext 参考UsernamePasswordCredentials
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String get(SSLContext sslContext, HttpClientContext httpClientContext, String url) throws Exception {
		return get(sslContext, httpClientContext, url, null, "UTF-8", false, defConnectTimeout, defSocketTimeout);
	}

	/**
	 * Get方法重载：发起http/https的get请求
	 * 
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param httpClientContext
	 * @param url
	 * @param headers
	 * @param charsetStr
	 * @param keepAlive
	 * @param connectTimeout
	 * @param socketTimeout
	 * @return
	 * @throws Exception
	 */
	public static String get(SSLContext sslContext, HttpClientContext httpClientContext, String url,
							  Map<String, String> headers, String charsetStr, boolean keepAlive, int connectTimeout, int socketTimeout)
			throws Exception {
		//HttpResponse response = getResponse(sslContext,httpClientContext,url,headers,charsetStr,keepAlive,connectTimeout,socketTimeout);
		//System.out.println(EntityUtils.toString(response.getEntity(),"utf-8"));
		//return response.getEntity().toString();
		return getResponse(sslContext,httpClientContext,url,headers,charsetStr,keepAlive,connectTimeout,socketTimeout);
	}

	/**
	 * Get方法重载：发起http/https的get请求
	 * @param sslContext
	 * @param httpClientContext
	 * @param url
	 * @param headers
	 * @param charsetStr
	 * @param keepAlive
	 * @param connectTimeout
	 * @param socketTimeout
     * @return
     * @throws Exception
     */
	public static String getResponse(SSLContext sslContext, HttpClientContext httpClientContext, String url,
													 Map<String, String> headers, String charsetStr, boolean keepAlive, int connectTimeout, int socketTimeout)
			throws Exception {
		HttpClient httpClient = null;
		HttpGet get = null;
		try {
			// 设置get请求参数
			HttpClientBuilder httpClientBuilder = getHttpClientBuilder(sslContext, headers, connectTimeout,
					socketTimeout);
			get = new HttpGet(url);
			get.addHeader("Connection", keepAlive ? "Keep-Alive" : "close");
			httpClient = httpClientBuilder.build();

			// 发送get请求
			HttpResponse response = null;
			if (httpClientContext != null) {
				response = httpClient.execute(get, httpClientContext);// 返回数据
			} else {
				response = httpClient.execute(get);// 返回数据
			}
			return EntityUtils.toString(response.getEntity(),"utf-8");
			//return response;
		} finally {
			if (get != null) {
				get.abort();
			}
		}
	}

	/**
	 * postJson方法重载：以EntityBuilder发起http/https的get请求
	 * 
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String postJson(SSLContext sslContext, String url, String str) throws Exception {
		return postJson(sslContext, url, null, str);
	}

	/**
	 * postJson方法重载：以EntityBuilder发起http/https的get请求
	 * 
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param headers
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String postJson(SSLContext sslContext, String url, Map<String, String> headers, String str)
			throws Exception {
		return postJson(sslContext, null, url, headers, str);
	}

	/**
	 * postJson方法重载：以EntityBuilder发起http/https的get请求
	 * 
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param httpClientContext
	 * @param url
	 * @param headers
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String postJson(SSLContext sslContext, HttpClientContext httpClientContext, String url,
			Map<String, String> headers, String str) throws Exception {
		return requestPost(sslContext, httpClientContext, url, headers, null, str, null, ContentType.APPLICATION_JSON,
				"UTF-8", false, defConnectTimeout, defSocketTimeout);
	}

	/**
	 * postJson方法重载：以EntityBuilder发起http/https的post请求
	 *
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param str
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public static String postJson(SSLContext sslContext, String url, String str, ContentType type) throws Exception {
		return requestPost(sslContext,null, url, null, null, str, null, type, "UTF-8", false, defConnectTimeout,
				defSocketTimeout);
	}

	/**
	 * postParames方法重载：以EntityBuilder发起http/https的get请求
	 * 
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param parames
	 * @return
	 * @throws Exception
	 */
	public static String postParames(SSLContext sslContext, String url, Map<String, String> parames) throws Exception {
		return postParames(sslContext, url, null, parames);
	}

	/**
	 * postParames方法重载：以EntityBuilder发起http/https的get请求
	 * 
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param url
	 * @param headers
	 * @param parames
	 * @return
	 * @throws Exception
	 */
	public static String postParames(SSLContext sslContext, String url, Map<String, String> headers,
			Map<String, String> parames) throws Exception {
		return postParames(sslContext, null, url, headers, parames);
	}

	/**
	 * postParames方法重载：以EntityBuilder发起http/https的get请求
	 * 
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param httpClientContext
	 * @param url
	 * @param headers
	 * @param parames
	 * @return
	 * @throws Exception
	 */
	public static String postParames(SSLContext sslContext, HttpClientContext httpClientContext, String url,
			Map<String, String> headers, Map<String, String> parames) throws Exception {
		return requestPost(sslContext, httpClientContext, url, headers, parames, null, null,
				ContentType.APPLICATION_FORM_URLENCODED, "UTF-8", false, defConnectTimeout, defSocketTimeout);
	}

	/**
	 * 普通Post方法重载：以EntityBuilder发起http/https的get请求
	 * 
	 * @param sslContext https证书，为空时发起http请求，否则发起https请求
	 * @param httpClientContext
	 * @param url
	 * @param headers
	 * @param parames parames、str、inputStream选择其中一种方式传参数
	 * @param str
	 * @param inputStream
	 * @param type
	 * @param charsetStr
	 * @param keepAlive
	 * @return
	 * @throws Exception
	 */
	public static String requestPost(SSLContext sslContext, HttpClientContext httpClientContext, String url,
			Map<String, String> headers, Map<String, String> parames, String str, InputStream inputStream,
			ContentType type, String charsetStr, boolean keepAlive, int connectTimeout, int socketTimeout)
			throws Exception {
		Charset charset = Charset.forName(charsetStr);
		EntityBuilder entityBuilder = EntityBuilder.create();
		entityBuilder.setContentType(type.withCharset(charset));
		entityBuilder.setContentEncoding(charsetStr);

		if (str != null) {
			entityBuilder.setText(str);
		} else if (inputStream != null) {
			entityBuilder.setStream(inputStream);
		} else {
			if (parames == null) {
				parames = new HashMap<>();
			}
			// 设置参数
			List<NameValuePair> parameters = new ArrayList<NameValuePair>();
			Iterator<Entry<String, String>> paramIterator = parames.entrySet().iterator();
			Entry<String, String> paramEntry = null;
			while (paramIterator.hasNext()) {
				paramEntry = paramIterator.next();
				parameters.add(new BasicNameValuePair(paramEntry.getKey(), paramEntry.getValue()));
			}
			entityBuilder.setParameters(parameters);
		}

		return post(sslContext, httpClientContext, url, headers, entityBuilder.build(), charset, keepAlive,
				connectTimeout, socketTimeout);
	}

	/**
	 * postFile方法重载：以MultipartEntityBuilder发起http/https的get请求
	 * 
	 * @param sslContext
	 * @param url
	 * @param headers
	 * @param parames
	 * @param files
	 * @return
	 * @throws Exception
	 */
	public static String postFile(SSLContext sslContext, String url, Map<String, String> headers,
			Map<String, String> parames, Map<String, List<File>> files) throws Exception {
		return postFile(sslContext, null, url, headers, parames, files, "UTF-8", false, defConnectTimeout,
				defSocketTimeout);
	}

	/**
	 * postFile方法重载：以MultipartEntityBuilder发起http/https的get请求
	 * 
	 * @param sslContext
	 * @param httpClientContext
	 * @param url
	 * @param headers
	 * @param parames
	 * @param files
	 * @param charsetStr
	 * @param keepAlive
	 * @param connectTimeout
	 * @param socketTimeout
	 * @return
	 * @throws Exception
	 */
	public static String postFile(SSLContext sslContext, HttpClientContext httpClientContext, String url,
			Map<String, String> headers, Map<String, String> parames, Map<String, List<File>> files, String charsetStr,
			boolean keepAlive, int connectTimeout, int socketTimeout) throws Exception {
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
		return post(sslContext, httpClientContext, url, headers, entityBuilder.build(), charset, keepAlive,
				connectTimeout, socketTimeout);
	}

	/**
	 * 发起http/https的post请求
	 * 
	 * @param sslContext
	 * @param httpClientContext
	 * @param url
	 * @param headers
	 * @param entiy
	 * @param charset
	 * @param keepAlive
	 * @param connectTimeout
	 * @param socketTimeout
	 * @return
	 * @throws Exception
	 */
	private static String post(SSLContext sslContext, HttpClientContext httpClientContext, String url,
			Map<String, String> headers, HttpEntity entiy, Charset charset, boolean keepAlive, int connectTimeout,
			int socketTimeout) throws Exception {
		HttpClient httpClient = null;
		HttpPost post = null;
		try {
			// 设置post请求参数
			HttpClientBuilder httpClientBuilder = getHttpClientBuilder(sslContext, headers, connectTimeout,
					socketTimeout);
			post = new HttpPost(url);
			post.addHeader("Connection", keepAlive ? "Keep-Alive" : "close");
			post.setEntity(entiy);
			httpClient = httpClientBuilder.build();
			HttpResponse response = null;

			// 发送post请求
			if (httpClientContext != null) {
				response = httpClient.execute(post, httpClientContext);// 返回数据
			} else {
				response = httpClient.execute(post);// 返回数据
			}
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
			httpClientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext, new String[] { "TLSv1" },
					null, new HostnameVerifier() {
						@Override
						public boolean verify(String arg0, SSLSession arg1) {
							return true;// 信任所有主机
						}
					}));
		}

		httpClientBuilder.setDefaultRequestConfig(RequestConfig.custom().setConnectTimeout(connectTimeout)
				.setSocketTimeout(socketTimeout).build());
		return httpClientBuilder;
	}

	/**
	 * 取得TCP安全套接字协议,获取的对象请设置为单例，以免频繁创建
	 * 
	 * @return
	 * @throws Exception
	 */
	public static SSLContext getSSLContext() throws Exception {
		return getSSLContext(null, null);
	}

	/**
	 * 取得TCP安全套接字协议,获取的对象请设置为单例，以免频繁创建
	 * 
	 * @param keyStorePath 密钥库路径
	 * @param keyStorepass 密钥库密码
	 * @return
	 * @throws Exception
	 */
	public static SSLContext getSSLContext(String keyStorePath, String keyStorepass) throws Exception {
		InputStream instream = null;
		try {
			instream = new FileInputStream(new File(keyStorePath));
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

	/***
	 * 获取请求授权证书,获取的对象请设置为单例，以免频繁创建
	 * 
	 * @param credentials 参考UsernamePasswordCredentials
	 * @param targetHost
	 * @return
	 */
	public static HttpClientContext getHttpClientContext(Credentials credentials, HttpHost targetHost) {
		CredentialsProvider provider = new BasicCredentialsProvider();
		provider.setCredentials(AuthScope.ANY, credentials);
		AuthCache authCache = new BasicAuthCache();
		authCache.put(targetHost, new BasicScheme());
		final HttpClientContext context = HttpClientContext.create();
		context.setCredentialsProvider(provider);
		context.setAuthCache(authCache);
		return context;
	}

}