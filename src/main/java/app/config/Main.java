package app.config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
	
	public static void main(String[] args) {
		String regexOptional = "\\b[1-3][0-9]{1}\\b";
		
		Pattern pattern = Pattern.compile(regexOptional);
		
		String strDate = "29";
		
		Matcher matcher = pattern.matcher(strDate);
		
		if (matcher.find()) {
			System.out.println("Matched");
			System.out.println("Date: " + strDate);
		} else {
			System.out.println("Not matched");
			System.out.println("Date: " + strDate);
		}
		
		
	}

}
