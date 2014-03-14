package tb2014.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.ISimpleTariffDao;
import tb2014.domain.Broker;
import tb2014.domain.tariff.SimpleTariff;

@Repository("SimpleTariffDao")
public class SimpleTariffDao implements ISimpleTariffDao {

	private SessionFactory sessionFactory;

	@Autowired
	public SimpleTariffDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public SimpleTariff get(Long id) {
		return (SimpleTariff) sessionFactory.getCurrentSession().get(SimpleTariff.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<SimpleTariff> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(SimpleTariff.class).list();
	}

	@Override
	public void save(SimpleTariff tariff) {
		sessionFactory.getCurrentSession().save(tariff);
	}

	@Override
	public SimpleTariff get(Broker broker) {
		return (SimpleTariff) sessionFactory.getCurrentSession().createCriteria(SimpleTariff.class)
				.add(Restrictions.eq("broker", broker)).uniqueResult();
	}

	@Override
	public void saveOrUpdate(SimpleTariff tariff) {
		sessionFactory.getCurrentSession().saveOrUpdate(tariff);
	}
}
