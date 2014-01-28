package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.business.IRequirementBusiness;
import tb2014.dao.IRequirementDao;
import tb2014.domain.order.Order;
import tb2014.domain.order.Requirement;

@Service("RequirementBusiness")
public class RequirementBusiness implements IRequirementBusiness {

	IRequirementDao requirementDao;

	@Autowired
	public RequirementBusiness(IRequirementDao requirementDao) {
		this.requirementDao = requirementDao;
	}

	@Transactional(readOnly = true)
	@Override
	public Requirement get(Long id) {
		return requirementDao.get(id);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Requirement> get(Order order) {
		return requirementDao.get(order);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Requirement> getAll() {
		return requirementDao.getAll();
	}

	@Transactional
	@Override
	public void save(Requirement requirement) {
		requirementDao.save(requirement);
	}

	@Transactional
	@Override
	public void saveOrUpdate(Requirement requirement) {
		requirementDao.saveOrUpdate(requirement);
	}

}
