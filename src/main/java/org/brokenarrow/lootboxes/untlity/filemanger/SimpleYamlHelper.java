package org.brokenarrow.lootboxes.untlity.filemanger;

import org.apache.commons.lang.StringUtils;
import org.brokenarrow.lootboxes.Lootboxes;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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
	private final boolean shallGenerateFiles;
	private final boolean singelFile;
	private boolean firstLoad = true;
	private FileConfiguration customConfig;
	private String extension;
	private File customConfigFile;
	protected final Plugin plugin = Lootboxes.getInstance();
	private final File dataFolder;

	public SimpleYamlHelper(final String name, final boolean shallGenerateFiles) {
		this(name, false, shallGenerateFiles);
	}

	public SimpleYamlHelper(final String name, final boolean singelFile, final boolean shallGenerateFiles) {
		if (this.plugin == null)
			throw new RuntimeException("You have not set the plugin, becuse it is null");
		this.dataFolder = this.plugin.getDataFolder();
		this.singelFile = singelFile;
		this.name = this.checkIfFileHasExtension(name);
		this.shallGenerateFiles = shallGenerateFiles;
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
		saveDataToFile(file);
	}

	@Nullable
	public <T extends ConfigurationSerializeUtility> T getData(String path, final Class<T> clazz) {
		Valid.checkBoolean(path != null, "path can't be null");
		if (clazz == null) return null;

		Map<String, Object> fileData = new HashMap<>();
		ConfigurationSection configurationSection = customConfig.getConfigurationSection(path);
		if (configurationSection != null)
			for (String data : configurationSection.getKeys(true)) {
				Object object = customConfig.get(path + "." + data);
				if (object instanceof MemorySection) continue;
				fileData.put(data, object);
			}
		Method deserializeMethod = getMethod(clazz, "deserialize", Map.class);
		return invokeStatic(clazz, deserializeMethod, fileData);
	}

	public void setData(@NotNull final File file, @NotNull String path, @NotNull ConfigurationSerializeUtility configuration) {
		Valid.checkBoolean(path != null, "path can't be null");
		Valid.checkBoolean(configuration != null, "Serialize utility can't be null, need provide a class instance some implements ConfigurationSerializeUtility");
		Valid.checkBoolean(configuration.serialize() != null, "Missing serialize method or it is null, can't serialize the class data.");

		this.getCustomConfig().set(path, null);
		for (Map.Entry<String, Object> key : configuration.serialize().entrySet()) {
			this.getCustomConfig().set(path + "." + key.getKey(), SerializeData.serialize(key.getValue()));
		}
		try {
			this.getCustomConfig().save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
		if (isSingelFile()) {
			File checkFile = new File(this.getDataFolder(), this.getName());
			if (!checkFile.exists() && this.shallGenerateFiles)
				createMissingFile();
			return new File(checkFile.getParent()).listFiles(file -> !file.isDirectory() && file.getName().equals(getName(this.getName())));
		}

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

	public String getName(String path) {
		Valid.checkBoolean(path != null && !path.isEmpty(), "The given path must not be empty!");
		int pos;

		if (path.lastIndexOf("/") == -1)
			pos = path.lastIndexOf("\\");
		else
			pos = path.lastIndexOf("/");

		if (pos > 0)
			path = path.substring(pos + 1);

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

	private Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... args) {
		for (final Method method : clazz.getMethods())
			if (method.getName().equals(methodName) && isClassListEqual(args, method.getParameterTypes())) {
				method.setAccessible(true);
				return method;
			}

		return null;
	}

	private <T extends ConfigurationSerializeUtility> T invokeStatic(final Class<T> clazz, final Method method, final Object... params) {
		if (method == null) return null;
		try {
			Valid.checkBoolean(!Modifier.isStatic(method.getModifiers()), "deserialize method need to be static");
			return clazz.cast(method.invoke(method, params));
		} catch (final IllegalAccessException | InvocationTargetException ex) {
			throw new CatchExceptions(ex, "Could not invoke static method " + method + " with params " + StringUtils.join(params));
		}
	}

	private boolean isClassListEqual(Class<?>[] first, Class<?>[] second) {
		if (first.length != second.length) {
			return false;
		} else {
			for (int i = 0; i < first.length; ++i) {
				if (first[i] != second[i]) {
					return false;
				}
			}

			return true;
		}
	}

	private void createMissingFile() {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(getPath(), true));
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class Valid extends RuntimeException {
		public static void checkBoolean(final boolean b, final String s) {
			if (!b)
				throw new CatchExceptions(s);
		}
	}

	private static class CatchExceptions extends RuntimeException {

		public CatchExceptions(Exception exception, final String message) {
			super(message, exception);
		}

		public CatchExceptions(final String message) {
			super(message);
		}
	}
}
