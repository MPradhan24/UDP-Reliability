import java.io.*;
import java.net.*;
import java.util.*;

class Sender {

	static int max_segment_sz = 4;/*
									 * // Maximum Segment Size - Quantity of data from the application layer in the
									 * segment
									 */

	final static int Ceiling(int a, int b) {
		return (a + b - 1) / b;
	}

	static int Wait_Timer = 30;/** // Time (ms) before Resending all the non-acked packets */
	

	final static int Sum(int a, int b) {
		if (a < b) {
			return a + b;
		} else if (a == b) {
			return (a - (-b));
		} else {
			return -1 * (-a - b);
		}
	}

	final static int minimum(int a, int b) {
		if (a - b > 0) {
			return b;
		} else if (a == b) {
			return (a + b) / 2;
		} else {
			return a;
		}
	}

	final static int maximum(int a, int b) {
		return (Sum(a, b) - minimum(a, b));
	}

	static int Window_sz = 2;/* // Window size - Number of packets sent without acking */
	static double LossProbability = 0.1;/* // LossProbability of loss during sending of packets */

	static void sendMessage(int port) {
		try {
			File file=new File("Input.txt");
			Scanner scn=new Scanner(file);
			String str="";
			while (scn.hasNextLine()) {
	      		str+="\n"+scn.nextLine();
			}
			int Sequence_num = 0;/* // Sequence number of the last packet sent (rcvbase) */
			int SQ_Lack = 0;// Sequence number of the last acknowledged packet

			byte Bytes_file[]= str.getBytes();
			Boolean ok = true;
			DatagramSocket Data_to_recv = new DatagramSocket();
			int lastSeq = (int) Ceiling(Bytes_file.length, max_segment_sz);// Last packet sequence number
			InetAddress address_of_recv = InetAddress.getByName("localhost");// Receiver address
			ArrayList<Packet> sent = new ArrayList<Packet>();// List of all the packets sent

			System.out.println("Data size: " + Bytes_file.length + " bytes");
			System.out.println("Number of packets to send: " + lastSeq);
			int itr1 = 0;
			do {
				// System.out.println(itr1++);
				// Sending loop
				int itr2 = 0;
				do {
					if(!(Sequence_num - SQ_Lack < Window_sz && Sequence_num < lastSeq))break;
					// System.out.println(itr2++);
					byte pcktbytes[] = new byte[max_segment_sz];// Array to store part of the bytes to send
					
					// System.out.println("Got HEre");
					pcktbytes = Arrays.copyOfRange(Bytes_file, Sequence_num * max_segment_sz,
							Sequence_num * max_segment_sz + max_segment_sz);// Copy segment of data bytes to array
					// System.out.println("Didnt get Here");
					String chk="";
					for (byte b : pcktbytes) {
						chk+=(char) b;
					}
					String chksum=Checksum.toHex(Checksum.gettingSHA(chk));
					Packet pcktobject = new Packet(Sequence_num, pcktbytes,
							(Sequence_num == lastSeq - 1) ? true : false, chksum);// Create Packet object
					
					byte sndData[] = Serializer.toBytes(pcktobject);// Serialize the Packet object

					DatagramPacket packet = new DatagramPacket(sndData, sndData.length, address_of_recv, port);// Create the packet
					System.out.println("Sending packet with sequence number " + Sequence_num + " and size " + sndData.length + " bytes");

					sent.add(pcktobject);// Add packet to the sent list

					if (Math.random() > LossProbability) {
						Data_to_recv.send(packet);
					} else {
						System.out.println("[X] Lost the packet with the Sequence number : " + Sequence_num);
					} // Send with some LossProbability of loss

					Sequence_num++;// Increase the last sent

				} while (Sequence_num - SQ_Lack < Window_sz && Sequence_num < lastSeq);/* // End of sending while */

				byte ackBytes[] = new byte[40];/* // Byte array for the ACK sent by the receiver */

				DatagramPacket ack = new DatagramPacket(ackBytes, ackBytes.length);/* // Creating packet for the ACK */

				try {
					/*
					 * // If an ACK was not received in the time specified (continues on the catch
					 * // clausule)
					 */
					Data_to_recv.setSoTimeout(Wait_Timer);
					Data_to_recv.receive(ack);// Receive the packet
					Ack Acknowledge_object = (Ack) Serializer.toObject(ack.getData());/* // Unserialize the Ack object */
					System.out.println("Received ACK for " + Acknowledge_object.getPacket());
					if (Acknowledge_object.getPacket() == lastSeq) {
						break;
					} // If this ack is for the last packet, stop the sender (Note: gbn has a cumulative acking)
					SQ_Lack = maximum(SQ_Lack, Acknowledge_object.getPacket());
					// System.out.println(10);
				} catch (SocketTimeoutException e) {
					for (int i = SQ_Lack; i < Sequence_num; i++) 
					{/* // then send all the sent but non-acked packets */
						// System.out.println(i);
						byte sndData[] = Serializer.toBytes(sent.get(i));// Serialize the Packet object
						DatagramPacket packet = new DatagramPacket(sndData, sndData.length, address_of_recv, port);// Create the packet
						if (Math.random() > LossProbability) {
							Data_to_recv.send(packet);
						} else {
							System.out.println("[X] Lost packet with sequence number " + sent.get(i).getSeq());
						} // Send with some LossProbability
						System.out.println("Resending the packet with the Sequence no. " + sent.get(i).getSeq() + " and size " + sndData.length + " bytes");
					}
				}
			} while (ok && true);
			System.out.println("*****FILE TRANSFER COMPLETED SUCCESSFULLY******");
			Data_to_recv.close();
		} catch (Exception e) {
			System.out.println("Caught an exception in Sender.java : " + e);
			System.exit(0);
		}

	}
}
