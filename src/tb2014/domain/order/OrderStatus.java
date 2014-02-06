package tb2014.domain.order;

import java.util.Date;

public class OrderStatus {

	private Long id;
	private Order order;
	private OrderStatusType status;
	private Date date;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public Order getOrder() {
		return order;
	}
	
	public void setOrder(Order order) {
		this.order = order;
	}
	
	public OrderStatusType getStatus() {
		return status;
	}
	
	public void setStatus(OrderStatusType status) {
		this.status = status;
	}
	
	public Date getDate() {
		return date;
	}
	
	public void setDate(Date date) {
		this.date = date;
	}
}
