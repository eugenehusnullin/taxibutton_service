package tb.dao;

import java.util.List;

import tb.domain.TariffDefinitionMapArea;

public interface ITariffDefinitionMapAreaDao {
	void add(TariffDefinitionMapArea tariffDefinitionMapArea);

	List<TariffDefinitionMapArea> getAll();

	void delete(String name);

	TariffDefinitionMapArea get(String name);

	void update(TariffDefinitionMapArea tariffDefinitionMapArea);
}
