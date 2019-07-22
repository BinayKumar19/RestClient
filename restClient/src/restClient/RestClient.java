package restClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import logic.ObjectSerialization;
import logic.OrderProcessing;
import model.Order;

/**
 * RestClient class: This class is used to handle interaction with the server.
 * 
 * @author binay
 *
 */
public class RestClient {

	private static final String REST_URI = "https://ops-interview.p.fullscript.io/";
	private Client client;
	private WebTarget baseTarget;
	private WebTarget getRequestTarget;
	private WebTarget postRequestTarget;
	private WebTarget authRequestTarget;
	private WebTarget startTestTarget;
	private WebTarget reStartTestTarget;
	private WebTarget scoreTarget;

	// UserName and password specific to the Fullscript server
	private String userName = "user_43712";
	private String password = "G45hDvzW5FnfTuBOAUDzIe0D";

	public RestClient() {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(userName, password);
		client = ClientBuilder.newClient();
		client.register(feature);
		baseTarget = client.target(REST_URI);

		// Authentication
		authRequestTarget = baseTarget.path("auth");

		startTestTarget = baseTarget.path("start");
		reStartTestTarget = baseTarget.path("restart");
		scoreTarget = baseTarget.path("score");

		getRequestTarget = baseTarget.path("orders");
		postRequestTarget = baseTarget.path("orders");
	}

	public static void main(String[] args) {

		RestClient rc = new RestClient();
		List<Order> orders;
		Scanner in = new Scanner(System.in);

		System.out.println("What operation do you want to perform?");
		System.out.println("1 - Authenticate");
		System.out.println("2 - Start the Test");
		System.out.println("3 - Get from Server and then Post to server");
		System.out.println("4 - Read Last Get from a JSON file and then Post to server");
		System.out.println("5 - score");
		System.out.println("6 - Re-start");

		String operation = in.nextLine();

		if (operation.equals("1")) {
			rc.testAuthentication();
		} else if (operation.equals("2")) {
			rc.startTest();
		} else if (operation.equals("5")) {
			rc.getScore();
		} else if (operation.equals("6")) {
			rc.reStartTest();
		} else {
			if (operation.equals("3")) { // receive list of orders from a GET Request
				orders = rc.getOrders();
				System.out.println("size of orders :" + orders.size());

				// save get response to a file (incase of a failure)
				ObjectSerialization.serializeObjects(orders);

			} else { // Fetch list of orders for the last GET from a file.
				System.out.print("Enter your File name From which JSON data is to be loaded:");
				String fileName = in.nextLine();
				orders = ObjectSerialization.deserializeObjects(fileName);
			}

			// Send post request
			rc.postOrder(orders);

		}
		in.close();
	}

	/**
	 * Method to send the get request to the server to test the authentication.
	 */
	public void testAuthentication() {
		Response response = this.authRequestTarget.request(MediaType.APPLICATION_JSON).get();

		System.out.println(response);
	}

	/**
	 * Method to send the Post request to the server to start the test.
	 */
	public void startTest() {
		Response response = this.startTestTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(null));
		System.out.println(response);
	}

	/**
	 * Method to send the Post request to the server to re-start the test.
	 */
	public void reStartTest() {
		Response response = this.reStartTestTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(null));
		System.out.println(response);
	}

	/**
	 * Method to send the Get request to the server to get the score of the test.
	 */
	public void getScore() {
		Response response = this.scoreTarget.request(MediaType.APPLICATION_JSON).get();
		System.out.println(response);
	}

	/**
	 * Method to send GET request to the server and return a list of orders.
	 * 
	 * @return List of orders
	 */
	private List<Order> getOrders() {
		Response response = getRequestTarget.request(MediaType.APPLICATION_JSON).get();

		// Receive the response as a String.
		String res = response.readEntity(String.class);
		System.out.println("status code for Get request:" + response.getStatus());
		System.out.println("response:" + res);

		List<Order> orders2 = null, orders3 = new ArrayList<Order>();
		try {
			// Convert Response String into a list of orders
			ObjectMapper objectMapper = new ObjectMapper();
			orders2 = objectMapper.readValue(res, orders3.getClass());
			orders3 = objectMapper.convertValue(orders2, new TypeReference<List<Order>>() {
			});

		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return orders3;

	}

	/**
	 * Method to create a financial order and send it to the server as a POST
	 * request.
	 * 
	 * @param orders
	 *            List of orders fetched from the server.
	 */
	private void postOrder(List<Order> orders) {
		OrderProcessing orderProcessing;
		Order responseOrder;

		for (Order order : orders) {
			orderProcessing = new OrderProcessing(order);
			orderProcessing.createFinancialOrder();
			responseOrder = orderProcessing.getProcessedOrder();

			Response postResponse = postRequestTarget.request().post(Entity.json(responseOrder));

			String responseBody = postResponse.readEntity(String.class);
			System.out.println("Post Respone Body:" + responseBody);

			if ((int) postResponse.getStatus() == 200) {
				System.out.println("Success for " + order.getOrder_id());
			} else {
				System.out.println("Failure for " + order.getOrder_id());
			}
		}

	}

}
