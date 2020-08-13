import java.io.*;

public class Ack implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 5957675253229339934L;
	private int pkt;

	public Ack(int pkt) {
		super();
		this.pkt = pkt;
	}

	public int getPacket() {
		return pkt;
	}

	public void setPacket(int pkt) {
		this.pkt = pkt;
	}

}
