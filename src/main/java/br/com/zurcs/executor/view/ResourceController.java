package br.com.zurcs.executor.view;

import java.io.InputStream;
import java.net.URL;

public class ResourceController {

	public static URL getUrl(String nome) {

		return ResourceController.class.getClassLoader().getResource(nome);
	}

	public static InputStream getInputStream(String nome) {
		
		return ResourceController.class.getClassLoader().getResourceAsStream(nome);
	}
}
