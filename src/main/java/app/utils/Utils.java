package app.utils;

import java.math.BigInteger;
import java.util.Random;

import app.sms.Sms;
import app.sms.SmsService;

public class Utils {
	
	public static String genWalletID () {
		Random random = new Random();
		String nextId = new BigInteger(50, random).toString(15);
		
		String extractDigits = "";
		
 		while (extractDigits.isEmpty() && nextId.length() >= 11) {
			extractDigits = nextId.substring(0, 10);
			
			if (extractDigits.length() == 10) break;
			
			nextId = new BigInteger(50, random).toString(15);
			extractDigits = "";
			
		}
		return extractDigits;
	}
	
	public static String genVerificationCode () {
		Random random = new Random();
		String nextId = new BigInteger(50, random).toString(10);
		
		String extractDigits = "";
		
 		while (extractDigits.isEmpty() && nextId.length() >= 7) {
			extractDigits = nextId.substring(0, 6);
			
			if (extractDigits.length() == 6) break;
			
			nextId = new BigInteger(50, random).toString(10);
			extractDigits = "";
			
		}
		return extractDigits;
	}
	
	public static String genOrganisationCode () {
		Random random = new Random();
		String nextId = new BigInteger(50, random).toString(10);
		
		String extractDigits = "";
		
 		while (extractDigits.isEmpty() && nextId.length() >= 7) {
			extractDigits = nextId.substring(0, 6);
			
			if (extractDigits.length() == 6) break;
			
			nextId = new BigInteger(50, random).toString(10);
			extractDigits = "";
			
		}
		return extractDigits;
	}
	
	public static String genReferralCode () {
		Random random = new Random();
		String nextId = new BigInteger(50, random).toString(15);
		
		String extractDigits = "";
		
 		while (extractDigits.isEmpty() && nextId.length() >= 11) {
			extractDigits = nextId.substring(0, 10);
			
			if (extractDigits.length() == 10) break;
			
			nextId = new BigInteger(50, random).toString(15);
			extractDigits = "";
		}
		return extractDigits;
	}
	
	public static String genTaskReference () {
		Random random = new Random();
		String nextId = new BigInteger(50, random).toString(12);
		
		String extractDigits = "";
		
 		while (extractDigits.isEmpty() && nextId.length() >= 9) {
			extractDigits = nextId.substring(0, 8);
			
			if (extractDigits.length() == 8) break;
			
			nextId = new BigInteger(50, random).toString(12);
			extractDigits = "";
			
		}
 		StringBuilder taskRef = new StringBuilder();
 		taskRef.append("TSK-");
 		taskRef.append(extractDigits.toUpperCase());
		return taskRef.toString();
	}
	
	public static void sendVerificationSMS(String[] numbers, 
			String verificationCode, Boolean isResetPassword, Boolean requestPassword, String appShortName) throws Exception{
		StringBuilder appender = new StringBuilder();
		appender.append(appShortName);
		Sms sms = new Sms();
		sms.setToNumbers(numbers);
		if (verificationCode != null) {
			if (isResetPassword) {
				sms.setSender(appender.append("-RESET").toString());
				sms.setMessage("You have request for a password reset " + "on " + appShortName + ".\nUse this PIN "
						+ verificationCode + " to reset your password.");
			} else {
				if (requestPassword) {
					sms.setSender(appender.append("-TOKEN").toString());
					sms.setMessage("Use this PIN "
							+ verificationCode + " to proceed " + "with your registration.");
				} else {
					sms.setSender(appender.append("-TOKEN").toString());
					sms.setMessage("You have shown interest to become a service provider " + "on "+ appShortName +".\nUse this PIN "
							+ verificationCode + " to proceed " + "with your registration.");
				}
			}
			
		} else {
			sms.setSender(appender.append("-RESET").toString());
			sms.setMessage("Your have reset your password successfully "
					+ "on "+ appShortName +".\nWelcome on board!");
		}
		SmsService.sendSMS(sms);
	}
	
	public static void sendResetPasswordSMS(String[] numbers, String appShortName) throws Exception{
		StringBuilder appender = new StringBuilder();
		appender.append(appShortName);
		Sms sms = new Sms();
		sms.setToNumbers(numbers);
		sms.setSender(appender.append("-RESET").toString());
		sms.setMessage("Your password has been reset successfully.");
		SmsService.sendSMS(sms);
	}
	
	public static void sendRegistrationCompleteSMS(String[] numbers, String appShortName) throws Exception{
		StringBuilder appender = new StringBuilder();
		appender.append(appShortName);
		Sms sms = new Sms();
		sms.setToNumbers(numbers);
		sms.setSender(appender.append("-REG").toString());
		sms.setMessage("Your registration has been completed successfully " + "on " + appShortName + ".\nWelcome on board!");
		SmsService.sendSMS(sms);
	}
	
	public static void main(String[] args) {
		//System.out.println(Utils.INSTANCE.genVerificationCode());
	}
}
