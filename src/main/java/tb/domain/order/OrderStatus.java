package tb.domain.order;

import java.util.Date;

public class OrderStatus {

	private Long id;
	private Order order;
	private OrderStatusType status;
	private String statusDescription;
	private Date date;
	
	public Long getId() {
		return id;
	}
	
	public String getStatusDescription() {
		return statusDescription;
	}

	public void setStatusDescription(String statusDescription) {
		this.statusDescription = statusDescription;
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
