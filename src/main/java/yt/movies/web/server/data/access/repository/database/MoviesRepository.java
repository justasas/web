package yt.movies.web.server.data.access.repository.database;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import yt.movies.web.server.data.access.mappers.MovieJDBCMapper;
import yt.movies.web.service.Movie;

@Repository
public class MoviesRepository {
	private static final String FIND_ALL_MOVIES_QUERY = "SELECT * FROM movie";
	private static final String FIND_MOVIE_QUERY = "SELECT * FROM movie WHERE id = ?";

	@Autowired
	private DataSource dataSource;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbctemplate;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private MovieJDBCMapper movieJDBCMapper;

	public Movie insert(Movie movie) {
		SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource).withTableName("auctions")
				.usingGeneratedKeyColumns("id");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("ytId", movie.getYoutubeId());
		parameters.put("title", movie.getName());
		parameters.put("rating", movie.getRating());
		parameters.put("year", movie.getYear());
		parameters.put("duration", movie.getDuration());
		movie.setId((Integer) simpleJdbcInsert.executeAndReturnKey(parameters));
		return movie;
	}

	public List<Movie> findAll() {
		return this.jdbcTemplate.query(FIND_ALL_MOVIES_QUERY, movieJDBCMapper);
	}

	public Movie find(int id) {
//		Movie movie = jdbcTemplate.queryForObject(FIND_MOVIE_QUERY, new Object[] { id }, movieJDBCMapper);
		Movie movie = new Movie();
		return movie;
	}

}
