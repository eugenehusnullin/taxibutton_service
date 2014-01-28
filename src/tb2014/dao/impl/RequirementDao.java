package tb2014.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tb2014.dao.IRequirementDao;
import tb2014.domain.order.Order;
import tb2014.domain.order.Requirement;

@Repository("RequirementDao")
public class RequirementDao implements IRequirementDao {

	private SessionFactory sessionFactory;

	@Autowired
	public RequirementDao(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Requirement get(Long id) {
		return (Requirement) sessionFactory.getCurrentSession().get(
				Requirement.class, id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Requirement> get(Order order) {
		return sessionFactory.getCurrentSession().createCriteria(Order.class)
				.add(Restrictions.eq("order", order)).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Requirement> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(Order.class).list();
	}

	@Override
	public void save(Requirement requirement) {
		sessionFactory.getCurrentSession().save(requirement);
	}

	@Override
	public void saveOrUpdate(Requirement requirement) {
		sessionFactory.getCurrentSession().saveOrUpdate(requirement);
	}

}
