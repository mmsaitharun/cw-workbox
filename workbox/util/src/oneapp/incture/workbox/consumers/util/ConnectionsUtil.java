package oneapp.incture.workbox.consumers.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.olingo.odata2.api.commons.HttpStatusCodes;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import oneapp.incture.workbox.util.PMCConstant;
import oneapp.incture.workbox.util.ServicesUtil;



/**
 * Contains utility functions to be used for consuming oData Services
 * 
 * @version R1
 */
public class ConnectionsUtil {


	public static String USERNAME;
	public static String PASSWORD; 

	public static InputStream execute(String userName ,String password ,String relativeUri, String contentType, String httpMethod) throws IOException {

		if(setUserPassword(userName,password).equals("FAILURE")){
			return null;
		}

		InputStream content  = null;
		HttpURLConnection connection = null;
		try {
			connection = initializeConnection(relativeUri, contentType, httpMethod);
			connection.connect();
			checkStatus(connection);
			content = connection.getInputStream();
			content = logRawContent(httpMethod + " request on uri '" + relativeUri + "' with content:\n  ", content, "\n");

		}catch(Exception e){
			System.err.println("[PMC][ConnectionsUtil][execute][error]  "+ e.getMessage());
		}
		finally{
			if(!ServicesUtil.isEmpty(connection)){
				connection.disconnect();
			}
		}
		return content;
	}


	private static HttpURLConnection initializeConnection(String absolutUri, String contentType, String httpMethod)
			throws MalformedURLException, IOException {
		URL url = new URL(absolutUri);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod(httpMethod);
		connection.setRequestProperty(PMCConstant.HTTP_HEADER_ACCEPT, contentType);
		connection.setRequestProperty("Authorization", ServicesUtil.getBasicAuth(USERNAME,PASSWORD));

		if (PMCConstant.HTTP_METHOD_POST.equals(httpMethod) || PMCConstant.HTTP_METHOD_PUT.equals(httpMethod)) {
			connection.setDoOutput(true);
			connection.setRequestProperty(PMCConstant.HTTP_HEADER_CONTENT_TYPE, contentType);
		}

		return connection;
	}

	private static HttpStatusCodes checkStatus(HttpURLConnection connection) throws IOException {
		HttpStatusCodes httpStatusCode = HttpStatusCodes.fromStatusCode(connection.getResponseCode());
		if (400 <= httpStatusCode.getStatusCode() && httpStatusCode.getStatusCode() <= 599) {
			throw new RuntimeException("Http Connection failed with status " + httpStatusCode.getStatusCode() + " "
					+ httpStatusCode.toString());
		}
		return httpStatusCode;
	}

	private static InputStream logRawContent(String prefix, InputStream content, String postfix) throws IOException {
		if (PMCConstant.PRINT_RAW_CONTENT) {
			byte[] buffer = streamToArray(content);
			//System.err.println("PMC"+new String(buffer));
			return new ByteArrayInputStream(buffer);
		}
		return content;
	}

	public static byte[] streamToArray(InputStream stream) throws IOException {
		byte[] result = new byte[0];
		byte[] tmp = new byte[8192];
		int readCount = stream.read(tmp);
		while (readCount >= 0) {
			byte[] innerTmp = new byte[result.length + readCount];
			System.arraycopy(result, 0, innerTmp, 0, result.length);
			System.arraycopy(tmp, 0, innerTmp, result.length, readCount);
			result = innerTmp;
			readCount = stream.read(tmp);
		}
		stream.close();
		return result;
	}


	public static ResponseDto executeActionHttp(String url,String userName,String scode) throws IOException
	{       
		if(setUserPassword(userName,scode).equals("FAILURE")){
			return null;
		}
		System.err.println("[PMC][ConnectionsUtil][actions][executeActionHttp][entry] " +url);
		ResponseDto returnMessage = new ResponseDto();
		String X_CSRF_TOKEN= "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost postRequest = new HttpPost(url);
		try{
			{
				HttpGet httpget = new HttpGet(url);
				httpget.setHeader("Authorization",ServicesUtil.getBasicAuth(USERNAME,PASSWORD));
				httpget.setHeader("x-csrf-token", "fetch");
				Header headers[] = httpget.getAllHeaders();
				HttpResponse res = httpclient.execute(httpget);                           
				headers = res.getAllHeaders();
				for (Header h : headers) {
					if (h.getName().equals("x-csrf-token")) {
						X_CSRF_TOKEN = h.getValue();
						System.err.println("[PMC][ConnectionsUtil][actions][executeActionHttp][csrf] " +X_CSRF_TOKEN);
					}
				}
			}

			postRequest.addHeader("Authorization",ServicesUtil.getBasicAuth(USERNAME,PASSWORD));
			postRequest.setHeader("x-csrf-token", X_CSRF_TOKEN);


			HttpResponse response = httpclient.execute(postRequest);
			String result = EntityUtils.toString(response.getEntity());
			int responseCode = response.getStatusLine().getStatusCode();

			System.err.println("Http Response: Response code " + responseCode +"result.toString()"+result.toString());
			returnMessage.setStatusCode(Integer.toString(responseCode));
			if(responseCode!=200){
				returnMessage.setStatus("FAILURE");
			}
			else{
				returnMessage.setStatus("SUCCESS");
			}

			if(responseCode!=200 && result.toString().contains("error")){
				try {
					DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
					DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
					Document doc;
					InputStream stream = new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8.name()));
					doc = dBuilder.parse(stream);
					doc.getDocumentElement().normalize();
					XPath xPath =  XPathFactory.newInstance().newXPath();
					XPathExpression expr1=xPath.compile("error/message/text()"); 
					NodeList node1 = (NodeList) expr1.evaluate(doc, XPathConstants.NODESET);
					returnMessage.setMessage(node1.item(0).getNodeValue());

				} catch (Exception e1) {
					System.err.println("[PMC][ConnectionsUtil][actions][executeActionHttp][togetError]"+e1.getMessage());
				}
			}

		}catch(Exception e ){
			System.err.println("[PMC][ConnectionsUtil][actions][executeActionHttp][error]"+e.getMessage());
			returnMessage.setMessage(e.getMessage());
			returnMessage.setStatus("FAILURE");
			returnMessage.setStatusCode("1");
		}
		System.err.println("[PMC][ConnectionsUtil][actions][executeActionHttp][exit]"+returnMessage);
		return returnMessage;
	}

	private static String setUserPassword(String userName,String password){
		if(!ServicesUtil.isEmpty(userName) && !ServicesUtil.isEmpty(password))
		{
			USERNAME = userName;
			PASSWORD = password;
		}
		else{
			System.err.println("[PMC][ConnectionsUtil][setUserPassword] either usernameor password is empty");
			return "FAILURE";
		}
		return "SUCCESS";
	}

}