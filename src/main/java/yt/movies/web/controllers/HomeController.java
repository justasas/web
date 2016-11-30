package yt.movies.web.controllers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import yt.movies.web.server.data.access.repository.database.MoviesRepository;
import yt.movies.web.service.Movie;

@Controller
public class HomeController {

	@Autowired
	private MoviesRepository moviesRepository;
	static final int MOVIES_PER_ROW = 2;

	@RequestMapping("/")
	public String home(Model model) {
		List<List<Movie>> rows = new ArrayList<List<Movie>>();
		List<Movie> rowMovies = new ArrayList<Movie>();
		int i = 0;
		List<Movie> allMovies = moviesRepository.findAll();
		for (Movie movie : allMovies) {
			if (itIsALastMovie(allMovies, movie))
				rowMovies.add(movie);
			if (i++ == MOVIES_PER_ROW || itIsALastMovie(allMovies, movie)) {
				rows.add(rowMovies);
				rowMovies = new ArrayList<Movie>();
				i = 0;
			} else
				rowMovies.add(movie);
		}
		model.addAttribute("rows", rows);
		return "index";
	}

	private boolean itIsALastMovie(List<Movie> allMovies, Movie movie) {
		return allMovies.get(allMovies.size() - 1) == movie;
	}

	@RequestMapping("/movie/{id}")
	public String viewMovie(@PathVariable String id, Model model) {
		model.addAttribute("movie", moviesRepository.find(Integer.valueOf(id)));
		return "movie";
	}

	private static String regex = "(adjective|noun|verb|adverb|pronoun)\",\\[(\"[^\"]+\",?){1,4}";
	private static Pattern pattern = Pattern.compile(regex);

	@RequestMapping(value = "/getTranslation/{language}/{word}", method = RequestMethod.GET)
	public @ResponseBody String getTranslation(@PathVariable String language, @PathVariable String word) {

		String ret = null;

		try {
			String translation = sendGet("https://translate.google.com/translate_a/single?client=gtx&sl=en&tl="
					+ language + "&hl=en&dt=bd&dt=t&ie=UTF-8&oe=UTF-8&q=" + word);

			Matcher m = pattern.matcher(translation);

			while (m.find()) {
				ret += m.group().replaceAll("(\")|(,\\[)|(adjective)|(noun)|(verb)|(adverb)(pronoun)", "") + "\n";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ret == null ? "could not get a translation" : ret;
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