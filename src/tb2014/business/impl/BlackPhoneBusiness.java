package tb2014.business.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tb2014.business.IBlackPhoneBusiness;
import tb2014.dao.IBlackPhoneDao;
import tb2014.domain.Broker;
import tb2014.domain.phone.BlackPhone;

@Service("BlackPhoneBusiness")
public class BlackPhoneBusiness implements IBlackPhoneBusiness {

	private IBlackPhoneDao blackPhoneDao;
	
	@Autowired
	public BlackPhoneBusiness(IBlackPhoneDao blackPhoneDao) {
		this.blackPhoneDao = blackPhoneDao;
	}
	
	@Transactional(readOnly=true)
	@Override
	public BlackPhone get(Long id) {
		return blackPhoneDao.get(id);
	}

	@Transactional(readOnly=true)
	@Override
	public List<BlackPhone> get(Broker broker) {
		return blackPhoneDao.get(broker);
	}

	@Transactional(readOnly=true)
	@Override
	public List<BlackPhone> getAll() {
		return blackPhoneDao.getAll();
	}

	@Transactional
	@Override
	public void save(BlackPhone phone) {
		blackPhoneDao.save(phone);
	}

	@Transactional
	@Override
	public void saveOrUpdate(BlackPhone phone) {
		blackPhoneDao.saveOrUpdate(phone);
	}

}
