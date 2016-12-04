package yt.movies.web.controllers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import yt.movies.web.server.data.access.repository.database.MoviesRepository;
import yt.movies.web.service.Movie;
import yt.movies.web.service.Subtitle;
import yt.movies.web.service.SubtitlesUtils;

@Controller
public class HomeController {

	@Autowired
	private MoviesRepository moviesRepository;
	private static final int MOVIES_PER_ROW = 2;
	private static final int MOVIE_SUBTITLES_TO_RETURN = 10;

	public static final Map<String, List<Subtitle>> MOVIE_ID_TO_SUBTITLES = getSubtitles();

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

	private static Map<String, List<Subtitle>> getSubtitles() {
		Map<String, List<Subtitle>> ret = new HashMap<String, List<Subtitle>>();

		ApplicationContext appContext = new ClassPathXmlApplicationContext();

		for (int i = 0; i < 1; i++) {
			try {
				org.springframework.core.io.Resource resource = appContext
						.getResource("classpath:/static/subtitles/" + 1 + ".srt");

				InputStream is;
				is = resource.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));

				ret.put(String.valueOf(i + 1), SubtitlesUtils.parseSubtitles(br));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ret;
	}

	private boolean itIsALastMovie(List<Movie> allMovies, Movie movie) {
		return allMovies.get(allMovies.size() - 1) == movie;
	}

	@RequestMapping("/movie/{id}")
	public String viewMovie(@PathVariable String id, Model model) {
		model.addAttribute("movie", moviesRepository.find(Integer.valueOf(id)));
		return "movie";
	}

	@RequestMapping("/movie/{id}/subtitlesF/{milli}")
	@ResponseBody
	public String getMovieSubtitlesForce(@PathVariable String id, @PathVariable Integer milli, Model model) {

		List<Subtitle> subtitles = MOVIE_ID_TO_SUBTITLES.get(id);

		String ret = "";

		if (subtitles != null) {
			int closestTimeIndex = indexOfClosestValue(subtitles, milli);
			for (int i = 0; i < MOVIE_SUBTITLES_TO_RETURN; i++) {
				if ((closestTimeIndex + i) < subtitles.size()) {
					Subtitle subtitle = subtitles.get(closestTimeIndex + i);
					ret += subtitle.id + "\n" + subtitle.startEnd + "\n" + subtitle.text + "\n\n";
				}
			}
		}

		return ret;
	}

	@RequestMapping("/movie/{id}/subtitles/{milli}")
	@ResponseBody
	public String getMovieSubtitles(@PathVariable String id, @PathVariable Integer milli, Model model) {

		List<Subtitle> subtitles = MOVIE_ID_TO_SUBTITLES.get(id);

		String ret = "";

		int w = 0;
		boolean found = false;
		if (subtitles != null) {
			for (Subtitle subtitle : subtitles) {
				if (w > MOVIE_SUBTITLES_TO_RETURN)
					break;
				if (found == true) {
					ret += subtitle.id + "\n" + subtitle.startEnd + "\n" + subtitle.text + "\n\n";
					w++;
				} else if (milli >= subtitle.start && milli <= subtitle.end) {
					found = true;
				}
			}
		}

		return ret;
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

	public static int indexOfClosestValue(List<Subtitle> subtitles, int milli) {
		int smallestDiff = Integer.MAX_VALUE;
		int i = 0;
		for (Subtitle subtitle : subtitles) {
			int time = subtitle.start;
			if (milli > time)
				break;
			int diff = Math.abs(time - milli);
			if (smallestDiff > diff) {
				return i;
				// 20 50 80
			}
			i++;
			smallestDiff = diff;
		}
		return -1;
	}
}