package yt.movies.web.controllers;

import java.util.ArrayList;
import java.util.List;

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
	
	@RequestMapping(value="/getTranslation/{language}/{word}", method = RequestMethod.GET)
	public @ResponseBody String getShopInJSON(@PathVariable String language, @PathVariable String word) {

		String translation = "HAHA";

		return translation;

	}
}