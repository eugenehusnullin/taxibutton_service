package tb2014.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

public class NetStreamUtils {

	public static String getStringFromInputStream(InputStream stream) throws IOException {
		StringBuffer stringBuffer = new StringBuffer();
		String line = null;
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
		while ((line = bufferedReader.readLine()) != null) {
			stringBuffer.append(line);
		}
		return stringBuffer.toString();
	}

	public static StringBuffer getHttpServletRequestBuffer(HttpServletRequest request) throws IOException {
		StringBuffer stringBuffer = new StringBuffer();
		String line = null;
		BufferedReader bufferedReader = request.getReader();
		while ((line = bufferedReader.readLine()) != null) {
			stringBuffer.append(line);
		}
		return stringBuffer;
	}
}
