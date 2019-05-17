package app.utils;

import static org.javalite.app_config.AppConfig.p;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import app.dto.ImageDTO;

public class CommonUtil {
	static Properties properties = null;
	static {
		properties = CommonUtil.loadPropertySettings("firebaseconfig");
	}
	public static Gson getJsonConvertor(boolean serializeNulls) {

		GsonBuilder builder = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting()
				.disableHtmlEscaping();

		if (serializeNulls) {
			builder.serializeNulls();
		}

		return builder.create();
	}

	public static Gson getJsonConvertor() {
		return getJsonConvertor(false);
	}

	public static String toJson(Object object) {
		return CommonUtil.getJsonConvertor().toJson(object);
	}

	public static String uploadImageToCloudinary(ImageDTO imageDto) throws IOException {
		Cloudinary cloudinary = new Cloudinary();
		@SuppressWarnings("rawtypes")
		Map params = ObjectUtils.asMap("cloud_name", properties.getProperty("cloud_name"), 
				"api_key", properties.getProperty("api_key"), "api_secret",
				properties.getProperty("api_secret"), "public_id", imageDto.getId(), 
				"folder", properties.getProperty("folder"), "overwrite", true,
				"format", "jpg", "resource_type", "image", "unique_filename", true, "eager",
				Arrays.asList(new Transformation().width(400).height(300).crop("pad"),
						new Transformation().width(260).height(200).crop("crop").gravity("north")));

		@SuppressWarnings("rawtypes")
		Map response = cloudinary.uploader().upload(imageDto.getImage(), params);
		String imageUrl = response.get("url").toString();
		return imageUrl;
	}

	public static Properties loadPropertySettings(String fileName) {
			System.out.println("Loading... " + p("config_location") + "/" + fileName + ".properties");
			File configFile = new File(p("config_location") + "/" + fileName + ".properties");
			try {
				Reader reader = new InputStreamReader(new FileInputStream(configFile));
				Properties properties = new Properties();
				properties.load(reader);

				return properties;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
	}
//	public static void main(String[] args) {
//		ImageDTO image = new ImageDTO();
//		image.setId("3343232");
//		image.setImage("https://res.cloudinary.com/boripe/image/upload/serve90/MY_FACE.png");
//
//		try {
//			System.out.println(uploadImageToCloudinary(image));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
}
