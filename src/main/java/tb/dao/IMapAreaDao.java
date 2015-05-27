package tb.dao;

import java.util.List;

import tb.domain.Broker;
import tb.domain.maparea.MapArea;

public interface IMapAreaDao {
	void add(MapArea mapArea);

	void delete(Broker broker);
	
	List<MapArea> getAll();
}
