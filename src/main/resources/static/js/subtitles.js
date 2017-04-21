convertSMPTEtoSeconds = function(SMPTE) {
	if (typeof SMPTE != 'string')
		return false;

	SMPTE = SMPTE.replace(',', '.');

	var secs = 0, decimalLen = (SMPTE.indexOf('.') != -1) ? SMPTE.split('.')[1].length
			: 0, multiplier = 1;

	SMPTE = SMPTE.split(':').reverse();

	for (var i = 0; i < SMPTE.length; i++) {
		multiplier = 1;
		if (i > 0) {
			multiplier = Math.pow(60, i);
		}
		secs += Number(SMPTE[i]) * multiplier;
	}
	return Number(secs.toFixed(decimalLen));
}

var entries = {
		id : [],
		text : [],
		timeStart : [],
		timeEnd : []
	};

function readSubtitlesToVariable(myContentFile)
{
    entries = {
              		id : [],
              		text : [],
              		timeStart : [],
              		timeEnd : []
              	};
	var lines = myContentFile.split(/\r?\n/);

	var pattern_identifier = /^([a-zA-z]+-)?[0-9]+$/;
	var pattern_timecode = /^([0-9]{2}:[0-9]{2}:[0-9]{2}([,.][0-9]{1,3})?) --\> ([0-9]{2}:[0-9]{2}:[0-9]{2}([,.][0-9]{3})?)(.*)$/;
	var text, timecode;

	for (var i = 0; i < lines.length; i++) {
		// check for the line number
		var lineNumber = pattern_identifier.exec(lines[i]);
		if (lineNumber) {
			// skip to the next line where the start --> end
			// time code should be
			i++;
			timecode = pattern_timecode.exec(lines[i]);
			if (timecode && i < lines.length) {
				i++;
				// grab all the (possibly multi-line) text
				// that follows
				text = lines[i];
				i++;
				while (lines[i] !== '' && i < lines.length) {
					text = text + '\n' + lines[i];
					i++;
				}
				// ////////text =
				// $.trim(text).replace(/(\b(https?|ftp|file):\/\/[-A-Z0-9+&@#\/%?=~_|!:,.;]*[-A-Z0-9+&@#\/%=~_|])/ig,
				// "<a href='$1' target='_blank'>$1</a>");
				// Text is in a different array so I can use
				// .join
				text = text.replace(/(\r\n|\n|\r)/gm, " ");
				entries.id.push(lineNumber)
				entries.text.push(text);
				entries.timeStart
						.push((convertSMPTEtoSeconds(timecode[1]) == 0) ? 0.200
								: convertSMPTEtoSeconds(timecode[1]));
				entries.timeEnd
						.push(convertSMPTEtoSeconds(timecode[3]));
			}
			;
		}
		;
	}
	;	
}

function readFromFile(id) {

	var src = "/"+id;

	$
			.get(
					src,
					function(myContentFile, status) {
						readSubtitlesToVariable(myContentFile)
					});
}

$(document).ready(function() {
// var select = document.getElementById('subsSelectLang');
// var movieUri = window.location.href.split('/');
// var movieId = movieUri[movieUri.length - 1];
// readFromFile(movieId);
}, 'text');

function getCurrentSubNr() {
	var time = Number(player.getCurrentTime());
	for (i = 0; i < entries.timeStart.length; i++) {
		if (time >= entries.timeStart[i] && time <= entries.timeEnd[i]) {
			return i;
			if (i == 0)
				console.log("1111111");
		}
	}
	return false;
}

/*
 * var counter = 50;
 * 
 * function onplayerStateChange(newState) { if (newState == 1) { player =
 * document.getElementById("myplayer"); function setNextCall() { //
 * http://stackoverflow.com/questions/1280263/changing-the-interval-of-setinterval-while-its-running
 * var i = getCurrentSubNr(); putSubtitle(i); i++; clearInterval(interval);
 * duration = (entries.timeEnd[i] - entries.timeStart[i]) * 1000; interval =
 * setInterval(setNextCall, duration); } var i = getCurrentSubNr(); first =
 * (entries.timeStart[i]) * 1000; var interval = setInterval(setNextCall,
 * first); } else { } }
 */

var prev = -1;

function onPlayerStateChange(newState) {
	if (newState.data == 1 && newState.data != -1) {
		var interval = setInterval(function() {
			var i = getCurrentSubNr();
			if (prev == i)
				return;
			prev = i;
			if (i != false) {
				$('.subs-block').html(entries.text[i]);
				$(".subs-block").lettering('words');
			}
		}, 1000);
	}
	if (newState.data != 1) {
		clearInterval(interval);
	}
}

//function getSubtitles()
//{
//	$.get(window.location.href + "/subtitles/" + player.getCurrentTime() * 1000, function(data,
//			status) {
//		if(data.length != 0)
//			readSubtitlesToVariable(data);
//	}, "text");
//}

//function getSubtitlesForce()
//{
//	$.get(window.location.href + "/subtitlesF/" + player.getCurrentTime() * 1000, function(data,
//			status) {
//		if(data.length != 0)
//			readSubtitlesToVariable(data);
//	}, "text");
//}
// pradeda grot, issiunciu requesta 10 pirmu subtitle gaut,
// imu paskutinio subtitle laiko ir dabartinio payerio laiko
// skirtuma ir tikrinu ar maziau nei 3 sekundes
// jei maziau, tai siunciu requesta 10 kitu subtitle gaut
// jei persoka 
//function onPlayerStateChange(newState) {
//	if (YT.PlayerState.PLAYING == newState.data) {
//		getSubtitlesForce();
//		var interval = setInterval(function() {
//
//			if(entries.timeStart.length != 0)
//			{
//				var timeStart = entries.timeStart;
//				if((timeStart[timeStart.length-1] - Number(player.getCurrentTime()) < 3))
//				{
//					getSubtitles();
//				}
//			}
//
//			var i = getCurrentSubNr();
//			if (prev == i)
//				return;
//			prev = i;
//			if (i != false) {
//				$('.subs-block').html(entries.text[i]);
//				$(".subs-block").lettering('words');
//			}
//		}, 1000);
//	}
//
//	if (newState.data == YT.PlayerState.ENDED || newState.data == YT.PlayerState.PAUSED) {
//		clearInterval(interval);
//	}
//}

function getCurrentSubNr() {
	var time = Number(player.getCurrentTime());
	for (i = 0; i < entries.timeStart.length; i++) {
		if (time >= entries.timeStart[i] && time <= entries.timeEnd[i]) {
			return i;
			if (i == 0)
				console.log("1111111");
		}
	}
	return false;
}

function putSubtitle(i) {
	$('.subs-block').html(entries.text[i]);
}

function play() {
	if (player) {
		player.playVideo();
	}
}

function shiftSubsPositions(milli) {
	var txtbox = document.getElementById(milli);
	var milliSeconds = txtbox.value;

	seconds = milli / 1000;
	for (i = 0; i < entries.timeStart.length; i++) {
		entries.timeStart[i] += seconds;
		entries.timeEnd[i] += seconds;
	}
	// entries.text
	// entries.timeStart
	// entries.timeEnd
}

//var subsCount;

function updateSubtitle() {
	var src = "/admin/movie/update-subtitles-set-movie-status-ok/" + youtId + "/" + $("#selectSub").prop('selectedIndex');

	$
			.get(
					src,
					function(myContentFile, status) {
						console.log(status);
					});
}