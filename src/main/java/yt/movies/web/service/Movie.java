package yt.movies.web.service;

import java.util.List;

public class Movie {
	public Movie(String name, String youtubeLink) {
		super();
		this.name = name;
		this.youtubeId = youtubeLink;
	}

	public Movie() {
	}

	private String id;
	private String name;
	private String youtubeId;
	private List<String> subsLocations;
	private List<String> genres;
	private String releaseYear;
	private MovieStatusEnum status;
	private String chosenSub;
	private String nameOfSavedMovieFile;

	public String getReleaseYear() {

		return releaseYear;
	}

	public void setReleaseYear(String releaseYear) {
		this.releaseYear = releaseYear;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	public List<String> getSubsLocations() {
		return subsLocations;
	}

	public void setSubsLocations(List<String> subsLocations) {
		this.subsLocations = subsLocations;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getYoutubeId() {
		return youtubeId;
	}

	public void setYoutubeId(String youtubeId) {
		this.youtubeId = youtubeId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public MovieStatusEnum getStatus() {
		return status;
	}

	public void setStatus(MovieStatusEnum status) {
		this.status = status;
	}

	public String getChosenSub() {
		return chosenSub;
	}

	public void setChosenSub(String chosenSub) {
		this.chosenSub = chosenSub;
	}

	public String getNameOfSavedMovieFile() {
		return nameOfSavedMovieFile;
	}

	public void setNameOfSavedMovieFile(String nameOfSavedMovieFile) {
		this.nameOfSavedMovieFile = nameOfSavedMovieFile;
	}
}
