package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb.dao.IBlackPhoneDao;
import tb.domain.Broker;
import tb.domain.phone.BlackPhone;

@Repository("BlackPhoneDao")
public class BlackPhoneDao implements IBlackPhoneDao {

	private SessionFactory sessionFactory;

	@Autowired
	public BlackPhoneDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public BlackPhone get(Long id) {
		return (BlackPhone) sessionFactory.getCurrentSession().get(
				BlackPhone.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BlackPhone> get(Broker broker) {
		return sessionFactory.getCurrentSession()
				.createCriteria(BlackPhone.class)
				.add(Restrictions.eq("broker", broker)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BlackPhone> getAll() {
		return sessionFactory.getCurrentSession()
				.createCriteria(BlackPhone.class).list();
	}

	@Override
	public void save(BlackPhone phone) {
		sessionFactory.getCurrentSession().save(phone);
	}

	@Override
	public void saveOrUpdate(BlackPhone phone) {
		sessionFactory.getCurrentSession().saveOrUpdate(phone);
	}

}
