package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import tb.dao.ITariffDao;
import tb.domain.Broker;
import tb.domain.Tariff;

@Repository("TariffDao")
public class TariffDao implements ITariffDao {

	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@SuppressWarnings("unchecked")
	@Override
	public List<Tariff> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(Tariff.class).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tariff> get(Broker broker) {
		return sessionFactory.getCurrentSession().createCriteria(Tariff.class)
				.add(Restrictions.eq("broker", broker))
				.list();
	}

	@Override
	public void saveOrUpdate(Tariff tariff) {
		sessionFactory.getCurrentSession().saveOrUpdate(tariff);
	}

	@Override
	public void delete(Broker broker) {
		String hqlDelete = "delete Tariff where broker = :broker";
		sessionFactory.getCurrentSession().createQuery(hqlDelete)
			.setEntity("broker", broker)
			.executeUpdate();
	}
}
