package yt.movies.web.server.data.access.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import yt.movies.web.service.Movie;

@Component
public class MovieJDBCMapper implements RowMapper<Movie> {

	@Override
	public Movie mapRow(ResultSet rs, int arg1) throws SQLException {
		Movie movie = new Movie();
		movie.setId(rs.getInt("id"));
		movie.setName(rs.getString("title"));
		movie.setDuration(rs.getInt("duration"));
		movie.setRating(rs.getInt("rating"));
		movie.setYear(rs.getInt("year"));
		movie.setYoutubeId(rs.getString("ytId"));
		return movie;
	}

}