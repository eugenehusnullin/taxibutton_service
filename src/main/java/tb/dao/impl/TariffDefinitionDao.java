package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import tb.dao.ITariffDefinitionDao;
import tb.domain.TariffDefinition;
import tb.domain.order.VehicleClass;

@Repository("TariffDefinitionDao")
public class TariffDefinitionDao implements ITariffDefinitionDao {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Override
	public List<TariffDefinition> get(VehicleClass vehicleClass) {
		return sessionFactory.getCurrentSession().createCriteria(TariffDefinition.class)
				.add(Restrictions.eq("vehicleClass", vehicleClass))
				.list();
	}

}
