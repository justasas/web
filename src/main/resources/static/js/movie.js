//var params = {
//	wmode : "transparent",
//	allowScriptAccess : "always",
//	allowfullscreen : "true"
//};
//var atts = {
//	id : "player"
//};

// 2. This code loads the IFrame Player API code asynchronously.
var tag = document.createElement('script');

tag.src = "https://www.youtube.com/iframe_api";
var firstScriptTag = document.getElementsByTagName('script')[0];
firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

// 3. This function creates an <iframe> (and YouTube player)
//    after the API code downloads.
var player;
function onYouTubeIframeAPIReady() {
  player = new YT.Player('player', {
    height: '550',
    width: '800',
    videoId: youtId,
//    origin: 'http://localhost:8080',
    wmode: 'opaque',
    events: {
      'onReady': onPlayerReady,
      'onStateChange': onPlayerStateChange
    }
  });
}

// 4. The API will call this function when the video player is ready.
function onPlayerReady(event) {
  player.addEventListener("onStateChange", "onplayerStateChange");
}

function stopVideo() {
  player.stopVideo();
}

function updateStatus(status) {
	var src = "/admin/movie/update-status/" + youtId + "/" + status;

	$
			.get(
					src,
					function(myContentFile, status) {
						console.log(status);
					});
}