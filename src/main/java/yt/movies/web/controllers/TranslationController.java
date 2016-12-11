package yt.movies.web.controllers;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import yt.movies.web.service.TranslationsService;

@Controller
public class TranslationController {
	private static String regex = "(adjective|noun|verb|adverb|pronoun)\",\\[(\"[^\"]+\",?){1,4}";
	private static Pattern pattern = Pattern.compile(regex);

	public static final Map<String, Map<String, String>> LANGUAGE_ID_TO_WORD_TRANSLATIONS = TranslationsService
			.getTranslations();

	@Test
	public void forTestingWordPartsToRemove() {
		assertEquals("word", "wo07rd's".replaceAll("'s|[^a-zA-Z]+", ""));
	}

	@RequestMapping(value = "/getTranslation/{language}/{word}", method = RequestMethod.GET)
	public @ResponseBody String getTranslation(@PathVariable String language, @PathVariable String word) {

		try {
			word = word.toLowerCase().replaceAll("'s|[^a-z]+", "");

			Map<String, String> translations = LANGUAGE_ID_TO_WORD_TRANSLATIONS.get(language);
			if (translations != null) {

				String wordTranslations = translations.get(word);
				if (wordTranslations != null) {
					return wordTranslations;
				} else {
					String translationsGoogleResponse = sendGet(
							"https://translate.google.com/translate_a/single?client=gtx&sl=en&tl=" + language
									+ "&hl=en&dt=bd&dt=t&ie=UTF-8&oe=UTF-8&q=" + word);

					Matcher m = pattern.matcher(translationsGoogleResponse);

					String ret = "";
					while (m.find()) {
						ret += m.group().replaceAll("(\")|(,\\[)|(adjective)|(noun)|(verb)|(adverb)(pronoun)", "")
								+ "#";
					}

					if (!ret.isEmpty()) {
						translations.put(word, ret);

						try {
							Files.write(Paths.get("src/main/resources/static/translations/" + language + ".txt"),
									(word + "#" + ret).getBytes(), StandardOpenOption.APPEND);
						} catch (IOException e) {
							System.out.println(e);
						}

						return ret;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "sorry, translations could not be found";
	}

	private final String USER_AGENT = "Mozilla/5.0";

	// HTTP GET request
	private String sendGet(String url) throws Exception {

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		// add request header
		con.setRequestProperty("User-Agent", USER_AGENT);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		return response.toString();
	}
}
