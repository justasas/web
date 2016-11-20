var result = new Array();
var language;
var subs;
$(document).on("mouseover", ".word", function() {
	player.pauseVideo();
	// var result = getTrans();
	// getTrans($(this).text());
	if (document.getElementById('selectLang').value == "")
		$("#customdiv").click();
	else {
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

$(document).ajaxComplete(function(event, xhr, settings) {
	// alert(settings.url+'{{path('getTranslations')}}');
	if (settings.url.indexOf('{{path(getTranslation') > -1)
		setTimeout(function() {
			tooltip.pop(subs, result['word'] + ' ' + result['translation']);
			// $(".mcTooltipInner").text(result['word'] + ' ' +
			// result['translation']);
		}, 400);
});

function getTrans(word) {
	$.get('http://localhost:8080/getTranslation/' + language + '/' + word, function(data,
			status) {
		result['word'] = data['word'];
		result['translation'] = data['translation'].replace('\n', '<br/>');
	}, "json");
}

function selectLang(lang) {
	language = lang;
}


$(document).ready(function() {
	$('#selectLang').mobileSelect();
}, 'text');