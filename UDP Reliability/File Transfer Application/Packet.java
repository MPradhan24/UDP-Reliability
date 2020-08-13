import java.io.*;
import java.util.*;

public class Packet implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	int seq;

	byte[] data;

	boolean last;

	String checkSum;

	Packet(int seq, byte[] data, boolean last,String checkSum) {
		super();
		this.seq = seq;
		this.data = data;
		this.last = last;
		this.checkSum=checkSum;
	}

	void setSeq(int seq) {
		this.seq = seq;
	}

	int getSeq() {
		return seq;
	}

	void setData(byte[] data) {
		this.data = data;
	}

	byte[] getData() {
		return data;
	}

	void setLast(boolean last) {
		this.last = last;
	}

	boolean isLast() {
		return last;
	}

	void setCheckSum(String checkSum){
		this.checkSum=checkSum;
	}

	String getCheckSum(){
		return checkSum;
	}

	@Override
	public String toString() {
		return "UDPPacket [seq=" + seq + ", data=" + Arrays.toString(data) + ", last=" + last + ",checksum="+checkSum+ "]";
	}

}
