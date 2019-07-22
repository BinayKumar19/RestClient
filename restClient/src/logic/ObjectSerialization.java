package logic;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import model.Order;

/**
 * ObjectSerialization class to save the GET request response to the file and
 * read from the file, will be used if POST response got unsuccessful.
 * 
 * @author binay
 *
 */
public class ObjectSerialization {

	/**
	 * Method to save the list of orders received from the server to a file.
	 * 
	 * @param orders
	 *            List of orders.
	 */
	public static void serializeObjects(List<Order> orders) {
		try {
			String fileName = "Get_Response_" + System.currentTimeMillis() + ".txt";
			FileOutputStream f = new FileOutputStream(new File(fileName));
			ObjectOutputStream o = new ObjectOutputStream(f);

			// Write objects to file
			for (Order order : orders) {
				o.writeObject(order);
			}

			o.close();
			f.close();

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Method to read list of orders from a given file.
	 * 
	 * @param fileName
	 *            File from which list of orders to be read
	 * @return List of orders read from the given file.
	 */
	public static List<Order> deserializeObjects(String fileName) {
		List<Order> orders = new ArrayList<>();
		try {

			FileInputStream fi = new FileInputStream(new File(fileName));
			ObjectInputStream oi = new ObjectInputStream(fi);

			boolean cont = true;
			while (cont) {
				Order order = (Order) oi.readObject();
				if (order != null)
					orders.add(order);
				else
					cont = false;
			}

			oi.close();
			fi.close();

		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
		} catch (EOFException e) {
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		return orders;
	}

}
