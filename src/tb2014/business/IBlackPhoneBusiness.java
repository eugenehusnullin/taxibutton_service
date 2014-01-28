package tb2014.business;

import java.util.List;

import tb2014.domain.Broker;
import tb2014.domain.phone.BlackPhone;

public interface IBlackPhoneBusiness {

	BlackPhone get(Long id);

	List<BlackPhone> get(Broker broker);

	List<BlackPhone> getAll();

	void save(BlackPhone phone);

	void saveOrUpdate(BlackPhone phone);
}
