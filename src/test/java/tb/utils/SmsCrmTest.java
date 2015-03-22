package tb.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SmsCrmTest {

	@Test
	public void testCreateOutput() {
		SmsCrm smsCrm = new SmsCrm();
		String s = smsCrm.createOutput("+37377701234", "1234");
		//System.out.println(s);

		String etalon = "[{\"sendNumber\":\"+37377701234\",\"sendText\":\"1234\"}]";
		assertEquals(etalon, s);
	}
}