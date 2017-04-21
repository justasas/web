package yt.movies.web.server.data.access.repository.database;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

//import yt.movies.web.server.data.access.mappers.MovieJDBCMapper;
import yt.movies.web.server.data.access.mappers.MovieJDBCMapper;
import yt.movies.web.service.Movie;
import yt.movies.web.service.MovieStatusEnum;

@Repository
public class MoviesRepository {
	private static final String FIND_ALL_MOVIES_QUERY = "SELECT * FROM movie";
	private static final String UPDATE_MOVIE_STATUS = "UPDATE movie SET status = ? WHERE ytId = ?";
	private static final String UPDATE_MOVIE_SUBTITLE = "UPDATE movie SET subtitleLocation = ?, status = ? WHERE ytId = ?";
	private static final String FIND_MOVIE_QUERY = "SELECT * FROM movie WHERE id = ?";
	private static final String UPDATE_MOVIE_SET_SAVED_FILE_NAME = "UPDATE movie SET nameOfSavedMovieFile  = ? WHERE ytId = ?";


	@Autowired
	private DataSource dataSource;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbctemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private MovieJDBCMapper movieJDBCMapper;

	public List<Movie> findAll() {
		setDynamicInformation();
		return new ArrayList(ytIdToMovie.values());
	}

	private void setDynamicInformation() {
		List<Movie> dynamicMovieInformation = this.jdbcTemplate.query(FIND_ALL_MOVIES_QUERY, movieJDBCMapper);

		for(Movie dynInfMovie : dynamicMovieInformation)
		{
			Movie movie = ytIdToMovie.get(dynInfMovie.getYoutubeId());
			movie.setStatus(dynInfMovie.getStatus());
			movie.setChosenSub(dynInfMovie.getChosenSub());
			movie.setNameOfSavedMovieFile(dynInfMovie.getNameOfSavedMovieFile());
		}

		for(Movie movie : ytIdToMovieWithOriginalSubs.values())
		{
			movie.setStatus(MovieStatusEnum.OK);
			movie.setChosenSub(movie.getSubsLocations().iterator().next());
		}
	}

	public Movie find(String id) {
//		Movie movie = jdbcTemplate.queryForObject(FIND_MOVIE_QUERY, new Object[] { id }, movieJDBCMapper);
		return ytIdToMovie.get(id);
	}

	public static Map<String, Movie> ytIdToMovieWithOriginalSubs = readMoviesWithSubsDownloadLocation("moviesWithOriginalSubs.txt");
	public static Map<String, Movie> ytIdToMovieWithSyncedSubs = readMoviesWithSubsDownloadLocation("moviesWithSyncedSubtitles.txt");
	public static Map<String, Movie> ytIdToMovieWithMultiplePossibleGoodSubtitles = readMoviesWithSubsDownloadLocation("moviesWithMultiplePossibleGoodSubtitles.txt");
	public static Map<String, Movie> ytIdToMovie = readAllMovies();
	//    private static Map<String, Movie> ytIdToMovieWithCcSubsFolder = readMoviesWithSubsDownloadLocation("moviesWithAutoCaptionedSubtitles.txt");

	private static Map<String, Movie> readAllMovies() {
		HashMap<String, Movie> ret = new HashMap<>();
		ret.putAll(ytIdToMovieWithMultiplePossibleGoodSubtitles);
		ret.putAll(ytIdToMovieWithSyncedSubs);
		ret.putAll(ytIdToMovieWithOriginalSubs);

		return ret;
	}

	private static Map<String, Movie> readMoviesWithSubsDownloadLocation(String fileName) {
		HashMap<String, Movie> ret = new HashMap<String, Movie>();
		List<Movie> movies = readMoviesFromFileWithSubsDownloadLocation(fileName);

		for (Movie movie : movies) {
			ret.put(movie.getYoutubeId(), movie);
		}
		return ret;
	}

	private static List<Movie> readMoviesFromFileWithSubsDownloadLocation(String fileName) {
		List<Movie> movies = new ArrayList<>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));

			String line;
			while ((line = br.readLine()) != null) {
				Movie m = new Movie();
				m.setName(line);
				m.setYoutubeId(br.readLine());
				m.setReleaseYear(br.readLine());
				m.setGenres(Arrays.asList(br.readLine().split(",")));
//				m.setSubsLocations(Arrays.asList(br.readLine().replaceFirst("^\\[","").replaceFirst("\\]$", "").split(",")));
				List<String> subsLocations = Arrays.asList(br.readLine().replaceFirst("^\\[", "").replaceFirst("\\]$", "").split(".srt ?"));
				for (int i = 0; i < subsLocations.size(); i++) {
					subsLocations.set(i, subsLocations.get(i) + ".srt");
				}
				m.setSubsLocations(subsLocations);
				br.readLine();
				movies.add(m);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return movies;
	}

	public int updateMovieStatus(MovieStatusEnum status, String ytId) {
		return jdbcTemplate.update(UPDATE_MOVIE_STATUS, status.name(), ytId);
	}

	public int updateMovie(String subtitleLocation, MovieStatusEnum movieStatusEnum, String ytId) {
		return jdbcTemplate.update(UPDATE_MOVIE_SUBTITLE, subtitleLocation, movieStatusEnum.toString(), ytId);
	}

	public void insertOrUpdateMovie(String ytId, MovieStatusEnum movieStatusEnum, String subtitleLocation) {
		if (updateMovie(subtitleLocation, movieStatusEnum, ytId) == 0)
			insertMovie(ytId, movieStatusEnum, subtitleLocation);
	}

	private int insertMovie(String ytId, MovieStatusEnum movieStatusEnum, String subtitleLocation) {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("movie");
		Map<String, Object> parameters = new HashMap();
		parameters.put("ytId", ytId);
		parameters.put("status", movieStatusEnum.toString());
		parameters.put("subtitleLocation", subtitleLocation);
		return simpleJdbcInsert.execute(parameters);
	}

	public void insertOrUpdateMovieStatus(String ytId, MovieStatusEnum movieStatusEnum) {
		if (updateMovieStatus(movieStatusEnum, ytId) == 0)
			insertMovieStatus(ytId, movieStatusEnum);
	}

	private int insertMovieStatus(String ytId, MovieStatusEnum movieStatusEnum) {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("movie");
		Map<String, Object> parameters = new HashMap();
		parameters.put("ytId", ytId);
		parameters.put("status", movieStatusEnum.toString());
		return simpleJdbcInsert.execute(parameters);
	}

	public int updateMovieSetSavedFileName(String ytId, boolean persisted) {
		return jdbcTemplate.update(UPDATE_MOVIE_SET_SAVED_FILE_NAME, persisted, ytId);
	}
}