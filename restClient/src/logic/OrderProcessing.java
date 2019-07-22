package logic;

import java.util.List;

import model.Item;
import model.Order;

/**
 * OrderProcessing class contains the business logic of creating a financial
 * order from a normal order.
 * 
 * @author binay
 *
 */
public class OrderProcessing {

	private Order order;

	/**
	 * This constructor will receive a Order object and save it into a local Order
	 * object.
	 * 
	 * @param order
	 *            Order object
	 */
	public OrderProcessing(Order order) {
		this.order = order;
	}

	/**
	 * This method will change the normal order into a financial order.
	 */
	public void createFinancialOrder() {

		List<Item> items = order.getItems();
		double totalTax = order.getTaxes();
		double totalShipping = order.getShipping();

		System.out.println("order:" + order.getOrder_id() + "       Taxes:" + order.getTaxes() + "       Shipping:"
				+ order.getShipping());
		System.out.println("order:" + "       Taxes:" + order.getTaxes() + "       Shipping:" + order.getShipping());

		double totalDiscount = 0;
		int totalQuantity = 0;
		double totalAmount = 0;
		double tax = 0;
		double shipping = 0;
		double amount = 0;
		double totalShippingTemp = totalShipping;
		double totalTaxTemp = totalTax;
		double discount = 0;

		totalAmount = items.stream().mapToDouble(a -> a.getMsrp() * a.getQuantity()).sum();
		int itemCount = items.size();
		int iterationCount = 1;

		for (Item item : items) {
			System.out.println(
					"item:" + item.getItem() + "       MRSP:" + item.getMsrp() + "      RP:" + item.getRetail_price());
			amount = item.getMsrp();
			if (iterationCount != itemCount) {
				tax = Math.round((((amount * item.getQuantity()) / totalAmount) * totalTax) * 100d) / 100d;
				shipping = Math.round((((amount * item.getQuantity()) / totalAmount) * totalShipping) * 100d) / 100d;
			} else {
				tax = totalTaxTemp;
				shipping = totalShippingTemp;

			}
			discount = Math.round(((amount - item.getRetail_price()) * item.getQuantity()) * 100d) / 100d;

			totalDiscount += discount;
			totalQuantity += item.getQuantity();

			item.setAmount(amount);
			item.setTaxes(tax);
			item.setShipping(shipping);
			item.setMsrp(0);
			item.setRetail_price(0);
			totalShippingTemp = Math.round((totalShippingTemp - shipping) * 100d) / 100d;
			totalTaxTemp = Math.round((totalTaxTemp - tax) * 100d) / 100d;
			iterationCount++;
		}

		// For discount
		Item discountItem = new Item();
		discountItem.setItem("Discount");
		discountItem.setQuantity(totalQuantity);
		discountItem.setAmount(-Math.round(totalDiscount * 100d) / 100d);
		discountItem.setShipping(0);
		discountItem.setTaxes(0);
		order.addItem(discountItem);

		order.setShipping(totalShippingTemp);
		order.setTaxes(totalTaxTemp);

	}

	/**
	 * This method will return the financial order.
	 * 
	 * @return Order object
	 */
	public Order getProcessedOrder() {
		return order;
	}

}
