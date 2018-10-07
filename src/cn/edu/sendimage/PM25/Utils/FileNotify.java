package cn.edu.sendimage.PM25.Utils;

import java.io.File;

import net.contentobjects.jnotify.JNotify;

public class FileNotify {
	public static void notify(String[] filepath) throws Exception {
		
		Listener listen =  new Listener();
		for (String str : filepath) {
			if (!new File(str).exists()) {
				return;
			}

			// path to watch
			// String path = System.getProperty("user.home");
			// String path1 = "F:\\PM25";

			// watch mask, specify events you care about,
			// or JNotify.FILE_ANY for all events.
			int mask = JNotify.FILE_CREATED | JNotify.FILE_DELETED | JNotify.FILE_MODIFIED | JNotify.FILE_RENAMED;

			// watch subtree?
			boolean watchSubtree = true;

			// add actual watch
			int watchID = JNotify.addWatch(str, mask, watchSubtree, listen);
			System.out.println(watchID);
		}

		// sleep a little, the application will exit if you
		// don't (watching is asynchronous), depending on your
		// application, this may not be required
		while (true) {
			Thread.sleep(1000 * 60 * 60 * 24);
		}

		// to remove watch the watch
		// boolean res = JNotify.removeWatch(watchID);
		// if (!res) {
		// // invalid watch ID specified.
		// }
	}

}
