package tb2014.business;

import java.util.List;

import tb2014.domain.order.Order;
import tb2014.domain.order.Requirement;

public interface IRequirementBusiness {

	Requirement get(Long id);

	List<Requirement> get(Order order);

	List<Requirement> getAll();

	void save(Requirement requirement);

	void saveOrUpdate(Requirement requirement);
}
