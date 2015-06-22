package Models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import DataSource.DatabaseDriver;

import Socket.*;

public class Dispositivo {

	public int id;
	private boolean isActive;
	private String modello;
	static Configurazione config;
	DatabaseDriver dd;
	private ServerSocket ss;
	private int socketport;
	private String ip = "localhost";
	private static Dispositivo dis;

	/*
	 * RilevatorePosiz rilPos; GestoreMessaggi gestMex;
	 */

	private Dispositivo(Integer id) throws IOException {
		this.id = id;
		ss = new ServerSocket(0);
		config = new Configurazione();
		dd = DatabaseDriver.getInstance();
		dd.openConnection();
		dd.setPort(id, "localhost", ss.getLocalPort());
		dd.closeConnection();
		this.update();
	}

	public static void Instantiate(int i) {
		try {
			dis = new Dispositivo(i);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Dispositivo getInstance() {
		return dis;
	}

	public void update() {

		try (Socket socket = new Socket("localhost", 4000);
				PrintWriter out = new PrintWriter(socket.getOutputStream(),
						true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));) {
			out.write(id + "\n");
			out.flush();
			while (!in.ready()) {
			}

			config.setfPos(Integer.parseInt(in.readLine()));
			config.setfSms(Integer.parseInt(in.readLine()));
			config.setspeedAlarm(Integer.parseInt(in.readLine()));
			config.setEmailEnabled(Boolean.parseBoolean(in.readLine()));
			config.setSmsEnabled(Boolean.parseBoolean(in.readLine()));

			socket.close();

		} catch (IOException e) {
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getModello() {
		return modello;
	}

	public void setModello(String modello) {
		this.modello = modello;
	}

	public static Configurazione getConfig() {
		return config;
	}

	public static void setConfig(Configurazione config) {
		Dispositivo.config = config;
	}

	public DatabaseDriver getDd() {
		return dd;
	}

	public void setDd(DatabaseDriver dd) {
		this.dd = dd;
	}

	public ServerSocket getSs() {
		return ss;
	}

	public void setSs(ServerSocket ss) {
		this.ss = ss;
	}

	public int getSocketport() {
		return socketport;
	}

	public void setSocketport(int socketport) {
		this.socketport = socketport;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public static void main(String[] args) {
		if (args.length == 1) {
			Dispositivo.Instantiate(Integer.parseInt(args[0]));
			System.out.println("avvio dispositivo "+args[0]);
			new Thread(new SocketConfigurazione(Dispositivo.getInstance()
					.getSs())).start();
			new Thread(new RilevamentoPosizione()).start();
		}
		else{
			System.out.println("errore");
		}

	}
	/*
	 * public void main() { // TODO Auto-generated method stub // update();
	 * System.out.println("Configurazione: fpos:" + config.getfPos());
	 * System.out.println("dispositivo id:" + id + " ricevere mex=" +
	 * config.isSmsEnabled());
	 * 
	 * new Thread(new SocketConfigurazione(ss, this)).start(); new Thread(new
	 * RilevamentoPosizione(this)).start();
	 * 
	 * while (true) { try { Thread.sleep(6000); } catch (InterruptedException e)
	 * { // TODO Auto-generated catch block e.printStackTrace(); } } }
	 */
}
