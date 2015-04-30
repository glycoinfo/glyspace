/*
 *  (C) Copyright 2012 Insula Tecnologia da Informacao Ltda.
 *
 *  This file is part of spring-security-janrain.
 *
 *  spring-security-janrain is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  spring-security-janrain is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with spring-security-janrain.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.glyspace.registry.security.janrain;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import ch.qos.logback.classic.Logger;

public class JanrainService {

	public static Logger logger=(Logger) LoggerFactory.getLogger("org.glyspace.registry.security.janrain.JanrainService");

	
	private HttpClient httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager());

	private String apiKey;
	
	
	public JanrainAuthenticationToken retrieve(String identifier) {
		try {
			HttpResponse httpResponse = httpClient.execute(createHttpPostUserRequest(identifier));
			InputStream content = httpResponse.getEntity().getContent();
			return parseJanrainAuthenticationToken(content);
		} catch (Exception e) {
			throw new JanrainException("Error processing token", e);
		}
	}

	
	public JanrainAuthenticationToken authenticate(String token) {
		try {
			HttpResponse httpResponse = httpClient.execute(createHttpPostRequest(token));
			InputStream content = httpResponse.getEntity().getContent();
			return parseJanrainAuthenticationToken(content);
		} catch (Exception e) {
			throw new JanrainException("Error processing token", e);
		}
	}
	
	/*
	 * <rsp stat='ok'><profile><providerName>Twitter</providerName>
<identifier>http://twitter.com/account/profile?user_id=2832353642</identifier>
<preferredUsername>glytoucan</preferredUsername>
<displayName>Gly</displayName>
<name><formatted>Gly</formatted></name>
<url>http://twitter.com/glytoucan</url>
<photo>https://pbs.twimg.com/profile_images/515296554433794048/JwLaCn8y_normal.png</photo>
<address><formatted/></address>
<providerSpecifier>twitter</providerSpecifier></profile></rsp>
	 */

	private JanrainAuthenticationToken parseJanrainAuthenticationToken(InputStream content)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		Document document = parseContent(content);
		logger.debug(document.toString());
		XPath xPath = createXPath();
		if (!getStringValue(document, xPath, "//rsp/@stat").equals("ok")) {
			return null;
		}
		String identifier = getStringValue(document, xPath, "//rsp/profile/identifier");
		String providerName = getStringValue(document, xPath, "//rsp/profile/providerName");
		String name = getStringValue(document, xPath, "//rsp/profile/name/formatted");
		String email = getStringValue(document, xPath, "//rsp/profile/email");
		String verifiedEmail = getStringValue(document, xPath, "//rsp/profile/verifiedEmail");
		
		String providerSpecifier = getStringValue(document, xPath, "//rsp/profile/providerSpecifier");

		// twitter specific		
		if(null != providerSpecifier && providerSpecifier.equals("twitter")) {
			if (null == email || email.isEmpty())
				email = getStringValue(document, xPath, "//rsp/profile/url");
			
			//http://twitter.com/account/profile?user_id=2832353642
			
			URL twiturl = new URL(identifier);
			identifier = providerSpecifier + twiturl.getQuery();
		}
		logger.debug("identifier:>" + identifier + "<");
		logger.debug("providerName:>" + providerName + "<");
		logger.debug("name:>" + name + "<");
		logger.debug("email:>" + email + "<");
		logger.debug("verifiedEmail:>" + verifiedEmail + "<");
		logger.debug("providerSpecifier:>" + providerSpecifier + "<");

		return new JanrainAuthenticationToken(identifier, verifiedEmail, email, providerName, name);
	}

	private HttpPost createHttpPostRequest(String token) throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost("https://rpxnow.com/api/v2/auth_info");
		UrlEncodedFormEntity entity = createHttpPostFormRequestEntity(token);
		httpPost.setEntity(entity);
		return httpPost;
	}

	private HttpPost createHttpPostUserRequest(String identifer) throws UnsupportedEncodingException {
		HttpPost httpPost = new HttpPost("https://rpxnow.com/api/v2/get_user_data");
		UrlEncodedFormEntity entity = createHttpPostFormRequestIdentifierEntity(identifer);
		httpPost.setEntity(entity);
		return httpPost;
	}

	
	private UrlEncodedFormEntity createHttpPostFormRequestEntity(String token) throws UnsupportedEncodingException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("format", "xml"));
		params.add(new BasicNameValuePair("apiKey", apiKey));
		params.add(new BasicNameValuePair("token", token));
		return new UrlEncodedFormEntity(params, "UTF-8");
	}

	private UrlEncodedFormEntity createHttpPostFormRequestIdentifierEntity(String identifier) throws UnsupportedEncodingException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("format", "xml"));
		params.add(new BasicNameValuePair("apiKey", apiKey));
		params.add(new BasicNameValuePair("identifier", identifier));
		return new UrlEncodedFormEntity(params, "UTF-8");
	}

	
	private Document parseContent(InputStream content) throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		return db.parse(content);
	}

	private XPath createXPath() {
		XPathFactory xPathFactory = XPathFactory.newInstance();
		return xPathFactory.newXPath();
	}

	private String getStringValue(Document document, XPath xPath, String expression) throws XPathExpressionException {
		String value = xPath.evaluate(expression, document).trim();
		return value.isEmpty() ? null : value;
	}

	@Required
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

}
