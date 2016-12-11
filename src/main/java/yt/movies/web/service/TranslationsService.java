package yt.movies.web.service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class TranslationsService {
	public static Map<String, Map<String, String>> getTranslations() {
		Map<String, Map<String, String>> ret = new HashMap<String, Map<String, String>>();

		List<String> languages = Arrays.asList("AR", "CS", "DU", "DA", "EL", "ES", "FR", "FA", "RU", "JA", "ZH", "POR",
				"IT", "PL", "TR", "SV", "LT", "ID", "KO", "VI", "RO", "HU", "TH", "SK", "BG", "NO", "IW", "HR", "UK",
				"SR", "SL");

		for (String lang : languages) {
			try (BufferedReader br = new BufferedReader(
					new FileReader("src/main/resources/static/translations/" + lang + ".txt"))) {

				HashMap<String, String> wordToTranslations = new HashMap<String, String>();
				ret.put(lang, wordToTranslations);

				for (String line; (line = br.readLine()) != null;) {
					String[] wordTranslations = line.split("#");
					wordToTranslations.put(wordTranslations[0], wordTranslations[1]);
				}
				// line is not visible here.
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return ret;
	}

	@Test
	public void getTranslationsTest() {
		getTranslations();
	}
}