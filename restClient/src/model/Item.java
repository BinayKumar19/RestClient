package model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Item class to map the items ordered by customer.
 * 
 * @author binay
 *
 */
@XmlRootElement
public class Item implements Serializable {

	private String item;
	private int quantity;
	private double msrp;
	private double retail_price;

	// for financial order
	private double amount;
	private double shipping;
	private double taxes;

	public Item() {
		super();
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getMsrp() {
		return msrp;
	}

	public void setMsrp(double msrp) {
		this.msrp = msrp;
	}

	public double getRetail_price() {
		return retail_price;
	}

	public void setRetail_price(double retail_price) {
		this.retail_price = retail_price;
	}

	public double getShipping() {
		return shipping;
	}

	public void setShipping(double shipping) {
		this.shipping = shipping;
	}

	public double getTaxes() {
		return taxes;
	}

	public void setTaxes(double taxes) {
		this.taxes = taxes;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

}
