package de.halfreal.spezi.gdx.system;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectHelper {

	public static <T extends Serializable> T clone(T serializable) {
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(serializable);
			oos.close();
			byte[] buf = bos.toByteArray();
			ObjectInputStream ois = new ObjectInputStream(
					new ByteArrayInputStream(buf));
			@SuppressWarnings("unchecked")
			T clone = (T) ois.readObject();
			ois.close();
			return clone;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
