package org.brokenarrow.lootboxes.settings;

import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

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
 * Helper clas to load and save data. You get data from one or several files and can save it also
 * have a serialize method you can use .
 */

public abstract class SimpleYamlHelper {

	private final String name;
	private final String yamlMainpath;
	private final boolean shallGenerateFiles;
	private final boolean singelFile;
	private boolean firstLoad = true;
	private FileConfiguration customConfig;
	private String extension;
	private File customConfigFile;
	protected final Plugin plugin = Lootboxes.getInstance();
	private final File dataFolder;

	public SimpleYamlHelper(final String name, final boolean shallGenerateFiles) {
		this(name, "", false, shallGenerateFiles);
	}

	public SimpleYamlHelper(final String name, final String yamlMainpath, final boolean shallGenerateFiles) {
		this(name, yamlMainpath, false, shallGenerateFiles);
	}

	public SimpleYamlHelper(final String name, final String yamlMainpath, final boolean singelFile, final boolean shallGenerateFiles) {
		if (this.plugin == null)
			throw new RuntimeException("You have not set the plugin, becuse it is null");
		this.dataFolder = this.plugin.getDataFolder();
		this.singelFile = singelFile;
		this.name = this.checkIfFileHasExtension(name);
		this.shallGenerateFiles = shallGenerateFiles;
		this.yamlMainpath = yamlMainpath;
	}

	public abstract void saveDataToFile(final File file);

	protected abstract void loadSettingsFromYaml(final File file);

	public FileConfiguration getCustomConfig() {
		return customConfig;
	}

	public File getCustomConfigFile() {
		return customConfigFile;
	}

	/**
	 * Check if the file name missing extension.
	 *
	 * @param name of the file.
	 * @return name with extension added if it is missing.
	 */
	public String checkIfFileHasExtension(final String name) {
		Valid.checkBoolean(name != null && !name.isEmpty(), "The given path must not be empty!");
		if (!isSingelFile())
			return name;
		final int pos = name.lastIndexOf(".");
		if (pos == -1)
			return name + "." + this.getExtension();
		this.setExtension(name.substring(pos));
		return name;
	}

	/**
	 * Get the extension of a file.
	 *
	 * @return the extension without the dot.
	 */
	public String getExtension() {
		if (this.extension == null) {
			return "yml";
		} else {
			String extension = this.extension;
			if (extension.startsWith("."))
				extension = extension.substring(1);
			return extension;
		}
	}

	/**
	 * Set the extension of the file. If you not set
	 * it in the Name.
	 *
	 * @param extension to the file, with out the dot.
	 */
	public void setExtension(final String extension) {
		this.extension = extension;
	}

	public void reload() {
		if (this.getCustomConfigFile() == null) {
			try {
				load(getAllFilesInPluginJar());
			} catch (final IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		} else {
			try {
				load(getFilesInPluginFolder(this.getName()));
			} catch (final IOException | InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
	}

	public void load(final File[] files) throws IOException, InvalidConfigurationException {
		for (final File file : files) {
			if (file == null) continue;
			if (getCustomConfigFile() == null) {
				this.customConfigFile = file;
			}
			if (!file.exists()) {
				this.plugin.saveResource(file.getName(), false);
			}
			if (this.firstLoad) {
				this.customConfig = YamlConfiguration.loadConfiguration(file);
			} else
				this.customConfig.load(file);
			loadSettingsFromYaml(file);
		}
		this.firstLoad = false;
	}

	public File getDataFolder() {
		return dataFolder;
	}

	public boolean removeFile(final String fileName) {
		final File dataFolder = new File(this.isSingelFile() ? this.getDataFolder().getParent() : this.getPath(), fileName + "." + getExtension());
		return dataFolder.delete();
	}

	public void save() {
		save(null);
	}

	public void save(final String fileToSave) {
		final File dataFolder = new File(getPath());
		if (!dataFolder.isDirectory()) {
			saveChecks(dataFolder);
			return;
		}
		final File[] dataFolders = dataFolder.listFiles();

		if (dataFolder.exists() && dataFolders != null) {
			if (fileToSave != null) {
				if (!checkFolderExist(fileToSave, dataFolders)) {
					final File newDataFolder = new File(getPath(), fileToSave + "." + this.getExtension());
					try {
						newDataFolder.createNewFile();
					} catch (final IOException e) {
						e.printStackTrace();
					} finally {
						saveChecks(newDataFolder);
					}
				} else {
					for (final File file : dataFolders) {
						if (getNameOfFile(file.getName()).equals(fileToSave)) {
							saveChecks(file);
						}
					}
				}
			} else
				for (final File file : dataFolders) {
					saveChecks(file);
				}
		}
	}

	public void saveChecks(final File file) {
		if (!this.yamlMainpath.isEmpty() && serialize() != null)
			saveSerializeData(file);
		else
			saveDataToFile(file);
	}

	public void saveSerializeData(final File file) {
		if (isSingelFile() && !file.getName().equals(getName())) return;
		try {
			customConfig = YamlConfiguration.loadConfiguration(file);
			this.getCustomConfig().set(this.yamlMainpath, null);
			for (final Map.Entry<?, ?> childrenKey : serialize().entrySet())
				if (childrenKey != null) {
					final Object obj = childrenKey.getValue();
				/*	if (obj instanceof Particle)
						obj = obj.toString();*/
					this.getCustomConfig().set(this.yamlMainpath + "." + childrenKey.getKey(), obj);
				}
			this.getCustomConfig().save(file);
		} catch (final Exception ex) {
			ex.printStackTrace();
		}
	}

	public Map<?, ?> serialize() {
		return null;
	}

	/**
	 * Get file or folder name.
	 *
	 * @return file or folder name.
	 */
	public String getName() {
		return name;
	}

	public boolean isSingelFile() {
		return singelFile;
	}

	/**
	 * check if the folder name is empty or null.
	 *
	 * @return true if folder name is empty or null.
	 */
	public boolean isFolderNameEmpty() {
		return this.getName() == null || this.getName().isEmpty();
	}


	public String getFileName() {
		return getNameOfFile(getName());
	}

	public File[] getAllFilesInPluginJar() {
		File[] files = getFilesInPluginFolder(getName());

		if (this.shallGenerateFiles) {
			final Set<String> map = new HashSet<>();
			List<String> filenamesFromDir = null;
			try {
				filenamesFromDir = getFilenamesForDirnameFromCP(getName());

			} catch (final URISyntaxException | IOException e) {
				e.printStackTrace();
			}
			if (filenamesFromDir == null) return null;

			if (files != null && files.length > 0) {
				for (final File file : files) {
					map.add(getNameOfFile(file.getName()));
				}

				for (final String file : filenamesFromDir) {
					if (map.contains(getNameOfFile(file))) {
						final File outFile = new File(this.getPath());
						if (!outFile.exists())
							this.plugin.saveResource(file, false);
					}
				}
			} else {
				for (final String file : filenamesFromDir) {
					final File outFile = new File(this.getPath());
					if (!outFile.exists())
						this.plugin.saveResource(file, false);
				}
				files = getFilesInPluginFolder(getName());
			}
		}

		return files;
	}

	public List<String> getFiles() {
		List<String> filenamesFromDir = null;
		try {
			filenamesFromDir = getFilenamesForDirnameFromCP(getName());
		} catch (final URISyntaxException | IOException e) {
			e.printStackTrace();
		}
		assert filenamesFromDir != null;

		return new ArrayList<>(filenamesFromDir);
	}

	public boolean checkFolderExist(final String fileToSave, final File[] dataFolders) {
		if (fileToSave != null)
			for (final File file : dataFolders) {
				final String fileName = getNameOfFile(file.getName());
				if (fileName.equals(fileToSave))
					return true;
			}
		return false;
	}

	public String getPath() {
		return this.getDataFolder() + "/" + this.getName();
	}

	public File[] getFilesInPluginFolder(final String directory) {
		if (isSingelFile())
			return new File(this.getDataFolder() + "").listFiles(file -> !file.isDirectory() && file.getName().equals(this.getName()));

		final File dataFolder = new File(this.getDataFolder(), directory);
		if (!dataFolder.exists() && !directory.isEmpty())
			dataFolder.mkdirs();

		return dataFolder.listFiles(file -> !file.isDirectory() && file.getName().endsWith("." + getExtension()));
	}

	public String getNameOfFile(String path) {
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

	public List<String> getFilenamesForDirnameFromCP(final String directoryName) throws URISyntaxException, IOException {
		final List<String> filenames = new ArrayList<>();

		final URL url = this.plugin.getClass().getClassLoader().getResource(directoryName);

		if (url != null) {
			if (url.getProtocol().equals("file")) {
				final File file = Paths.get(url.toURI()).toFile();
				if (file != null) {
					final File[] files = file.listFiles();
					if (files != null) {
						for (final File filename : files) {
							filenames.add(filename.toString());
						}
					}
				}
			} else if (url.getProtocol().equals("jar")) {

				final String dirname = isSingelFile() ? directoryName : directoryName + "/";
				final String path = url.getPath();
				final String jarPath = path.substring(5, path.indexOf("!"));
				try (final JarFile jar = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8.name()))) {
					final Enumeration<JarEntry> entries = jar.entries();
					while (entries.hasMoreElements()) {
						final JarEntry entry = entries.nextElement();
						final String name = entry.getName();
						if (!this.isSingelFile() && name.startsWith(this.getFileName())) {
							filenames.add(name);
						} else if (name.startsWith(dirname)) {
							final URL resource = this.plugin.getClass().getClassLoader().getResource(name);
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
		public static void checkBoolean(final boolean b, final String s) {
			if (!b)
				throw new CatchExceptions(s);
		}

		private static class CatchExceptions extends RuntimeException {
			public CatchExceptions(final String message) {
				super(message);
			}
		}
	}
}