package yt.movies.web.server.data.access.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import yt.movies.web.service.Movie;
import yt.movies.web.service.MovieStatusEnum;

@Component
public class MovieJDBCMapper implements RowMapper<Movie> {

	@Override
	public Movie mapRow(ResultSet rs, int arg1) throws SQLException {
		Movie movie = new Movie();
		movie.setYoutubeId(rs.getString("ytId"));
        movie.setChosenSub(rs.getString("subtitleLocation"));
        movie.setStatus(MovieStatusEnum.valueOf(rs.getString("status")));
		movie.setNameOfSavedMovieFile(rs.getString("nameOfSavedMovieFile"));
		return movie;
	}

}