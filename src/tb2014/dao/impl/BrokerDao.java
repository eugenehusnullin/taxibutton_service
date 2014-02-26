package tb2014.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.IBrokerDao;
import tb2014.domain.Broker;

@Repository("BrokerDao")
public class BrokerDao implements IBrokerDao {

	private SessionFactory sessionFactory;

	@Autowired
	public BrokerDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Broker get(Long id) {
		return (Broker) sessionFactory.getCurrentSession().get(Broker.class, id);
	}

	@Override
	public Broker get(String uuid) {
		return (Broker) sessionFactory.getCurrentSession().createCriteria(Broker.class)
				.add(Restrictions.eq("uuid", uuid)).uniqueResult();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Broker> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(Broker.class).list();
	}

	@Override
	public void save(Broker broker) {
		sessionFactory.getCurrentSession().save(broker);
	}

	@Override
	public Broker getByApiId(String id) {
		return (Broker) sessionFactory.getCurrentSession().createCriteria(Broker.class)
				.add(Restrictions.eq("apiId", id)).uniqueResult();
	}

	@Override
	public void delete(Broker broker) {
		sessionFactory.getCurrentSession().delete(broker);
	}

	@Override
	public void saveOrUpdate(Broker broker) {
		sessionFactory.getCurrentSession().saveOrUpdate(broker);
	}

}
