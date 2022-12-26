import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server{

	int count = 1;
	ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
	TheServer server;
	private Consumer<Serializable> callback;
	ArrayList<Integer> cls = new ArrayList<Integer>();
	messageInfo info = new messageInfo();


	Server(Consumer<Serializable> call){

		callback = call;
		server = new TheServer();
		server.start();
	}


	public class TheServer extends Thread{

		public void run() {

			try(ServerSocket mysocket = new ServerSocket(5555);){
				System.out.println("Server is waiting for a client!");


				while(true) {

					ClientThread c = new ClientThread(mysocket.accept(), count);
					info.msg = "client has connected to server: " + "client #" + count;
					callback.accept(info);
					clients.add(c);
					cls.add(count);
					c.start();

					count++;

				}
			}//end of try
			catch(Exception e) {
				messageInfo info = new messageInfo();
				info.msg = "Server socket did not launch";
				callback.accept(info);
			}
		}//end of while
	}


	class ClientThread extends Thread{


		Socket connection;
		int count;
		ObjectInputStream in;
		ObjectOutputStream out;

		ClientThread(Socket s, int count){
			this.connection = s;
			this.count = count;
		}

		public synchronized void updateClients(messageInfo message) {
			messageInfo info = new messageInfo();
			info = message;
			info.allClients = cls;


			for(int i = 0; i < clients.size(); i++) {
				ClientThread t = clients.get(i);

				if (info.listclicked) {
					if (info.clickedClients.contains(t.count)) {
						try {
							t.out.reset();
							t.out.writeObject(info);
						}
						catch(Exception e) {}
					}
				}
				else {
					try {
						t.out.reset();
						t.out.writeObject(info);
					}
					catch(Exception e) {}
				}
			}
		}

		public void run(){

			try {
				in = new ObjectInputStream(connection.getInputStream());
				out = new ObjectOutputStream(connection.getOutputStream());
				connection.setTcpNoDelay(true);
			}
			catch(Exception e) {
				System.out.println("Streams not open");
			}

			info.msg = "new client on server: client #" + count;
			updateClients(info);

			while(true) {
				try {
					messageInfo data = (messageInfo) in.readObject();
					data.msg = "client: " + count + " sent " + data.msg;
					callback.accept(data);
					updateClients(data);

				}
				catch(Exception e) {
					info.msg = "OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!";
					callback.accept(info);
					info.msg = "Client #" + count + " has left the server!";
					updateClients(info);
					cls.remove(count);
					clients.remove(this);
					break;
				}
			}
		}//end of run

	}//end of client thread
}