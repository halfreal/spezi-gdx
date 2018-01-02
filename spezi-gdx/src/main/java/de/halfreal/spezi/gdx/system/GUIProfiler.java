package de.halfreal.spezi.gdx.system;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.halfreal.spezi.gdx.math.Timer;
import de.halfreal.spezi.gdx.math.Timer.Task;

public class GUIProfiler {

	public static class Profile {

		public int accessCount;

		public HashSet<String> screens;

		public Profile() {
			screens = new HashSet<String>();
			accessCount = 0;
		}

	}

	private static GUIProfiler instance;

	private static Logger log = LoggerFactory.getLogger(GUIProfiler.class);

	public static GUIProfiler instance() {
		if (instance == null) {
			instance = new GUIProfiler();
		}

		return instance;
	}

	private HashMap<String, Profile> profileMap = new HashMap<String, GUIProfiler.Profile>();

	private boolean profiling = System.getProperty("profiling") != null;

	private String profilingFile = System.getProperty("profilingFile");

	public GUIProfiler() {
		Task writeProfile = new Task() {

			@Override
			public void runUI() {

				TreeSet<Entry<String, Profile>> sortedProfileData = new TreeSet<Entry<String, Profile>>(
						new Comparator<Entry<String, Profile>>() {

							@Override
							public int compare(Entry<String, Profile> o1,
									Entry<String, Profile> o2) {
								int diff = o2.getValue().screens.size()
										- o1.getValue().screens.size();
								if (diff != 0) {
									return diff;
								} else {
									return o2.getKey().compareTo(o1.getKey());
								}
							}
						});
				sortedProfileData.addAll(profileMap.entrySet());

				StringBuffer sb = new StringBuffer();

				for (Entry<String, Profile> entry : sortedProfileData) {
					sb.append(entry.getKey());
					sb.append("\t").append(entry.getValue().accessCount);
					sb.append("\t");
					sb.append(entry.getValue().screens.size());
					sb.append("\n");
				}

				if (profilingFile == null) {
					profilingFile = "profile.cvs";
				}

				try {
					FileWriter fileWriter = new FileWriter(profilingFile);
					fileWriter.write(sb.toString());
					fileWriter.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		if (profiling) {
			Timer.schedule(writeProfile, 2f, 2f);
		}

	}

	public void assetAccess(String assetName, Class<?> clazz) {
		// TODO
	}

	private String decapitalize(String simpleName) {
		return simpleName.substring(0, 1).toLowerCase()
				+ simpleName.substring(1);
	}

	public Set<String> searchForRefs(String... refs) {
		Set<String> realRefs = new HashSet<String>();

		// TODO
		// for (String ref : refs) {
		//
		// ST style = null;
		// for (ST st : ST.values()) {
		// if (st.key().equals(ref)) {
		// style = st;
		// break;
		// }
		// }
		//
		// if (style != null && style.refs() != null
		// && !style.name().contains("textureRegion")) {
		// Set<String> searchForRefs = searchForRefs(style.refs());
		// realRefs.addAll(searchForRefs);
		// } else if (style != null && style.refs() != null
		// && style.name().contains("textureRegion")) {
		// realRefs.addAll(Arrays.asList(style.refs()));
		// } else if (style != null && style.name().contains("textureRegion")) {
		// realRefs.add(ref);
		// } else {
		// // realRefs.add(ref);
		// System.err.println("not added: " + ref);
		// }
		//
		// }
		return realRefs;
	}
}
