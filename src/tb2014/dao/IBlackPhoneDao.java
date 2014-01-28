package tb2014.dao;

import java.util.List;

import tb2014.domain.Broker;
import tb2014.domain.phone.BlackPhone;

public interface IBlackPhoneDao {

	BlackPhone get(Long id);
	
	List<BlackPhone> get(Broker broker);
	
	List<BlackPhone> getAll();
	
	void save(BlackPhone phone);
	
	void saveOrUpdate(BlackPhone phone);
}
