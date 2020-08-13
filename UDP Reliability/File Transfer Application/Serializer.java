import java.io.*;

class Serializer {
	
	public static byte[] toBytes(Object obj) {
		try{
			ByteArrayOutputStream b = new ByteArrayOutputStream();
			ObjectOutputStream o = new ObjectOutputStream(b);
			o.writeObject(obj);
			return b.toByteArray();
		}
		catch(IOException ex){
			System.out.println("Exception in toBytes Function in Serializer.java:"+ex); 	
		}
		return null;
	}

	public static Object toObject(byte[] bytes) {
		try{
			ByteArrayInputStream b = new ByteArrayInputStream(bytes);
			ObjectInputStream o = new ObjectInputStream(b);
			return o.readObject();
		}
		catch(IOException | ClassNotFoundException ex ){
			System.out.println("Exception in toObject Function in Serializer.java:"+ex); 			
		}
		return bytes;
	}
}
