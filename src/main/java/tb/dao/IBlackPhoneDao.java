package tb.dao;

import java.util.List;

import tb.domain.Broker;
import tb.domain.phone.BlackPhone;

public interface IBlackPhoneDao {

	BlackPhone get(Long id);
	
	List<BlackPhone> get(Broker broker);
	
	List<BlackPhone> getAll();
	
	void save(BlackPhone phone);
	
	void saveOrUpdate(BlackPhone phone);
}
