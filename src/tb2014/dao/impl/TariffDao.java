package tb2014.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.ITariffDao;
import tb2014.domain.Broker;
import tb2014.domain.tariff.Tariff;

@Repository("SimpleTariffDao")
public class TariffDao implements ITariffDao {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Tariff get(Long id) {
		return (Tariff) sessionFactory.getCurrentSession().get(Tariff.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tariff> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(Tariff.class).list();
	}

	@Override
	public void save(Tariff tariff) {
		sessionFactory.getCurrentSession().save(tariff);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Tariff> getActive(Broker broker) {
		return sessionFactory.getCurrentSession().createCriteria(Tariff.class)
				.add(Restrictions.eq("broker", broker))
				.add(Restrictions.isNull("endDate"))
				.list();
	}

	@Override
	public void saveOrUpdate(Tariff tariff) {
		sessionFactory.getCurrentSession().saveOrUpdate(tariff);
	}

	@Override
	public Tariff getActive(Broker broker, String tariffId) {
		return (Tariff) sessionFactory.getCurrentSession().createCriteria(Tariff.class)
				.add(Restrictions.eq("broker", broker))
				.add(Restrictions.eq("tariffId", tariffId))
				.add(Restrictions.isNull("endDate"))
				.uniqueResult();
	}
}
