package tb2014.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;

public class NetStreamUtils {

	public static String getStringFromInputStream(InputStream stream) {

		StringBuffer stringBuffer = new StringBuffer();
		String line = null;

		try {

			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(stream));

			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
		} catch (Exception ex) {
			System.out.println("Error greating string from input stream: "
					+ ex.toString());
		}

		return stringBuffer.toString();
	}

	public static StringBuffer getHttpServletRequestBuffer(
			HttpServletRequest request) {

		StringBuffer stringBuffer = new StringBuffer();
		String line = null;

		try {

			BufferedReader bufferedReader = request.getReader();

			while ((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line);
			}
		} catch (Exception ex) {
			System.out
					.println("Error creating string buffer from HttpServletRequest: "
							+ ex.toString());
		}

		return stringBuffer;
	}
}
