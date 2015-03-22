package tb.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class Sms48 {

	@Value("#{mainSettings['sms48.request']}")
	private String request;
	@Value("#{mainSettings['sms48.login']}")
	private String login;
	@Value("#{mainSettings['sms48.pass']}")
	private String pass;
	@Value("#{mainSettings['sms48.sender']}")
	private String sender;

	public void send(String reciever, String msg) {
		send(reciever, msg, sender);
	}

	public void send(String reciever, String msg, String customSender) {
		String md5;
		try {
			md5 = md5(login + md5(pass) + reciever);

			String apiSms = request;
			apiSms = apiSms.replace("[login]", login).replace("[reciever]", urlEncode(reciever))
					.replace("[sender]", urlEncode(customSender)).replace("[msg]", urlEncode(msg))
					.replace("[md5]", urlEncode(md5));

			URL url = new URL(apiSms);
			URLConnection connection = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String md5(String sourceString) throws NoSuchAlgorithmException {
		byte[] defaultBytes = sourceString.getBytes();
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.reset();
		md.update(defaultBytes);
		byte[] digest = md.digest();
		String hexString = "";
		for (int i = 0; i < digest.length; i++) {
			hexString += (Integer.toHexString(0xFF & digest[i]));
		}
		return hexString;
	}

	private String urlEncode(String str) throws UnsupportedEncodingException {
		return URLEncoder.encode(str, "cp1251");
	}
}