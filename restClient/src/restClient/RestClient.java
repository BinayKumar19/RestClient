package restClient;

import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import logic.OrderProcessing;
import model.Item;
import model.Order;

public class RestClient {

	private static final String REST_URI 
    //= "http://localhost:8080/RestServer/rest/DemoService/";
	 ="https://ops-interview.p.fullscript.io/";
	 Client client;
	 WebTarget baseTarget;
	 WebTarget getRequestTarget;
	 WebTarget postRequestTarget;
	 WebTarget authRequestTarget;

	 String userName = "user_43712";
	 String password = "G45hDvzW5FnfTuBOAUDzIe0D";
	 
	public RestClient(){
		 HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(userName, password);
		 client = ClientBuilder.newClient();
		 client.register(feature);
		 baseTarget = client.target(REST_URI);
		 
		 //Authentication		 		 
		 authRequestTarget = baseTarget.path("auth");
		 
		 getRequestTarget = baseTarget.path("users");
		 postRequestTarget = baseTarget.path("save");
	}
		
	public static void main(String[] args) {
	
	RestClient rc = new RestClient();
	
	rc.testAuthentication();
	
//	List<Order> orders = rc.getOrders();
 //	Boolean postRequestStatus = rc.postOrder(orders);
	
	}
	
	public void testAuthentication() {

		 System.out.println("Before Authentication");

		 Response response = authRequestTarget
			     .request(MediaType.APPLICATION_JSON)
			     .get();	

		 System.out.println(response);

		 System.out.println(response.getStatus());
	}

	
	private List<Order> getOrders()
	{	 
		 Response response = getRequestTarget
	     .request(MediaType.APPLICATION_JSON)
	     .get();	

		 //save get response to a file 
		 
	    List<Order> orders = response.readEntity(new GenericType<List<Order>>() {});
	    return orders;
	    
	}

	private Boolean postOrder(List<Order> orders)
	{  for (Order order : orders) {
	 		Order ResponseOrder = createFinancialOrder(order);
	 		Response postResponse = postRequestTarget
	 				.request()
	 				.post(Entity.json(ResponseOrder));
	 		//postResponse.getStatus();
	 		 		
	 		if ((int)postResponse.getStatus()/100 == 2) {
		 		System.out.println(postResponse.getStatus());
	 			 			
	 		}
	 		else {
	 			System.out.println("(int)postResponse.getStatus()/100"+(int)postResponse.getStatus()/100);
		 			
	 		}
	 	}
	
		return true;
	}
	
	private Order createFinancialOrder(Order order)
	{
		OrderProcessing op = new OrderProcessing(order);
		
		op.processOrder();
		Order responseOrder = op.getProcessedOrder();
		
		List<Item> items = responseOrder.getItems();
		double tax = responseOrder.getTaxes();
		double shipping = responseOrder.getShipping();

		System.out.println("Order Id:"+responseOrder.getOrder_id());
		System.out.println("tax:"+tax);
		System.out.println("shipping:"+shipping);
		System.out.println("-----------------------------------------");
		
		for (Item item : items) {
			System.out.println("item:"+item.getItem());
			System.out.println("Msrp:"+item.getMsrp());
			System.out.println("Amount:"+item.getAmount());
			System.out.println("Quantity:"+item.getQuantity());
			System.out.println("Retail_price:"+item.getRetail_price());
			System.out.println("Shipping:"+item.getShipping());
			System.out.println("Taxes:"+item.getTaxes());
			System.out.println("------------------------------------");			
		}

	return responseOrder;	
	}
	
}



