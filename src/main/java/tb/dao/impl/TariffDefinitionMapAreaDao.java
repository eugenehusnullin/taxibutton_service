package tb.dao.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tb.dao.ITariffDefinitionMapAreaDao;
import tb.domain.TariffDefinitionMapArea;

@Repository("TariffDefinitionMapAreaDao")
public class TariffDefinitionMapAreaDao implements ITariffDefinitionMapAreaDao {
	@Autowired
	@Qualifier("sessionFactory")
	private SessionFactory sessionFactory;

	@Transactional
	@Override
	public void add(TariffDefinitionMapArea tariffDefinitionMapArea) {
		sessionFactory.getCurrentSession().save(tariffDefinitionMapArea);
	}

	@Transactional
	@SuppressWarnings("unchecked")
	@Override
	public List<TariffDefinitionMapArea> getAll() {
		return sessionFactory.getCurrentSession().createCriteria(TariffDefinitionMapArea.class).list();
	}

	@Transactional
	@Override
	public void delete(String name) {
		String delete = "delete TariffDefinitionMapArea where name = :name";
		sessionFactory.getCurrentSession().createQuery(delete)
				.setString("name", name)
				.executeUpdate();
	}
}
