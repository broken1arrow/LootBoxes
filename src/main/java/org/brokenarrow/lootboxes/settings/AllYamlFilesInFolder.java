package org.brokenarrow.lootboxes.settings;

import org.brokenarrow.lootboxes.Lootboxes;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Get all files inside resorurses inside a folder and create if it not exist.
 */

public class AllYamlFilesInFolder {

	private final String folderName;
	private final boolean shallGenerateFiles;
	private final Lootboxes plugin;

	public AllYamlFilesInFolder(String folderName, boolean shallGenerateFiles) {
		this.folderName = folderName;
		this.shallGenerateFiles = shallGenerateFiles;
		this.plugin = Lootboxes.getInstance();
	}

	public void reload() {
	}

	public File[] getAllFiles() {
		Map<String, File> map = new HashMap<>();
		List<String> filenamesFromDir = null;
		try {
			filenamesFromDir = getFilenamesForDirnameFromCP(this.folderName);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		File[] files = getYamlFiles(this.folderName, "yml");

		if (shallGenerateFiles) {
			int conter = 0;
			for (File file : files) {
				map.put(file.getName().replace(".yml", ""), file);
			}


			if (filenamesFromDir != null && (!map.isEmpty() || files.length == 0)) {
				for (String file : filenamesFromDir) {

					if (map.get(getFileName(file)) == null) {
						this.plugin.saveResource(file, false);
						conter++;
					}

					if (conter + 1 > filenamesFromDir.size())
						map.clear();
				}
			}
		}
		return files;
	}
	
	public List<String> getFolders() {
		List<String> filenamesFromDir = null;
		try {
			filenamesFromDir = getFilenamesForDirnameFromCP(this.folderName);
		} catch (URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		assert filenamesFromDir != null;

		return new ArrayList<>(filenamesFromDir);
	}

	public boolean checkFolderExist(String fileToSave, File[] dataFolders) {
		if (fileToSave != null)
			for (File file : dataFolders) {
				String fileName = getFileName(file.getName());
				if (fileName.equals(fileToSave))
					return true;
			}
		return false;
	}

	public File[] getYamlFiles(String directory, String extension) {
		if (extension.startsWith("."))
			extension = extension.substring(1);

		final File dataFolder = new File(this.plugin.getDataFolder(), directory);

		if (!dataFolder.exists())
			dataFolder.mkdirs();

		final String finalExtension = extension;

		return dataFolder.listFiles(file -> !file.isDirectory() && file.getName().endsWith("." + finalExtension));
	}

	public String getFileName(String path) {
		Valid.checkBoolean(path != null && !path.isEmpty(), "The given path must not be empty!");
		int pos;

		if (path.lastIndexOf("/") == -1)
			pos = path.lastIndexOf("\\");
		else
			pos = path.lastIndexOf("/");

		if (pos > 0)
			path = path.substring(pos + 1);

		pos = path.lastIndexOf(".");

		if (pos > 0)
			path = path.substring(0, pos);
		return path;
	}

	public List<String> getFilenamesForDirnameFromCP(String directoryName) throws URISyntaxException, IOException {
		List<String> filenames = new ArrayList<>();

		URL url = this.plugin.getClass().getClassLoader().getResource(directoryName);

		if (url != null) {
			if (url.getProtocol().equals("file")) {
				File file = Paths.get(url.toURI()).toFile();
				if (file != null) {
					File[] files = file.listFiles();
					if (files != null) {
						for (File filename : files) {
							filenames.add(filename.toString());
						}
					}
				}
			} else if (url.getProtocol().equals("jar")) {
				String dirname = directoryName + "/";
				String path = url.getPath();
				String jarPath = path.substring(5, path.indexOf("!"));
				try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8.name()))) {
					Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						JarEntry entry = entries.nextElement();
						String name = entry.getName();
						//System.out.println("name " + name + "entry " + entry + jarPath);
						if (name.startsWith(dirname) && !dirname.equals(name)) {
							URL resource = this.plugin.getClass().getClassLoader().getResource(name);
							if (resource != null) {
								filenames.add(name);
							} else
								this.plugin.getLogger().warning("Missing files in plugins/" + this.plugin + ".jar/" + directoryName + "/, contact the author of " + this.plugin.getName() + ".");
						}
					}
				}
			}
		}
		return filenames;
	}


	private static class Valid extends RuntimeException {
		public static void checkBoolean(boolean b, String s) {
			if (!b)
				throw new CatchExceptions(s);
		}

		private static class CatchExceptions extends RuntimeException {
			public CatchExceptions(String message) {
				super(message);
			}
		}
	}
}