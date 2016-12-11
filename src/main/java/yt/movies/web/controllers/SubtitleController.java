package yt.movies.web.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import yt.movies.web.service.Subtitle;
import yt.movies.web.service.SubtitlesUtils;

@Controller
public class SubtitleController {
	private static final int MOVIE_SUBTITLES_TO_RETURN = 10;
	public static final Map<String, List<Subtitle>> MOVIE_ID_TO_SUBTITLES = SubtitlesUtils.getSubtitles();

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
