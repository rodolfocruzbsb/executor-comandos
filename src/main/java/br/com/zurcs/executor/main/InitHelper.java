package br.com.zurcs.executor.main;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class InitHelper {

	private static final Logger LOGGER = LogManager.getLogger(InitHelper.class);

	public static void printInit() {

		StringBuilder sb = new StringBuilder();

		sb.append("\n      _______  _______ ____ _   _ _____ ___  ____     ____ __  __ ____          _____                _ ____    ");
		sb.append("\n     | ____\\ \\/ / ____/ ___| | | |_   _/ _ \\|  _ \\   / ___|  \\/  |  _ \\        |__  /   _ _ __ ___  ( ) ___|   ");
		sb.append("\n     |  _|  \\  /|  _|| |   | | | | | || | | | |_) | | |   | |\\/| | | | |_____    / / | | | '__/ __| |/\\___ \\   ");
		sb.append("\n     | |___ /  \\| |__| |___| |_| | | || |_| |  _ <  | |___| |  | | |_| |_____|  / /| |_| | | | (__     ___) |  ");
		sb.append("\n     |_____/_/\\_\\_____\\____|\\___/  |_| \\___/|_| \\_\\  \\____|_|  |_|____/        /____\\__,_|_|  \\___|   |____/   ");
		                                                                                                         
		LOGGER.info(sb.toString());
	}

}
