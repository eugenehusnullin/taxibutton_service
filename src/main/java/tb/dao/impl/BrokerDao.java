package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tb.dao.IBrokerDao;
import tb.domain.Broker;
import tb.domain.TariffType;

@Repository("BrokerDao")
public class BrokerDao implements IBrokerDao {

	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@Override
	public Broker get(Long id) {
		return (Broker) sessionFactory.getCurrentSession().get(Broker.class, id);
	}

	@Override
	public Broker get(String uuid) {
		return (Broker) sessionFactory.getCurrentSession()
				.createCriteria(Broker.class)
				.add(Restrictions.eq("uuid", uuid))
				.uniqueResult();
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

	@Transactional
	@Override
	public Broker getByApiId(String id) {
		return (Broker) sessionFactory.getCurrentSession().createCriteria(Broker.class)
				.add(Restrictions.eq("apiId", id)).uniqueResult();
	}

	@Override
	public Broker getByApiId(String apiId, String apiKey) {
		return (Broker) sessionFactory.getCurrentSession().createCriteria(Broker.class)
				.add(Restrictions.eq("apiId", apiId))
				.add(Restrictions.eq("apiKey", apiKey)).uniqueResult();
	}

	@Override
	public void delete(Broker broker) {
		sessionFactory.getCurrentSession().delete(broker);
	}

	@Override
	public void saveOrUpdate(Broker broker) {
		sessionFactory.getCurrentSession().saveOrUpdate(broker);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Broker> getActive() {
		return sessionFactory.getCurrentSession().createCriteria(Broker.class).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Broker> getBrokersNeedMapareaSynch() {
		return sessionFactory.getCurrentSession().createCriteria(Broker.class)
				.add(Restrictions.neOrIsNotNull("mapareaUrl", ""))
				.add(Restrictions.eq("tariffType", TariffType.JSON))
				.list();
	}
}
