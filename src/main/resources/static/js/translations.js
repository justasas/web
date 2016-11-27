var result = new Array();
var language;
var subs;
$(document).on("mouseover", ".word", function() {
	player.pauseVideo();
	// var result = getTrans();
	// getTrans($(this).text());
	if (document.getElementById('selectLang').value == "")
		$("#customdiv").click();else {
		setTimeout(function() {
			tooltip.pop(subs, 'loading...');
			// + "\n" + "Definition: "+ result['definition'] );
			// do something special

		}, 200);
		var word = $(this).text();
		console.log(word);
		word = word.replace(/^[^a-zA-Z0-9]*/, '');
		word = word.replace(/[^a-zA-Z0-9]*$/, '');
		getTrans(word);
		// getDef($(this).text());
		subs = this;
	}
});

$(document).on("mouseleave", ".word", function() {
	player.playVideo();
});

function getTrans(word) {
	$.get('/getTranslation/' + language + '/' + word, function(data,
		status) {
		result['word'] = data['word'];
		result['translation'] = data['translation'].replace('\n', '<br/>');
	}, "json");
}

function selectLang(lang) {
	language = lang;
	if (typeof (Storage) !== "undefined") {
		localStorage.setItem('transLang', lang);
	}
}


$(document).ready(function() {
	$('#selectLang').mobileSelect();
	if (typeof (Storage) !== "undefined") {
		if (localStorage.getItem('transLang') != null) {
			$('#selectLang').val(localStorage.getItem("transLang")).change();
		}
	}
}, 'text');

function doGet(sourceText, sourceLang) {
	var targetLang = 'lt';

	var url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl="
	+ sourceLang + "&tl=" + targetLang + "&dt=t&q=" + encodeURI(sourceText);

	var result;
	var json;
	$.get(url, function(data,
		status) {
//		result['word'] = data['word'];
//		result['translation'] = data['translation'].replace('\n', '<br/>');
		
		var result = JSON.parse(data);
		 
		var translatedText = result[0][0][0];

		json = {
			'sourceText' : sourceText,
			'translatedText' : translatedText
		};
		
		setTimeout(function() {
			tooltip.pop(subs, result['word'] + ' ' + result['translation']);
		// $(".mcTooltipInner").text(result['word'] + ' ' +
		// result['translation']);
		}, 400);
	}, "text");

	// return JSONP
	return json;
}