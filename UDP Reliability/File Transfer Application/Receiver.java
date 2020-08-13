import java.net.*;
import java.util.*;
import java.io.*;

public class Receiver {

	final static int IncreaseCounter(int n){
		return n+1;
	}

	public static final double probability_ackloss = 0.1;// probability_ackloss of ACK loss

	static void rcvMessage(int port) {
		try{
			DatagramSocket From_the_Sender = new DatagramSocket(port);
			byte receivedData[] = new byte[Sender.max_segment_sz + 200];// 200 is the base size (in bytes) of a serialized Packet object
			int waitingFor = 0;
			ArrayList<Packet> received = new ArrayList<Packet>();
			boolean end = false;
			do{
				System.out.println("Waiting for packet");

				DatagramPacket receivedPacket = new DatagramPacket(receivedData, receivedData.length);// Receive packet
				From_the_Sender.receive(receivedPacket);

				Packet packet = (Packet) Serializer.toObject(receivedPacket.getData());// Unserialize to a Packet object

				System.out.println("Packet with sequence number " + packet.getSeq() + " received (last: " + packet.isLast() + " )");

				String chk="";
				for (byte b : packet.getData()) {
					chk+=(char) b;
				}
				String chksum=Checksum.toHex(Checksum.gettingSHA(chk));
				if(!chksum.equals(packet.getCheckSum())){
					System.out.println("Wrong Checksum,Packet Corrupt,Dropped" + chksum +" VS." +packet.getCheckSum());
					continue;
				}
				else{
					System.out.println("Correct Checksum: "+chksum);
				}

				if (packet.getSeq() == waitingFor && packet.isLast()) {
					waitingFor = IncreaseCounter(waitingFor);
					received.add(packet);
					System.out.println("Last packet received");
					end = true;
				} else if (packet.getSeq() == waitingFor) {
					waitingFor = IncreaseCounter(waitingFor);
					received.add(packet);
					System.out.println("Packed stored in buffer");
				} else {
					System.out.println("Packet discarded (not in order)");
				}

				Ack ackObject = new Ack(waitingFor);// Create an Ack object
				byte ackBytes[] = Serializer.toBytes(ackObject);// Serialize
				DatagramPacket ackPacket = new DatagramPacket(ackBytes, ackBytes.length, receivedPacket.getAddress(),receivedPacket.getPort());
				if (Math.random() > probability_ackloss) {
					From_the_Sender.send(ackPacket);
				} else {
					System.out.println("[X] Lost ack with sequence number " + ackObject.getPacket());
				} // Send with some probability_ackloss of loss

				System.out.println("Sending ACK to seq " + waitingFor + " with " + ackBytes.length + " bytes");
			}while(true && !end);

			// Print the data received
			// System.out.println(" ********DATA OBTAINED********");

			FileWriter fw=new FileWriter("Output.txt");
			for(Packet p : received){
				for(byte b: p.getData()){
					fw.write((char) b);
				}
			}
			fw.close();
			From_the_Sender.close();
			System.out.println(" ********FILE TRANSFER COMPLETED SUCCESSFULLY********");
		}
		catch(Exception ex){
			System.out.println("Exception in Receiver.java"); 
		}
	}

}
