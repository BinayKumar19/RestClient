package logic;

import java.text.DecimalFormat;
import java.util.List;

import model.Item;
import model.Order;

public class OrderProcessing {

	Order order;

	public OrderProcessing(Order order) {
		this.order = order;
	}

	public void processOrder() {

		List<Item> items = order.getItems();
		double totalTax = order.getTaxes();
		double totalShipping = order.getShipping();
		double totalDiscount = 0;
		int totalQuantity = 0;
		double totalAmount = 0;
		double tax = 0;
		double shipping = 0;
		double amount = 0;
		double totalShippingTemp = totalShipping;
		double totalTaxTemp = totalTax;
		double discount = 0;

		totalAmount = items.stream().mapToDouble(a -> a.getMsrp()).sum();
		int itemCount = items.size();
		int iterationCount = 1;

		for (Item item : items) {
			amount = item.getMsrp();
			if (iterationCount != itemCount) {
				tax = Math.round(((amount / totalAmount) * totalTax) * 100d) / 100d;
				shipping = Math.round(((amount / totalAmount) * totalShipping) * 100d) / 100d;
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
		discountItem.setAmount(-totalDiscount);
		discountItem.setShipping(0);
		discountItem.setTaxes(0);
		order.addItem(discountItem);

		order.setShipping(totalShippingTemp);
		order.setTaxes(totalTaxTemp);

	}
		
	
	public Order getProcessedOrder() {
		return order;
	}

}
