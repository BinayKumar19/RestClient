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
import com.google.gson.Gson;

import logic.ObjectSerialization;
import logic.OrderProcessing;
import model.Item;
import model.Order;

public class RestClient {

	private static final String REST_URI = "https://ops-interview.p.fullscript.io/";
	Client client;
	WebTarget baseTarget;
	WebTarget getRequestTarget;
	WebTarget postRequestTarget;
	WebTarget authRequestTarget;
	WebTarget startTestTarget;
	WebTarget reStartTestTarget;
	WebTarget scoreTarget;

	String userName = "user_43712";
	String password = "G45hDvzW5FnfTuBOAUDzIe0D";

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
			if (operation.equals("3")) {
				orders = rc.getOrders();
				System.out.println("size of orders :" + orders.size());

				// save get response to a file (Incase of Failure)
				ObjectSerialization.serializeObjects(orders);
			} else {
				System.out.print("Enter your File name From which JSON data is to be loaded:");
				String fileName = in.nextLine();
				orders = ObjectSerialization.deserializeObjects(fileName);
			}

			rc.postOrder(orders);

		}
		in.close();
	}

	public void testAuthentication() {
		Response response = authRequestTarget.request(MediaType.APPLICATION_JSON).get();

		System.out.println(response);
	}

	public void startTest() {
		Response response = this.startTestTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(null));
		System.out.println(response);
	}

	public void reStartTest() {
		Response response = this.reStartTestTarget.request(MediaType.APPLICATION_JSON).post(Entity.json(null));
		System.out.println(response);
	}

	public void getScore() {
		Response response = this.scoreTarget.request(MediaType.APPLICATION_JSON).get();
		System.out.println(response);
	}

	private List<Order> getOrders() {
		Response response = getRequestTarget.request(MediaType.APPLICATION_JSON)
				// .request()
				.get();

		String res = response.readEntity(String.class);
		System.out.println("status code for Get request:" + response.getStatus());
		System.out.println("response:" + res);

		List<Order> orders2 = null, orders3 = new ArrayList<Order>();
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			orders2 = objectMapper.readValue(res, orders3.getClass());
			orders3 = objectMapper.convertValue(orders2, new TypeReference<List<Order>>() {
			});

		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// List<Order> orders = response.readEntity(new GenericType<List<Order>>() {});
		return orders3;

	}

	private void postOrder(List<Order> orders) {
		OrderProcessing orderProcessing;

		for (Order order : orders) {
			orderProcessing = new OrderProcessing(order);
			orderProcessing.createFinancialOrder();
			Order responseOrder = orderProcessing.getProcessedOrder();

			Gson gson = new Gson();
			String ResponseOrderJson = gson.toJson(responseOrder);
			System.out.println("ResponseOrderJson:" + ResponseOrderJson);

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
