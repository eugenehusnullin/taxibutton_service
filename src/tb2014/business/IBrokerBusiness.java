package tb2014.business;

import java.util.List;

import tb2014.domain.Broker;

public interface IBrokerBusiness {
	Broker getById(Long id);
	List<Broker> getAll();
}
