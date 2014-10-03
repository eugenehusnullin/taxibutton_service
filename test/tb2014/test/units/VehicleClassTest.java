package tb2014.test.units;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import tb2014.domain.order.VehicleClass;

public class VehicleClassTest {

	@Test
	public void testOrdinal() {
		assertEquals(0, VehicleClass.Ecomon.ordinal());
		assertEquals(1, VehicleClass.Standard.ordinal());
		assertEquals(2, VehicleClass.Business.ordinal());

		assertEquals(0, VehicleClass.values()[0].ordinal());
		assertEquals(1, VehicleClass.values()[1].ordinal());
		assertEquals(2, VehicleClass.values()[2].ordinal());
	}
}
